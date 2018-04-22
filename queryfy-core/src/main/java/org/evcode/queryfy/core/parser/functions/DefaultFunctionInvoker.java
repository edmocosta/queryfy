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

import java.util.*;

public class DefaultFunctionInvoker implements FunctionInvoker {

    public static final int DEFAULT_PRIORITY = 0;
    public static final int PROVIDED_PRIORITY = -1;
    private final static Set<CustomFunction> discoveredFunctions = new HashSet<>();

    static {
        Iterator<CustomFunction> functionInvokerIterator = ServiceLoader.load(CustomFunction.class).iterator();
        functionInvokerIterator.forEachRemaining(discoveredFunctions::add);
    }

    private final Set<CustomFunction> registeredInvokers = new HashSet<>();

    public DefaultFunctionInvoker() {
        registeredInvokers.add(CustomFunction.of(new TemporalFunctionInvoker(), PROVIDED_PRIORITY));
        registeredInvokers.add(CustomFunction.of(new StringFunctionInvoker(), PROVIDED_PRIORITY));
        registeredInvokers.addAll(discoveredFunctions);
    }

    public static DefaultFunctionInvoker with(FunctionInvoker... functionInvoker) {
        DefaultFunctionInvoker invoker = new DefaultFunctionInvoker();
        Arrays.asList(functionInvoker).stream().forEach(invoker::register);
        return invoker;
    }

    public static DefaultFunctionInvoker with(CustomFunction... functionInvoker) {
        DefaultFunctionInvoker invoker = new DefaultFunctionInvoker();
        Arrays.asList(functionInvoker).stream().forEach(invoker::register);
        return invoker;
    }

    public DefaultFunctionInvoker register(CustomFunction function) {
        this.registeredInvokers.add(function);
        return this;
    }

    public DefaultFunctionInvoker register(FunctionInvoker function) {
        this.registeredInvokers.add(CustomFunction.of(function));
        return this;
    }

    public DefaultFunctionInvoker register(FunctionInvoker function, int priority) {
        this.registeredInvokers.add(CustomFunction.of(function, priority));
        return this;
    }

    public DefaultFunctionInvoker unregister(CustomFunction function) {
        this.registeredInvokers.remove(function);
        return this;
    }

    public DefaultFunctionInvoker unregisterAll() {
        this.registeredInvokers.clear();
        return this;
    }

    @Override
    public Object invoke(String function, Object... args) {
        FunctionInvoker invoker = registeredInvokers.stream()
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
}