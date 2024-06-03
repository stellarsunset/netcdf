package com.stellarsunset.netcdf.field;

import java.io.IOException;

@FunctionalInterface
public non-sealed interface FloatSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, float value) throws IOException;
}
