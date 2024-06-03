package com.stellarsunset.netcdf.field;

import java.io.IOException;

@FunctionalInterface
public non-sealed interface ShortSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, short value) throws IOException;
}
