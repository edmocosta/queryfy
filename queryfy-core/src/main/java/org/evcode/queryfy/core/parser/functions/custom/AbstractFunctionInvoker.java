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

package org.evcode.queryfy.core.parser.functions.custom;

import org.evcode.queryfy.core.parser.functions.CustomFunctionInvoker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractFunctionInvoker implements CustomFunctionInvoker {

    protected final HashMap<String, List<Class[]>> functions = new HashMap<>();

    protected void addFunction(String functionName, List<Class[]> signatures) {
        functions.put(functionName, signatures);
    }

    @Override
    public boolean canHandle(String function, Object... args) {
        if (!functions.containsKey(function))
            return false;

        List<Class[]> signatureTypes = functions.get(function);
        if (args.length == 0 && signatureTypes.size() == 0) {
            return true;
        }

        if (signatureTypes.size() == 0 && args.length > 0) {
            return false;
        }

        Class[] callingArgsCls = Arrays.asList(args).stream()
                .map(p -> p.getClass())
                .collect(Collectors.toList())
                .toArray(new Class[0]);

        return signatureTypes.stream()
                .filter(p -> p.length == callingArgsCls.length)
                .anyMatch(signature -> isValidArgsForSignature(signature, callingArgsCls));
    }

    protected boolean isValidArgsForSignature(Class[] signature, Class[] args) {
        for (int i = 0; i < signature.length; i++) {
            if (!signature[i].isAssignableFrom(args[i])) {
                return false;
            }
        }
        return true;
    }
}
