package io.github.stellarsunset.netcdf;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecordInitializerTest {

    @Test
    void testDemoteExceptions() {
        RecordInitializer<String> initializer = () -> {
            throw new IOException();
        };

        assertAll(
                () -> assertThrows(IOException.class, initializer::get, "Throwing"),
                () -> assertThrows(Hypercube.RecordCreationException.class, () -> initializer.demoteExceptions().get(), "Demoted")
        );
    }
}
