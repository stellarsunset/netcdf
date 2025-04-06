package com.stellarsunset.netcdf;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.stellarsunset.netcdf.field.*;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.util.*;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

record SchemaBindingValidator<T>(NetcdfFile file, SchemaBinding<T> binding) {

    /**
     * Check the validity of a set of bindings against a target {@link NetcdfFile} to parse.
     *
     * @param file    the NetCDF formatted file to check the bindings against
     * @param binding the desired bindings
     */
    static <T> void checkValidity(NetcdfFile file, SchemaBinding<T> binding) {

        Optional<Error> error = new SchemaBindingValidator<>(file, binding).findErrors();

        if (error.isPresent()) {
            throw error.get().asException();
        }
    }

    /**
     * Optionally return an {@link Error} describing any issues which would result in errors if the provided schema was
     * run against the provided {@link NetcdfFile}.
     */
    Optional<Error> findErrors() {
        // this order does matter, if the variable exists then the dimensions do too, saving us some checks
        return checkVariables().or(this::checkDimensions);
    }

    /**
     * Check all the variables for existing and correct type-to-setter bindings.
     */
    private Optional<Error> checkVariables() {

        Error.Combined.Builder builder = Error.Combined.builder();

        for (Map.Entry<String, FieldSetter<T>> coordinateVariable : binding.coordinateVariables().entrySet()) {

            String variableName = coordinateVariable.getKey();
            FieldSetter<T> setter = coordinateVariable.getValue();

            checkVariable(variableName, setter).ifPresent(builder::addError);
        }

        for (Map.Entry<String, FieldSetter<T>> dimensionVariable : binding.dimensionVariables().entrySet()) {

            String variableName = dimensionVariable.getKey();
            FieldSetter<T> setter = dimensionVariable.getValue();

            checkVariable(variableName, setter).ifPresent(builder::addError);
        }

        return builder.build();
    }

    /**
     * Check a requested variable exists and its setter type handles the {@link DataType} associated with that variable
     * in the provided file.
     */
    private Optional<Error> checkVariable(String variableName, FieldSetter<T> setter) {

        Variable variable = file.findVariable(variableName);

        if (variable == null) {
            return Optional.of(new Error.MissingVariable(variableName));
        }

        return checkType(variable, setter);
    }

    /**
     * Copied in part from {@link ucar.ma2.Array#factory(DataType, int[])}.
     */
    private Optional<Error> checkType(Variable variable, FieldSetter<T> setter) {
        return switch (variable.getDataType()) {
            case DOUBLE -> shouldBe(variable, setter, DoubleSetter.class);
            case FLOAT -> shouldBe(variable, setter, FloatSetter.class);
            case CHAR -> shouldBe(variable, setter, CharacterSetter.class);
            case BOOLEAN -> shouldBe(variable, setter, BooleanSetter.class);
            case ENUM4, UINT, INT -> shouldBe(variable, setter, IntSetter.class);
            case ENUM2, USHORT, SHORT -> shouldBe(variable, setter, ShortSetter.class);
            case ENUM1, UBYTE, BYTE -> shouldBe(variable, setter, ByteSetter.class);
            case ULONG, LONG -> shouldBe(variable, setter, LongSetter.class);
            case STRING, STRUCTURE, SEQUENCE, OPAQUE, OBJECT ->
                    Optional.of(new Error.UnhandledVariableType(variable.getFullName(), variable.getDataType()));

        };
    }

    @SuppressWarnings("rawtypes")
    private Optional<Error> shouldBe(Variable variable, FieldSetter<T> setter, Class<? extends FieldSetter> expected) {
        if (!expected.isAssignableFrom(setter.getClass())) {

            // Runtime class can be a lambda... need to map it to a meaningful name
            Class<? extends FieldSetter> setterType = switch (setter) {
                case BooleanSetter<T> bs -> BooleanSetter.class;
                case ByteSetter<T> bs -> ByteSetter.class;
                case CharacterSetter<T> cs -> CharacterSetter.class;
                case DoubleSetter<T> ds -> DoubleSetter.class;
                case FloatSetter<T> fs -> FloatSetter.class;
                case IntSetter<T> is -> IntSetter.class;
                case LongSetter<T> ls -> LongSetter.class;
                case ShortSetter<T> ss -> ShortSetter.class;
                case NoopSetter<T> ns -> NoopSetter.class;
            };

            Error error = new Error.IncorrectVariableType(
                    variable.getFullName(),
                    variable.getDataType(),
                    setterType
            );

            return Optional.of(error);
        }

        return Optional.empty();
    }

    /**
     * Check the consistency of the dimensions referenced by our coordinate and dimension variables.
     */
    private Optional<Error> checkDimensions() {

        Multimap<String, String> dimensionsToCoordinateVariables = HashMultimap.create();
        Set<String> dimensions = new HashSet<>();

        for (String variableName : binding.coordinateVariables().keySet()) {

            Variable variable = requireNonNull(file.findVariable(variableName));

            dimensionsToCoordinateVariables.put(
                    makeDimensionKey(variable),
                    variableName
            );

            variable.getDimensions().forEach(dimension -> dimensions.add(dimension.getName()));
        }

        if (dimensionsToCoordinateVariables.keySet().size() > 1) {
            return Optional.of(
                    new Error.MismatchedCoordinateVariableDimensions(dimensionsToCoordinateVariables)
            );
        }

        Error.Combined.Builder builder = Error.Combined.builder();

        for (String variableName : binding.dimensionVariables().keySet()) {

            Variable variable = requireNonNull(file.findVariable(variableName));

            if (variable.getDimensions().size() > 1) {
                builder.addError(
                        new Error.NonScalarDimensionVariable(variableName, makeDimensionKey(variable))
                );
                continue;
            }

            Dimension dimension = variable.getDimension(0);
            if (!dimensions.contains(dimension.getName())) {
                builder.addError(
                        new Error.MismatchedDimensionVariableDimensions(
                                variableName,
                                dimension.getName(),
                                dimensions
                        )
                );
            }
        }

        return builder.build();
    }

    private String makeDimensionKey(Variable variable) {
        return variable.getDimensions().stream().map(Dimension::getName).collect(joining(","));
    }

    /**
     * Sealed hierarchy of error modes when comparing bindings to files to allow downstream consumers to switch over each
     * type safely and potentially handle/correct them.
     */
    sealed interface Error {

        RuntimeException asException();

        /**
         * A composite {@link Error} class for more fluent handling and returns.
         */
        record Combined(Set<Error> errors) implements Error {

            static Builder builder() {
                return new Builder();
            }

            @Override
            public RuntimeException asException() {

                IllegalArgumentException exception = new IllegalArgumentException(
                        String.format(
                                "Errors(%d) expected when applying the provided schema bindings to the given file.",
                                errors.size()
                        )
                );

                errors.forEach(error -> exception.addSuppressed(error.asException()));
                return exception;
            }

            static final class Builder {

                private final Set<Error> errors = new HashSet<>();

                private Builder() {
                }

                Builder addError(Error error) {
                    this.errors.add(error);
                    return this;
                }

                Builder addErrors(Collection<Error> errors) {
                    this.errors.addAll(errors);
                    return this;
                }

                Optional<Error> build() {
                    return errors.isEmpty() ? Optional.empty() : Optional.of(new Combined(errors));
                }
            }
        }

        /**
         * Indicates the variable with the requested name could not be found in the file.
         */
        record MissingVariable(String variableName) implements Error {

            @Override
            public IllegalArgumentException asException() {
                return new IllegalArgumentException("File missing requested variable: " + variableName);
            }
        }

        /**
         * Indicates the {@link DataType} of the requested variable is not supported for object schema.
         */
        record UnhandledVariableType(String variableName, DataType dataType) implements Error {
            @Override
            public RuntimeException asException() {

                String message = String.format("Binding to Java objects of %s variable types not supported. Requested variable was: %s.",
                        dataType,
                        variableName
                );

                return new IllegalArgumentException(message);
            }
        }

        /**
         * Indicates the configured field setter for the requested variable does not match the declared {@link DataType} of the
         * variable's data in the file.
         */
        @SuppressWarnings("rawtypes")
        record IncorrectVariableType(String variableName, DataType fileType,
                                     Class<? extends FieldSetter> setterType) implements Error {
            @Override
            public IllegalArgumentException asException() {

                String message = String.format("Provided setter %s doesn't match the variable data type in the file %s for variable %s.",
                        setterType.getTypeName(),
                        fileType,
                        variableName
                );

                return new IllegalArgumentException(message);
            }
        }

        /**
         * Indicates one or more coordinate variables do not vary over the same shared dimensions.
         */
        record MismatchedCoordinateVariableDimensions(
                Multimap<String, String> dimensionsToCoordinateVariables) implements Error {
            @Override
            public RuntimeException asException() {

                StringBuilder builder = new StringBuilder()
                        .append("Coordinates do not vary over the same dimensions...\n");

                for (Map.Entry<String, Collection<String>> entry : dimensionsToCoordinateVariables.asMap().entrySet()) {

                    String dimensions = entry.getKey();
                    String variables = String.join(",", entry.getValue());

                    builder.append("\t").append("Dimensions: ").append(dimensions)
                            .append("; Variables: ").append(variables).append("\n");
                }

                return new IllegalArgumentException(builder.toString());
            }
        }

        /**
         * Indicates the given dimension variable varies over a dimension that doesn't match any of the dimensions the
         * requested coordinate variables vary over.
         */
        record MismatchedDimensionVariableDimensions(String variableName, String dimensionName,
                                                     Set<String> coordinateDimensions) implements Error {
            @Override
            public RuntimeException asException() {

                String message = String.format("Dimension variable %s with dimension %s doesn't vary over one of the coordinate dimensions %s",
                        variableName,
                        dimensionName,
                        String.join(",", coordinateDimensions)
                );

                return new IllegalArgumentException(message);
            }
        }

        /**
         * Indicates the given dimension variable doesn't vary over a single dimension, and as such should be a coordinate
         * variable.
         *
         * <p>This library doesn't currently support arbitrary sub-dimensionality for dimension variables, they must be
         * scalar.
         */
        record NonScalarDimensionVariable(String variableName, String dimensionNames) implements Error {

            @Override
            public RuntimeException asException() {

                String message = String.format("The requested dimension variable %s doesn't vary over a single dimension, it varies over %s",
                        variableName,
                        String.join(",", dimensionNames)
                );

                return new IllegalArgumentException(message);
            }
        }
    }
}
