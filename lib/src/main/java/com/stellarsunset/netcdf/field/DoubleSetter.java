package com.stellarsunset.netcdf.field;

import java.io.IOException;

@FunctionalInterface
public non-sealed interface DoubleSetter<T> extends FieldSetter<T> {
    T accept(T setOnMe, double value) throws IOException;
}
