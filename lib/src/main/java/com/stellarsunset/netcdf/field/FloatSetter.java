package com.stellarsunset.netcdf.field;

@FunctionalInterface
public non-sealed interface FloatSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, float value);
}
