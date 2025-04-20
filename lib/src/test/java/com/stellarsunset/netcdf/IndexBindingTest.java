package com.stellarsunset.netcdf;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IndexBindingTest {

    @Test
    void testD1() {
        IndexBinding.D1<Ints> binding = IndexBinding.D1.compose(List.of(Ints::addInt));
        Ints ints = Ints.mutable();
        binding.set(ints, 1);
        assertEquals(List.of(1), ints.data());
    }

    @Test
    void testD2() {
        IndexBinding.D2<Ints> binding = IndexBinding.D2.compose(List.of((o, i, j) -> o.addInt(i).addInt(j)));
        Ints ints = Ints.mutable();
        binding.set(ints, 1, 2);
        assertEquals(List.of(1, 2), ints.data());
    }

    @Test
    void testD3() {
        IndexBinding.D3<Ints> binding = IndexBinding.D3.compose(List.of((o, i, j, k) -> o.addInt(i).addInt(j).addInt(k)));
        Ints ints = Ints.mutable();
        binding.set(ints, 1, 2, 3);
        assertEquals(List.of(1, 2, 3), ints.data());
    }

    @Test
    void testD4() {
        IndexBinding.D4<Ints> binding = IndexBinding.D4.compose(List.of((o, i, j, k, u) -> o.addInt(i).addInt(j).addInt(k).addInt(u)));
        Ints ints = Ints.mutable();
        binding.set(ints, 1, 2, 3, 4);
        assertEquals(List.of(1, 2, 3, 4), ints.data());
    }

    record Ints(List<Integer> data) {

        static Ints mutable() {
            return new Ints(new ArrayList<>());
        }

        Ints addInt(int i) {
            this.data.add(i);
            return this;
        }
    }
}
