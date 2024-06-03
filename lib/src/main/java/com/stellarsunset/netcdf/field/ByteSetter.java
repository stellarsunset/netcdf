package com.stellarsunset.netcdf.field;

import java.io.IOException;

@FunctionalInterface
public non-sealed interface ByteSetter<T> extends FieldSetter<T> {
    T accept(T record, byte val) throws IOException;
}
