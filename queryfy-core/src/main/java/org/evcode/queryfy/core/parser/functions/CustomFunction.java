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

import java.util.Objects;

import static org.evcode.queryfy.core.parser.functions.DefaultFunctionInvoker.DEFAULT_PRIORITY;

public class CustomFunction {

    private final int priority;
    private final FunctionInvoker invoker;

    public CustomFunction(FunctionInvoker invoker) {
        this.invoker = invoker;
        this.priority = DEFAULT_PRIORITY;
    }

    public CustomFunction(FunctionInvoker invoker, int priority) {
        this.priority = priority;
        this.invoker = invoker;
    }

    public static CustomFunction of(FunctionInvoker invoker) {
        return new CustomFunction(invoker);
    }

    public static CustomFunction of(FunctionInvoker invoker, int priority) {
        return new CustomFunction(invoker, priority);
    }

    public int getPriority() {
        return priority;
    }

    public FunctionInvoker getInvoker() {
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
