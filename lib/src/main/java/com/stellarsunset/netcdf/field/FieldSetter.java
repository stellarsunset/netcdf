package com.stellarsunset.netcdf.field;

/**
 * Super-interface for the various primitive-specific setter instances, allows for easy switch-by-type handling to wrap
 * them in type-specialized handlers.
 *
 * <p>Using {@link java.util.function.Consumer} or anything with a generic type forces boxing and unboxing of primitive
 * values on read and incurs significant overhead.
 */
public sealed interface FieldSetter<T> permits NoopSetter, ByteSetter, CharacterSetter, BooleanSetter, ShortSetter, IntSetter, LongSetter, FloatSetter, DoubleSetter {
}
