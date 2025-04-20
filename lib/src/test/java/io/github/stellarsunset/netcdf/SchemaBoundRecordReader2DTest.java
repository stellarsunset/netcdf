package io.github.stellarsunset.netcdf;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ucar.ma2.DataType;
import ucar.nc2.NetcdfFiles;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void test2D_BytesOnly() throws IOException {

        var binding = SchemaBinding.<Data2D.Builder>builder()
                .recordInitializer(Data2D::builder)
                .intDimensionVariable("x", Data2D.Builder::x)
                .intDimensionVariable("y", Data2D.Builder::y)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .build();

        Hypercube.D2<Data2D.Builder> cube = (Hypercube.D2<Data2D.Builder>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        );

        assertAll(
                () -> assertEquals(90, cube.d0Max(), "D0 Max"),
                () -> assertEquals(180, cube.d1Max(), "D1 Max")
        );

        Data2D first = cube.read(0, 0).build();
        Data2D ninety = cube.read(40, 100).build();

        assertAll(
                () -> assertEquals(0, first.x(), "First X"),
                () -> assertEquals(0, first.y(), "First Y"),
                () -> assertEquals(Set.of("byte"), first.variables().keySet(), "First Variables"),

                () -> assertEquals(40, ninety.x(), "40th X"),
                () -> assertEquals(100, ninety.y(), "100th Y"),
                () -> assertEquals(Set.of("byte"), ninety.variables().keySet(), "90th Variables")
        );
    }

    @Test
    void test2D_BytesAndDoubles() throws IOException {

        var binding = SchemaBinding.<Data2D.Builder>builder()
                .recordInitializer(Data2D::builder)
                .intDimensionVariable("x", Data2D.Builder::x)
                .intDimensionVariable("y", Data2D.Builder::y)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .intCoordinateVariable("int", (b, v) -> b.variable("int", v))
                .doubleCoordinateVariable("double", (b, v) -> b.variable("double", v))
                .build();

        Hypercube.D2<Data2D.Builder> cube = (Hypercube.D2<Data2D.Builder>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        );

        assertAll(
                () -> assertEquals(90, cube.d0Max(), "D0 Max"),
                () -> assertEquals(180, cube.d1Max(), "D1 Max")
        );

        Data2D first = cube.read(0, 0).build();
        Data2D ninety = cube.read(0, 90).build();

        assertAll(
                () -> assertEquals(0, first.x(), "First X"),
                () -> assertEquals(0, first.y(), "First Y"),
                () -> assertEquals(Set.of("byte", "int", "double"), first.variables().keySet(), "First Variables"),

                () -> assertEquals(0, ninety.x(), "90th X"),
                () -> assertEquals(90, ninety.y(), "90th Y"),
                () -> assertEquals(Set.of("byte", "int", "double"), ninety.variables().keySet(), "90th Variables")
        );
    }

    @Test
    void test2D_OmitDimensions() throws IOException {

        var binding = SchemaBinding.<Data2D.Builder>builder()
                .recordInitializer(Data2D::builder)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .build();

        Hypercube.D2<Data2D.Builder> cube = (Hypercube.D2<Data2D.Builder>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        );

        assertAll(
                () -> assertEquals(90, cube.d0Max(), "D0 Max"),
                () -> assertEquals(180, cube.d1Max(), "D1 Max")
        );
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
