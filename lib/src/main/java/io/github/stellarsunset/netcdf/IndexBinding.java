package io.github.stellarsunset.netcdf;

import java.io.IOException;
import java.util.List;

/**
 * Internal type representing the schema of an {@link Array} index to an object of type 'T' via a {@link FieldBinding}.
 *
 * <p>The goal of this interface is to hide the underlying type of the field that's being bound (e.g. an int, float, etc.)
 * from the code performing the index-based schema to create an object.
 */
sealed interface IndexBinding<T> {

    /**
     * Wraps the provided {@link IndexBinding} as one that fails quietly if {@link IOException}s are encountered during
     * calls to the set method.
     *
     * <p>In most cases this is <i>probably</i> not desirable behavior.
     *
     * @param binding the binding to decorate with quiet exception handling
     */
    static <T> IndexBinding<T> quiet(IndexBinding<T> binding) {
        return binding; // TODO
    }

    @FunctionalInterface
    non-sealed interface D0<T> extends IndexBinding<T> {

        static <T> D0<T> compose(List<D0<T>> bindings) {
            return bindings.stream().reduce(obj -> obj, D0::combine);
        }

        static <T> D0<T> combine(D0<T> one, D0<T> two) {
            return object -> two.set(one.set(object));
        }

        static <T> D0<T> noop() {
            return object -> object;
        }

        T set(T object);
    }

    @FunctionalInterface
    non-sealed interface D1<T> extends IndexBinding<T> {

        static <T> D1<T> compose(List<D1<T>> bindings) {
            return bindings.stream().reduce(D1.noop(), D1::combine);
        }

        static <T> D1<T> combine(D1<T> one, D1<T> two) {
            return (object, i) -> two.set(one.set(object, i), i);
        }

        static <T> D1<T> noop() {
            return (object, i) -> object;
        }

        T set(T object, int i);
    }

    @FunctionalInterface
    non-sealed interface D2<T> extends IndexBinding<T> {

        static <T> D2<T> compose(List<D2<T>> bindings) {
            return bindings.stream().reduce(D2.noop(), D2::combine);
        }

        static <T> D2<T> combine(D2<T> one, D2<T> two) {
            return (object, i0, i1) -> two.set(one.set(object, i0, i1), i0, i1);
        }

        static <T> D2<T> noop() {
            return (object, i0, i1) -> object;
        }

        T set(T object, int i0, int i1);
    }

    @FunctionalInterface
    non-sealed interface D3<T> extends IndexBinding<T> {

        static <T> D3<T> compose(List<D3<T>> bindings) {
            return bindings.stream().reduce(D3.noop(), D3::combine);
        }

        static <T> D3<T> combine(D3<T> one, D3<T> two) {
            return (object, i0, i1, i2) -> two.set(one.set(object, i0, i1, i2), i0, i1, i2);
        }

        static <T> D3<T> noop() {
            return (object, i0, i1, i2) -> object;
        }

        T set(T object, int i0, int i1, int i2);
    }

    @FunctionalInterface
    non-sealed interface D4<T> extends IndexBinding<T> {

        static <T> D4<T> compose(List<D4<T>> bindings) {
            return bindings.stream().reduce(D4.noop(), D4::combine);
        }

        static <T> D4<T> combine(D4<T> one, D4<T> two) {
            return (object, i0, i1, i2, i3) -> two.set(one.set(object, i0, i1, i2, i3), i0, i1, i2, i3);
        }

        static <T> D4<T> noop() {
            return (object, i0, i1, i2, i3) -> object;
        }

        T set(T object, int i0, int i1, int i2, int i3);
    }
}
