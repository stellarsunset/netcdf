package com.stellarsunset.netcdf;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.stellarsunset.netcdf.SchemaBindingValidator.Error.IncorrectVariableType;
import com.stellarsunset.netcdf.SchemaBindingValidator.Error.MismatchedCoordinateVariableDimensions;
import com.stellarsunset.netcdf.SchemaBindingValidator.Error.MismatchedDimensionVariableDimensions;
import com.stellarsunset.netcdf.SchemaBindingValidator.Error.MissingVariable;
import com.stellarsunset.netcdf.field.ShortSetter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;
import ucar.nc2.write.NetcdfFormatWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SchemaBindingValidatorTest {

    private static File FILE;

    @BeforeAll
    static void setup(@TempDir Path temp) {
        FILE = temp.resolve("interesting.nc").toFile();
        writeInterestingFile(FILE);
    }

    @Test
    void testMissingCoordinateVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .intCoordinateVariable("fake", (b, v) -> b + v)
                .build();

        Optional<SchemaBindingValidator.Error> error = validate(binding).map(this::unwrap);
        assertEquals(Optional.of(new MissingVariable("fake")), error);
    }

    @Test
    void testBadCoordinateVariableType() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .shortCoordinateVariable("xy", (b, v) -> b + v)
                .build();

        Optional<SchemaBindingValidator.Error> error = validate(binding).map(this::unwrap);
        assertEquals(Optional.of(new IncorrectVariableType("xy", DataType.INT, ShortSetter.class)), error);
    }

    @Test
    void testMissingDimensionVariable() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .intDimensionVariable("x", "fake", (b, v) -> b + v)
                .intCoordinateVariable("xy", (b, v) -> b + v)
                .build();

        Optional<SchemaBindingValidator.Error> error = validate(binding).map(this::unwrap);
        assertEquals(Optional.of(new MissingVariable("fake")), error);
    }

    @Test
    void testBadDimensionVariableType() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .shortDimensionVariable("x", "x", (b, v) -> b + v)
                .intCoordinateVariable("xy", (b, v) -> b + v)
                .build();

        Optional<SchemaBindingValidator.Error> error = validate(binding).map(this::unwrap);
        assertEquals(Optional.of(new IncorrectVariableType("x", DataType.INT, ShortSetter.class)), error);
    }

    @Test
    void testMismatchedCoordinateVariableDimensions() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .intCoordinateVariable("x", (b, v) -> b + v)
                .intCoordinateVariable("xy", (b, y) -> b + y)
                .build();

        Optional<SchemaBindingValidator.Error> error = validate(binding).map(this::unwrap);

        Multimap<String, String> errorContent = HashMultimap.create();
        errorContent.put("x,y", "xy");
        errorContent.put("x", "x");

        assertEquals(Optional.of(new MismatchedCoordinateVariableDimensions(errorContent)), error);
    }

    @Test
    void testMismatchedDimensionVariableDimensions() {

        SchemaBinding<String> binding = SchemaBinding.<String>builder()
                .recordInitializer(() -> "")
                .intDimensionVariable("z", "z", (b, v) -> b + v)
                .intCoordinateVariable("xy", (b, v) -> b + v)
                .build();

        Optional<SchemaBindingValidator.Error> error = validate(binding).map(this::unwrap);

        MismatchedDimensionVariableDimensions expected = new MismatchedDimensionVariableDimensions(
                "z",
                "z",
                Set.of("x", "y")
        );

        assertEquals(Optional.of(expected), error);
    }

    private SchemaBindingValidator.Error unwrap(SchemaBindingValidator.Error error) {
        return switch (error) {
            case SchemaBindingValidator.Error.Combined c -> unwrap(c.errors().iterator().next());
            default -> error;
        };
    }

    private <T> Optional<SchemaBindingValidator.Error> validate(SchemaBinding<T> binding) {
        try (NetcdfFile file = NetcdfFiles.open(FILE.getAbsolutePath())) {
            return new SchemaBindingValidator<>(file, binding).findErrors();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static void writeInterestingFile(File file) {

        NetcdfFormatWriter.Builder builder = NetcdfFormatWriter.createNewNetcdf3(file.getAbsolutePath());

        Dimension dimX = builder.addDimension("x", 10);
        Dimension dimY = builder.addDimension("y", 10);
        Dimension dimZ = builder.addDimension("z", 5);

        // Possible dimension variables
        builder.addVariable("x", DataType.INT, List.of(dimX));
        builder.addVariable("y", DataType.INT, List.of(dimY));
        builder.addVariable("z", DataType.INT, List.of(dimZ));

        // Possible coordinate variables
        builder.addVariable("xy", DataType.INT, List.of(dimX, dimY));
        builder.addVariable("zy", DataType.INT, List.of(dimZ, dimY));
        builder.addVariable("xyz", DataType.INT, List.of(dimX, dimY, dimZ));

        builder.setFill(true);

        ArrayInt xData = makeDimensionArray(10);
        ArrayInt yData = makeDimensionArray(10);
        ArrayInt zData = makeDimensionArray(5);

        try (NetcdfFormatWriter writer = builder.build()) {

            Variable varX = writer.findVariable("x");
            writer.write(varX, xData);

            Variable varY = writer.findVariable("y");
            writer.write(varY, yData);

            Variable varZ = writer.findVariable("z");
            writer.write(varZ, zData);

        } catch (InvalidRangeException e) {
            throw new IllegalArgumentException("Bad range for write.", e);
        } catch (IOException e) {
            throw new RuntimeException("IO error occurred during write.", e);
        }
    }

    private static ArrayInt makeDimensionArray(int max) {

        ArrayInt data = new ArrayInt.D1(max, false);
        Index index = data.getIndex();

        for (int i = 0; i < data.getShape()[0]; i++) {
            data.set(index.set(i), i);
        }

        return data;
    }
}
