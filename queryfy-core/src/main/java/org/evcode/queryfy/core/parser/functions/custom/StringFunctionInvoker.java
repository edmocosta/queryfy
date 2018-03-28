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

import java.util.Arrays;
import java.util.Collections;

public class StringFunctionInvoker extends AbstractFunctionInvoker {

    private static final String UPPER = "upper";
    private static final String LOWER = "lower";
    private static final String REPLACE = "replace";
    private static final String LENGTH = "length";
    private static final String SUBSTRING = "substring";

    public StringFunctionInvoker() {
        addFunction(UPPER, Collections.singletonList(new Class[]{String.class}));
        addFunction(LOWER, Collections.singletonList(new Class[]{String.class}));
        addFunction(REPLACE, Collections.singletonList(new Class[]{String.class, String.class, String.class}));
        addFunction(LENGTH, Collections.singletonList(new Class[]{String.class}));
        addFunction(SUBSTRING, Arrays.asList(new Class[]{String.class, Long.class, Long.class},
                new Class[]{String.class, Long.class}));
    }

    @Override
    public Object invoke(String function, Object... args) {
        switch (function) {
            case UPPER:
                return String.valueOf(args[0]).toUpperCase();
            case LOWER:
                return String.valueOf(args[0]).toLowerCase();
            case REPLACE:
                return String.valueOf(args[0]).replace((String) args[1], (String) args[2]);
            case LENGTH:
                return String.valueOf(args[0]).length();
            case SUBSTRING: {
                boolean hasEndIndexArg = args.length > 2;
                return !hasEndIndexArg ? String.valueOf(args[0]).substring(((Long) args[1]).intValue()) :
                        String.valueOf(args[0]).substring(((Long) args[1]).intValue(), ((Long) args[2]).intValue());
            }
            default:
                throw new IllegalArgumentException("Function " + function + " is not supported.");
        }
    }
}
