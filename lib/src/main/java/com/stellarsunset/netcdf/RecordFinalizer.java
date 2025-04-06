package com.stellarsunset.netcdf;

import com.stellarsunset.netcdf.field.FieldSetter;

import java.io.IOException;
import java.util.function.Consumer;

/**
 * Finalization operation similar to a {@link Consumer} but with the ability to throw an {@link IOException} for the same
 * reasons outlined on {@link FieldSetter}.
 */
@FunctionalInterface
public interface RecordFinalizer<T> {
    void accept(T record) throws IOException;

    /**
     * Returns this record finalizer as a {@link Consumer} that will throw demoted runtime exceptions if exceptions are
     * encountered in the finalization step.
     */
    default Consumer<T> demoteExceptions() {
        var self = this;
        return record -> {
            try {
                self.accept(record);
            } catch (IOException e) {
                throw new Hypercube.RecordCreationException("Error finalizing record.", e);
            }
        };
    }
}
