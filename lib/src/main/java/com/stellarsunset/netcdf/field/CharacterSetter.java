package com.stellarsunset.netcdf.field;

@FunctionalInterface
public non-sealed interface CharacterSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, char value);
}
