package io.github.stellarsunset.netcdf;

import ucar.nc2.NetcdfFile;

import java.io.IOException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Interface for N-dimensional "hyper"-cubes containing gridded NetCDF weather information.
 */
public sealed interface Hypercube<T> extends AutoCloseable {

    /**
     * Creates a new {@link Hypercube} backed by data in the provided {@link NetcdfFile} and generating objects based on
     * the field bindings provided by the {@link SchemaBinding}.
     *
     * @param file    the netcdf file to read values from
     * @param binding the binding to use to generate objects from the underlying data
     */
    static <T> Hypercube<T> schemaBound(NetcdfFile file, SchemaBinding<T> binding) {
        return SchemaBoundHyperCubes.make(ValidatedBinding.validate(file, binding));
    }

    /**
     * Creates an iterator that will return all records in the {@link Hypercube} traversing each dimension in canonical
     * order (left->right, top->bottom).
     */
    Iterator<T> iterator();

    /**
     * Stream all records out of the associated {@link Hypercube} traversing each dimension in order.
     *
     * <p>The default implementation uses a {@link Spliterator} of unknown size, implementations may want to overwrite
     * this if they know the shape of the iterator.
     */
    default Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.SORTED), false);
    }

    non-sealed interface D1<T> extends Hypercube<T> {
        T read(int i);

        int max();

        @Override
        default Iterator<T> iterator() {
            return new Iter<>(this);
        }

        @Override
        default Stream<T> stream() {
            return StreamSupport.stream(Spliterators.spliterator(iterator(), max(), Spliterator.SORTED), false);
        }

        final class Iter<T> implements Iterator<T> {

            private final D1<T> cube;
            private int index = 0;

            private Iter(D1<T> cube) {
                this.cube = cube;
            }

            @Override
            public boolean hasNext() {
                return index < cube.max();
            }

            @Override
            public T next() {
                T t = cube.read(index);
                index++;
                return t;
            }
        }
    }

    non-sealed interface D2<T> extends Hypercube<T> {
        T read(int i0, int i1);

        int d0Max();

        int d1Max();

        @Override
        default Iterator<T> iterator() {
            return new Iter<>(this);
        }

        @Override
        default Stream<T> stream() {
            return StreamSupport.stream(
                    Spliterators.spliterator(iterator(), (long) d0Max() * d1Max(), Spliterator.SORTED), false
            );
        }

        final class Iter<T> implements Iterator<T> {

            private final D2<T> cube;
            private int i0 = 0;
            private int i1 = 0;

            private Iter(D2<T> cube) {
                this.cube = cube;
            }

            @Override
            public boolean hasNext() {
                return i0 < cube.d0Max() && i1 < cube.d1Max();
            }

            @Override
            public T next() {
                T t = cube.read(i0, i1);
                i1++;
                if (i1 >= cube.d1Max()) {
                    i0++;
                    i1 = 0;
                }
                return t;
            }
        }
    }

    non-sealed interface D3<T> extends Hypercube<T> {
        T read(int i0, int i1, int i2);

        int d0Max();

        int d1Max();

        int d2Max();

        @Override
        default Iterator<T> iterator() {
            return new Iter<>(this);
        }

        @Override
        default Stream<T> stream() {
            return StreamSupport.stream(
                    Spliterators.spliterator(iterator(), (long) d0Max() * d1Max() * d2Max(), Spliterator.SORTED), false
            );
        }

        final class Iter<T> implements Iterator<T> {

            private final D3<T> cube;
            private int i0 = 0;
            private int i1 = 0;
            private int i2 = 0;

            private Iter(D3<T> cube) {
                this.cube = cube;
            }

            @Override
            public boolean hasNext() {
                return i0 < cube.d0Max() && i1 < cube.d1Max() && i2 < cube.d2Max();
            }

            @Override
            public T next() {
                T t = cube.read(i0, i1, i2);
                i2++;
                if (i2 >= cube.d2Max()) {
                    i1++;
                    i2 = 0;
                }
                if (i1 >= cube.d1Max()) {
                    i0++;
                    i1 = 0;
                }
                return t;
            }
        }
    }

    non-sealed interface D4<T> extends Hypercube<T> {
        T read(int d0, int d1, int d2, int d3);

        int d0Max();

        int d1Max();

        int d2Max();

        int d3Max();

        @Override
        default Iterator<T> iterator() {
            return new Iter<>(this);
        }

        @Override
        default Stream<T> stream() {
            return StreamSupport.stream(
                    Spliterators.spliterator(iterator(), (long) d0Max() * d1Max() * d2Max() * d3Max(), Spliterator.SORTED), false
            );
        }

        final class Iter<T> implements Iterator<T> {

            private final D4<T> cube;
            private int i0 = 0;
            private int i1 = 0;
            private int i2 = 0;
            private int i3 = 0;

            private Iter(D4<T> cube) {
                this.cube = cube;
            }

            @Override
            public boolean hasNext() {
                return i0 < cube.d0Max() && i1 < cube.d1Max() && i2 < cube.d2Max() && i3 < cube.d3Max();
            }

            @Override
            public T next() {
                T t = cube.read(i0, i1, i2, i3);
                i3++;
                if (i3 >= cube.d3Max()) {
                    i2++;
                    i3 = 0;
                }
                if (i2 >= cube.d2Max()) {
                    i1++;
                    i2 = 0;
                }
                if (i1 >= cube.d1Max()) {
                    i0++;
                    i1 = 0;
                }
                return t;
            }
        }
    }

    /**
     * Dedicated runtime exception class for issues encountered when streaming records out of a netcdf file.
     */
    final class RecordCreationException extends RuntimeException {
        RecordCreationException(String message, IOException e) {
            super(message, e);
        }
    }
}
