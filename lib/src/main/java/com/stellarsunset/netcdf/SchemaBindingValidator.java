package com.stellarsunset.netcdf;

import ucar.nc2.NetcdfFile;

/**
 * Check the requested dimensions and
 */
record SchemaBindingValidator<T>(NetcdfFile file, SchemaBinding<T> binding) {

    static <T> void checkValidity(NetcdfFile file, SchemaBinding<T> binding) {
        new SchemaBindingValidator<>(file, binding).checkValidity();
    }

    private void checkValidity() {

    }
}
