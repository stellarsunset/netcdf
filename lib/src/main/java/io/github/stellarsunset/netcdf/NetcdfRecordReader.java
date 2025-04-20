package io.github.stellarsunset.netcdf;

import ucar.nc2.NetcdfFile;

import java.io.IOException;
import java.util.stream.Stream;

@FunctionalInterface
public interface NetcdfRecordReader<T> {

    /**
     * Record reader implementation generating a stream of POJOs via a {@link SchemaBinding}. See the docs on that class
     * for more details.
     *
     * @param binding the schema to use when reading variables from the files in to POJO fields
     */
    static <T> NetcdfRecordReader<T> schemaBound(SchemaBinding<T> binding) {
        return file -> Hypercube.schemaBound(file, binding).stream();
    }

    Stream<T> read(NetcdfFile file) throws IOException;
}
