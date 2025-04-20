package io.github.stellarsunset.netcdf;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.stellarsunset.commons.Either;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

/**
 * Wrapper for a {@link SchemaBinding} instance that has been validated as consistent with the provided NetCDF file, and
 * is therefore valid for reading sub-schema records from it.
 *
 * <p>Validated bindings include:
 * <ol>
 *     <li>The inferred dimensionality of the coordinate data being read so downstream classes can use that information
 *     to generate appropriate {@link Hypercube}, etc. objects</li>
 *     <li>A close method, as they require opening the file to validate the schema contents and there's no real reason
 *     to re-close and re-open it downstream.</li>
 * </ol>
 */
sealed interface ValidatedBinding<T> extends AutoCloseable {

    /**
     * Validate the provided {@link SchemaBinding} against the data in the provided {@link NetcdfFile} returning a handle
     * for a validated schema downstream classes can have good expectations against (simplifying their logic).
     */
    static <T> ValidatedBinding<T> validate(NetcdfFile file, SchemaBinding<T> schema) {
        return new Validator<>(file, schema).validate().orThrowRight(Validator.Error::asException);
    }

    /**
     * The {@link NetcdfFile} the schema was evaluated against for consistency.
     */
    NetcdfFile context();

    /**
     * The {@link SchemaBinding} in question.
     */
    SchemaBinding<T> schema();

    @Override
    default void close() throws IOException {
        context().close();
    }

    private static <T> Variable anyVariable(ValidatedBinding<T> binding) {
        Variable anyVariable = binding.context()
                .findVariable(binding.schema().coordinateVariables().entrySet().iterator().next().getKey());
        return requireNonNull(anyVariable, "Variable should always be present in a validated binding.");
    }

    /**
     * Returns the length of the dimension at the provided index.
     *
     * <p>This is done as a private static method because I don't think we actually want to expose this on the interface
     * directly, as in the D0 case it doesn't really make sense.
     *
     * @param binding the validated schema binding containing the dimensions/variables we care about
     * @param index   the index of the dimension we want the length of
     */
    private static <T> int dimensionSize(ValidatedBinding<T> binding, int index) {
        return anyVariable(binding).getDimension(index).getLength();
    }

    /**
     * Returns the subset of dimension variables in the validated binding that are tied to the index.
     *
     * <p>This is done as a private static method because I don't think we actually want to expose this on the interface
     * directly, as in the D0 case it doesn't really make sense.
     *
     * @param binding the validated schema binding containing the dimensions/variables we care about
     * @param index   the index of the dimension we want the variables for
     */
    private static <T> Map<String, FieldBinding<T>> dimensionVariables(ValidatedBinding<T> binding, int index) {

        Variable anyVariable = anyVariable(binding);
        Dimension dimension = anyVariable.getDimension(index);

        return binding.schema().dimensionVariables().entrySet().stream()
                .filter(entry -> requireNonNull(binding.context().findVariable(entry.getKey())).getDimension(0).equals(dimension))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    record D0<T>(NetcdfFile context, SchemaBinding<T> schema) implements ValidatedBinding<T> {
    }

    record D1<T>(NetcdfFile context, SchemaBinding<T> schema) implements ValidatedBinding<T> {
        public int max() {
            return ValidatedBinding.dimensionSize(this, 0);
        }

        public Map<String, FieldBinding<T>> dimensionVariables() {
            return ValidatedBinding.dimensionVariables(this, 0);
        }
    }

    record D2<T>(NetcdfFile context, SchemaBinding<T> schema) implements ValidatedBinding<T> {

        public int d0Max() {
            return ValidatedBinding.dimensionSize(this, 0);
        }

        public Map<String, FieldBinding<T>> d0DimensionVariables() {
            return ValidatedBinding.dimensionVariables(this, 0);
        }

        public int d1Max() {
            return ValidatedBinding.dimensionSize(this, 1);
        }

        public Map<String, FieldBinding<T>> d1DimensionVariables() {
            return ValidatedBinding.dimensionVariables(this, 1);
        }
    }

    record D3<T>(NetcdfFile context, SchemaBinding<T> schema) implements ValidatedBinding<T> {
        public int d0Max() {
            return ValidatedBinding.dimensionSize(this, 0);
        }

        public Map<String, FieldBinding<T>> d0DimensionVariables() {
            return ValidatedBinding.dimensionVariables(this, 0);
        }

        public int d1Max() {
            return ValidatedBinding.dimensionSize(this, 1);
        }

        public Map<String, FieldBinding<T>> d1DimensionVariables() {
            return ValidatedBinding.dimensionVariables(this, 1);
        }

        public int d2Max() {
            return ValidatedBinding.dimensionSize(this, 2);
        }

        public Map<String, FieldBinding<T>> d2DimensionVariables() {
            return ValidatedBinding.dimensionVariables(this, 2);
        }
    }

    record D4<T>(NetcdfFile context, SchemaBinding<T> schema) implements ValidatedBinding<T> {
        public int d0Max() {
            return ValidatedBinding.dimensionSize(this, 0);
        }

        public Map<String, FieldBinding<T>> d0DimensionVariables() {
            return ValidatedBinding.dimensionVariables(this, 0);
        }

        public int d1Max() {
            return ValidatedBinding.dimensionSize(this, 1);
        }

        public Map<String, FieldBinding<T>> d1DimensionVariables() {
            return ValidatedBinding.dimensionVariables(this, 1);
        }

        public int d2Max() {
            return ValidatedBinding.dimensionSize(this, 2);
        }

        public Map<String, FieldBinding<T>> d2DimensionVariables() {
            return ValidatedBinding.dimensionVariables(this, 2);
        }

        public int d3Max() {
            return ValidatedBinding.dimensionSize(this, 3);
        }

        public Map<String, FieldBinding<T>> d3DimensionVariables() {
            return ValidatedBinding.dimensionVariables(this, 3);
        }
    }

    record Validator<T>(NetcdfFile file, SchemaBinding<T> schema) {

        /**
         * Optionally return an {@link Error} describing any issues which would result in errors if the provided schema was
         * run against the provided {@link NetcdfFile}.
         */
        Either<ValidatedBinding<T>, Error> validate() {
            // this order does matter, if the variable exists then the dimensions do too, saving us some checks
            return checkVariables().map(Either::<ValidatedBinding<T>, Error>ofRight).orElseGet(this::checkDimensions);
        }

        /**
         * Check all the variables for existing and correct type-to-setter bindings.
         */
        private Optional<Error> checkVariables() {

            Error.Combined.Builder builder = Error.Combined.builder();

            for (Map.Entry<String, FieldBinding<T>> coordinateVariable : schema.coordinateVariables().entrySet()) {

                String variableName = coordinateVariable.getKey();
                FieldBinding<T> setter = coordinateVariable.getValue();

                checkVariable(variableName, setter).ifPresent(builder::addError);
            }

            for (Map.Entry<String, FieldBinding<T>> dimensionVariable : schema.dimensionVariables().entrySet()) {

                String variableName = dimensionVariable.getKey();
                FieldBinding<T> setter = dimensionVariable.getValue();

                checkVariable(variableName, setter).ifPresent(builder::addError);
            }

            return builder.build();
        }

        /**
         * Check a requested variable exists and its setter type handles the {@link DataType} associated with that variable
         * in the provided file.
         */
        private Optional<Error> checkVariable(String variableName, FieldBinding<T> setter) {

            Variable variable = file.findVariable(variableName);

            if (variable == null) {
                return Optional.of(new Error.MissingVariable(variableName));
            }

            return checkType(variable, setter);
        }

        /**
         * Copied in part from {@link ucar.ma2.Array#factory(DataType, int[])}.
         */
        private Optional<Error> checkType(Variable variable, FieldBinding<T> setter) {
            return switch (variable.getDataType()) {
                case DOUBLE -> shouldBe(variable, setter, FieldBinding.Double.class);
                case FLOAT -> shouldBe(variable, setter, FieldBinding.Float.class);
                case CHAR -> shouldBe(variable, setter, FieldBinding.Char.class);
                case BOOLEAN -> shouldBe(variable, setter, FieldBinding.Bool.class);
                case ENUM4, UINT, INT -> shouldBe(variable, setter, FieldBinding.Int.class);
                case ENUM2, USHORT, SHORT -> shouldBe(variable, setter, FieldBinding.Short.class);
                case ENUM1, UBYTE, BYTE -> shouldBe(variable, setter, FieldBinding.Byte.class);
                case ULONG, LONG -> shouldBe(variable, setter, FieldBinding.Long.class);
                case STRING, STRUCTURE, SEQUENCE, OPAQUE, OBJECT ->
                        Optional.of(new Error.UnhandledVariableType(variable.getFullName(), variable.getDataType()));

            };
        }

        @SuppressWarnings("rawtypes")
        private Optional<Error> shouldBe(Variable variable, FieldBinding<T> setter, Class<? extends FieldBinding> expected) {
            if (!expected.isAssignableFrom(setter.getClass())) {

                // Runtime class can be a lambda... need to map it to a meaningful name
                Class<? extends FieldBinding> setterType = switch (setter) {
                    case FieldBinding.Bool<T> bs -> FieldBinding.Bool.class;
                    case FieldBinding.Byte<T> bs -> FieldBinding.Byte.class;
                    case FieldBinding.Char<T> cs -> FieldBinding.Char.class;
                    case FieldBinding.Double<T> ds -> FieldBinding.Double.class;
                    case FieldBinding.Float<T> fs -> FieldBinding.Float.class;
                    case FieldBinding.Int<T> is -> FieldBinding.Int.class;
                    case FieldBinding.Long<T> ls -> FieldBinding.Long.class;
                    case FieldBinding.Short<T> ss -> FieldBinding.Short.class;
                    case FieldBinding.Noop<T> ns -> FieldBinding.Noop.class;
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
        private Either<ValidatedBinding<T>, Error> checkDimensions() {

            Multimap<String, String> dimensionsToCoordinateVariables = HashMultimap.create();
            Set<String> dimensions = new HashSet<>();

            for (String variableName : schema.coordinateVariables().keySet()) {

                Variable variable = requireNonNull(file.findVariable(variableName));

                dimensionsToCoordinateVariables.put(
                        makeDimensionKey(variable),
                        variableName
                );

                variable.getDimensions().forEach(dimension -> dimensions.add(dimension.getName()));
            }

            if (dimensionsToCoordinateVariables.keySet().size() > 1) {
                return Either.ofRight(
                        new Error.MismatchedCoordinateVariableDimensions(dimensionsToCoordinateVariables)
                );
            }

            Error.Combined.Builder builder = Error.Combined.builder();

            for (String variableName : schema.dimensionVariables().keySet()) {

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

            Optional<Error> maybeError = builder.build();

            if (maybeError.isPresent()) {
                return Either.ofRight(maybeError.get());
            }

            return switch (dimensions.size()) {
                case 0 -> Either.ofLeft(new ValidatedBinding.D0<>(file, schema));
                case 1 -> Either.ofLeft(new ValidatedBinding.D1<>(file, schema));
                case 2 -> Either.ofLeft(new ValidatedBinding.D2<>(file, schema));
                case 3 -> Either.ofLeft(new ValidatedBinding.D3<>(file, schema));
                case 4 -> Either.ofLeft(new ValidatedBinding.D4<>(file, schema));
                default ->
                        Either.ofRight(new Error.UnsupportedCoordinateVariableDimensionality(dimensions.size(), schema.coordinateVariables().keySet()));
            };
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
                                         Class<? extends FieldBinding> setterType) implements Error {
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

            /**
             * Indicates coordinate variables of the given number of dimension are not currently supported.
             */
            record UnsupportedCoordinateVariableDimensionality(int dimensionCount,
                                                               Set<String> variableNames) implements Error {

                @Override
                public RuntimeException asException() {

                    String message = String.format(
                            "Currently the library does not support working with coordinate variables of the provided number of dimensions %s. The requested variables were: %s.",
                            dimensionCount,
                            String.join(",", variableNames)
                    );

                    return new IllegalArgumentException(message);
                }
            }
        }
    }
}
