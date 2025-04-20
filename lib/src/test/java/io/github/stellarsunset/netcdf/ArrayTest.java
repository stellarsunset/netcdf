package io.github.stellarsunset.netcdf;

import org.junit.jupiter.api.Test;
import ucar.ma2.DataType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ArrayTest {

    @Test
    void testD0_Byte() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.BYTE, new int[]{});
        array.setByte(0, (byte) 0);

        Array.D0.Byte b = (Array.D0.Byte) Array.wrap(array);
        assertEquals((byte) 0, b.read(), "Read");
    }

    @Test
    void testD1_Byte() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.BYTE, new int[]{1});
        array.setByte(0, (byte) 0);

        Array.D1.Byte b = (Array.D1.Byte) Array.wrap(array);
        assertEquals((byte) 0, b.read(0), "Read");
    }

    @Test
    void testD2_Byte() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.BYTE, new int[]{1, 1});
        array.setByte(0, (byte) 0);

        Array.D2.Byte b = (Array.D2.Byte) Array.wrap(array);
        assertEquals((byte) 0, b.read(0, 0), "Read");
    }

    @Test
    void testD3_Byte() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.BYTE, new int[]{1, 1, 1});
        array.setByte(0, (byte) 0);

        Array.D3.Byte b = (Array.D3.Byte) Array.wrap(array);
        assertEquals((byte) 0, b.read(0, 0, 0), "Read");
    }

    @Test
    void testD4_Byte() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.BYTE, new int[]{1, 1, 1, 1});
        array.setByte(0, (byte) 0);

        Array.D4.Byte b = (Array.D4.Byte) Array.wrap(array);
        assertEquals((byte) 0, b.read(0, 0, 0, 0), "Read");
    }

    @Test
    void testD0_Bool() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.BOOLEAN, new int[]{});
        array.setBoolean(0, false);

        Array.D0.Bool b = (Array.D0.Bool) Array.wrap(array);
        assertFalse(b.read(), "Read");
    }

    @Test
    void testD1_Bool() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.BOOLEAN, new int[]{1});
        array.setBoolean(0, false);

        Array.D1.Bool b = (Array.D1.Bool) Array.wrap(array);
        assertFalse(b.read(0), "Read");
    }

    @Test
    void testD2_Bool() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.BOOLEAN, new int[]{1, 1});
        array.setBoolean(0, false);

        Array.D2.Bool b = (Array.D2.Bool) Array.wrap(array);
        assertFalse(b.read(0, 0), "Read");
    }

    @Test
    void testD3_Bool() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.BOOLEAN, new int[]{1, 1, 1});
        array.setBoolean(0, false);

        Array.D3.Bool b = (Array.D3.Bool) Array.wrap(array);
        assertFalse(b.read(0, 0, 0), "Read");
    }

    @Test
    void testD4_Bool() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.BOOLEAN, new int[]{1, 1, 1, 1});
        array.setBoolean(0, false);

        Array.D4.Bool b = (Array.D4.Bool) Array.wrap(array);
        assertFalse(b.read(0, 0, 0, 0), "Read");
    }

    @Test
    void testD0_Char() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.CHAR, new int[]{});
        array.setChar(0, 'a');

        Array.D0.Char b = (Array.D0.Char) Array.wrap(array);
        assertEquals('a', b.read(), "Read");
    }

    @Test
    void testD1_Char() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.CHAR, new int[]{1});
        array.setChar(0, 'a');

        Array.D1.Char b = (Array.D1.Char) Array.wrap(array);
        assertEquals('a', b.read(0), "Read");
    }

    @Test
    void testD2_Char() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.CHAR, new int[]{1, 1});
        array.setChar(0, 'a');

        Array.D2.Char b = (Array.D2.Char) Array.wrap(array);
        assertEquals('a', b.read(0, 0), "Read");
    }

    @Test
    void testD3_Char() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.CHAR, new int[]{1, 1, 1});
        array.setChar(0, 'a');

        Array.D3.Char b = (Array.D3.Char) Array.wrap(array);
        assertEquals('a', b.read(0, 0, 0), "Read");
    }

    @Test
    void testD4_Char() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.CHAR, new int[]{1, 1, 1, 1});
        array.setChar(0, 'a');

        Array.D4.Char b = (Array.D4.Char) Array.wrap(array);
        assertEquals('a', b.read(0, 0, 0, 0), "Read");
    }

    @Test
    void testD0_Double() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.DOUBLE, new int[]{});
        array.setDouble(0, 0.1);

        Array.D0.Double b = (Array.D0.Double) Array.wrap(array);
        assertEquals(0.1, b.read(), "Read");
    }

    @Test
    void testD1_Double() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.DOUBLE, new int[]{1});
        array.setDouble(0, 0.1);

        Array.D1.Double b = (Array.D1.Double) Array.wrap(array);
        assertEquals(0.1, b.read(0), "Read");
    }

    @Test
    void testD2_Double() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.DOUBLE, new int[]{1, 1});
        array.setDouble(0, 0.1);

        Array.D2.Double b = (Array.D2.Double) Array.wrap(array);
        assertEquals(0.1, b.read(0, 0), "Read");
    }

    @Test
    void testD3_Double() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.DOUBLE, new int[]{1, 1, 1});
        array.setDouble(0, 0.1);

        Array.D3.Double b = (Array.D3.Double) Array.wrap(array);
        assertEquals(0.1, b.read(0, 0, 0), "Read");
    }

    @Test
    void testD4_Double() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.DOUBLE, new int[]{1, 1, 1, 1});
        array.setDouble(0, 0.1);

        Array.D4.Double b = (Array.D4.Double) Array.wrap(array);
        assertEquals(0.1, b.read(0, 0, 0, 0), "Read");
    }

    @Test
    void testD0_Float() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.FLOAT, new int[]{});
        array.setFloat(0, 0.1f);

        Array.D0.Float b = (Array.D0.Float) Array.wrap(array);
        assertEquals(0.1f, b.read(), "Read");
    }

    @Test
    void testD1_Float() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.FLOAT, new int[]{1});
        array.setFloat(0, 0.1f);

        Array.D1.Float b = (Array.D1.Float) Array.wrap(array);
        assertEquals(0.1f, b.read(0), "Read");
    }

    @Test
    void testD2_Float() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.FLOAT, new int[]{1, 1});
        array.setFloat(0, 0.1f);

        Array.D2.Float b = (Array.D2.Float) Array.wrap(array);
        assertEquals(0.1f, b.read(0, 0), "Read");
    }

    @Test
    void testD3_Float() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.FLOAT, new int[]{1, 1, 1});
        array.setFloat(0, 0.1f);

        Array.D3.Float b = (Array.D3.Float) Array.wrap(array);
        assertEquals(0.1f, b.read(0, 0, 0), "Read");
    }

    @Test
    void testD4_Float() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.FLOAT, new int[]{1, 1, 1, 1});
        array.setFloat(0, 0.1f);

        Array.D4.Float b = (Array.D4.Float) Array.wrap(array);
        assertEquals(0.1f, b.read(0, 0, 0, 0), "Read");
    }

    @Test
    void testD0_Int() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.INT, new int[]{});
        array.setInt(0, 2);

        Array.D0.Int b = (Array.D0.Int) Array.wrap(array);
        assertEquals(2, b.read(), "Read");
    }

    @Test
    void testD1_Int() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.INT, new int[]{1});
        array.setInt(0, 2);

        Array.D1.Int b = (Array.D1.Int) Array.wrap(array);
        assertEquals(2, b.read(0), "Read");
    }

    @Test
    void testD2_Int() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.INT, new int[]{1, 1});
        array.setInt(0, 2);

        Array.D2.Int b = (Array.D2.Int) Array.wrap(array);
        assertEquals(2, b.read(0, 0), "Read");
    }

    @Test
    void testD3_Int() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.INT, new int[]{1, 1, 1});
        array.setInt(0, 2);

        Array.D3.Int b = (Array.D3.Int) Array.wrap(array);
        assertEquals(2, b.read(0, 0, 0), "Read");
    }

    @Test
    void testD4_Int() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.INT, new int[]{1, 1, 1, 1});
        array.setInt(0, 2);

        Array.D4.Int b = (Array.D4.Int) Array.wrap(array);
        assertEquals(2, b.read(0, 0, 0, 0), "Read");
    }

    @Test
    void testD0_Long() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.LONG, new int[]{});
        array.setLong(0, 1L);

        Array.D0.Long b = (Array.D0.Long) Array.wrap(array);
        assertEquals(1L, b.read(), "Read");
    }

    @Test
    void testD1_Long() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.LONG, new int[]{1});
        array.setLong(0, 1L);

        Array.D1.Long b = (Array.D1.Long) Array.wrap(array);
        assertEquals(1L, b.read(0), "Read");
    }

    @Test
    void testD2_Long() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.LONG, new int[]{1, 1});
        array.setLong(0, 1L);

        Array.D2.Long b = (Array.D2.Long) Array.wrap(array);
        assertEquals(1L, b.read(0, 0), "Read");
    }

    @Test
    void testD3_Long() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.LONG, new int[]{1, 1, 1});
        array.setLong(0, 1L);

        Array.D3.Long b = (Array.D3.Long) Array.wrap(array);
        assertEquals(1L, b.read(0, 0, 0), "Read");
    }

    @Test
    void testD4_Long() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.LONG, new int[]{1, 1, 1, 1});
        array.setLong(0, 1L);

        Array.D4.Long b = (Array.D4.Long) Array.wrap(array);
        assertEquals(1L, b.read(0, 0, 0, 0), "Read");
    }

    @Test
    void testD0_Short() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.SHORT, new int[]{});
        array.setShort(0, (short) 2);

        Array.D0.Short b = (Array.D0.Short) Array.wrap(array);
        assertEquals((short) 2, b.read(), "Read");
    }

    @Test
    void testD1_Short() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.SHORT, new int[]{1});
        array.setShort(0, (short) 2);

        Array.D1.Short b = (Array.D1.Short) Array.wrap(array);
        assertEquals((short) 2, b.read(0), "Read");
    }

    @Test
    void testD2_Short() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.SHORT, new int[]{1, 1});
        array.setShort(0, (short) 2);

        Array.D2.Short b = (Array.D2.Short) Array.wrap(array);
        assertEquals((short) 2, b.read(0, 0), "Read");
    }

    @Test
    void testD3_Short() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.SHORT, new int[]{1, 1, 1});
        array.setShort(0, (short) 2);

        Array.D3.Short b = (Array.D3.Short) Array.wrap(array);
        assertEquals((short) 2, b.read(0, 0, 0), "Read");
    }

    @Test
    void testD4_Short() {
        ucar.ma2.Array array = ucar.ma2.Array.factory(DataType.SHORT, new int[]{1, 1, 1, 1});
        array.setShort(0, (short) 2);

        Array.D4.Short b = (Array.D4.Short) Array.wrap(array);
        assertEquals((short) 2, b.read(0, 0, 0, 0), "Read");
    }
}
