/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.strepsirrhini.chaoslemur;

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
