package com.stellarsunset.netcdf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

/**
 * Super-interface for the various primitive-specific setter instances, allows for easy switch-by-type handling to wrap
 * them in type-specialized handlers.
 *
 * <p>Using {@link Consumer} or anything with a generic type forces boxing and unboxing of primitive values on read and
 * incurs significant overhead given the high gridded-data density in a normal NetCDF file.
 *
 * <p>Most of the implementations also support the accept operation throwing a checked {@link IOException}, this is for
 * fluency, as some bindings will want to throw data directly into an {@link OutputStream}, and those can throw on each
 * call to {@link OutputStream#write(byte[])}.
 */
public sealed interface FieldBinding<T> {

    record Noop<T>() implements FieldBinding<T> {
    }

    @FunctionalInterface
    non-sealed interface Bool<T> extends FieldBinding<T> {
        T accept(T setOnMe, boolean value) throws IOException;
    }

    @FunctionalInterface
    non-sealed interface Byte<T> extends FieldBinding<T> {
        T accept(T record, byte val) throws IOException;
    }

    @FunctionalInterface
    non-sealed interface Char<T> extends FieldBinding<T> {
        T accept(T setOnMe, char value) throws IOException;
    }

    @FunctionalInterface
    non-sealed interface Double<T> extends FieldBinding<T> {
        T accept(T setOnMe, double value) throws IOException;
    }

    @FunctionalInterface
    non-sealed interface Float<T> extends FieldBinding<T> {
        T accept(T setOnMe, float value) throws IOException;
    }

    @FunctionalInterface
    non-sealed interface Int<T> extends FieldBinding<T> {
        T accept(T setOnMe, int value) throws IOException;
    }

    @FunctionalInterface
    non-sealed interface Long<T> extends FieldBinding<T> {
        T accept(T setOnMe, long value) throws IOException;
    }

    @FunctionalInterface
    non-sealed interface Short<T> extends FieldBinding<T> {
        T accept(T setOnMe, short value) throws IOException;
    }
}
