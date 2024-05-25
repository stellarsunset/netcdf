package com.stellarsunset.netcdf.field;

@FunctionalInterface
public non-sealed interface LongSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, long value);
}
