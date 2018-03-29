/*
 *  Copyright 2018 EVCode
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.evcode.queryfy.core.parser.functions;

import org.evcode.queryfy.core.parser.functions.custom.StringFunctionInvoker;
import org.evcode.queryfy.core.parser.functions.custom.TemporalFunctionInvoker;

import java.util.*;

public class DefaultCustomFunctionInvoker implements CustomFunctionInvoker {

    public static final int DEFAULT_PRIORITY = 0;
    public static final int PROVIDED_PRIORITY = -1;
    private Set<CustomFunction> registeredInvokers = new HashSet<>();

    public DefaultCustomFunctionInvoker() {
        registeredInvokers.add(CustomFunction.of(new TemporalFunctionInvoker(), PROVIDED_PRIORITY));
        registeredInvokers.add(CustomFunction.of(new StringFunctionInvoker(), PROVIDED_PRIORITY));
    }

    public static DefaultCustomFunctionInvoker with(CustomFunctionInvoker... functionInvoker) {
        DefaultCustomFunctionInvoker invoker = new DefaultCustomFunctionInvoker();
        Arrays.asList(functionInvoker).stream().forEach(invoker::register);
        return invoker;
    }

    public static DefaultCustomFunctionInvoker with(CustomFunction... functionInvoker) {
        DefaultCustomFunctionInvoker invoker = new DefaultCustomFunctionInvoker();
        Arrays.asList(functionInvoker).stream().forEach(invoker::register);
        return invoker;
    }

    public DefaultCustomFunctionInvoker register(CustomFunction function) {
        this.registeredInvokers.add(function);
        return this;
    }

    public DefaultCustomFunctionInvoker register(CustomFunctionInvoker function) {
        this.registeredInvokers.add(CustomFunction.of(function));
        return this;
    }

    public DefaultCustomFunctionInvoker register(CustomFunctionInvoker function, int priority) {
        this.registeredInvokers.add(CustomFunction.of(function, priority));
        return this;
    }

    public DefaultCustomFunctionInvoker unregister(CustomFunction function) {
        this.registeredInvokers.remove(function);
        return this;
    }

    public DefaultCustomFunctionInvoker unregisterAll() {
        this.registeredInvokers.clear();
        return this;
    }

    @Override
    public Object invoke(String function, Object... args) {
        CustomFunctionInvoker invoker = registeredInvokers.stream()
                .filter(p -> p.getInvoker().canHandle(function, args))
                .sorted(Comparator.comparingInt(p -> p.getPriority()))
                .map(p -> p.getInvoker())
                .findFirst()
                .orElse(null);

        if (invoker == null) {
            throw new IllegalArgumentException(String.format("Custom function %s with %s argument(s) is not supported",
                    function, args.length));
        }

        return invoker.invoke(function, args);
    }

    @Override
    public boolean canHandle(String function, Object... args) {
        return registeredInvokers.parallelStream()
                .filter(p -> p.getInvoker().canHandle(function, args))
                .findFirst()
                .isPresent();
    }

    public static class CustomFunction {
        private final int priority;
        private final CustomFunctionInvoker invoker;

        public CustomFunction(CustomFunctionInvoker invoker) {
            this.invoker = invoker;
            this.priority = DEFAULT_PRIORITY;
        }

        public CustomFunction(CustomFunctionInvoker invoker, int priority) {
            this.priority = priority;
            this.invoker = invoker;
        }

        public static CustomFunction of(CustomFunctionInvoker invoker) {
            return new CustomFunction(invoker);
        }

        public static CustomFunction of(CustomFunctionInvoker invoker, int priority) {
            return new CustomFunction(invoker, priority);
        }

        public int getPriority() {
            return priority;
        }

        public CustomFunctionInvoker getInvoker() {
            return invoker;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CustomFunction function = (CustomFunction) o;
            return Objects.equals(invoker, function.invoker);
        }

        @Override
        public int hashCode() {
            return Objects.hash(invoker);
        }
    }
}