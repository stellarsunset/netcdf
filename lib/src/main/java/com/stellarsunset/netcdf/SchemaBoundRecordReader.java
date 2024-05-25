package com.stellarsunset.netcdf;

import com.stellarsunset.netcdf.field.*;
import ucar.ma2.Array;
import ucar.ma2.ArrayByte;
import ucar.ma2.Index;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

record SchemaBoundRecordReader<T>(SchemaBinding<T> binding) implements NetcdfRecordReader<T> {

    @SuppressWarnings("unchecked")
    @Override
    public Stream<T> read(NetcdfFile file) throws IOException {

        SchemaBindingValidator.checkValidity(file, binding);

        List<Setter<T>> varSetters = new ArrayList<>();
        List<Array> varArrays = new ArrayList<>();

        for (var entry : binding.variables().entrySet()) {

            Variable variable = file.findVariable(entry.getKey());

            varSetters.add(Setter.from(entry.getValue()));
            varArrays.add(variable.read().reduce()); // reduce any dimensions of length 1
        }

        VariablesSetter<T> variablesSetter = VariablesSetter.from(
                varSetters.toArray(Setter[]::new),
                varArrays.toArray(Array[]::new)
        );

        // Grab only the dimensions of our variables with a length greater than 1
        var orderedNonZeroDimensions = file.findVariable(binding.variables().keySet().iterator().next())
                .getDimensions()
                .stream()
                .filter(d -> d.getLength() != 1)
                .toList();

        List<Setter<T>> dimSetters = new ArrayList<>();
        List<Array> dimArrays = new ArrayList<>();

        for (var dimension : orderedNonZeroDimensions) {

            Optional<Variable> oDimension = ofNullable(file.findVariable(dimension.getName()));

            Optional<Setter<T>> oSetter = ofNullable(binding.dimensions().get(dimension.getName()))
                    .map(Setter::from);

            if (oSetter.isEmpty()) {

                dimSetters.add(Setter.noop());
                dimArrays.add(new ArrayByte.D1(dimension.getLength(), false));

            } else {

                Variable dimVariable = oDimension.orElseThrow(
                        () -> new IllegalArgumentException("Requested Dimension without adjacent variable defining values: " + dimension.getName())
                );

                dimSetters.add(oSetter.get());
                dimArrays.add(dimVariable.read());
            }
        }

        RecordIterator<T> iterator = RecordIterator.from(
                binding().recordSupplier(),
                dimSetters.toArray(Setter[]::new),
                dimArrays.toArray(Array[]::new),
                variablesSetter
        );

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.NONNULL),
                false
        );
    }

    /**
     * Internal setter interface to wrap the client-facing {@link FieldSetter} types and hide them from the internals of the read operation.
     *
     * <p>These setters need to encapsulate the boolean/character/etc. read operations.
     */
    private sealed interface Setter<T> {

        static <T> Setter<T> noop() {
            return new Noop<>();
        }

        static <T> Setter<T> from(FieldSetter<T> setter) {
            return switch (setter) {
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
                return bs.accept(record, data.getByte(element));
            }
        }

        record Character<T>(CharacterSetter<T> cs) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                return cs.accept(record, data.getChar(element));
            }
        }

        record Boolean<T>(BooleanSetter<T> bs) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                return bs.accept(record, data.getBoolean(element));
            }
        }

        record Short<T>(ShortSetter<T> ss) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                return ss.accept(record, data.getShort(element));
            }
        }

        record Int<T>(IntSetter<T> is) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                return is.accept(record, data.getInt(element));
            }
        }

        record Long<T>(LongSetter<T> ls) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                return ls.accept(record, data.getLong(element));
            }
        }

        record Float<T>(FloatSetter<T> fs) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                return fs.accept(record, data.getFloat(element));
            }
        }

        record Double<T>(DoubleSetter<T> ds) implements Setter<T> {
            @Override
            public T set(T record, int element, Array data) {
                return ds.accept(record, data.getDouble(element));
            }
        }
    }

    private sealed interface RecordIterator<T> extends Iterator<T> {

        /**
         * Create a new record iterator which returns a lazy sequence of records pulled from the underlying cdm data.
         *
         * @param supplier   supplier for new record instances to call setters against
         * @param dimSetters setters on the objects for dimension values
         * @param dimensions the data arrays containing the dimension values
         * @param varsSetter setter for injecting the variable value at the dimension coordinates into the record
         */
        static <T> RecordIterator<T> from(Supplier<T> supplier, Setter<T>[] dimSetters, Array[] dimensions, VariablesSetter<T> varsSetter) {
            return switch (dimSetters.length) {
                case 1 -> new D1<>(
                        supplier,
                        dimSetters[0],
                        dimensions[0],
                        varsSetter
                );
                case 2 -> new D2<>(
                        supplier,
                        dimSetters[0],
                        dimensions[0],
                        dimSetters[1],
                        dimensions[1],
                        varsSetter
                );
                case 3 -> new D3<>(
                        supplier,
                        dimSetters[0],
                        dimensions[0],
                        dimSetters[1],
                        dimensions[1],
                        dimSetters[2],
                        dimensions[2],
                        varsSetter
                );
                case 4 -> new D4<>(
                        supplier,
                        dimSetters[0],
                        dimensions[0],
                        dimSetters[1],
                        dimensions[1],
                        dimSetters[2],
                        dimensions[2],
                        dimSetters[3],
                        dimensions[3],
                        varsSetter
                );
                default -> new DN<>(
                        supplier,
                        dimSetters,
                        dimensions,
                        varsSetter,
                        dimensions[0].getIndex()
                );
            };
        }

        final class D1<T> implements RecordIterator<T> {

            private final Supplier<T> supplier;
            private final Setter<T> s0;
            private final Array d0;
            private final VariablesSetter<T> varsSetter;
            private final int max0;
            private int i0;

            private D1(Supplier<T> supplier, Setter<T> s0, Array d0, VariablesSetter<T> varsSetter) {
                this.supplier = requireNonNull(supplier);
                this.s0 = requireNonNull(s0);
                this.d0 = requireNonNull(d0);
                this.varsSetter = requireNonNull(varsSetter);
                this.max0 = d0.getShape()[0];
                this.i0 = 0;
            }

            @Override
            public boolean hasNext() {
                return i0 < max0;
            }

            @Override
            public T next() {
                T t = varsSetter.set(s0.set(supplier.get(), i0, d0), i0);
                i0++;
                return t;
            }
        }

        final class D2<T> implements RecordIterator<T> {

            private final Supplier<T> supplier;
            private final Setter<T> s0;
            private final Array d0;
            private final Setter<T> s1;
            private final Array d1;
            private final VariablesSetter<T> varsSetter;

            private final int max0;
            private int i0;
            private final int max1;
            private int i1;
            private int element;

            private D2(Supplier<T> supplier, Setter<T> s0, Array d0, Setter<T> s1, Array d1, VariablesSetter<T> varsSetter) {
                this.supplier = requireNonNull(supplier);
                this.s0 = requireNonNull(s0);
                this.d0 = requireNonNull(d0);
                this.s1 = requireNonNull(s1);
                this.d1 = requireNonNull(d1);
                this.varsSetter = requireNonNull(varsSetter);
                this.max0 = d0.getShape()[0];
                this.i0 = 0;
                this.max1 = d1.getShape()[0];
                this.i1 = 0;
                this.element = 0;
            }

            @Override
            public boolean hasNext() {
                return i0 < max0 && i1 < max1;
            }

            @Override
            public T next() {
                T t = varsSetter.set(s0.set(s1.set(supplier.get(), i1, d1), i0, d0), element);

                if (i0 + 1 < max0) {
                    i0++;
                } else {
                    i0 = 0;
                    i1++;
                }

                element++;
                return t;
            }
        }

        final class D3<T> implements RecordIterator<T> {

            private final Supplier<T> supplier;
            private final Setter<T> s0;
            private final Array d0;
            private final Setter<T> s1;
            private final Array d1;
            private final Setter<T> s2;
            private final Array d2;
            private final VariablesSetter<T> varsSetter;

            private final int max0;
            private int i0;
            private final int max1;
            private int i1;
            private final int max2;
            private int i2;
            private int element;

            private D3(Supplier<T> supplier, Setter<T> s0, Array d0, Setter<T> s1, Array d1, Setter<T> s2, Array d2, VariablesSetter<T> varsSetter) {
                this.supplier = requireNonNull(supplier);
                this.s0 = requireNonNull(s0);
                this.d0 = requireNonNull(d0);
                this.s1 = requireNonNull(s1);
                this.d1 = requireNonNull(d1);
                this.s2 = requireNonNull(s2);
                this.d2 = requireNonNull(d2);
                this.varsSetter = requireNonNull(varsSetter);
                this.max0 = d0.getShape()[0];
                this.i0 = 0;
                this.max1 = d1.getShape()[0];
                this.i1 = 0;
                this.max2 = d2.getShape()[0];
                this.i2 = 0;
                this.element = 0;
            }

            @Override
            public boolean hasNext() {
                return i0 < max0 && i1 < max1 && i2 < max2;
            }

            @Override
            public T next() {
                T t = varsSetter.set(s0.set(s1.set(s2.set(supplier.get(), i2, d2), i1, d1), i0, d0), element);

                if (i0 + 1 < max0) {
                    i0++;
                } else if (i1 + 1 < max1) {
                    i0 = 0;
                    i1++;
                } else {
                    i0 = 0;
                    i1 = 0;
                    i2++;
                }

                element++;
                return t;
            }
        }

        final class D4<T> implements RecordIterator<T> {

            private final Supplier<T> supplier;
            private final Setter<T> s0;
            private final Array d0;
            private final Setter<T> s1;
            private final Array d1;
            private final Setter<T> s2;
            private final Array d2;
            private final Setter<T> s3;
            private final Array d3;
            private final VariablesSetter<T> varsSetter;

            private final int max0;
            private int i0;
            private final int max1;
            private int i1;
            private final int max2;
            private int i2;
            private final int max3;
            private int i3;
            private int element;

            private D4(Supplier<T> supplier, Setter<T> s0, Array d0, Setter<T> s1, Array d1, Setter<T> s2, Array d2, Setter<T> s3, Array d3, VariablesSetter<T> varsSetter) {
                this.supplier = requireNonNull(supplier);
                this.s0 = requireNonNull(s0);
                this.d0 = requireNonNull(d0);
                this.s1 = requireNonNull(s1);
                this.d1 = requireNonNull(d1);
                this.s2 = requireNonNull(s2);
                this.d2 = requireNonNull(d2);
                this.s3 = requireNonNull(s3);
                this.d3 = requireNonNull(d3);
                this.varsSetter = requireNonNull(varsSetter);
                this.max0 = d0.getShape()[0];
                this.i0 = 0;
                this.max1 = d1.getShape()[0];
                this.i1 = 0;
                this.max2 = d2.getShape()[0];
                this.i2 = 0;
                this.max3 = d3.getShape()[0];
                this.i3 = 0;
                this.element = 0;
            }

            @Override
            public boolean hasNext() {
                return i0 < max0 && i1 < max1 && i2 < max2 && i3 < max3;
            }

            @Override
            public T next() {
                T t = varsSetter.set(s0.set(s1.set(s2.set(s3.set(supplier.get(), i3, d3), i2, d2), i1, d1), i0, d0), element);

                if (i0 + 1 < max0) {
                    i0++;
                } else if (i1 + 1 < max1) {
                    i0 = 0;
                    i1++;
                } else if (i2 + 1 < max2) {
                    i0 = 0;
                    i1 = 0;
                    i2++;
                } else {
                    i0 = 0;
                    i1 = 0;
                    i2 = 0;
                    i3++;
                }

                element++;
                return t;
            }
        }


        record DN<T>(Supplier<T> supplier, Setter<T>[] dimSetters, Array[] dimensions,
                     VariablesSetter<T> varsSetter, Index index) implements RecordIterator<T> {

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public T next() {
                return null;
            }
        }
    }

    private sealed interface VariablesSetter<T> {

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
                for (int i = 0; i < setters.length; i++) {
                    temp = setters[i].set(temp, element, arrays[i]);
                }

                return temp;
            }
        }
    }
}