package com.stellarsunset.netcdf;

import ucar.nc2.NetcdfFile;

record SchemaBindingValidator<T>(NetcdfFile file, SchemaBinding<T> binding) {

    /**
     * Check the validity of a set of bindings against a target {@link NetcdfFile} to parse.
     *
     * @param file    the NetCDF formatted file to check the bindings against
     * @param binding the desired bindings
     */
    static <T> void checkValidity(NetcdfFile file, SchemaBinding<T> binding) {
        new SchemaBindingValidator<>(file, binding).checkValidity();
    }

    private void checkValidity() {

    }
}
