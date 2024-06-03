package com.stellarsunset.netcdf.field;

import java.io.IOException;

@FunctionalInterface
public non-sealed interface LongSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, long value) throws IOException;
}
