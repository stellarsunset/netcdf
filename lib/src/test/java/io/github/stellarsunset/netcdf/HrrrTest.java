package io.github.stellarsunset.netcdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

/**
 * To run this unit test modify the build.gradle.kts file to include {@code
 * testImplementation(libs.grib)}.
 */
@Disabled("Not committing HRRR to the repo, files are large.")
class HrrrTest {

  private static final String file =
      System.getProperty("user.dir") + "/src/test/resources/hrrr/hrrr.grib2";

  @Test
  void testReadFile() {

    try (NetcdfFile netcdfFile = NetcdfFiles.open(file)) {

      var binding =
          SchemaBinding.<Vil.Builder>builder()
              .recordInitializer(Vil::builder)
              .floatDimensionVariable("x", Vil.Builder::coordX)
              .floatDimensionVariable("y", Vil.Builder::coordY)
              .floatCoordinateVariable(
                  "Vertically_integrated_liquid_water_VIL_entire_atmosphere", Vil.Builder::vil)
              .build();

      var reader = NetcdfRecordReader.schemaBound(binding);

      long total = reader.read(netcdfFile).count();

      assertEquals(1059 * 1799, total, "Expected some records.");

    } catch (IOException e) {
      fail(e);
    }
  }

  record Vil(float coordX, float coordY, float vil) {

    private Vil(Builder builder) {
      this(builder.coordX, builder.coordY, builder.vil);
    }

    static Builder builder() {
      return new Builder();
    }

    static final class Builder {

      private float coordX;

      private float coordY;

      private float vil;

      private Builder() {}

      Builder coordX(float x) {
        this.coordX = x;
        return this;
      }

      Builder coordY(float y) {
        this.coordY = y;
        return this;
      }

      Builder vil(float vil) {
        this.vil = vil;
        return this;
      }

      Vil build() {
        return new Vil(this);
      }
    }
  }
}
