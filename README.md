# NetCDF

[![Test](https://github.com/stellarsunset/netcdf/actions/workflows/test.yaml/badge.svg)](https://github.com/stellarsunset/netcdf/actions/workflows/test.yaml)
[![codecov](https://codecov.io/github/stellarsunset/netcdf/graph/badge.svg?token=JIzptwIhbN)](https://codecov.io/github/stellarsunset/netcdf)

A thin wrapper around [NetCDF-Java](https://docs.unidata.ucar.edu/netcdf-java/current/userguide/index.html) for reading
sub-schemas of variables into Java objects.

## Motivation

Take for example a simple NetCDF file with the following dimensions and variables:

```markdown
Dimensions

- x(length=10)
- y(length=10)
- z(length=10)

Variables

- latitude(type=double, dimensions=[x]) // each step along the x dimension corresponds to new latitude
- longitude(type=double, dimensions=[y]) // each step along the y dimension corresponds to new longitude
- level(type=int, dimensions=[z]) // each step along the z dimension corresponds to new step higher in the atmosphere

- temperature(type=float, dimensions=[x,y,z]) // at each (x,y,z) coordinate we have a temperature observation/prediction
- pressure(type=float, dimensions=[x,y,z]) // at each (x,y,z) coordinate we have a pressure observation/prediction
```

This library allows clients to quickly read these variables and organize them into simple objects for more natural use
in an application:

```java
record Measurement(double latitude, double longitude, int level, float temperature,
                   float pressure) { /* Plus a builder */
}

var binding = SchemaBinding.<Measurement.Builder>builder()
        .doubleDimensionVariable("x", "latitude", Measurement.Builder::latitude)
        .doubleDimensionVariable("y", "longitude", Measurement.Builder::longitude)
        .intDimensionVariable("z", "level", Measurement.Builder::level)
        .floatCoordinateVariable("temperature", Measurement.Builder::temperature)
        .floatCoordinateVariable("pressure", Measurement.Builder::pressure)
        .build();

// To read alternate file types add the appropriate runtime dependency, e.g. runtimeOnly(edu.ucar:grib) for grib2
NetcdfFile file = NetcdfFiles.open("/path/to/some/file.nc");

Stream<Measurement> measurements = NetcdfRecordReader.schemaBound(binding).read(file)
        .map(Measurement.Builder::build);
```

### Notes

- To resolve the transitive ucar dependencies you'll need to configure their repository as a source (see
  the `lib/build.gradle.kts` of this project)
- This repo is published under the artifact `io.github.stellarsunset:netcdf`, see releases for versions
- The additional Java artifacts for reading alternate file types can be
  found [on the ucar site](https://docs.unidata.ucar.edu/netcdf-java/current/userguide/using_netcdf_java_artifacts.html)

### TODOs

- Add support for dimension range restrictions
- Coordinate transform extractors (e.g. [lambertian](https://en.wikipedia.org/wiki/Lambert_conformal_conic_projection))