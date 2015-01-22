/*
 * Copyright 2015 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

final class Precedence<T> {

    private final Object monitor = new Object();

    private final List<Supplier<T>> suppliers = new ArrayList<>();

    Precedence<T> candidate(Supplier<T> supplier) {
        synchronized (this.monitor) {
            this.suppliers.add(supplier);
            return this;
        }
    }

    Precedence<T> candidate(T value) {
        return candidate(() -> value);
    }

    T get() {
        return get(Function.<T>identity());
    }

    <U> U get(Function<T, U> f) {
        synchronized (this.monitor) {
            for (Supplier<T> supplier : this.suppliers) {
                T value = supplier.get();

                if (value != null) {
                    return f.apply(value);
                }
            }

            throw new IllegalStateException("No non-null values supplied");
        }
    }
}
