package io.github.stellarsunset.netcdf;

import java.util.function.Consumer;

/**
 * Super-interface for the various primitive-specific setter instances, allows for easy switch-by-type handling to wrap
 * them in type-specialized handlers.
 *
 * <p>Using {@link Consumer} or anything with a generic type forces boxing and unboxing of primitive values on read and
 * incurs significant overhead given the high gridded-data density in a normal NetCDF file.
 */
public sealed interface FieldBinding<T> {

    record Noop<T>() implements FieldBinding<T> {
    }

    @FunctionalInterface
    non-sealed interface Bool<T> extends FieldBinding<T> {
        T accept(T setOnMe, boolean value);
    }

    @FunctionalInterface
    non-sealed interface Byte<T> extends FieldBinding<T> {
        T accept(T record, byte val);
    }

    @FunctionalInterface
    non-sealed interface Char<T> extends FieldBinding<T> {
        T accept(T setOnMe, char value);
    }

    @FunctionalInterface
    non-sealed interface Double<T> extends FieldBinding<T> {
        T accept(T setOnMe, double value);
    }

    @FunctionalInterface
    non-sealed interface Float<T> extends FieldBinding<T> {
        T accept(T setOnMe, float value);
    }

    @FunctionalInterface
    non-sealed interface Int<T> extends FieldBinding<T> {
        T accept(T setOnMe, int value);
    }

    @FunctionalInterface
    non-sealed interface Long<T> extends FieldBinding<T> {
        T accept(T setOnMe, long value);
    }

    @FunctionalInterface
    non-sealed interface Short<T> extends FieldBinding<T> {
        T accept(T setOnMe, short value);
    }
}
