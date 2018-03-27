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
package org.evcode.queryfy.core.parser.functions.temporal;

import org.evcode.queryfy.core.parser.functions.UserFunctionInvoker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class TemporalFunctionInvoker implements UserFunctionInvoker {

    private static final String DATE_NOW = "Date.now";
    private static final String DATE_END_OF_MONTH = "Date.endOfMonth";
    private static final String DATETIME_NOW = "DateTime.now";
    private static final String DATETIME_END_OF_MONTH = "DateTime.endOfMonth";
    private static final String TEMPORAL_PLUS_DAYS = "Temporal.plusDays";
    private static final String TEMPORAL_PLUS_MONTHS = "Temporal.plusMonths";
    private static final String TEMPORAL_PLUS_YEARS = "Temporal.plusYears";

    private static final HashMap<String, List<Class[]>> functions = new HashMap<>();

    static {
        functions.put(DATE_NOW, Collections.emptyList());
        functions.put(DATE_END_OF_MONTH, Collections.emptyList());
        functions.put(DATETIME_NOW, Collections.emptyList());
        functions.put(DATETIME_END_OF_MONTH, Collections.emptyList());
        functions.put(TEMPORAL_PLUS_DAYS, Collections.singletonList(new Class[]{Temporal.class, Long.class}));
        functions.put(TEMPORAL_PLUS_MONTHS, Collections.singletonList(new Class[]{Temporal.class, Long.class}));
        functions.put(TEMPORAL_PLUS_YEARS, Collections.singletonList(new Class[]{Temporal.class, Long.class}));
    }

    @Override
    public Object invoke(String function, Object... args) {
        switch (function) {
            case DATE_NOW:
                return LocalDate.now();
            case DATE_END_OF_MONTH:
                return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            case DATETIME_NOW:
                return LocalDate.now();
            case DATETIME_END_OF_MONTH:
                return LocalDateTime.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            case TEMPORAL_PLUS_DAYS:
                return ((Temporal) args[0]).plus((Long) args[1], ChronoUnit.DAYS);
            case TEMPORAL_PLUS_MONTHS:
                return ((Temporal) args[0]).plus((Long) args[1], ChronoUnit.MONTHS);
            case TEMPORAL_PLUS_YEARS:
                return ((Temporal) args[0]).plus((Long) args[1], ChronoUnit.YEARS);
            default:
                throw new IllegalArgumentException("Function " + function + " is not supported.");
        }
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

    private boolean isValidArgsForSignature(Class[] signature, Class[] args) {
        for (int i = 0; i < signature.length; i++) {
            if (!signature[i].isAssignableFrom(args[i])) {
                return false;
            }
        }
        return true;
    }
}