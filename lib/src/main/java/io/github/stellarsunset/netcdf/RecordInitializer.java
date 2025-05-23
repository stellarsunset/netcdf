package io.github.stellarsunset.netcdf;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Initialization operation similar to a {@link Supplier} but with the ability to throw an {@link IOException} for the
 * same reasons outlined on {@link FieldBinding}.
 */
@FunctionalInterface
public interface RecordInitializer<T> {
    T get() throws IOException;

    /**
     * Returns this initializer as a normal {@link Supplier} that will throw demoted runtime exceptions if there are errors
     * in record creation.
     */
    default Supplier<T> demoteExceptions() {
        var self = this;
        return () -> {
            try {
                return self.get();
            } catch (IOException e) {
                throw new Hypercube.RecordCreationException("Error initializing record.", e);
            }
        };
    }
}
