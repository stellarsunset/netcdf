package com.stellarsunset.netcdf.field;

/**
 * Super-interface for the various primitive-specific setter instances, allows for easy switch-by-type handling to wrap
 * them type-specialized handlers.
 *
 * <p>Using the {@link java.util.function.Consumer} forces boxing and unboxing of primitive types and incurs significant
 * overhead when processing billions of data points.
 */
public sealed interface FieldSetter<T> permits NoopSetter, ByteSetter, CharacterSetter, BooleanSetter, ShortSetter, IntSetter, LongSetter, FloatSetter, DoubleSetter {
}
