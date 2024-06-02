package com.stellarsunset.netcdf.field;

/**
 * Super-interface for the various primitive-specific setter instances, allows for easy switch-by-type handling to wrap
 * them in type-specialized handlers.
 *
 * <p>Using {@link java.util.function.Consumer} or anything with a generic type forces boxing and unboxing of primitive
 * values on read and incurs significant overhead.
 *
 * <p>Static factory methods are provided for convenience for methods that take a {@link FieldSetter} as an argument. As
 * this interface isn't functional it can't accept lambda functions like one might want these factory methods allow:
 * <pre>{@code
 * <T> void method(FieldSetter<T> setter); // some method accepting a field setter
 *
 * method((t, value) -> t); // wont accept lambdas
 * method(FieldSetter.bytes((t, bytes) -> t.setBytes(bytes))); // use the factory to downcast
 * }</pre>
 *
 * <p>Its not the most elegant thing, but maybe more fluent in some cases.
 */
public sealed interface FieldSetter<T> permits NoopSetter, ByteSetter, CharacterSetter, BooleanSetter, ShortSetter, IntSetter, LongSetter, FloatSetter, DoubleSetter {

    static <T> FieldSetter<T> noop() {
        return new NoopSetter<>();
    }

    static <T> FieldSetter<T> bytes(ByteSetter<T> setter) {
        return setter;
    }

    static <T> FieldSetter<T> characters(CharacterSetter<T> setter) {
        return setter;
    }

    static <T> FieldSetter<T> booleans(BooleanSetter<T> setter) {
        return setter;
    }

    static <T> FieldSetter<T> shorts(ShortSetter<T> setter) {
        return setter;
    }

    static <T> FieldSetter<T> ints(IntSetter<T> setter) {
        return setter;
    }

    static <T> FieldSetter<T> longs(LongSetter<T> setter) {
        return setter;
    }

    static <T> FieldSetter<T> floats(FloatSetter<T> setter) {
        return setter;
    }

    static <T> FieldSetter<T> doubles(DoubleSetter<T> setter) {
        return setter;
    }
}
