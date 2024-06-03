package com.stellarsunset.netcdf.field;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Finalization operation similar to a {@link Consumer} but with the ability to throw an {@link IOException} for the same
 * reasons outlined on {@link FieldSetter}.
 */
@FunctionalInterface
public interface RecordFinalizer<T> {
    void accept(T record) throws IOException;
}
