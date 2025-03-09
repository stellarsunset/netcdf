package com.stellarsunset.netcdf;

import ucar.nc2.NetcdfFile;

import java.io.IOException;

/** Interface for N-dimensional "hyper"-cubes containing gridded NetCDF weather information. */
public sealed interface Hypercube<T> extends AutoCloseable {

    static <T> Hypercube<T> schemaBound(NetcdfFile file, SchemaBinding<T> binding) {
        return  SchemaBoundHyperCubeMaker.make(file, binding);
    }

    non-sealed interface D1<T> extends Hypercube<T> {
        T read(int d0);

        int d0Max();
    }

    non-sealed interface D2<T> extends Hypercube<T> {
        T read(int d0, int d1);

        int d0Max();

        int d1Max();
    }

    non-sealed interface D3<T> extends Hypercube<T> {
        T read(int d0, int d1, int d2);

        int d0Max();

        int d1Max();

        int d2Max();
    }

    non-sealed interface D4<T> extends Hypercube<T> {
        T read(int d0, int d1, int d2, int d3);

        int d0Max();

        int d1Max();

        int d2Max();

        int d3Max();
    }

    /**
     * Dedicated runtime exception class for issues encountered when streaming records out of a netcdf file.
     */
    final class RecordCreationException extends RuntimeException {
        RecordCreationException(String message, IOException e) {
            super(message, e);
        }
    }
}
