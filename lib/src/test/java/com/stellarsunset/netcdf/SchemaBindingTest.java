package com.stellarsunset.netcdf;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchemaBindingTest {

    @Test
    void testByteDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .byteDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(FieldBinding.Byte.class, binding.dimensionVariableBinding("var"), "Should be a Byte binding")
        );
    }

    @Test
    void testCharacterDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .charDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(FieldBinding.Char.class, binding.dimensionVariableBinding("var"), "Should be a Char binding")
        );
    }

    @Test
    void testBooleanDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .booleanDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(FieldBinding.Bool.class, binding.dimensionVariableBinding("var"), "Should be a Bool binding")
        );
    }

    @Test
    void testShortDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .shortDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(FieldBinding.Short.class, binding.dimensionVariableBinding("var"), "Should be a Short binding")
        );
    }

    @Test
    void testIntDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .intDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(FieldBinding.Int.class, binding.dimensionVariableBinding("var"), "Should be a Int binding")
        );
    }

    @Test
    void testLongDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .longDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(FieldBinding.Long.class, binding.dimensionVariableBinding("var"), "Should be a Long binding")
        );
    }

    @Test
    void testFloatDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .floatDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(FieldBinding.Float.class, binding.dimensionVariableBinding("var"), "Should be a Float binding")
        );
    }

    @Test
    void testDoubleDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .doubleDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(FieldBinding.Double.class, binding.dimensionVariableBinding("var"), "Should be a Double binding")
        );
    }

    @Test
    void testByteCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .byteCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(FieldBinding.Byte.class, binding.coordinateVariableBinding("var"), "Should be a Byte binding");
    }

    @Test
    void testCharacterCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .charCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(FieldBinding.Char.class, binding.coordinateVariableBinding("var"), "Should be a Char binding");
    }

    @Test
    void testBooleanCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .booleanCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(FieldBinding.Bool.class, binding.coordinateVariableBinding("var"), "Should be a Bool binding");
    }

    @Test
    void testShortCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .shortCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(FieldBinding.Short.class, binding.coordinateVariableBinding("var"), "Should be a Short binding");
    }

    @Test
    void testIntCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .intCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(FieldBinding.Int.class, binding.coordinateVariableBinding("var"), "Should be a Int binding");
    }

    @Test
    void testLongCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .longCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(FieldBinding.Long.class, binding.coordinateVariableBinding("var"), "Should be a Long binding");
    }

    @Test
    void testFloatCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .floatCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(FieldBinding.Float.class, binding.coordinateVariableBinding("var"), "Should be a Float binding");
    }

    @Test
    void testDoubleCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .doubleCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(FieldBinding.Double.class, binding.coordinateVariableBinding("var"), "Should be a Double binding");
    }
}
