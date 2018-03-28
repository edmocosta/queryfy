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
package org.evcode.queryfy.core.parser;

import java.util.Arrays;
import java.util.Objects;

class CustomFunctionNode {

    private final String function;
    private final Object[] arguments;

    public CustomFunctionNode(String function, Object... arguments) {
        this.function = function;
        this.arguments = arguments;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String getFunction() {
        return function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomFunctionNode that = (CustomFunctionNode) o;
        return Objects.equals(function, that.function) &&
                Arrays.equals(arguments, that.arguments);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(function);
        result = 31 * result + Arrays.hashCode(arguments);
        return result;
    }
}