package com.stellarsunset.netcdf;

import ucar.nc2.NetcdfFile;

import java.io.IOException;
import java.util.stream.Stream;

public interface NetcdfRecordReader<T> {

    static <T> NetcdfRecordReader<T> schemaBound(SchemaBinding<T> binding) {
        return new SchemaBoundRecordReader<>(binding);
    }

    Stream<T> read(NetcdfFile file) throws IOException;
}
