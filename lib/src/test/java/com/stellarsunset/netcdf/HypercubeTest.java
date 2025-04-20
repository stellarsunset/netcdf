package com.stellarsunset.netcdf;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HypercubeTest {

    @Test
    void testD1() {
        String[] elements = new String[]{"a", "b", "c", "d"};

        Hypercube.D1<String> cube = new D1Cube<>(elements);
        assertAll(
                () -> assertEquals(4, cube.max(), "Max"),
                () -> assertEquals(elements[1], cube.read(1), "Index 1"),
                () -> assertEquals(List.of(elements), cube.stream().toList(), "Element Stream")
        );
    }

    record D1Cube<T>(T[] array) implements Hypercube.D1<T> {
        @Override
        public T read(int i) {
            return array[i];
        }

        @Override
        public int max() {
            return array.length;
        }

        @Override
        public void close() {
        }
    }

    @Test
    void testD2() {
        Integer[][] elements = new Integer[][]{new Integer[]{0, 1, 2}, new Integer[]{2, 4, 6}};

        Hypercube.D2<Integer> cube = new D2Cube<>(elements);
        assertAll(
                () -> assertEquals(2, cube.d0Max(), "D0 Max"),
                () -> assertEquals(3, cube.d1Max(), "D1 Max"),
                () -> assertEquals(1, cube.read(0, 1), "Read 0, 1"),
                () -> assertEquals(List.of(0, 1, 2, 2, 4, 6), cube.stream().toList(), "Element Stream")
        );
    }

    record D2Cube<T>(T[][] array) implements Hypercube.D2<T> {
        @Override
        public T read(int i, int j) {
            return array[i][j];
        }

        @Override
        public int d0Max() {
            return array.length;
        }

        @Override
        public int d1Max() {
            return array[0].length;
        }

        @Override
        public void close() {
        }
    }

    @Test
    void testD3() {
        Integer[][][] elements = new Integer[][][]{
                new Integer[][]{
                        new Integer[]{1, 2, 3},
                        new Integer[]{2, 4, 6}
                }
        };

        Hypercube.D3<Integer> cube = new D3Cube<>(elements);
        assertAll(
                () -> assertEquals(1, cube.d0Max(), "D0 Max"),
                () -> assertEquals(2, cube.d1Max(), "D1 Max"),
                () -> assertEquals(3, cube.d2Max(), "D2 Max"),
                () -> assertEquals(6, cube.read(0, 1, 2), "Read 0, 1, 2"),
                () -> assertEquals(List.of(1, 2, 3, 2, 4, 6), cube.stream().toList(), "Element Stream")
        );
    }

    record D3Cube<T>(T[][][] array) implements Hypercube.D3<T> {
        @Override
        public T read(int i, int j, int k) {
            return array[i][j][k];
        }

        @Override
        public int d0Max() {
            return array.length;
        }

        @Override
        public int d1Max() {
            return array[0].length;
        }

        @Override
        public int d2Max() {
            return array[0][0].length;
        }

        @Override
        public void close() {
        }
    }

    @Test
    void testD4() {
        Integer[][][][] elements = new Integer[][][][]{
                new Integer[][][]{
                        new Integer[][]{
                                new Integer[]{1, 2, 3, 4},
                                new Integer[]{2, 4, 6, 8},
                                new Integer[]{3, 6, 9, 12}
                        },
                        new Integer[][]{
                                new Integer[]{4, 8, 12, 16},
                                new Integer[]{5, 10, 15, 20},
                                new Integer[]{6, 12, 18, 24}
                        }
                }
        };

        List<Integer> all = List.of(
                1, 2, 3, 4, 2, 4, 6, 8, 3, 6, 9, 12, 4, 8, 12, 16, 5, 10, 15, 20, 6, 12, 18, 24
        );

        Hypercube.D4<Integer> cube = new D4Cube<>(elements);
        assertAll(
                () -> assertEquals(1, cube.d0Max(), "D0 Max"),
                () -> assertEquals(2, cube.d1Max(), "D1 Max"),
                () -> assertEquals(3, cube.d2Max(), "D2 Max"),
                () -> assertEquals(24, cube.read(0, 1, 2, 3), "Read 0, 1, 2"),
                () -> assertEquals(all, cube.stream().toList(), "Element Stream")
        );
    }

    record D4Cube<T>(T[][][][] array) implements Hypercube.D4<T> {
        @Override
        public T read(int i, int j, int k, int u) {
            return array[i][j][k][u];
        }

        @Override
        public int d0Max() {
            return array.length;
        }

        @Override
        public int d1Max() {
            return array[0].length;
        }

        @Override
        public int d2Max() {
            return array[0][0].length;
        }

        @Override
        public int d3Max() {
            return array[0][0][0].length;
        }

        @Override
        public void close() {
        }
    }
}
