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

class SchemaBoundRecordReader3DTest {

  private static File FILE;

  @BeforeAll
  static void setup(@TempDir Path temp) {
    FILE = temp.resolve("test-data.nc").toFile();

    var generator = new NetcdfFileGenerator.Xyz(10, 20, 30);

    generator.writeVariables(
        FILE,
        NetcdfFileGenerator.varSpec("byte", DataType.BYTE),
        NetcdfFileGenerator.varSpec("int", DataType.INT),
        NetcdfFileGenerator.varSpec("double", DataType.DOUBLE));
  }

  @Test
  void test3D_bytesOnly() throws IOException {

    var binding =
        SchemaBinding.<Data3D.Builder>builder()
            .recordInitializer(Data3D::builder)
            .intDimensionVariable("x", Data3D.Builder::coordX)
            .intDimensionVariable("y", Data3D.Builder::coordY)
            .intDimensionVariable("z", Data3D.Builder::coordZ)
            .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
            .build();

    Hypercube.D3<Data3D> cube =
        (Hypercube.D3<Data3D>)
            Hypercube.schemaBound(NetcdfFiles.open(FILE.getAbsolutePath()), binding)
                .transform(Data3D.Builder::build);

    assertAll(
        () -> assertEquals(10, cube.d0Max(), "D0 Max"),
        () -> assertEquals(20, cube.d1Max(), "D1 Max"),
        () -> assertEquals(30, cube.d2Max(), "D2 Max"));

    Data3D first = cube.read(0, 0, 0);
    Data3D tenth = cube.read(0, 0, 10);
    Data3D twoHundredth = cube.read(0, 6, 20);

    assertAll(
        () -> assertEquals(0, first.coordX(), "First X"),
        () -> assertEquals(0, first.coordY(), "First Y"),
        () -> assertEquals(0, first.coordZ(), "First Z"),
        () -> assertEquals(Set.of("byte"), first.variables().keySet(), "First Variables"),
        () -> assertEquals(0, tenth.coordX(), "10th X"),
        () -> assertEquals(0, tenth.coordY(), "10th Y"),
        () -> assertEquals(10, tenth.coordZ(), "10th Z"),
        () -> assertEquals(Set.of("byte"), tenth.variables().keySet(), "10th Variables"),
        () -> assertEquals(0, twoHundredth.coordX(), "200th X"),
        () -> assertEquals(6, twoHundredth.coordY(), "200th Y"),
        () -> assertEquals(20, twoHundredth.coordZ(), "200th Z"),
        () -> assertEquals(Set.of("byte"), tenth.variables().keySet(), "200th Variables"));
  }

  @Test
  void test3D_bytesAndDoubles() throws IOException {

    var binding =
        SchemaBinding.<Data3D.Builder>builder()
            .recordInitializer(Data3D::builder)
            .intDimensionVariable("x", Data3D.Builder::coordX)
            .intDimensionVariable("y", Data3D.Builder::coordY)
            .intDimensionVariable("z", Data3D.Builder::coordZ)
            .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
            .intCoordinateVariable("int", (b, v) -> b.variable("int", v))
            .doubleCoordinateVariable("double", (b, v) -> b.variable("double", v))
            .build();

    Hypercube.D3<Data3D> cube =
        (Hypercube.D3<Data3D>)
            Hypercube.schemaBound(NetcdfFiles.open(FILE.getAbsolutePath()), binding)
                .transform(Data3D.Builder::build);

    assertAll(
        () -> assertEquals(10, cube.d0Max(), "D0 Max"),
        () -> assertEquals(20, cube.d1Max(), "D1 Max"),
        () -> assertEquals(30, cube.d2Max(), "D2 Max"));

    Data3D first = cube.read(0, 0, 0);
    Data3D tenth = cube.read(0, 0, 10);
    Data3D twoHundredth = cube.read(0, 6, 20);

    assertAll(
        () -> assertEquals(0, first.coordX(), "First X"),
        () -> assertEquals(0, first.coordY(), "First Y"),
        () -> assertEquals(0, first.coordZ(), "First Z"),
        () ->
            assertEquals(
                Set.of("byte", "int", "double"), first.variables().keySet(), "First Variables"),
        () -> assertEquals(0, tenth.coordX(), "10th X"),
        () -> assertEquals(0, tenth.coordY(), "10th Y"),
        () -> assertEquals(10, tenth.coordZ(), "10th Z"),
        () ->
            assertEquals(
                Set.of("byte", "int", "double"), tenth.variables().keySet(), "10th Variables"),
        () -> assertEquals(0, twoHundredth.coordX(), "200th X"),
        () -> assertEquals(6, twoHundredth.coordY(), "200th Y"),
        () -> assertEquals(20, twoHundredth.coordZ(), "200th Z"),
        () ->
            assertEquals(
                Set.of("byte", "int", "double"), tenth.variables().keySet(), "200th Variables"));
  }

  @Test
  void test3D_omitDimensions() throws IOException {

    var binding =
        SchemaBinding.<Data3D.Builder>builder()
            .recordInitializer(Data3D::builder)
            .byteCoordinateVariable("byte", (b, v) -> b.variable("byte", v))
            .build();

    Hypercube.D3<Data3D> cube =
        (Hypercube.D3<Data3D>)
            Hypercube.schemaBound(NetcdfFiles.open(FILE.getAbsolutePath()), binding)
                .transform(Data3D.Builder::build);

    assertAll(
        () -> assertEquals(10, cube.d0Max(), "D0 Max"),
        () -> assertEquals(20, cube.d1Max(), "D1 Max"),
        () -> assertEquals(30, cube.d2Max(), "D2 Max"));
  }

  private record Data3D(int coordX, int coordY, int coordZ, Map<String, Object> variables) {

    private Data3D(Builder builder) {
      this(builder.coordX, builder.coordY, builder.coordZ, Map.copyOf(builder.variables));
    }

    static Builder builder() {
      return new Builder();
    }

    static final class Builder {

      private int coordX;
      private int coordY;
      private int coordZ;
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

      Builder coordZ(int z) {
        this.coordZ = z;
        return this;
      }

      Builder variable(String name, Object value) {
        this.variables.put(name, value);
        return this;
      }

      Data3D build() {
        return new Data3D(this);
      }
    }
  }
}
