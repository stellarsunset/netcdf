package io.github.stellarsunset.netcdf;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ucar.ma2.DataType;
import ucar.nc2.NetcdfFiles;

class SchemaBoundRecordReader2DTest {

  private static File FILE;

  @BeforeAll
  static void setup(@TempDir Path temp) {
    FILE = temp.resolve("test-data.nc").toFile();

    var generator = new NetcdfFileGenerator.Xy(90, 180);

    generator.writeVariables(
        FILE,
        NetcdfFileGenerator.varSpec("byte", DataType.BYTE),
        NetcdfFileGenerator.varSpec("int", DataType.INT),
        NetcdfFileGenerator.varSpec("double", DataType.DOUBLE));
  }

  @Test
  void test2D_bytesOnly() throws IOException {

    var binding =
        SchemaBinding.<Data2D.Builder>builder()
            .recordInitializer(Data2D::builder)
            .intDimensionVariable("x", Data2D.Builder::coordX)
            .intDimensionVariable("y", Data2D.Builder::coordY)
            .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
            .build();

    Hypercube.D2<Data2D> cube =
        (Hypercube.D2<Data2D>)
            Hypercube.schemaBound(NetcdfFiles.open(FILE.getAbsolutePath()), binding)
                .transform(Data2D.Builder::build);

    assertAll(
        () -> assertEquals(90, cube.d0Max(), "D0 Max"),
        () -> assertEquals(180, cube.d1Max(), "D1 Max"));

    Data2D first = cube.read(0, 0);
    Data2D ninety = cube.read(40, 100);

    assertAll(
        () -> assertEquals(0, first.coordX(), "First X"),
        () -> assertEquals(0, first.coordY(), "First Y"),
        () -> assertEquals(Set.of("byte"), first.variables().keySet(), "First Variables"),
        () -> assertEquals(40, ninety.coordX(), "40th X"),
        () -> assertEquals(100, ninety.coordY(), "100th Y"),
        () -> assertEquals(Set.of("byte"), ninety.variables().keySet(), "90th Variables"));
  }

  @Test
  void test2D_bytesAndDoubles() throws IOException {

    var binding =
        SchemaBinding.<Data2D.Builder>builder()
            .recordInitializer(Data2D::builder)
            .intDimensionVariable("x", Data2D.Builder::coordX)
            .intDimensionVariable("y", Data2D.Builder::coordY)
            .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
            .intCoordinateVariable("int", (b, v) -> b.variable("int", v))
            .doubleCoordinateVariable("double", (b, v) -> b.variable("double", v))
            .build();

    Hypercube.D2<Data2D> cube =
        (Hypercube.D2<Data2D>)
            Hypercube.schemaBound(NetcdfFiles.open(FILE.getAbsolutePath()), binding)
                .transform(Data2D.Builder::build);

    assertAll(
        () -> assertEquals(90, cube.d0Max(), "D0 Max"),
        () -> assertEquals(180, cube.d1Max(), "D1 Max"));

    Data2D first = cube.read(0, 0);
    Data2D ninety = cube.read(0, 90);

    assertAll(
        () -> assertEquals(0, first.coordX(), "First X"),
        () -> assertEquals(0, first.coordY(), "First Y"),
        () ->
            assertEquals(
                Set.of("byte", "int", "double"), first.variables().keySet(), "First Variables"),
        () -> assertEquals(0, ninety.coordX(), "90th X"),
        () -> assertEquals(90, ninety.coordY(), "90th Y"),
        () ->
            assertEquals(
                Set.of("byte", "int", "double"), ninety.variables().keySet(), "90th Variables"));
  }

  @Test
  void test2D_omitDimensions() throws IOException {

    var binding =
        SchemaBinding.<Data2D.Builder>builder()
            .recordInitializer(Data2D::builder)
            .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
            .build();

    Hypercube.D2<Data2D> cube =
        (Hypercube.D2<Data2D>)
            Hypercube.schemaBound(NetcdfFiles.open(FILE.getAbsolutePath()), binding)
                .transform(Data2D.Builder::build);

    assertAll(
        () -> assertEquals(90, cube.d0Max(), "D0 Max"),
        () -> assertEquals(180, cube.d1Max(), "D1 Max"));
  }

  private record Data2D(int coordX, int coordY, Map<String, Object> variables) {

    private Data2D(Builder builder) {
      this(builder.coordX, builder.coordY, Map.copyOf(builder.variables));
    }

    static Builder builder() {
      return new Builder();
    }

    static final class Builder {

      private int coordX;
      private int coordY;
      private final Map<String, Object> variables = new HashMap<>();

      private Builder() {}

      Builder coordX(int x) {
        this.coordX = x;
        return this;
      }

      Builder coordY(int y) {
        this.coordY = y;
        return this;
      }

      Builder variable(String name, Object value) {
        this.variables.put(name, value);
        return this;
      }

      Data2D build() {
        return new Data2D(this);
      }
    }
  }
}
