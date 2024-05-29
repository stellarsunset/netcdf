package com.stellarsunset.netcdf;

import com.stellarsunset.netcdf.field.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SchemaBindingTest {

    @Test
    void testByteDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .byteDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(ByteSetter.class, binding.dimensionVariables().get("var"), "Should be a ByteSetter")
        );
    }

    @Test
    void testCharacterDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .charDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(CharacterSetter.class, binding.dimensionVariables().get("var"), "Should be a CharacterSetter")
        );
    }

    @Test
    void testBooleanDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .booleanDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(BooleanSetter.class, binding.dimensionVariables().get("var"), "Should be a BooleanSetter")
        );
    }

    @Test
    void testShortDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .shortDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(ShortSetter.class, binding.dimensionVariables().get("var"), "Should be a ShortSetter")
        );
    }

    @Test
    void testIntDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .intDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(IntSetter.class, binding.dimensionVariables().get("var"), "Should be a IntSetter")
        );
    }

    @Test
    void testLongDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .longDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(LongSetter.class, binding.dimensionVariables().get("var"), "Should be a LongSetter")
        );
    }

    @Test
    void testFloatDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .floatDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(FloatSetter.class, binding.dimensionVariables().get("var"), "Should be a FloatSetter")
        );
    }

    @Test
    void testDoubleDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .doubleDimensionVariable("x", "var", (s, v) -> s + v)
                .build();

        assertAll(
                () -> assertEquals(List.of("var"), binding.dimensionToVariables().get("x"), "x->[var]"),
                () -> assertInstanceOf(DoubleSetter.class, binding.dimensionVariables().get("var"), "Should be a DoubleSetter")
        );
    }

    @Test
    void testByteCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .byteCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(ByteSetter.class, binding.coordinateVariableSetter("var"), "Should be a ByteSetter");
    }

    @Test
    void testCharacterCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .charCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(CharacterSetter.class, binding.coordinateVariableSetter("var"), "Should be a CharacterSetter");
    }

    @Test
    void testBooleanCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .booleanCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(BooleanSetter.class, binding.coordinateVariableSetter("var"), "Should be a BooleanSetter");
    }

    @Test
    void testShortCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .shortCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(ShortSetter.class, binding.coordinateVariableSetter("var"), "Should be a ShortSetter");
    }

    @Test
    void testIntCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .intCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(IntSetter.class, binding.coordinateVariableSetter("var"), "Should be a IntSetter");
    }

    @Test
    void testLongCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .longCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(LongSetter.class, binding.coordinateVariableSetter("var"), "Should be a LongSetter");
    }

    @Test
    void testFloatCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .floatCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(FloatSetter.class, binding.coordinateVariableSetter("var"), "Should be a FloatSetter");
    }

    @Test
    void testDoubleCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordSupplier(() -> "")
                .doubleCoordinateVariable("var", (s, v) -> s + v)
                .build();

        assertInstanceOf(DoubleSetter.class, binding.coordinateVariableSetter("var"), "Should be a DoubleSetter");
    }
}
