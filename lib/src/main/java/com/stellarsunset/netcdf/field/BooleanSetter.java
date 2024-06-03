package com.stellarsunset.netcdf.field;

import java.io.IOException;

@FunctionalInterface
public non-sealed interface BooleanSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, boolean value) throws IOException;
}
