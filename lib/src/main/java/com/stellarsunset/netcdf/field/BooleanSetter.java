package com.stellarsunset.netcdf.field;

@FunctionalInterface
public non-sealed interface BooleanSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, boolean value);
}
