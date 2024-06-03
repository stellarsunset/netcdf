package com.stellarsunset.netcdf.field;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Initialization operation similar to a {@link Supplier} but with the ability to throw an {@link IOException} for the
 * same reasons outlined on {@link FieldSetter}.
 */
@FunctionalInterface
public interface RecordInitializer<T> {
    T get() throws IOException;
}
