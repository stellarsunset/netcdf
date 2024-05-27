package com.stellarsunset.netcdf;

import com.stellarsunset.netcdf.SchemaBoundRecordReader.Setter;
import com.stellarsunset.netcdf.SchemaBoundRecordReader.VariablesSetter;
import org.junit.jupiter.api.Test;
import ucar.ma2.Array;
import ucar.ma2.ArrayInt;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VariablesSetterTest {

    private static final Setter<String> APPEND_INT = (s, e, a) -> s + a.getInt(e);

    @Test
    void testN1() {

        VariablesSetter<String> n1 = new VariablesSetter.N1<>(
                (s, e, a) -> s + a.getInt(e),
                intArray(1)
        );

        assertEquals("1", n1.set("", 0));
    }

    @Test
    void testN2() {

        VariablesSetter<String> n2 = new VariablesSetter.N2<>(
                APPEND_INT, intArray(1),
                APPEND_INT, intArray(2)
        );

        assertEquals("21", n2.set("", 0));
    }

    @Test
    void testN3() {

        VariablesSetter<String> n3 = new VariablesSetter.N3<>(
                APPEND_INT, intArray(1),
                APPEND_INT, intArray(2),
                APPEND_INT, intArray(3)
        );

        assertEquals("321", n3.set("", 0));
    }

    @Test
    void testN4() {

        VariablesSetter<String> n4 = new VariablesSetter.N4<>(
                APPEND_INT, intArray(1),
                APPEND_INT, intArray(2),
                APPEND_INT, intArray(3),
                APPEND_INT, intArray(4)
        );

        assertEquals("4321", n4.set("", 0));
    }

    @Test
    void testN5() {

        VariablesSetter<String> n5 = new VariablesSetter.N5<>(
                APPEND_INT, intArray(1),
                APPEND_INT, intArray(2),
                APPEND_INT, intArray(3),
                APPEND_INT, intArray(4),
                APPEND_INT, intArray(5)
        );

        assertEquals("54321", n5.set("", 0));
    }

    @Test
    void testN6() {

        VariablesSetter<String> n6 = new VariablesSetter.N6<>(
                APPEND_INT, intArray(1),
                APPEND_INT, intArray(2),
                APPEND_INT, intArray(3),
                APPEND_INT, intArray(4),
                APPEND_INT, intArray(5),
                APPEND_INT, intArray(6)
        );

        assertEquals("654321", n6.set("", 0));
    }

    @Test
    void testN7() {

        VariablesSetter<String> n7 = new VariablesSetter.N7<>(
                APPEND_INT, intArray(1),
                APPEND_INT, intArray(2),
                APPEND_INT, intArray(3),
                APPEND_INT, intArray(4),
                APPEND_INT, intArray(5),
                APPEND_INT, intArray(6),
                APPEND_INT, intArray(7)
        );

        assertEquals("7654321", n7.set("", 0));
    }

    @Test
    void testNAny() {

        Setter<String>[] setters = new Setter[10];
        Array[] arrays = new Array[10];

        for (int i = 0; i < 10; i++) {
            setters[i] = APPEND_INT;
            arrays[i] = intArray(i + 1);
        }

        VariablesSetter<String> nAny = new VariablesSetter.NAny<>(
                setters,
                arrays
        );

        assertEquals("10987654321", nAny.set("", 0));
    }

    private ArrayInt.D1 intArray(int onlyValue) {
        ArrayInt.D1 array = new ArrayInt.D1(1, false);
        array.set(0, onlyValue);
        return array;
    }
}
