package com.stellarsunset.netcdf.field;

@FunctionalInterface
public non-sealed interface ShortSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, short value);
}
