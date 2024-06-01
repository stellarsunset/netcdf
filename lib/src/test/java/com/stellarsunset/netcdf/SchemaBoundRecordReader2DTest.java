package com.stellarsunset.netcdf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ucar.ma2.DataType;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SchemaBoundRecordReader2DTest {

    private static File FILE;

    @BeforeAll
    static void setup(@TempDir Path temp) {
        FILE = temp.resolve("test-data.nc").toFile();

        var generator = new NetcdfFileGenerator.XY(90, 180);

        generator.writeVariables(
                FILE,
                NetcdfFileGenerator.varSpec("byte", DataType.BYTE),
                NetcdfFileGenerator.varSpec("int", DataType.INT),
                NetcdfFileGenerator.varSpec("double", DataType.DOUBLE)
        );
    }

    @Test
    void test2D_BytesOnly() {

        var binding = SchemaBinding.<Data2D.Builder>builder()
                .recordInitializer(Data2D::builder)
                .intDimensionVariable("x", Data2D.Builder::x)
                .intDimensionVariable("y", Data2D.Builder::y)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .build();

        List<Data2D> data = readAll(binding);
        assertEquals(90 * 180, data.size(), "Should be an element for each (x,y) dimension.");

        Data2D first = data.getFirst();
        Data2D ninety = data.get(90);

        assertAll(
                () -> assertEquals(0, first.x(), "First X"),
                () -> assertEquals(0, first.y(), "First Y"),
                () -> assertEquals(Set.of("byte"), first.variables().keySet(), "First Variables"),

                () -> assertEquals(0, ninety.x(), "90th X"),
                () -> assertEquals(1, ninety.y(), "90th Y"),
                () -> assertEquals(Set.of("byte"), ninety.variables().keySet(), "90th Variables")
        );
    }

    @Test
    void test2D_BytesAndDoubles() {

        var binding = SchemaBinding.<Data2D.Builder>builder()
                .recordInitializer(Data2D::builder)
                .intDimensionVariable("x", Data2D.Builder::x)
                .intDimensionVariable("y", Data2D.Builder::y)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .intCoordinateVariable("int", (b, v) -> b.variable("int", v))
                .doubleCoordinateVariable("double", (b, v) -> b.variable("double", v))
                .build();

        List<Data2D> data = readAll(binding);
        assertEquals(90 * 180, data.size(), "Should be an element for each (x,y) dimension.");

        Data2D first = data.getFirst();
        Data2D ninety = data.get(90);

        assertAll(
                () -> assertEquals(0, first.x(), "First X"),
                () -> assertEquals(0, first.y(), "First Y"),
                () -> assertEquals(Set.of("byte", "int", "double"), first.variables().keySet(), "First Variables"),

                () -> assertEquals(0, ninety.x(), "90th X"),
                () -> assertEquals(1, ninety.y(), "90th Y"),
                () -> assertEquals(Set.of("byte", "int", "double"), ninety.variables().keySet(), "90th Variables")
        );
    }

    @Test
    void test2D_OmitDimensions() {

        var binding = SchemaBinding.<Data2D.Builder>builder()
                .recordInitializer(Data2D::builder)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .build();

        List<Data2D> data = readAll(binding);
        assertEquals(90 * 180, data.size(), "Should be an element for each (x,y) dimension.");
    }

    private List<Data2D> readAll(SchemaBinding<Data2D.Builder> binding) {

        try (NetcdfFile netcdfFile = NetcdfFiles.open(FILE.getAbsolutePath())) {

            var reader = NetcdfRecordReader.schemaBound(binding);

            return reader.read(netcdfFile)
                    .map(Data2D.Builder::build)
                    .toList();

        } catch (IOException e) {
            fail(e);
            return List.of();
        }
    }

    private record Data2D(int x, int y, Map<String, Object> variables) {

        private Data2D(Builder builder) {
            this(builder.x, builder.y, Map.copyOf(builder.variables));
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder {

            private int x;
            private int y;
            private final Map<String, Object> variables = new HashMap<>();

            private Builder() {
            }

            public Builder x(int x) {
                this.x = x;
                return this;
            }

            public Builder y(int y) {
                this.y = y;
                return this;
            }

            public Builder variable(String name, Object value) {
                this.variables.put(name, value);
                return this;
            }

            public Data2D build() {
                return new Data2D(this);
            }
        }
    }
}
