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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Collections;

public class TemporalFunctionInvoker extends AbstractFunctionInvoker {

    private static final String DATE_TODAY = "today";
    private static final String DATE_END_OF_MONTH = "endOfMonth";
    private static final String DATETIME_NOW = "now";
    private static final String TEMPORAL_PLUS_DAYS = "plusDays";
    private static final String TEMPORAL_PLUS_MONTHS = "plusMonths";
    private static final String TEMPORAL_PLUS_YEARS = "plusYears";

    public TemporalFunctionInvoker() {
        addFunction(DATE_TODAY, Collections.emptyList());
        addFunction(DATE_END_OF_MONTH, Collections.emptyList());
        addFunction(DATETIME_NOW, Collections.emptyList());
        addFunction(TEMPORAL_PLUS_DAYS, Collections.singletonList(new Class[]{Temporal.class, Long.class}));
        addFunction(TEMPORAL_PLUS_MONTHS, Collections.singletonList(new Class[]{Temporal.class, Long.class}));
        addFunction(TEMPORAL_PLUS_YEARS, Collections.singletonList(new Class[]{Temporal.class, Long.class}));
    }

    @Override
    public Object invoke(String function, Object... args) {
        switch (function) {
            case DATE_TODAY:
                return LocalDate.now();
            case DATE_END_OF_MONTH:
                return LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            case DATETIME_NOW:
                return LocalDateTime.now();
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
}