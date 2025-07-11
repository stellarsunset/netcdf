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

class SchemaBoundRecordReader1DTest {

    private static File FILE;

    @BeforeAll
    static void setup(@TempDir Path temp) {
        FILE = temp.resolve("test-data.nc").toFile();

        var generator = new NetcdfFileGenerator.X(90);

        generator.writeVariables(
                FILE,
                NetcdfFileGenerator.varSpec("byte", DataType.BYTE),
                NetcdfFileGenerator.varSpec("int", DataType.INT),
                NetcdfFileGenerator.varSpec("double", DataType.DOUBLE)
        );
    }

    @Test
    void test1D_BytesOnly() throws IOException {

        var binding = SchemaBinding.<Data1D.Builder>builder()
                .recordInitializer(Data1D::builder)
                .intDimensionVariable("x", Data1D.Builder::x)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .build();

        Hypercube.D1<Data1D> cube = (Hypercube.D1<Data1D>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        ).transform(Data1D.Builder::build);

        assertEquals(90, cube.max(), "Max");

        Data1D first = cube.read(0);
        Data1D eightyNine = cube.read(89);

        assertAll(
                () -> assertEquals(0, first.x(), "First X"),
                () -> assertEquals(Set.of("byte"), first.variables().keySet(), "First Variables"),

                () -> assertEquals(89, eightyNine.x(), "89th X"),
                () -> assertEquals(Set.of("byte"), eightyNine.variables().keySet(), "89th Variables")
        );
    }

    @Test
    void test1D_BytesAndDoubles() throws IOException {

        var binding = SchemaBinding.<Data1D.Builder>builder()
                .recordInitializer(Data1D::builder)
                .intDimensionVariable("x", "x", Data1D.Builder::x)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .intCoordinateVariable("int", (b, v) -> b.variable("int", v))
                .doubleCoordinateVariable("double", (b, v) -> b.variable("double", v))
                .build();

        Hypercube.D1<Data1D> cube = (Hypercube.D1<Data1D>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        ).transform(Data1D.Builder::build);

        assertEquals(90, cube.max(), "Max");

        Data1D first = cube.read(0);
        Data1D eightyNine = cube.read(89);

        assertAll(
                () -> assertEquals(0, first.x(), "First X"),
                () -> assertEquals(Set.of("byte", "int", "double"), first.variables().keySet(), "First Variables"),

                () -> assertEquals(89, eightyNine.x(), "89th X"),
                () -> assertEquals(Set.of("byte", "int", "double"), eightyNine.variables().keySet(), "89th Variables")
        );
    }

    @Test
    void test1D_OmitDimensions() throws IOException {

        var binding = SchemaBinding.<Data1D.Builder>builder()
                .recordInitializer(Data1D::builder)
                .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
                .build();

        Hypercube.D1<Data1D> cube = (Hypercube.D1<Data1D>) Hypercube.schemaBound(
                NetcdfFiles.open(FILE.getAbsolutePath()),
                binding
        ).transform(Data1D.Builder::build);

        assertEquals(90, cube.max(), "Max");
    }

    private record Data1D(int x, Map<String, Object> variables) {

        private Data1D(Builder builder) {
            this(builder.x, Map.copyOf(builder.variables));
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder {

            private int x;
            private final Map<String, Object> variables = new HashMap<>();

            private Builder() {
            }

            public Builder x(int x) {
                this.x = x;
                return this;
            }

            public Builder variable(String name, Object value) {
                this.variables.put(name, value);
                return this;
            }

            public Data1D build() {
                return new Data1D(this);
            }
        }
    }
}