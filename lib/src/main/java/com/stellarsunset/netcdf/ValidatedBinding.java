package com.stellarsunset.netcdf;

import ucar.nc2.NetcdfFile;

public sealed interface ValidatedBinding<T> {

    static <T> ValidatedBinding<T> validate(NetcdfFile file, SchemaBinding<T> binding) {
        return null;
    }

    SchemaBinding<T> binding();

    record D0<T>(SchemaBinding<T> binding) implements ValidatedBinding<T> {}

    record D1<T>(SchemaBinding<T> binding) implements ValidatedBinding<T> {}

    record D2<T>(SchemaBinding<T> binding) implements ValidatedBinding<T> {}

    record D3<T>(SchemaBinding<T> binding) implements ValidatedBinding<T> {}

    record D4<T>(SchemaBinding<T> binding) implements ValidatedBinding<T> {}
}
