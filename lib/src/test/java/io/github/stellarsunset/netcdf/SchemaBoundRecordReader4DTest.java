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

class SchemaBoundRecordReader4DTest {

    private static File FILE;

    @BeforeAll
    static void setup(@TempDir Path temp) {
        FILE = temp.resolve("test-data.nc").toFile();

        var generator = new NetcdfFileGenerator.XYZT(10, 20, 30, 40);

        generator.writeVariables(
                FILE,
                NetcdfFileGenerator.varSpec("byte", DataType.BYTE),
                NetcdfFileGenerator.varSpec("int", DataType.INT),
                NetcdfFileGenerator.varSpec("double", DataType.DOUBLE)
        );
    }

    @Test
    void test4D_BytesOnly() throws IOException {

        var binding = SchemaBinding.<Data4D.Builder>builder()
                .recordInitializer(Data4D::builder)
                .intDimensionVariable("x", Data4D.Builder::x)
                .intDimensionVariable("y", Data4D.Builder::y)
                .intDimensionVariable("z", Data4D.Builder::z)
                .intDimensionVariable("t", Data4D.Builder::t)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .build();

        Hypercube.D4<Data4D.Builder> cube = (Hypercube.D4<Data4D.Builder>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        );

        assertAll(
                () -> assertEquals(10, cube.d0Max(), "D0 Max"),
                () -> assertEquals(20, cube.d1Max(), "D1 Max"),
                () -> assertEquals(30, cube.d2Max(), "D2 Max"),
                () -> assertEquals(40, cube.d3Max(), "D3 Max")
        );

        Data4D first = cube.read(0, 0, 0, 0).build();
        Data4D tenth = cube.read(0, 0, 0, 10).build();
        Data4D twoHundredth = cube.read(0, 0, 5, 0).build();
        Data4D sixThousandth = cube.read(0, 5, 0, 0).build();

        assertAll(
                () -> assertEquals(0, first.x(), "First X"),
                () -> assertEquals(0, first.y(), "First Y"),
                () -> assertEquals(0, first.z(), "First Z"),
                () -> assertEquals(0, first.t(), "First T"),
                () -> assertEquals(Set.of("byte"), first.variables().keySet(), "First Variables"),

                () -> assertEquals(0, tenth.x(), "10th X"),
                () -> assertEquals(0, tenth.y(), "10th Y"),
                () -> assertEquals(0, tenth.z(), "10th Z"),
                () -> assertEquals(10, tenth.t(), "10th T"),
                () -> assertEquals(Set.of("byte"), tenth.variables().keySet(), "10th Variables"),

                () -> assertEquals(0, twoHundredth.x(), "200th X"),
                () -> assertEquals(0, twoHundredth.y(), "200th Y"),
                () -> assertEquals(5, twoHundredth.z(), "200th Z"),
                () -> assertEquals(0, twoHundredth.t(), "200th T"),
                () -> assertEquals(Set.of("byte"), twoHundredth.variables().keySet(), "200th Variables"),

                () -> assertEquals(0, sixThousandth.x(), "6000th X"),
                () -> assertEquals(5, sixThousandth.y(), "6000th Y"),
                () -> assertEquals(0, sixThousandth.z(), "6000th Z"),
                () -> assertEquals(0, sixThousandth.t(), "6000th T"),
                () -> assertEquals(Set.of("byte"), sixThousandth.variables().keySet(), "6000th Variables")
        );
    }

    @Test
    void test3D_BytesAndDoubles() throws IOException {

        var binding = SchemaBinding.<Data4D.Builder>builder()
                .recordInitializer(Data4D::builder)
                .intDimensionVariable("x", Data4D.Builder::x)
                .intDimensionVariable("y", Data4D.Builder::y)
                .intDimensionVariable("z", Data4D.Builder::z)
                .intDimensionVariable("t", Data4D.Builder::t)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .intCoordinateVariable("int", (b, v) -> b.variable("int", v))
                .doubleCoordinateVariable("double", (b, v) -> b.variable("double", v))
                .build();

        Hypercube.D4<Data4D.Builder> cube = (Hypercube.D4<Data4D.Builder>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        );

        assertAll(
                () -> assertEquals(10, cube.d0Max(), "D0 Max"),
                () -> assertEquals(20, cube.d1Max(), "D1 Max"),
                () -> assertEquals(30, cube.d2Max(), "D2 Max"),
                () -> assertEquals(40, cube.d3Max(), "D3 Max")
        );

        Data4D first = cube.read(0, 0, 0, 0).build();
        Data4D tenth = cube.read(0, 0, 0, 10).build();
        Data4D twoHundredth = cube.read(0, 0, 5, 0).build();
        Data4D sixThousandth = cube.read(0, 5, 0, 0).build();

        assertAll(
                () -> assertEquals(0, first.x(), "First X"),
                () -> assertEquals(0, first.y(), "First Y"),
                () -> assertEquals(0, first.z(), "First Z"),
                () -> assertEquals(0, first.t(), "First T"),
                () -> assertEquals(Set.of("byte", "int", "double"), first.variables().keySet(), "First Variables"),

                () -> assertEquals(0, tenth.x(), "10th X"),
                () -> assertEquals(0, tenth.y(), "10th Y"),
                () -> assertEquals(0, tenth.z(), "10th Z"),
                () -> assertEquals(10, tenth.t(), "10th T"),
                () -> assertEquals(Set.of("byte", "int", "double"), tenth.variables().keySet(), "10th Variables"),

                () -> assertEquals(0, twoHundredth.x(), "200th X"),
                () -> assertEquals(0, twoHundredth.y(), "200th Y"),
                () -> assertEquals(5, twoHundredth.z(), "200th Z"),
                () -> assertEquals(0, twoHundredth.t(), "200th T"),
                () -> assertEquals(Set.of("byte", "int", "double"), twoHundredth.variables().keySet(), "200th Variables"),

                () -> assertEquals(0, sixThousandth.x(), "6000th X"),
                () -> assertEquals(5, sixThousandth.y(), "6000th Y"),
                () -> assertEquals(0, sixThousandth.z(), "6000th Z"),
                () -> assertEquals(0, sixThousandth.t(), "6000th T"),
                () -> assertEquals(Set.of("byte", "int", "double"), sixThousandth.variables().keySet(), "6000th Variables")
        );
    }

    @Test
    void test3D_OmitDimensions() throws IOException {

        var binding = SchemaBinding.<Data4D.Builder>builder()
                .recordInitializer(Data4D::builder)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .build();

        Hypercube.D4<Data4D.Builder> cube = (Hypercube.D4<Data4D.Builder>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        );

        assertAll(
                () -> assertEquals(10, cube.d0Max(), "D0 Max"),
                () -> assertEquals(20, cube.d1Max(), "D1 Max"),
                () -> assertEquals(30, cube.d2Max(), "D2 Max"),
                () -> assertEquals(40, cube.d3Max(), "D3 Max")
        );
    }

    private record Data4D(int x, int y, int z, int t, Map<String, Object> variables) {

        private Data4D(Builder builder) {
            this(builder.x, builder.y, builder.z, builder.t, Map.copyOf(builder.variables));
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder {

            private int x;
            private int y;
            private int z;
            private int t;
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

            public Builder z(int z) {
                this.z = z;
                return this;
            }

            public Builder t(int t) {
                this.t = t;
                return this;
            }

            public Builder variable(String name, Object value) {
                this.variables.put(name, value);
                return this;
            }

            public Data4D build() {
                return new Data4D(this);
            }
        }
    }
}