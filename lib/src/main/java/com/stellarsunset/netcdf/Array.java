package com.stellarsunset.netcdf;

import ucar.ma2.ArrayBoolean;
import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayChar;

/**
 * This interface hides the NetCDF {@link ucar.ma2.Array} type from the code in the repo adding two features we want when
 * using these downstream.
 * <ol>
 *     <li>Sealing - we want compile-time errors if any of the library code doesn't handle some new array primitive type</li>
 *     <li>Re-Organization - this re-organizes the Array class hierarchy to put dimensionality before type, the inverse
 *     of in the underlying ucar library, this makes some operations we want to do in-repo, simpler</li>
 * </ol>
 */
public sealed interface Array {

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

    sealed interface D0 extends Array {

        record Byte(ArrayByte.D0 delegate) implements D0 {
            byte read() {
                return delegate.get();
            }
        }
    }

    sealed interface D1 extends Array {

        record Byte(ArrayByte.D1 delegate) implements D1 {

            public byte read(int i) {
                return delegate.get(i);
            }
        }

        record Bool(ArrayBoolean.D1 delegate) implements D1 {
            boolean read(int i) {
                return delegate.get(i);
            }
        }

        record Char(ArrayChar.D1 delegate) implements D1 {
            char read(int i) {
                return delegate.get(i);
            }
        }
    }

    sealed interface D2 extends Array {

        record Byte(ArrayByte.D2 delegate) implements D2 {
            byte read(int i0, int i1) {
                return delegate.get(i0, i1);
            }
        }
    }

    sealed interface D3 extends Array {

        record Byte(ArrayByte.D3 delegate) implements D3 {
            byte read(int i0, int i1, int i2) {
                return delegate.get(i0, i1, i2);
            }
        }
    }

    sealed interface D4 extends Array {

        record Byte(ArrayByte.D4 delegate) implements D4 {
            public byte read(int i0, int i1, int i2, int i3) {
                return delegate.get(i0, i1, i2, i3);
            }
        }
    }
}
