package com.stellarsunset.netcdf;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.stellarsunset.netcdf.field.*;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

/**
 * Object holding bindings for variables in a CDM-compliant file type to a POJO of type {@code T}.
 *
 * <p>This class introduces the concept of two different classes of variables clients may want to bind into Java objects:
 * <ol>
 *     <li>Dimension Variables - these are 1D variables that vary directly along a single dimension</li>
 *     <li>Coordinate Variables - these are (often) multi-dimensional variables that are have entries for each coordinate
 *     in a multi-dimensional grid</li>
 * </ol>
 *
 * <p>As a quick example take a file with the following dimensions and variables:
 * <pre>{@code
 * Dimensions
 * - x=[0, 10]
 * - y=[0, 10]
 * - z=[0, 10]
 * Variables
 * - latitude(type=double,dims=[x])
 * - longitude(type=double,dims=[y])
 * - level(type=int,dims=[z])
 * - temperature(type=float,dims=[x,y,z])
 * - pressure(type=float,dims=[x,y,z])
 * }</pre>
 *
 * <p>The above maps to a 10x10x10 cube where each x coordinate has a corresponding latitude, y coordinate has a longitude,
 * and z coordinate has a "level" denoting some height above ground. Then there are measurements or fields available at
 * each coordinate in the grid e.g. temperature and pressure.
 *
 * <p>This class supports clients binding those dimension and coordinate variables into POJOs like:
 * <pre>{@code
 * record AtmosphereMeasurement(
 *   double latitude, // dimension variable
 *   double longitude, // dimension variable
 *   int level, // dimension variable
 *   float temperature, // coordinate variable
 *   float pressure // coordinate variable
 * ) {}
 * }</pre>
 *
 * <p>In a complete binding:
 * <ol>
 *     <li>All coordinate variables share the same common dimensions</li>
 *     <li>Dimension variables match at least one of the shared coordinate variable dimensions</li>
 * </ol>
 *
 * <p>Note this binding instance may also be used directly with an {@link OutputStream} template type to directly sink
 * records to some outbound data stream.
 */
public final class SchemaBinding<T> {

    private final Supplier<T> recordInitializer;

    private final Multimap<String, String> dimensionToVariables;

    private final Map<String, FieldSetter<T>> dimensionVariables;

    private final Map<String, FieldSetter<T>> coordinateVariables;

    private final Consumer<T> recordFinalizer;

    private SchemaBinding(Builder<T> builder) {
        this.recordInitializer = requireNonNull(builder.recordInitializer);
        this.dimensionToVariables = ImmutableMultimap.copyOf(builder.dimensionToVariables);
        this.dimensionVariables = Map.copyOf(builder.dimensionVariables);
        this.coordinateVariables = Map.copyOf(builder.coordinateVariables);
        this.recordFinalizer = requireNonNull(builder.recordFinalizer);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public Supplier<T> recordInitializer() {
        return recordInitializer;
    }

    public Multimap<String, String> dimensionToVariables() {
        return dimensionToVariables;
    }

    public Collection<String> variablesFor(String dimensionName) {
        return dimensionToVariables.get(dimensionName);
    }

    public Map<String, FieldSetter<T>> dimensionVariables() {
        return dimensionVariables;
    }

    public FieldSetter<T> dimensionVariableSetter(String variableName) {
        return ofNullable(dimensionVariables.get(variableName)).orElseGet(NoopSetter::new);
    }

    public Map<String, FieldSetter<T>> coordinateVariables() {
        return coordinateVariables;
    }

    public FieldSetter<T> coordinateVariableSetter(String variableName) {
        return ofNullable(coordinateVariables.get(variableName)).orElseGet(NoopSetter::new);
    }

    public Consumer<T> recordFinalizer() {
        return recordFinalizer;
    }

    public static final class Builder<T> {

        private Supplier<T> recordInitializer;

        private final Multimap<String, String> dimensionToVariables = HashMultimap.create();

        private final Map<String, FieldSetter<T>> dimensionVariables = new HashMap<>();

        private final Map<String, FieldSetter<T>> coordinateVariables = new HashMap<>();

        private Consumer<T> recordFinalizer = record -> {
        };

        private Builder() {
        }

        /**
         * Initialization method for getting a new instance of a record of type {@link T} that the dimension/coordinate
         * variable binding operations can then be called against.
         *
         * <p>Usually this is some form of {@code new T.Builder();}, but for bindings that directly pipe fields into an
         * {@link OutputStream} this may simply return the same shared output stream instance.
         *
         * @param recordInitializer called to initialize a new record to bind the netcdf-provided fields into
         */
        public Builder<T> recordInitializer(Supplier<T> recordInitializer) {
            this.recordInitializer = requireNonNull(recordInitializer);
            return this;
        }

        /**
         * Configure an object binding for the provided dimension variable varying across the given dimension, e.g. the
         * latitude variable varying along the 'x' dimension.
         *
         * <p>See the top-level Javadocs for a full description.
         *
         * <p>This is a less type-safe version of the well-named setters that may be more suitable in some situations.
         *
         * @param dimensionName the name of the single dimension the variable varies across
         * @param variableName  the name of the variable whose values we want to bind into the record
         * @param setter        the setter to use when binding the variable values to the object
         */
        public Builder<T> dimensionVariable(String dimensionName, String variableName, FieldSetter<T> setter) {
            this.dimensionToVariables.put(dimensionName, variableName);
            this.dimensionVariables.put(variableName, setter);
            return this;
        }

        public Builder<T> byteDimensionVariable(String dimensionName, String variableName, ByteSetter<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> charDimensionVariable(String dimensionName, String variableName, CharacterSetter<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> booleanDimensionVariable(String dimensionName, String variableName, BooleanSetter<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> shortDimensionVariable(String dimensionName, String variableName, ShortSetter<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> intDimensionVariable(String dimensionName, String variableName, IntSetter<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> longDimensionVariable(String dimensionName, String variableName, LongSetter<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> floatDimensionVariable(String dimensionName, String variableName, FloatSetter<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> doubleDimensionVariable(String dimensionName, String variableName, DoubleSetter<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> byteDimensionVariable(String dimensionName, ByteSetter<T> setter) {
            return byteDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> charDimensionVariable(String dimensionName, CharacterSetter<T> setter) {
            return charDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> booleanDimensionVariable(String dimensionName, BooleanSetter<T> setter) {
            return booleanDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> shortDimensionVariable(String dimensionName, ShortSetter<T> setter) {
            return shortDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> intDimensionVariable(String dimensionName, IntSetter<T> setter) {
            return intDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> longDimensionVariable(String dimensionName, LongSetter<T> setter) {
            return longDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> floatDimensionVariable(String dimensionName, FloatSetter<T> setter) {
            return floatDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> doubleDimensionVariable(String dimensionName, DoubleSetter<T> setter) {
            return doubleDimensionVariable(dimensionName, dimensionName, setter);
        }

        /**
         * Configure an object binding for the provided coordinate variable varying.
         *
         * <p>See the top-level Javadocs for a full description.
         *
         * <p>This is a less type-safe version of the well-named setters that may be more suitable in some situations.
         *
         * @param name   the name of the variable whose values we want to bind into the record
         * @param setter the setter to use when binding the variable values to the object
         */
        public Builder<T> coordinateVariable(String name, FieldSetter<T> setter) {
            this.coordinateVariables.put(name, setter);
            return this;
        }

        public Builder<T> byteCoordinateVariable(String name, ByteSetter<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> charCoordinateVariable(String name, CharacterSetter<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> booleanCoordinateVariable(String name, BooleanSetter<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> shortCoordinateVariable(String name, ShortSetter<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> intCoordinateVariable(String name, IntSetter<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> longCoordinateVariable(String name, LongSetter<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> floatCoordinateVariable(String name, FloatSetter<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> doubleCoordinateVariable(String name, DoubleSetter<T> setter) {
            return coordinateVariable(name, setter);
        }

        /**
         * Optional finalization operation that well be called under the hood before the record is made available in the
         * {@link Stream} output of the {@link NetcdfRecordReader}.
         *
         * <p>This hook is primarily useful when the template type of the binding is flavor of {@link OutputStream} or a
         * {@link Writer} implementation and data returned from the file is being directly re-written to that stream but
         * may need a record termination indicator, e.g. a closing brace for a JSON-based output stream or a newline for
         * a plain text based one.
         *
         * @param recordFinalizer "finalizer" operation to run after the various variable bindings have been invoked
         */
        public Builder<T> recordFinalizer(Consumer<T> recordFinalizer) {
            this.recordFinalizer = requireNonNull(recordFinalizer);
            return this;
        }

        public SchemaBinding<T> build() {
            return new SchemaBinding<>(this);
        }
    }
}
