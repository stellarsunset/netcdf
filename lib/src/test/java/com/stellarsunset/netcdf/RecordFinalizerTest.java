package com.stellarsunset.netcdf;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecordFinalizerTest {

    @Test
    void testDemoteExceptions() {
        RecordFinalizer<String> finalizer = obj -> {
            throw new IOException();
        };

        assertAll(
                () -> assertThrows(IOException.class, () -> finalizer.accept(""), "Throwing"),
                () -> assertThrows(Hypercube.RecordCreationException.class, () -> finalizer.demoteExceptions().accept(""), "Demoted")
        );
    }
}
