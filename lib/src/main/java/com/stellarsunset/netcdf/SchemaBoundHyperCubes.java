package com.stellarsunset.netcdf;

import com.stellarsunset.netcdf.Hypercube.RecordCreationException;
import com.stellarsunset.netcdf.field.*;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Container class for creating {@link Hypercube} instances from {@link SchemaBinding}s.
 */
class SchemaBoundHyperCubes {

    /**
     * Creates a new {@link Hypercube} using the {@link ValidatedBinding} that can be queried by index (i.e. x,y,z) for
     * records of type {@code T}.
     *
     * @param binding the validated schema binding containing the field bindings and paired {@link NetcdfFile}
     */
    static <T> Hypercube<T> make(ValidatedBinding<T> binding) {
        return switch (binding) {
            case ValidatedBinding.D0<T> d0 ->
                    throw new IllegalArgumentException("D0 hypercubes aren't supported... why would you need one?");
            case ValidatedBinding.D1<T> d1 -> makeD1(d1);
            case ValidatedBinding.D2<T> d2 -> makeD2(d2);
            case ValidatedBinding.D3<T> d3 -> makeD3(d3);
            case ValidatedBinding.D4<T> d4 -> makeD4(d4);
        };
    }

        /*
        NetcdfFile file = binding.context();
        SchemaBinding<T> schema = binding.schema();

        List<Setter<T>> coordinateSetters = new ArrayList<>();
        List<Array> coordinateArrays = new ArrayList<>();

        for (var entry : schema.coordinateVariables().entrySet()) {

            Variable variable = file.findVariable(entry.getKey());

            coordinateSetters.add(Setter.from(entry.getValue()));
            var array = com.stellarsunset.netcdf.Array.wrap(variable.read().reduce());
        }

        VariablesSetter<T> coordinatesSetter = VariablesSetter.from(
                coordinateSetters.toArray(Setter[]::new),
                coordinateArrays.toArray(Array[]::new)
        );

        // Grab only the dimensions of our variables with a length greater than 1
        var orderedNonZeroDimensions = file.findVariable(binding.coordinateVariables().keySet().iterator().next())
                .getDimensions()
                .stream()
                .filter(d -> d.getLength() != 1)
                .toList();

        int dimensionCount = orderedNonZeroDimensions.size();

        VariablesSetter<T>[] dimensionsSetters = new VariablesSetter[dimensionCount];

        int[] dimensionLengths = orderedNonZeroDimensions.stream()
                .mapToInt(Dimension::getLength)
                .toArray();

        for (int i = 0; i < dimensionCount; i++) {

            var dimension = orderedNonZeroDimensions.get(i);

            List<Setter<T>> dimensionSetters = new ArrayList<>();
            List<Array> dimensionArrays = new ArrayList<>();

            for (var variableName : binding.variablesFor(dimension.getName())) {

                Variable variable = file.findVariable(variableName);

                dimensionSetters.add(Setter.from(binding.dimensionVariableBinding(variableName)));
                dimensionArrays.add(variable.read());
            }

            VariablesSetter<T> dimensionsSetter = VariablesSetter.from(
                    dimensionSetters.toArray(Setter[]::new),
                    dimensionArrays.toArray(Array[]::new)
            );

            dimensionsSetters[i] = dimensionsSetter;
        }

        return from(
                asSupplier(binding.recordInitializer()),
                dimensionsSetters,
                dimensionLengths,
                coordinatesSetter,
                asConsumer(binding.recordFinalizer())
        );
    */


    private static <T> Hypercube.D1<T> makeD1(ValidatedBinding.D1<T> binding) {

        NetcdfFile file = binding.context();
        SchemaBinding<T> schema = binding.schema();

        IndexBinding.D1<T> coordinates = schema.coordinateVariables().entrySet().stream()
                .map(entry -> createD1Binding(
                        requireNonNull(file.findVariable(entry.getKey()), "Variable always present in validated binding."),
                        entry.getValue()))
                .reduce(IndexBinding.D1.noop(), IndexBinding.D1::combine);

        IndexBinding.D1<T> dimension = schema.dimensionVariables().entrySet().stream()
                .map(entry -> createD1Binding(
                        requireNonNull(file.findVariable(entry.getKey()), "Variable always present in validated binding."),
                        entry.getValue()))
                .reduce(IndexBinding.D1.noop(), IndexBinding.D1::combine);

        return new D1<>(
                schema.recordInitializer().demoteExceptions(),
                dimension,
                binding.d1Max(),
                coordinates,
                schema.recordFinalizer().demoteExceptions()
        );
    }

    private static <T> IndexBinding.D1<T> createD1Binding(Variable variable, FieldBinding<T> fieldBinding) {
        Array.D1 array = Array.wrap(variable.read().reduce());
        return array.bindIndex(fieldBinding);
    }

    private static <T> Hypercube.D2<T> makeD2(ValidatedBinding.D2<T> binding) {
        return null;
    }

    private static <T> Hypercube.D3<T> makeD3(ValidatedBinding.D3<T> binding) {
        return null;
    }

    private static <T> Hypercube.D4<T> makeD4(ValidatedBinding.D4<T> binding) {
        return null;
    }

    /**
     * Create a new record iterator which returns a lazy sequence of records pulled from the underlying cdm data.
     *
     * @param initializer      supplier for new record instances to call setters against
     * @param dimensionSetters setters on the objects for dimension values
     * @param dimensionLengths the length of each dimension
     * @param varsSetter       setter for injecting the variable value at the dimension coordinates into the record
     * @param finalizer        finalizer for records before they are returned from the iterator
     */
    private static <T> Hypercube<T> from(Supplier<T> initializer, VariablesSetter<T>[] dimensionSetters, int[] dimensionLengths, VariablesSetter<T> varsSetter, Consumer<T> finalizer) {
        return switch (dimensionLengths.length) {
            case 1 -> new D1<>(
                    initializer,
                    dimensionSetters[0],
                    dimensionLengths[0],
                    varsSetter,
                    finalizer
            );
            case 2 -> new D2<>(
                    initializer,
                    dimensionSetters[0],
                    dimensionLengths[0],
                    dimensionSetters[1],
                    dimensionLengths[1],
                    varsSetter,
                    finalizer
            );
            case 3 -> new D3<>(
                    initializer,
                    dimensionSetters[0],
                    dimensionLengths[0],
                    dimensionSetters[1],
                    dimensionLengths[1],
                    dimensionSetters[2],
                    dimensionLengths[2],
                    varsSetter,
                    finalizer
            );
            case 4 -> new D4<>(
                    initializer,
                    dimensionSetters[0],
                    dimensionLengths[0],
                    dimensionSetters[1],
                    dimensionLengths[1],
                    dimensionSetters[2],
                    dimensionLengths[2],
                    dimensionSetters[3],
                    dimensionLengths[3],
                    varsSetter,
                    finalizer
            );
            default -> throw new IllegalArgumentException(
                    String.format(
                            "Currently only optimised readers over up to %s dimensions are supported. Request a D%sIterator.",
                            4,
                            dimensionLengths.length
                    )
            );
        };
    }

    /**
     * 1-Dimensional hypercube implementation, meant for reading simple linear variables (e.g. time) from the underlying
     * NetCDF file standalone.
     *
     * <p>Prefer instantiation via {@link Hypercube#schemaBound(NetcdfFile, SchemaBinding)}.
     */
    record D1<T>(Supplier<T> initializer, IndexBinding.D1<T> d0Setter, int max, IndexBinding.D1<T> coordinatesSetter,
                 Consumer<T> finalizer) implements Hypercube.D1<T> {

        @Override
        public T read(int i) {
            T r = coordinatesSetter.set(d0Setter.set(initializer.get(), i), i);
            finalizer.accept(r);
            return r;
        }

        @Override
        public void close() {
        }
    }

    record D2<T>(Supplier<T> initializer, IndexBinding.D1<T> d0Setter, int d0Max, IndexBinding.D1<T> d1Setter,
                 int d1Max, IndexBinding.D2<T> coordinatesSetter, Consumer<T> finalizer) implements Hypercube.D2<T> {

        @Override
        public T read(int i0, int i1) {
            T r = coordinatesSetter.set(d0Setter.set(d1Setter.set(initializer.get(), i1), i0), i0, i1);
            finalizer.accept(r);
            return r;
        }

        @Override
        public void close() {
        }
    }

    record D3<T>(Supplier<T> initializer, IndexBinding.D1<T> d0Setter, int d0Max, IndexBinding.D1<T> d1Setter,
                 int d1Max, IndexBinding.D1<T> d2Setter, int d2Max, IndexBinding.D3<T> coordinatesSetter,
                 Consumer<T> finalizer) implements Hypercube.D3<T> {

        @Override
        public T read(int i0, int i1, int i2) {
            T r = coordinatesSetter.set(d0Setter.set(d1Setter.set(d2Setter.set(initializer.get(), i2), i1), i0), i0, i1, i2);
            finalizer.accept(r);
            return r;
        }

        @Override
        public void close() {
        }
    }

    record D4<T>(Supplier<T> initializer, IndexBinding.D1<T> d0Setter, int d0Max, IndexBinding.D1<T> d1Setter,
                 int d1Max, IndexBinding.D1<T> d2Setter, int d2Max, IndexBinding.D1<T> d3Setter, int d3Max,
                 IndexBinding.D4<T> coordinatesSetter, Consumer<T> finalizer) implements Hypercube.D4<T> {

        public T read(int x, int y, int z, int t) {
            T r = coordinatesSetter.set(d0Setter.set(d1Setter.set(d2Setter.set(d3Setter.set(initializer.get(), t), z), y), x), x, y, z, t);
            finalizer.accept(r);
            return r;
        }

        @Override
        public void close() {
        }
    }


    /**
     * Internal setter interface to wrap the client-facing {@link FieldSetter} types and hide them from the internals of the read operation.
     *
     * <p>These setters need to encapsulate the boolean/character/etc. read operations.
     */
    interface Setter<T> {

        static <T> Setter<T> noop() {
            return new Noop<>();
        }

        static <T> Setter<T> from(FieldSetter<T> setter) {
            return switch (setter) {
                case NoopSetter<T> ns -> new Noop<>();
                case ByteSetter<T> bs -> new Byte<>(bs);
                case CharacterSetter<T> cs -> new Character<>(cs);
                case BooleanSetter<T> bs -> new Boolean<>(bs);
                case ShortSetter<T> ss -> new Short<>(ss);
                case IntSetter<T> is -> new Int<>(is);
                case LongSetter<T> ls -> new Long<>(ls);
                case FloatSetter<T> fs -> new Float<>(fs);
                case DoubleSetter<T> ds -> new Double<>(ds);
            };
        }

        T set(T record, int element, Array data);

        record Noop<T>() implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                return record;
            }
        }

        record Byte<T>(ByteSetter<T> bs) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                try {
                    return bs.accept(record, data.getByte(element));
                } catch (IOException e) {
                    throw new RecordCreationException("Error setting byte field at offset: " + element, e);
                }
            }
        }

        record Character<T>(CharacterSetter<T> cs) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                try {
                    return cs.accept(record, data.getChar(element));
                } catch (IOException e) {
                    throw new RecordCreationException("Error setting char field at offset: " + element, e);
                }
            }
        }

        record Boolean<T>(BooleanSetter<T> bs) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                try {
                    return bs.accept(record, data.getBoolean(element));
                } catch (IOException e) {
                    throw new RecordCreationException("Error setting boolean field at offset: " + element, e);
                }
            }
        }

        record Short<T>(ShortSetter<T> ss) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                try {
                    return ss.accept(record, data.getShort(element));
                } catch (IOException e) {
                    throw new RecordCreationException("Error setting short field at offset: " + element, e);
                }
            }
        }

        record Int<T>(IntSetter<T> is) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                try {
                    return is.accept(record, data.getInt(element));
                } catch (IOException e) {
                    throw new RecordCreationException("Error setting int field at offset: " + element, e);
                }
            }
        }

        record Long<T>(LongSetter<T> ls) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                try {
                    return ls.accept(record, data.getLong(element));
                } catch (IOException e) {
                    throw new RecordCreationException("Error setting long field at offset: " + element, e);
                }
            }
        }

        record Float<T>(FloatSetter<T> fs) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                try {
                    return fs.accept(record, data.getFloat(element));
                } catch (IOException e) {
                    throw new RecordCreationException("Error setting long field at offset: " + element, e);
                }
            }
        }

        record Double<T>(DoubleSetter<T> ds) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                try {
                    return ds.accept(record, data.getDouble(element));
                } catch (IOException e) {
                    throw new RecordCreationException("Error setting double field at offset: " + element, e);
                }
            }
        }
    }


    sealed interface VariablesSetter<T> {

        /**
         * Create a composite setter for a collection of variables and their arrays, for speed call the setters directly
         * to avoid incurring the nested for-loop initialization overhead on each set call.
         */
        static <T> VariablesSetter<T> from(Setter<T>[] setters, Array[] variables) {
            return switch (setters.length) {
                case 1 -> new N1<>(setters[0], variables[0]);
                case 2 -> new N2<>(setters[0], variables[0], setters[1], variables[1]);
                case 3 -> new N3<>(setters[0], variables[0], setters[1], variables[1], setters[2], variables[2]);
                case 4 ->
                        new N4<>(setters[0], variables[0], setters[1], variables[1], setters[2], variables[2], setters[3], variables[3]);
                case 5 ->
                        new N5<>(setters[0], variables[0], setters[1], variables[1], setters[2], variables[2], setters[3], variables[3], setters[4], variables[4]);
                case 6 ->
                        new N6<>(setters[0], variables[0], setters[1], variables[1], setters[2], variables[2], setters[3], variables[3], setters[4], variables[4], setters[5], variables[5]);
                case 7 ->
                        new N7<>(setters[0], variables[0], setters[1], variables[1], setters[2], variables[2], setters[3], variables[3], setters[4], variables[4], setters[5], variables[5], setters[6], variables[6]);
                default -> new NAny<>(setters, variables);
            };
        }

        T set(T record, int element);

        record N1<T>(Setter<T> i0, Array d0) implements VariablesSetter<T> {
            @Override
            public T set(T record, int element) {
                return i0.set(record, element, d0);
            }
        }

        record N2<T>(Setter<T> i0, Array d0, Setter<T> i1, Array d1) implements VariablesSetter<T> {
            @Override
            public T set(T record, int element) {
                return i0.set(i1.set(record, element, d1), element, d0);
            }
        }

        record N3<T>(Setter<T> i0, Array d0, Setter<T> i1, Array d1, Setter<T> i2,
                     Array d2) implements VariablesSetter<T> {
            @Override
            public T set(T record, int element) {
                return i0.set(i1.set(i2.set(record, element, d2), element, d1), element, d0);
            }
        }

        record N4<T>(Setter<T> i0, Array d0, Setter<T> i1, Array d1, Setter<T> i2,
                     Array d2, Setter<T> i3, Array d3) implements VariablesSetter<T> {
            @Override
            public T set(T record, int element) {
                return i0.set(i1.set(i2.set(i3.set(record, element, d3), element, d2), element, d1), element, d0);
            }
        }

        record N5<T>(Setter<T> i0, Array d0, Setter<T> i1, Array d1, Setter<T> i2,
                     Array d2, Setter<T> i3, Array d3, Setter<T> i4, Array d4) implements VariablesSetter<T> {
            @Override
            public T set(T record, int element) {
                return i0.set(i1.set(i2.set(i3.set(i4.set(record, element, d4), element, d3), element, d2), element, d1), element, d0);
            }
        }

        record N6<T>(Setter<T> i0, Array d0, Setter<T> i1, Array d1, Setter<T> i2,
                     Array d2, Setter<T> i3, Array d3, Setter<T> i4, Array d4, Setter<T> i5,
                     Array d5) implements VariablesSetter<T> {
            @Override
            public T set(T record, int element) {
                return i0.set(i1.set(i2.set(i3.set(i4.set(i5.set(record, element, d5), element, d4), element, d3), element, d2), element, d1), element, d0);
            }
        }

        record N7<T>(Setter<T> i0, Array d0, Setter<T> i1, Array d1, Setter<T> i2,
                     Array d2, Setter<T> i3, Array d3, Setter<T> i4, Array d4, Setter<T> i5,
                     Array d5, Setter<T> i6, Array d6) implements VariablesSetter<T> {
            @Override
            public T set(T record, int element) {
                return i0.set(i1.set(i2.set(i3.set(i4.set(i5.set(i6.set(record, element, d6), element, d5), element, d4), element, d3), element, d2), element, d1), element, d0);
            }
        }

        /**
         * Slower due to loop initialization overhead and nesting (within some outer caller).
         */
        record NAny<T>(Setter<T>[] setters, Array[] arrays) implements VariablesSetter<T> {

            @Override
            public T set(T record, int element) {

                T temp = record;
                for (int i = setters.length - 1; i >= 0; i--) {
                    temp = setters[i].set(temp, element, arrays[i]);
                }

                return temp;
            }
        }
    }
}
