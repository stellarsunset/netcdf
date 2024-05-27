# NetCDF

A thin wrapper around [NetCDF-Java](https://docs.unidata.ucar.edu/netcdf-java/current/userguide/index.html) for reading sub-schemas of 
variables into Java objects.

## Motivation

Take for example a simple NetCDF file with the following dimensions and variables:
```java
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

This library allows clients to quickly read these variables and organize them into simple objects for more natural use in an application:

```java
record Measurement(double latitude, double longitude, int level, float temperature, float pressure) { /* Plus a builder */ }

var binding = SchemaBinding.<Measurement.Builder>builder()
    .doubleDimensionVariable("x", "latitude", Measurement.Builder::latitude)
    .doubleDimensionVariable("y", "longitude", Measurement.Builder::longitude)
    .intDimensionVariable("z", "level", Measurement.Builder::level)
    .floatCoordinateVariable("temperature", Measurement.Builder::temperature)
    .floatCoordinateVariable("pressure", Measurement.Builder::pressure)
    .build();

// For alternate file type support bind in the appropriate dependency as "runtimeOnly", e.g. edu.ucar:grib for grib2
NetcdfFile file = NetcdfFiles.open("/path/to/some/file.nc")

Stream<Measurement> measurements = NetcdfRecordReader.schemaBound(binding)
    .map(Measurement.Builder::build);
```

