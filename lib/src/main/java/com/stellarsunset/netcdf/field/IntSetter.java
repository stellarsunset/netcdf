package com.stellarsunset.netcdf.field;

@FunctionalInterface
public non-sealed interface IntSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, int value);
}
