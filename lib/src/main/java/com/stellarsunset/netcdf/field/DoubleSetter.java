package com.stellarsunset.netcdf.field;

@FunctionalInterface
public non-sealed interface DoubleSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, double value);
}
