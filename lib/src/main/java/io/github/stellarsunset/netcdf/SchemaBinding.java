package io.github.stellarsunset.netcdf;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
 * <p>This class supports clients schema those dimension and coordinate variables into POJOs like:
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
 * <p>In a complete schema:
 * <ol>
 *     <li>All coordinate variables share the same common dimensions</li>
 *     <li>Dimension variables match at least one of the shared coordinate variable dimensions</li>
 * </ol>
 *
 * <p>Note this schema instance may also be used directly with an {@link OutputStream} template type to directly sink
 * records to some outbound data stream.
 */
public final class SchemaBinding<T> {

    private final RecordInitializer<T> recordInitializer;

    private final Multimap<String, String> dimensionToVariables;

    private final Map<String, FieldBinding<T>> dimensionVariables;

    private final Map<String, FieldBinding<T>> coordinateVariables;

    private final RecordFinalizer<T> recordFinalizer;

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

    public RecordInitializer<T> recordInitializer() {
        return recordInitializer;
    }

    public Multimap<String, String> dimensionToVariables() {
        return dimensionToVariables;
    }

    public Collection<String> variablesFor(String dimensionName) {
        return dimensionToVariables.get(dimensionName);
    }

    public Map<String, FieldBinding<T>> dimensionVariables() {
        return dimensionVariables;
    }

    public FieldBinding<T> dimensionVariableBinding(String variableName) {
        return ofNullable(dimensionVariables.get(variableName)).orElseGet(FieldBinding.Noop::new);
    }

    public Map<String, FieldBinding<T>> coordinateVariables() {
        return coordinateVariables;
    }

    public FieldBinding<T> coordinateVariableBinding(String variableName) {
        return ofNullable(coordinateVariables.get(variableName)).orElseGet(FieldBinding.Noop::new);
    }

    public RecordFinalizer<T> recordFinalizer() {
        return recordFinalizer;
    }

    public static final class Builder<T> {

        private RecordInitializer<T> recordInitializer;

        private final Multimap<String, String> dimensionToVariables = HashMultimap.create();

        private final Map<String, FieldBinding<T>> dimensionVariables = new HashMap<>();

        private final Map<String, FieldBinding<T>> coordinateVariables = new HashMap<>();

        private RecordFinalizer<T> recordFinalizer = record -> {
        };

        private Builder() {
        }

        /**
         * Initialization method for getting a new instance of a record of type {@link T} that the dimension/coordinate
         * variable schema operations can then be called against.
         *
         * <p>Usually this is some form of {@code new T.Builder();}, but for bindings that directly pipe fields into an
         * {@link OutputStream} this may simply return the same shared output stream instance.
         *
         * @param recordInitializer called to initialize a new record to bind the netcdf-provided fields into
         */
        public Builder<T> recordInitializer(RecordInitializer<T> recordInitializer) {
            this.recordInitializer = requireNonNull(recordInitializer);
            return this;
        }

        /**
         * Configure an object schema for the provided dimension variable varying across the given dimension, e.g. the
         * latitude variable varying along the 'x' dimension.
         *
         * <p>See the top-level Javadocs for a full description.
         *
         * <p>This is a less type-safe version of the well-named setters that may be more suitable in some situations.
         *
         * @param dimensionName the name of the single dimension the variable varies across
         * @param variableName  the name of the variable whose values we want to bind into the record
         * @param setter        the setter to use when schema the variable values to the object
         */
        public Builder<T> dimensionVariable(String dimensionName, String variableName, FieldBinding<T> setter) {
            this.dimensionToVariables.put(dimensionName, variableName);
            this.dimensionVariables.put(variableName, setter);
            return this;
        }

        public Builder<T> byteDimensionVariable(String dimensionName, String variableName, FieldBinding.Byte<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> charDimensionVariable(String dimensionName, String variableName, FieldBinding.Char<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> booleanDimensionVariable(String dimensionName, String variableName, FieldBinding.Bool<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> shortDimensionVariable(String dimensionName, String variableName, FieldBinding.Short<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> intDimensionVariable(String dimensionName, String variableName, FieldBinding.Int<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> longDimensionVariable(String dimensionName, String variableName, FieldBinding.Long<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> floatDimensionVariable(String dimensionName, String variableName, FieldBinding.Float<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> doubleDimensionVariable(String dimensionName, String variableName, FieldBinding.Double<T> setter) {
            return dimensionVariable(dimensionName, variableName, setter);
        }

        public Builder<T> byteDimensionVariable(String dimensionName, FieldBinding.Byte<T> setter) {
            return byteDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> charDimensionVariable(String dimensionName, FieldBinding.Char<T> setter) {
            return charDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> booleanDimensionVariable(String dimensionName, FieldBinding.Bool<T> setter) {
            return booleanDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> shortDimensionVariable(String dimensionName, FieldBinding.Short<T> setter) {
            return shortDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> intDimensionVariable(String dimensionName, FieldBinding.Int<T> setter) {
            return intDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> longDimensionVariable(String dimensionName, FieldBinding.Long<T> setter) {
            return longDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> floatDimensionVariable(String dimensionName, FieldBinding.Float<T> setter) {
            return floatDimensionVariable(dimensionName, dimensionName, setter);
        }

        public Builder<T> doubleDimensionVariable(String dimensionName, FieldBinding.Double<T> setter) {
            return doubleDimensionVariable(dimensionName, dimensionName, setter);
        }

        /**
         * Configure an object schema for the provided coordinate variable varying.
         *
         * <p>See the top-level Javadocs for a full description.
         *
         * <p>This is a less type-safe version of the well-named setters that may be more suitable in some situations.
         *
         * @param name   the name of the variable whose values we want to bind into the record
         * @param setter the setter to use when schema the variable values to the object
         */
        public Builder<T> coordinateVariable(String name, FieldBinding<T> setter) {
            this.coordinateVariables.put(name, setter);
            return this;
        }

        public Builder<T> byteCoordinateVariable(String name, FieldBinding.Byte<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> charCoordinateVariable(String name, FieldBinding.Char<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> booleanCoordinateVariable(String name, FieldBinding.Bool<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> shortCoordinateVariable(String name, FieldBinding.Short<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> intCoordinateVariable(String name, FieldBinding.Int<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> longCoordinateVariable(String name, FieldBinding.Long<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> floatCoordinateVariable(String name, FieldBinding.Float<T> setter) {
            return coordinateVariable(name, setter);
        }

        public Builder<T> doubleCoordinateVariable(String name, FieldBinding.Double<T> setter) {
            return coordinateVariable(name, setter);
        }

        /**
         * Optional finalization operation that well be called under the hood before the record is made available in the
         * {@link Stream} output of the {@link NetcdfRecordReader}.
         *
         * <p>This hook is primarily useful when the template type of the schema is flavor of {@link OutputStream} or a
         * {@link Writer} implementation and data returned from the file is being directly re-written to that stream but
         * may need a record termination indicator, e.g. a closing brace for a JSON-based output stream or a newline for
         * a plain text based one.
         *
         * @param recordFinalizer "finalizer" operation to run after the various variable bindings have been invoked
         */
        public Builder<T> recordFinalizer(RecordFinalizer<T> recordFinalizer) {
            this.recordFinalizer = requireNonNull(recordFinalizer);
            return this;
        }

        public SchemaBinding<T> build() {
            return new SchemaBinding<>(this);
        }
    }
}
