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

class SchemaBoundRecordReader3DTest {

    private static File FILE;

    @BeforeAll
    static void setup(@TempDir Path temp) {
        FILE = temp.resolve("test-data.nc").toFile();

        var generator = new NetcdfFileGenerator.XYZ(10, 20, 30);

        generator.writeVariables(
                FILE,
                NetcdfFileGenerator.varSpec("byte", DataType.BYTE),
                NetcdfFileGenerator.varSpec("int", DataType.INT),
                NetcdfFileGenerator.varSpec("double", DataType.DOUBLE)
        );
    }

    @Test
    void test3D_BytesOnly() throws IOException {

        var binding = SchemaBinding.<Data3D.Builder>builder()
                .recordInitializer(Data3D::builder)
                .intDimensionVariable("x", Data3D.Builder::x)
                .intDimensionVariable("y", Data3D.Builder::y)
                .intDimensionVariable("z", Data3D.Builder::z)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .build();

        Hypercube.D3<Data3D> cube = (Hypercube.D3<Data3D>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        ).transform(Data3D.Builder::build);

        assertAll(
                () -> assertEquals(10, cube.d0Max(), "D0 Max"),
                () -> assertEquals(20, cube.d1Max(), "D1 Max"),
                () -> assertEquals(30, cube.d2Max(), "D2 Max")
        );

        Data3D first = cube.read(0, 0, 0);
        Data3D tenth = cube.read(0, 0, 10);
        Data3D twoHundredth = cube.read(0, 6, 20);

        assertAll(
                () -> assertEquals(0, first.x(), "First X"),
                () -> assertEquals(0, first.y(), "First Y"),
                () -> assertEquals(0, first.z(), "First Z"),
                () -> assertEquals(Set.of("byte"), first.variables().keySet(), "First Variables"),

                () -> assertEquals(0, tenth.x(), "10th X"),
                () -> assertEquals(0, tenth.y(), "10th Y"),
                () -> assertEquals(10, tenth.z(), "10th Z"),
                () -> assertEquals(Set.of("byte"), tenth.variables().keySet(), "10th Variables"),

                () -> assertEquals(0, twoHundredth.x(), "200th X"),
                () -> assertEquals(6, twoHundredth.y(), "200th Y"),
                () -> assertEquals(20, twoHundredth.z(), "200th Z"),
                () -> assertEquals(Set.of("byte"), tenth.variables().keySet(), "200th Variables")
        );
    }

    @Test
    void test3D_BytesAndDoubles() throws IOException {

        var binding = SchemaBinding.<Data3D.Builder>builder()
                .recordInitializer(Data3D::builder)
                .intDimensionVariable("x", Data3D.Builder::x)
                .intDimensionVariable("y", Data3D.Builder::y)
                .intDimensionVariable("z", Data3D.Builder::z)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .intCoordinateVariable("int", (b, v) -> b.variable("int", v))
                .doubleCoordinateVariable("double", (b, v) -> b.variable("double", v))
                .build();

        Hypercube.D3<Data3D> cube = (Hypercube.D3<Data3D>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        ).transform(Data3D.Builder::build);

        assertAll(
                () -> assertEquals(10, cube.d0Max(), "D0 Max"),
                () -> assertEquals(20, cube.d1Max(), "D1 Max"),
                () -> assertEquals(30, cube.d2Max(), "D2 Max")
        );

        Data3D first = cube.read(0, 0, 0);
        Data3D tenth = cube.read(0, 0, 10);
        Data3D twoHundredth = cube.read(0, 6, 20);

        assertAll(
                () -> assertEquals(0, first.x(), "First X"),
                () -> assertEquals(0, first.y(), "First Y"),
                () -> assertEquals(0, first.z(), "First Z"),
                () -> assertEquals(Set.of("byte", "int", "double"), first.variables().keySet(), "First Variables"),

                () -> assertEquals(0, tenth.x(), "10th X"),
                () -> assertEquals(0, tenth.y(), "10th Y"),
                () -> assertEquals(10, tenth.z(), "10th Z"),
                () -> assertEquals(Set.of("byte", "int", "double"), tenth.variables().keySet(), "10th Variables"),

                () -> assertEquals(0, twoHundredth.x(), "200th X"),
                () -> assertEquals(6, twoHundredth.y(), "200th Y"),
                () -> assertEquals(20, twoHundredth.z(), "200th Z"),
                () -> assertEquals(Set.of("byte", "int", "double"), tenth.variables().keySet(), "200th Variables")
        );
    }

    @Test
    void test3D_OmitDimensions() throws IOException {

        var binding = SchemaBinding.<Data3D.Builder>builder()
                .recordInitializer(Data3D::builder)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .build();

        Hypercube.D3<Data3D> cube = (Hypercube.D3<Data3D>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        ).transform(Data3D.Builder::build);

        assertAll(
                () -> assertEquals(10, cube.d0Max(), "D0 Max"),
                () -> assertEquals(20, cube.d1Max(), "D1 Max"),
                () -> assertEquals(30, cube.d2Max(), "D2 Max")
        );
    }

    private record Data3D(int x, int y, int z, Map<String, Object> variables) {

        private Data3D(Builder builder) {
            this(builder.x, builder.y, builder.z, Map.copyOf(builder.variables));
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder {

            private int x;
            private int y;
            private int z;
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

            public Builder variable(String name, Object value) {
                this.variables.put(name, value);
                return this;
            }

            public Data3D build() {
                return new Data3D(this);
            }
        }
    }
}
