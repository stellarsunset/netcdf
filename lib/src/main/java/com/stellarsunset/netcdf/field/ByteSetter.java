package com.stellarsunset.netcdf.field;

@FunctionalInterface
public non-sealed interface ByteSetter<T> extends FieldSetter<T>{
    T accept(T record, byte val);
}
