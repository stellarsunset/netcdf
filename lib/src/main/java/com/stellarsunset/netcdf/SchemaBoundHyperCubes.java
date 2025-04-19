package com.stellarsunset.netcdf;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Container class for creating {@link Hypercube} instances from {@link SchemaBinding}s.
 */
class SchemaBoundHyperCubes {

    /**
     * Creates a new {@link Hypercube} using the {@link ValidatedBinding} that can be queried by index (i.e. x,y,z) for
     * records of type {@code T}.
     *
     * @param binding the validated schema binding containing the field bindings and paired {@link NetcdfFile}
     */
    static <T> Hypercube<T> make(ValidatedBinding<T> binding) {
        return switch (binding) {
            case ValidatedBinding.D0<T> d0 ->
                    throw new IllegalArgumentException("D0 hypercubes aren't supported... why would you need one?");
            case ValidatedBinding.D1<T> d1 -> makeD1(d1);
            case ValidatedBinding.D2<T> d2 -> makeD2(d2);
            case ValidatedBinding.D3<T> d3 -> makeD3(d3);
            case ValidatedBinding.D4<T> d4 -> makeD4(d4);
        };
    }

    private static <T> Hypercube.D1<T> makeD1(ValidatedBinding.D1<T> binding) {

        NetcdfFile file = binding.context();
        SchemaBinding<T> schema = binding.schema();

        IndexBinding.D1<T> coordinates = schema.coordinateVariables().entrySet().stream()
                .map(entry -> createD1Binding(
                        requireNonNull(file.findVariable(entry.getKey()), "Missing required variable, check validation logic."),
                        entry.getValue()))
                .reduce(IndexBinding.D1.noop(), IndexBinding.D1::combine);

        IndexBinding.D1<T> dimension = combineDimensionVariableBindings(file, schema.dimensionVariables());

        return new D1<>(
                schema.recordInitializer().demoteExceptions(),
                dimension,
                binding.max(),
                coordinates,
                schema.recordFinalizer().demoteExceptions()
        );
    }

    private static <T> IndexBinding.D1<T> combineDimensionVariableBindings(
            NetcdfFile file,
            Map<String, FieldBinding<T>> dimensionVariables
    ) {
        return dimensionVariables.entrySet().stream()
                .map(entry -> createD1Binding(
                        requireNonNull(file.findVariable(entry.getKey()), "Missing required dimension, check validation logic."),
                        entry.getValue()))
                .reduce(IndexBinding.D1.noop(), IndexBinding.D1::combine);
    }

    private static <T> IndexBinding.D1<T> createD1Binding(Variable variable, FieldBinding<T> fieldBinding) {
        try {
            Array.D1 array = (Array.D1) Array.wrap(variable.read().reduce());
            return array.bindIndex(fieldBinding);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Binding should have already been validated...", e);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error reading variable %s's data from underlying file.", variable.getFullName()), e);
        }
    }

    private static <T> Hypercube.D2<T> makeD2(ValidatedBinding.D2<T> binding) {

        NetcdfFile file = binding.context();
        SchemaBinding<T> schema = binding.schema();

        IndexBinding.D2<T> coordinates = schema.coordinateVariables().entrySet().stream()
                .map(entry -> createD2Binding(
                        requireNonNull(file.findVariable(entry.getKey()), "Missing required variable, check validation logic."),
                        entry.getValue()))
                .reduce(IndexBinding.D2.noop(), IndexBinding.D2::combine);

        IndexBinding.D1<T> d0 = combineDimensionVariableBindings(file, binding.d0DimensionVariables());
        IndexBinding.D1<T> d1 = combineDimensionVariableBindings(file, binding.d1DimensionVariables());

        return new D2<>(
                schema.recordInitializer().demoteExceptions(),
                d0,
                binding.d0Max(),
                d1,
                binding.d1Max(),
                coordinates,
                schema.recordFinalizer().demoteExceptions()
        );
    }

    private static <T> IndexBinding.D2<T> createD2Binding(Variable variable, FieldBinding<T> fieldBinding) {
        try {
            Array.D2 array = (Array.D2) Array.wrap(variable.read().reduce());
            return array.bindIndex(fieldBinding);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Binding should have already been validated...", e);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error reading variable %s's data from underlying file.", variable.getFullName()), e);
        }
    }

    private static <T> Hypercube.D3<T> makeD3(ValidatedBinding.D3<T> binding) {
        NetcdfFile file = binding.context();
        SchemaBinding<T> schema = binding.schema();

        IndexBinding.D3<T> coordinates = schema.coordinateVariables().entrySet().stream()
                .map(entry -> createD3Binding(
                        requireNonNull(file.findVariable(entry.getKey()), "Missing required variable, check validation logic."),
                        entry.getValue()))
                .reduce(IndexBinding.D3.noop(), IndexBinding.D3::combine);

        IndexBinding.D1<T> d0 = combineDimensionVariableBindings(file, binding.d0DimensionVariables());
        IndexBinding.D1<T> d1 = combineDimensionVariableBindings(file, binding.d1DimensionVariables());
        IndexBinding.D1<T> d2 = combineDimensionVariableBindings(file, binding.d2DimensionVariables());

        return new D3<>(
                schema.recordInitializer().demoteExceptions(),
                d0,
                binding.d0Max(),
                d1,
                binding.d1Max(),
                d2,
                binding.d2Max(),
                coordinates,
                schema.recordFinalizer().demoteExceptions()
        );
    }

    private static <T> IndexBinding.D3<T> createD3Binding(Variable variable, FieldBinding<T> fieldBinding) {
        try {
            Array.D3 array = (Array.D3) Array.wrap(variable.read().reduce());
            return array.bindIndex(fieldBinding);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Binding should have already been validated...", e);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error reading variable %s's data from underlying file.", variable.getFullName()), e);
        }
    }

    private static <T> Hypercube.D4<T> makeD4(ValidatedBinding.D4<T> binding) {
        NetcdfFile file = binding.context();
        SchemaBinding<T> schema = binding.schema();

        IndexBinding.D4<T> coordinates = schema.coordinateVariables().entrySet().stream()
                .map(entry -> createD4Binding(
                        requireNonNull(file.findVariable(entry.getKey()), "Missing required variable, check validation logic."),
                        entry.getValue()))
                .reduce(IndexBinding.D4.noop(), IndexBinding.D4::combine);

        IndexBinding.D1<T> d0 = combineDimensionVariableBindings(file, binding.d0DimensionVariables());
        IndexBinding.D1<T> d1 = combineDimensionVariableBindings(file, binding.d1DimensionVariables());
        IndexBinding.D1<T> d2 = combineDimensionVariableBindings(file, binding.d2DimensionVariables());
        IndexBinding.D1<T> d3 = combineDimensionVariableBindings(file, binding.d3DimensionVariables());

        return new D4<>(
                schema.recordInitializer().demoteExceptions(),
                d0,
                binding.d0Max(),
                d1,
                binding.d1Max(),
                d2,
                binding.d2Max(),
                d3,
                binding.d3Max(),
                coordinates,
                schema.recordFinalizer().demoteExceptions()
        );
    }

    private static <T> IndexBinding.D4<T> createD4Binding(Variable variable, FieldBinding<T> fieldBinding) {
        try {
            Array.D4 array = (Array.D4) Array.wrap(variable.read().reduce());
            return array.bindIndex(fieldBinding);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Binding should have already been validated...", e);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Error reading variable %s's data from underlying file.", variable.getFullName()), e);
        }
    }

    /**
     * 1-Dimensional hypercube implementation, meant for reading simple linear variables (e.g. time) from the underlying
     * NetCDF file standalone.
     *
     * <p>Prefer instantiation via {@link Hypercube#schemaBound(NetcdfFile, SchemaBinding)}.
     */
    record D1<T>(Supplier<T> initializer, IndexBinding.D1<T> d0Setter, int max, IndexBinding.D1<T> coordinatesSetter,
                 Consumer<T> finalizer) implements Hypercube.D1<T> {

        @Override
        public T read(int i) {
            T r = coordinatesSetter.set(d0Setter.set(initializer.get(), i), i);
            finalizer.accept(r);
            return r;
        }

        @Override
        public void close() {
        }
    }

    record D2<T>(Supplier<T> initializer, IndexBinding.D1<T> d0Setter, int d0Max, IndexBinding.D1<T> d1Setter,
                 int d1Max, IndexBinding.D2<T> coordinatesSetter, Consumer<T> finalizer) implements Hypercube.D2<T> {

        @Override
        public T read(int i0, int i1) {
            T r = coordinatesSetter.set(d0Setter.set(d1Setter.set(initializer.get(), i1), i0), i0, i1);
            finalizer.accept(r);
            return r;
        }

        @Override
        public void close() {
        }
    }

    record D3<T>(Supplier<T> initializer, IndexBinding.D1<T> d0Setter, int d0Max, IndexBinding.D1<T> d1Setter,
                 int d1Max, IndexBinding.D1<T> d2Setter, int d2Max, IndexBinding.D3<T> coordinatesSetter,
                 Consumer<T> finalizer) implements Hypercube.D3<T> {

        @Override
        public T read(int i0, int i1, int i2) {
            T r = coordinatesSetter.set(d0Setter.set(d1Setter.set(d2Setter.set(initializer.get(), i2), i1), i0), i0, i1, i2);
            finalizer.accept(r);
            return r;
        }

        @Override
        public void close() {
        }
    }

    record D4<T>(Supplier<T> initializer, IndexBinding.D1<T> d0Setter, int d0Max, IndexBinding.D1<T> d1Setter,
                 int d1Max, IndexBinding.D1<T> d2Setter, int d2Max, IndexBinding.D1<T> d3Setter, int d3Max,
                 IndexBinding.D4<T> coordinatesSetter, Consumer<T> finalizer) implements Hypercube.D4<T> {

        public T read(int x, int y, int z, int t) {
            T r = coordinatesSetter.set(d0Setter.set(d1Setter.set(d2Setter.set(d3Setter.set(initializer.get(), t), z), y), x), x, y, z, t);
            finalizer.accept(r);
            return r;
        }

        @Override
        public void close() {
        }
    }
}
