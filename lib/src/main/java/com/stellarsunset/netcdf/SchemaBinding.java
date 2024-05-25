package com.stellarsunset.netcdf;

import com.stellarsunset.netcdf.field.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class SchemaBinding<T> {

    private final Supplier<T> recordSupplier;

    private final Map<String, FieldSetter<T>> dimensions;

    private final Map<String, FieldSetter<T>> variables;

    private SchemaBinding(Builder<T> builder) {
        this.recordSupplier = requireNonNull(builder.recordSupplier);
        this.dimensions = Map.copyOf(builder.dimensions);
        this.variables = Map.copyOf(builder.variables);
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public Supplier<T> recordSupplier() {
        return recordSupplier;
    }

    public Map<String, FieldSetter<T>> dimensions() {
        return dimensions;
    }

    public Map<String, FieldSetter<T>> variables() {
        return variables;
    }

    public static final class Builder<T> {

        private Supplier<T> recordSupplier;

        private final Map<String, FieldSetter<T>> dimensions = new HashMap<>();

        private final Map<String, FieldSetter<T>> variables = new HashMap<>();

        private Builder() {
        }

        public Builder<T> recordSupplier(Supplier<T> recordSupplier) {
            this.recordSupplier = requireNonNull(recordSupplier);
            return this;
        }

        public Builder<T> byteDimension(String name, ByteSetter<T> setter) {
            this.dimensions.put(name, setter);
            return this;
        }

        public Builder<T> charDimension(String name, CharacterSetter<T> setter) {
            this.dimensions.put(name, setter);
            return this;
        }

        public Builder<T> booleanDimension(String name, BooleanSetter<T> setter) {
            this.dimensions.put(name, setter);
            return this;
        }

        public Builder<T> shortDimension(String name, ShortSetter<T> setter) {
            this.dimensions.put(name, setter);
            return this;
        }

        public Builder<T> intDimension(String name, IntSetter<T> setter) {
            this.dimensions.put(name, setter);
            return this;
        }

        public Builder<T> longDimension(String name, LongSetter<T> setter) {
            this.dimensions.put(name, setter);
            return this;
        }
        
        public Builder<T> floatDimension(String name, FloatSetter<T> setter) {
            this.dimensions.put(name, setter);
            return this;
        }
        
        public Builder<T> doubleDimension(String name, DoubleSetter<T> setter){
            this.dimensions.put(name, setter);
            return this;
        }

        public Builder<T> byteVariable(String name, ByteSetter<T> setter) {
            this.variables.put(name, setter);
            return this;
        }

        public Builder<T> charVariable(String name, CharacterSetter<T> setter) {
            this.variables.put(name, setter);
            return this;
        }

        public Builder<T> booleanVariable(String name, BooleanSetter<T> setter) {
            this.variables.put(name, setter);
            return this;
        }

        public Builder<T> shortVariable(String name, ShortSetter<T> setter) {
            this.variables.put(name, setter);
            return this;
        }

        public Builder<T> intVariable(String name, IntSetter<T> setter) {
            this.variables.put(name, setter);
            return this;
        }

        public Builder<T> longVariable(String name, LongSetter<T> setter) {
            this.variables.put(name, setter);
            return this;
        }

        public Builder<T> floatVariable(String name, FloatSetter<T> setter) {
            this.variables.put(name, setter);
            return this;
        }

        public Builder<T> doubleVariable(String name, DoubleSetter<T> setter){
            this.variables.put(name, setter);
            return this;
        }

        public SchemaBinding<T> build() {
            return new SchemaBinding<>(this);
        }
    }
}
