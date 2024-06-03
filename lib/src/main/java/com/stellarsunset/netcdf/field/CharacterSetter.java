package com.stellarsunset.netcdf.field;

import java.io.IOException;

@FunctionalInterface
public non-sealed interface CharacterSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, char value) throws IOException;
}
