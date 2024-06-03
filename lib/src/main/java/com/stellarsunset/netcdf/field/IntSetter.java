package com.stellarsunset.netcdf.field;

import java.io.IOException;

@FunctionalInterface
public non-sealed interface IntSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, int value) throws IOException;
}
