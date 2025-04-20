package io.github.stellarsunset.netcdf;

import ucar.ma2.*;

/**
 * This interface hides the NetCDF {@link ucar.ma2.Array} type from the code in the repo adding two features we want when
 * using these downstream.
 * <ol>
 *     <li>Sealing - we want compile-time errors if any of the library code doesn't handle some new array primitive type</li>
 *     <li>Re-Organization - this re-organizes the Array class hierarchy to put dimensionality before type, the inverse
 *     of in the underlying ucar library, this makes some operations we want to do in-repo, simpler</li>
 *     <li>Specific Functionality - bonus methods integrating with {@link FieldBinding}s</li>
 * </ol>
 */
sealed interface Array {

    /**
     * Wrap the provided UCAR array instance as sealed array type in the codebase to simplify interactions between types
     * and dimensions of arrays in-repo.
     */
    static Array wrap(ucar.ma2.Array delegate) {
        return switch (delegate) {
            case ArrayByte.D0 d0 -> new D0.Byte(d0);
            case ArrayByte.D1 d1 -> new D1.Byte(d1);
            case ArrayByte.D2 d2 -> new D2.Byte(d2);
            case ArrayByte.D3 d3 -> new D3.Byte(d3);
            case ArrayByte.D4 d4 -> new D4.Byte(d4);
            case ArrayBoolean.D0 d0 -> new D0.Bool(d0);
            case ArrayBoolean.D1 d1 -> new D1.Bool(d1);
            case ArrayBoolean.D2 d2 -> new D2.Bool(d2);
            case ArrayBoolean.D3 d3 -> new D3.Bool(d3);
            case ArrayBoolean.D4 d4 -> new D4.Bool(d4);
            case ArrayChar.D0 d0 -> new D0.Char(d0);
            case ArrayChar.D1 d1 -> new D1.Char(d1);
            case ArrayChar.D2 d2 -> new D2.Char(d2);
            case ArrayChar.D3 d3 -> new D3.Char(d3);
            case ArrayChar.D4 d4 -> new D4.Char(d4);
            case ArrayDouble.D0 d0 -> new D0.Double(d0);
            case ArrayDouble.D1 d1 -> new D1.Double(d1);
            case ArrayDouble.D2 d2 -> new D2.Double(d2);
            case ArrayDouble.D3 d3 -> new D3.Double(d3);
            case ArrayDouble.D4 d4 -> new D4.Double(d4);
            case ArrayFloat.D0 d0 -> new D0.Float(d0);
            case ArrayFloat.D1 d1 -> new D1.Float(d1);
            case ArrayFloat.D2 d2 -> new D2.Float(d2);
            case ArrayFloat.D3 d3 -> new D3.Float(d3);
            case ArrayFloat.D4 d4 -> new D4.Float(d4);
            case ArrayInt.D0 d0 -> new D0.Int(d0);
            case ArrayInt.D1 d1 -> new D1.Int(d1);
            case ArrayInt.D2 d2 -> new D2.Int(d2);
            case ArrayInt.D3 d3 -> new D3.Int(d3);
            case ArrayInt.D4 d4 -> new D4.Int(d4);
            case ArrayLong.D0 d0 -> new D0.Long(d0);
            case ArrayLong.D1 d1 -> new D1.Long(d1);
            case ArrayLong.D2 d2 -> new D2.Long(d2);
            case ArrayLong.D3 d3 -> new D3.Long(d3);
            case ArrayLong.D4 d4 -> new D4.Long(d4);
            case ArrayShort.D0 d0 -> new D0.Short(d0);
            case ArrayShort.D1 d1 -> new D1.Short(d1);
            case ArrayShort.D2 d2 -> new D2.Short(d2);
            case ArrayShort.D3 d3 -> new D3.Short(d3);
            case ArrayShort.D4 d4 -> new D4.Short(d4);
            default -> throw new UnsupportedArrayTypeException(delegate);
        };
    }

    class UnsupportedArrayTypeException extends RuntimeException {
        UnsupportedArrayTypeException(ucar.ma2.Array array) {
            super(
                    String.format(
                            "Unsupported Array type for wrapping: %s. Feel free to add a mapping for this.",
                            array.getClass()
                    )
            );
        }
    }

    static <S, T extends S> T getAsOrThrow(S s, Class<T> clz) {
        if (clz.isInstance(s)) {
            return (T) s;
        }
        var message = String.format(
                "Unable to downcast %s to %s",
                s.getClass(),
                clz
        );
        throw new IllegalArgumentException(message);
    }

    sealed interface D0 extends Array {

        <T> IndexBinding.D0<T> bindIndex(FieldBinding<T> field);

        record Byte(ArrayByte.D0 delegate) implements D0 {

            public byte read() {
                return delegate.get();
            }

            @Override
            public <T> IndexBinding.D0<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Byte<T> b = getAsOrThrow(field, FieldBinding.Byte.class);
                return object -> b.accept(object, delegate.get());
            }
        }

        record Bool(ArrayBoolean.D0 delegate) implements D0 {

            public boolean read() {
                return delegate.get();
            }

            @Override
            public <T> IndexBinding.D0<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Bool<T> b = getAsOrThrow(field, FieldBinding.Bool.class);
                return object -> b.accept(object, delegate.get());
            }
        }

        record Char(ArrayChar.D0 delegate) implements D0 {

            public char read() {
                return delegate.get();
            }

            @Override
            public <T> IndexBinding.D0<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Char<T> c = getAsOrThrow(field, FieldBinding.Char.class);
                return object -> c.accept(object, delegate.get());
            }
        }

        record Double(ArrayDouble.D0 delegate) implements D0 {

            public double read() {
                return delegate.get();
            }

            @Override
            public <T> IndexBinding.D0<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Double<T> c = getAsOrThrow(field, FieldBinding.Double.class);
                return object -> c.accept(object, delegate.get());
            }
        }

        record Float(ArrayFloat.D0 delegate) implements D0 {

            public float read() {
                return delegate.get();
            }

            @Override
            public <T> IndexBinding.D0<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Float<T> c = getAsOrThrow(field, FieldBinding.Float.class);
                return object -> c.accept(object, delegate.get());
            }
        }

        record Int(ArrayInt.D0 delegate) implements D0 {

            public int read() {
                return delegate.get();
            }

            @Override
            public <T> IndexBinding.D0<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Int<T> c = getAsOrThrow(field, FieldBinding.Int.class);
                return object -> c.accept(object, delegate.get());
            }
        }

        record Long(ArrayLong.D0 delegate) implements D0 {

            public long read() {
                return delegate.get();
            }

            @Override
            public <T> IndexBinding.D0<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Long<T> c = getAsOrThrow(field, FieldBinding.Long.class);
                return object -> c.accept(object, delegate.get());
            }
        }

        record Short(ArrayShort.D0 delegate) implements D0 {

            public short read() {
                return delegate.get();
            }

            @Override
            public <T> IndexBinding.D0<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Short<T> c = getAsOrThrow(field, FieldBinding.Short.class);
                return object -> c.accept(object, delegate.get());
            }
        }
    }

    sealed interface D1 extends Array {

        <T> IndexBinding.D1<T> bindIndex(FieldBinding<T> field);

        record Byte(ArrayByte.D1 delegate) implements D1 {

            public byte read(int i) {
                return delegate.get(i);
            }

            @Override
            public <T> IndexBinding.D1<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Byte<T> b = getAsOrThrow(field, FieldBinding.Byte.class);
                return (object, i) -> b.accept(object, delegate.get(i));
            }
        }

        record Bool(ArrayBoolean.D1 delegate) implements D1 {

            public boolean read(int i) {
                return delegate.get(i);
            }

            @Override
            public <T> IndexBinding.D1<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Bool<T> b = getAsOrThrow(field, FieldBinding.Bool.class);
                return (object, i) -> b.accept(object, delegate.get(i));
            }
        }

        record Char(ArrayChar.D1 delegate) implements D1 {

            public char read(int i) {
                return delegate.get(i);
            }

            @Override
            public <T> IndexBinding.D1<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Char<T> c = getAsOrThrow(field, FieldBinding.Char.class);
                return (object, i) -> c.accept(object, delegate.get(i));
            }
        }

        record Double(ArrayDouble.D1 delegate) implements D1 {

            public double read(int i) {
                return delegate.get(i);
            }

            @Override
            public <T> IndexBinding.D1<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Double<T> c = getAsOrThrow(field, FieldBinding.Double.class);
                return (object, i) -> c.accept(object, delegate.get(i));
            }
        }

        record Float(ArrayFloat.D1 delegate) implements D1 {

            public float read(int i) {
                return delegate.get(i);
            }

            @Override
            public <T> IndexBinding.D1<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Float<T> c = getAsOrThrow(field, FieldBinding.Float.class);
                return (object, i) -> c.accept(object, delegate.get(i));
            }
        }

        record Int(ArrayInt.D1 delegate) implements D1 {

            public int read(int i) {
                return delegate.get(i);
            }

            @Override
            public <T> IndexBinding.D1<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Int<T> c = getAsOrThrow(field, FieldBinding.Int.class);
                return (object, i) -> c.accept(object, delegate.get(i));
            }
        }

        record Long(ArrayLong.D1 delegate) implements D1 {

            public long read(int i) {
                return delegate.get(i);
            }

            @Override
            public <T> IndexBinding.D1<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Long<T> c = getAsOrThrow(field, FieldBinding.Long.class);
                return (object, i) -> c.accept(object, delegate.get(i));
            }
        }

        record Short(ArrayShort.D1 delegate) implements D1 {

            public short read(int i) {
                return delegate.get(i);
            }

            @Override
            public <T> IndexBinding.D1<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Short<T> c = getAsOrThrow(field, FieldBinding.Short.class);
                return (object, i) -> c.accept(object, delegate.get(i));
            }
        }
    }

    sealed interface D2 extends Array {

        <T> IndexBinding.D2<T> bindIndex(FieldBinding<T> field);

        record Byte(ArrayByte.D2 delegate) implements D2 {

            public byte read(int i, int j) {
                return delegate.get(i, j);
            }

            @Override
            public <T> IndexBinding.D2<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Byte<T> b = getAsOrThrow(field, FieldBinding.Byte.class);
                return (object, i, j) -> b.accept(object, delegate.get(i, j));
            }
        }

        record Bool(ArrayBoolean.D2 delegate) implements D2 {

            public boolean read(int i, int j) {
                return delegate.get(i, j);
            }

            @Override
            public <T> IndexBinding.D2<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Bool<T> b = getAsOrThrow(field, FieldBinding.Bool.class);
                return (object, i, j) -> b.accept(object, delegate.get(i, j));
            }
        }

        record Char(ArrayChar.D2 delegate) implements D2 {

            public char read(int i, int j) {
                return delegate.get(i, j);
            }

            @Override
            public <T> IndexBinding.D2<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Char<T> c = getAsOrThrow(field, FieldBinding.Char.class);
                return (object, i, j) -> c.accept(object, delegate.get(i, j));
            }
        }

        record Double(ArrayDouble.D2 delegate) implements D2 {

            public double read(int i, int j) {
                return delegate.get(i, j);
            }

            @Override
            public <T> IndexBinding.D2<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Double<T> c = getAsOrThrow(field, FieldBinding.Double.class);
                return (object, i, j) -> c.accept(object, delegate.get(i, j));
            }
        }

        record Float(ArrayFloat.D2 delegate) implements D2 {

            public float read(int i, int j) {
                return delegate.get(i, j);
            }

            @Override
            public <T> IndexBinding.D2<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Float<T> c = getAsOrThrow(field, FieldBinding.Float.class);
                return (object, i, j) -> c.accept(object, delegate.get(i, j));
            }
        }

        record Int(ArrayInt.D2 delegate) implements D2 {

            public int read(int i, int j) {
                return delegate.get(i, j);
            }

            @Override
            public <T> IndexBinding.D2<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Int<T> c = getAsOrThrow(field, FieldBinding.Int.class);
                return (object, i, j) -> c.accept(object, delegate.get(i, j));
            }
        }

        record Long(ArrayLong.D2 delegate) implements D2 {

            public long read(int i, int j) {
                return delegate.get(i, j);
            }

            @Override
            public <T> IndexBinding.D2<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Long<T> c = getAsOrThrow(field, FieldBinding.Long.class);
                return (object, i, j) -> c.accept(object, delegate.get(i, j));
            }
        }

        record Short(ArrayShort.D2 delegate) implements D2 {

            public short read(int i, int j) {
                return delegate.get(i, j);
            }

            @Override
            public <T> IndexBinding.D2<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Short<T> c = getAsOrThrow(field, FieldBinding.Short.class);
                return (object, i, j) -> c.accept(object, delegate.get(i, j));
            }
        }
    }

    sealed interface D3 extends Array {

        <T> IndexBinding.D3<T> bindIndex(FieldBinding<T> field);

        record Byte(ArrayByte.D3 delegate) implements D3 {

            public byte read(int i, int j, int k) {
                return delegate.get(i, j, k);
            }

            @Override
            public <T> IndexBinding.D3<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Byte<T> b = getAsOrThrow(field, FieldBinding.Byte.class);
                return (object, i, j, k) -> b.accept(object, delegate.get(i, j, k));
            }
        }

        record Bool(ArrayBoolean.D3 delegate) implements D3 {

            public boolean read(int i, int j, int k) {
                return delegate.get(i, j, k);
            }

            @Override
            public <T> IndexBinding.D3<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Bool<T> b = getAsOrThrow(field, FieldBinding.Bool.class);
                return (object, i, j, k) -> b.accept(object, delegate.get(i, j, k));
            }
        }

        record Char(ArrayChar.D3 delegate) implements D3 {

            public char read(int i, int j, int k) {
                return delegate.get(i, j, k);
            }

            @Override
            public <T> IndexBinding.D3<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Char<T> c = getAsOrThrow(field, FieldBinding.Char.class);
                return (object, i, j, k) -> c.accept(object, delegate.get(i, j, k));
            }
        }

        record Double(ArrayDouble.D3 delegate) implements D3 {

            public double read(int i, int j, int k) {
                return delegate.get(i, j, k);
            }

            @Override
            public <T> IndexBinding.D3<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Double<T> c = getAsOrThrow(field, FieldBinding.Double.class);
                return (object, i, j, k) -> c.accept(object, delegate.get(i, j, k));
            }
        }

        record Float(ArrayFloat.D3 delegate) implements D3 {

            public float read(int i, int j, int k) {
                return delegate.get(i, j, k);
            }

            @Override
            public <T> IndexBinding.D3<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Float<T> c = getAsOrThrow(field, FieldBinding.Float.class);
                return (object, i, j, k) -> c.accept(object, delegate.get(i, j, k));
            }
        }

        record Int(ArrayInt.D3 delegate) implements D3 {

            public int read(int i, int j, int k) {
                return delegate.get(i, j, k);
            }

            @Override
            public <T> IndexBinding.D3<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Int<T> c = getAsOrThrow(field, FieldBinding.Int.class);
                return (object, i, j, k) -> c.accept(object, delegate.get(i, j, k));
            }
        }

        record Long(ArrayLong.D3 delegate) implements D3 {

            public long read(int i, int j, int k) {
                return delegate.get(i, j, k);
            }

            @Override
            public <T> IndexBinding.D3<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Long<T> c = getAsOrThrow(field, FieldBinding.Long.class);
                return (object, i, j, k) -> c.accept(object, delegate.get(i, j, k));
            }
        }

        record Short(ArrayShort.D3 delegate) implements D3 {

            public short read(int i, int j, int k) {
                return delegate.get(i, j, k);
            }

            @Override
            public <T> IndexBinding.D3<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Short<T> c = getAsOrThrow(field, FieldBinding.Short.class);
                return (object, i, j, k) -> c.accept(object, delegate.get(i, j, k));
            }
        }
    }

    sealed interface D4 extends Array {

        <T> IndexBinding.D4<T> bindIndex(FieldBinding<T> field);

        record Byte(ArrayByte.D4 delegate) implements D4 {

            public byte read(int i, int j, int k, int u) {
                return delegate.get(i, j, k, u);
            }

            @Override
            public <T> IndexBinding.D4<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Byte<T> b = getAsOrThrow(field, FieldBinding.Byte.class);
                return (object, i, j, k, u) -> b.accept(object, delegate.get(i, j, k, u));
            }
        }

        record Bool(ArrayBoolean.D4 delegate) implements D4 {

            public boolean read(int i, int j, int k, int u) {
                return delegate.get(i, j, k, u);
            }

            @Override
            public <T> IndexBinding.D4<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Bool<T> b = getAsOrThrow(field, FieldBinding.Bool.class);
                return (object, i, j, k, u) -> b.accept(object, delegate.get(i, j, k, u));
            }
        }

        record Char(ArrayChar.D4 delegate) implements D4 {

            public char read(int i, int j, int k, int u) {
                return delegate.get(i, j, k, u);
            }

            @Override
            public <T> IndexBinding.D4<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Char<T> c = getAsOrThrow(field, FieldBinding.Char.class);
                return (object, i, j, k, u) -> c.accept(object, delegate.get(i, j, k, u));
            }
        }

        record Double(ArrayDouble.D4 delegate) implements D4 {

            public double read(int i, int j, int k, int u) {
                return delegate.get(i, j, k, u);
            }

            @Override
            public <T> IndexBinding.D4<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Double<T> c = getAsOrThrow(field, FieldBinding.Double.class);
                return (object, i, j, k, u) -> c.accept(object, delegate.get(i, j, k, u));
            }
        }

        record Float(ArrayFloat.D4 delegate) implements D4 {

            public float read(int i, int j, int k, int u) {
                return delegate.get(i, j, k, u);
            }

            @Override
            public <T> IndexBinding.D4<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Float<T> c = getAsOrThrow(field, FieldBinding.Float.class);
                return (object, i, j, k, u) -> c.accept(object, delegate.get(i, j, k, u));
            }
        }

        record Int(ArrayInt.D4 delegate) implements D4 {

            public int read(int i, int j, int k, int u) {
                return delegate.get(i, j, k, u);
            }

            @Override
            public <T> IndexBinding.D4<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Int<T> c = getAsOrThrow(field, FieldBinding.Int.class);
                return (object, i, j, k, u) -> c.accept(object, delegate.get(i, j, k, u));
            }
        }

        record Long(ArrayLong.D4 delegate) implements D4 {

            public long read(int i, int j, int k, int u) {
                return delegate.get(i, j, k, u);
            }

            @Override
            public <T> IndexBinding.D4<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Long<T> c = getAsOrThrow(field, FieldBinding.Long.class);
                return (object, i, j, k, u) -> c.accept(object, delegate.get(i, j, k, u));
            }
        }

        record Short(ArrayShort.D4 delegate) implements D4 {

            public short read(int i, int j, int k, int u) {
                return delegate.get(i, j, k, u);
            }

            @Override
            public <T> IndexBinding.D4<T> bindIndex(FieldBinding<T> field) {
                FieldBinding.Short<T> c = getAsOrThrow(field, FieldBinding.Short.class);
                return (object, i, j, k, u) -> c.accept(object, delegate.get(i, j, k, u));
            }
        }
    }
}
