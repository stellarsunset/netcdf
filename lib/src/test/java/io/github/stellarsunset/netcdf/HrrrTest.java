package io.github.stellarsunset.netcdf;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * To run this unit test modify the build.gradle.kts file to include {@code testImplementation(libs.grib)}.
 */
@Disabled("Not committing HRRR to the repo, files are large.")
class HrrrTest {

    private static final String file = System.getProperty("user.dir") + "/src/test/resources/hrrr/hrrr.grib2";

    @Test
    void testReadFile() {

        try (NetcdfFile netcdfFile = NetcdfFiles.open(file)) {

            var binding = SchemaBinding.<Vil.Builder>builder()
                    .recordInitializer(Vil::builder)
                    .floatDimensionVariable("x", Vil.Builder::x)
                    .floatDimensionVariable("y", Vil.Builder::y)
                    .floatCoordinateVariable("Vertically_integrated_liquid_water_VIL_entire_atmosphere", Vil.Builder::vil)
                    .build();

            var reader = NetcdfRecordReader.schemaBound(binding);

            long total = reader.read(netcdfFile)
                    .count();

            assertEquals(1059 * 1799, total, "Expected some records.");

        } catch (IOException e) {
            fail(e);
        }
    }

    record Vil(float x, float y, float vil) {

        private Vil(Builder builder) {
            this(builder.x, builder.y, builder.vil);
        }

        static Builder builder() {
            return new Builder();
        }

        static final class Builder {

            private float x;

            private float y;

            private float vil;

            private Builder() {
            }

            Builder x(float x) {
                this.x = x;
                return this;
            }

            Builder y(float y) {
                this.y = y;
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
