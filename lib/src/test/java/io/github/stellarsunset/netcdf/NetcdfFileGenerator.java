package io.github.stellarsunset.netcdf;

import java.io.File;
import java.io.IOException;
import java.util.List;
import ucar.ma2.ArrayInt;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.ma2.InvalidRangeException;
import ucar.nc2.Dimension;
import ucar.nc2.Variable;
import ucar.nc2.write.NetcdfFormatWriter;

/**
 * Simple NC3-format file generator allowing 2/3/4D int dimensions and an arbitrary number of
 * variables.
 *
 * <p>Here to make it easy to write simple unit tests.
 */
sealed interface NetcdfFileGenerator {

  static VariableSpec varSpec(String name, DataType type) {
    return new VariableSpec(name, type);
  }

  /**
   * Write the provided variables to a file along the dimensions pre-configured when the generator
   * was created.
   *
   * @param file the file to write the variables to
   * @param variables the variables to write to the file
   */
  void writeVariables(File file, VariableSpec... variables);

  record VariableSpec(String name, DataType type) {}

  record X(int maxX) implements NetcdfFileGenerator {

    @Override
    public void writeVariables(File file, VariableSpec... variables) {

      NetcdfFormatWriter.Builder builder =
          NetcdfFormatWriter.createNewNetcdf3(file.getAbsolutePath());

      Dimension dimX = builder.addDimension("x", maxX);
      builder.addVariable("x", DataType.INT, List.of(dimX));

      for (VariableSpec spec : variables) {
        builder.addVariable(spec.name(), spec.type(), List.of(dimX));
      }

      builder.setFill(true);

      ArrayInt dataX = makeDimensionArray(maxX);

      try (NetcdfFormatWriter writer = builder.build()) {

        Variable varX = writer.findVariable("x");
        writer.write(varX, dataX);

      } catch (InvalidRangeException e) {
        throw new IllegalArgumentException("Bad range for write.", e);
      } catch (IOException e) {
        throw new RuntimeException("IO error occurred during write.", e);
      }
    }

    private ArrayInt makeDimensionArray(int max) {

      ArrayInt data = new ArrayInt.D1(max, false);
      Index index = data.getIndex();

      for (int i = 0; i < data.getShape()[0]; i++) {
        data.set(index.set(i), i);
      }

      return data;
    }
  }

  record Xy(int maxX, int maxY) implements NetcdfFileGenerator {

    @Override
    public void writeVariables(File file, VariableSpec... variables) {

      NetcdfFormatWriter.Builder builder =
          NetcdfFormatWriter.createNewNetcdf3(file.getAbsolutePath());

      Dimension dimX = builder.addDimension("x", maxX);
      Dimension dimY = builder.addDimension("y", maxY);

      builder.addVariable("x", DataType.INT, List.of(dimX));
      builder.addVariable("y", DataType.INT, List.of(dimY));

      for (VariableSpec spec : variables) {
        builder.addVariable(spec.name(), spec.type(), List.of(dimX, dimY));
      }

      builder.setFill(true);

      ArrayInt dataX = makeDimensionArray(maxX);
      ArrayInt dataY = makeDimensionArray(maxY);

      try (NetcdfFormatWriter writer = builder.build()) {

        Variable varX = writer.findVariable("x");
        writer.write(varX, dataX);

        Variable varY = writer.findVariable("y");
        writer.write(varY, dataY);

      } catch (InvalidRangeException e) {
        throw new IllegalArgumentException("Bad range for write.", e);
      } catch (IOException e) {
        throw new RuntimeException("IO error occurred during write.", e);
      }
    }

    private ArrayInt makeDimensionArray(int max) {

      ArrayInt data = new ArrayInt.D1(max, false);
      Index index = data.getIndex();

      for (int i = 0; i < data.getShape()[0]; i++) {
        data.set(index.set(i), i);
      }

      return data;
    }
  }

  record Xyz(int maxX, int maxY, int maxZ) implements NetcdfFileGenerator {

    @Override
    public void writeVariables(File file, VariableSpec... variables) {

      NetcdfFormatWriter.Builder builder =
          NetcdfFormatWriter.createNewNetcdf3(file.getAbsolutePath());

      Dimension dimX = builder.addDimension("x", maxX);
      Dimension dimY = builder.addDimension("y", maxY);
      Dimension dimZ = builder.addDimension("z", maxZ);

      builder.addVariable("x", DataType.INT, List.of(dimX));
      builder.addVariable("y", DataType.INT, List.of(dimY));
      builder.addVariable("z", DataType.INT, List.of(dimZ));

      for (VariableSpec spec : variables) {
        builder.addVariable(spec.name(), spec.type(), List.of(dimX, dimY, dimZ));
      }

      builder.setFill(true);

      ArrayInt dataX = makeDimensionArray(maxX);
      ArrayInt dataY = makeDimensionArray(maxY);
      ArrayInt dataZ = makeDimensionArray(maxZ);

      try (NetcdfFormatWriter writer = builder.build()) {

        Variable varX = writer.findVariable("x");
        writer.write(varX, dataX);

        Variable varY = writer.findVariable("y");
        writer.write(varY, dataY);

        Variable varZ = writer.findVariable("z");
        writer.write(varZ, dataZ);

      } catch (InvalidRangeException e) {
        throw new IllegalArgumentException("Bad range for write.", e);
      } catch (IOException e) {
        throw new RuntimeException("IO error occurred during write.", e);
      }
    }

    private ArrayInt makeDimensionArray(int max) {

      ArrayInt data = new ArrayInt.D1(max, false);
      Index index = data.getIndex();

      for (int i = 0; i < data.getShape()[0]; i++) {
        data.set(index.set(i), i);
      }

      return data;
    }
  }

  record Xyzt(int maxX, int maxY, int maxZ, int maxT) implements NetcdfFileGenerator {

    @Override
    public void writeVariables(File file, VariableSpec... variables) {

      NetcdfFormatWriter.Builder builder =
          NetcdfFormatWriter.createNewNetcdf3(file.getAbsolutePath());

      Dimension dimX = builder.addDimension("x", maxX);
      Dimension dimY = builder.addDimension("y", maxY);
      Dimension dimZ = builder.addDimension("z", maxZ);
      Dimension dimT = builder.addDimension("t", maxT);

      builder.addVariable("x", DataType.INT, List.of(dimX));
      builder.addVariable("y", DataType.INT, List.of(dimY));
      builder.addVariable("z", DataType.INT, List.of(dimZ));
      builder.addVariable("t", DataType.INT, List.of(dimT));

      for (VariableSpec spec : variables) {
        builder.addVariable(spec.name(), spec.type(), List.of(dimX, dimY, dimZ, dimT));
      }

      builder.setFill(true);

      ArrayInt dataX = makeDimensionArray(maxX);
      ArrayInt dataY = makeDimensionArray(maxY);
      ArrayInt dataZ = makeDimensionArray(maxZ);
      ArrayInt dataT = makeDimensionArray(maxT);

      try (NetcdfFormatWriter writer = builder.build()) {

        Variable varX = writer.findVariable("x");
        writer.write(varX, dataX);

        Variable varY = writer.findVariable("y");
        writer.write(varY, dataY);

        Variable varZ = writer.findVariable("z");
        writer.write(varZ, dataZ);

        Variable varT = writer.findVariable("t");
        writer.write(varT, dataT);

      } catch (InvalidRangeException e) {
        throw new IllegalArgumentException("Bad range for write.", e);
      } catch (IOException e) {
        throw new RuntimeException("IO error occurred during write.", e);
      }
    }

    private ArrayInt makeDimensionArray(int max) {

      ArrayInt data = new ArrayInt.D1(max, false);
      Index index = data.getIndex();

      for (int i = 0; i < data.getShape()[0]; i++) {
        data.set(index.set(i), i);
      }

      return data;
    }
  }
}
