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

import org.evcode.queryfy.core.parser.functions.temporal.TemporalFunctionInvoker;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class DefaultUserFunctionInvoker implements UserFunctionInvoker {

    public static final int DEFAULT_PRIORITY = 0;
    private Set<UserFunction> registeredInvokers = new HashSet<>();

    public DefaultUserFunctionInvoker() {
        registeredInvokers.add(UserFunction.of(new TemporalFunctionInvoker()));
    }

    public DefaultUserFunctionInvoker register(UserFunction function) {
        this.registeredInvokers.add(function);
        return this;
    }

    public DefaultUserFunctionInvoker unregister(UserFunction function) {
        this.registeredInvokers.remove(function);
        return this;
    }

    @Override
    public Object invoke(String function, Object... args) {
        UserFunctionInvoker invoker = registeredInvokers.stream()
                .filter(p -> p.getInvoker().canHandle(function, args))
                .sorted(Comparator.comparingInt(p -> p.getPriority()))
                .map(p -> p.getInvoker())
                .findFirst()
                .orElse(null);

        if (invoker == null) {
            throw new IllegalArgumentException(String.format("User function %s with %s argument(s) is not supported",
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

    public static class UserFunction {
        private final int priority;
        private final UserFunctionInvoker invoker;

        public UserFunction(UserFunctionInvoker invoker) {
            this.invoker = invoker;
            this.priority = DEFAULT_PRIORITY;
        }

        public UserFunction(UserFunctionInvoker invoker, int priority) {
            this.priority = priority;
            this.invoker = invoker;
        }

        public static UserFunction of(UserFunctionInvoker invoker) {
            return new UserFunction(invoker);
        }

        public static UserFunction of(UserFunctionInvoker invoker, int priority) {
            return new UserFunction(invoker, priority);
        }

        public int getPriority() {
            return priority;
        }

        public UserFunctionInvoker getInvoker() {
            return invoker;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserFunction function = (UserFunction) o;
            return Objects.equals(invoker, function.invoker);
        }

        @Override
        public int hashCode() {
            return Objects.hash(invoker);
        }
    }
}