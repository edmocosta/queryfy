/*
 * Copyright 2017 EVCode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.evcode.queryfy.core.parser;

import org.parboiled.support.Var;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

class LocalDateVar extends Var<LocalDate> {

    private String year;
    private String month;
    private String day;

    @Override
    public LocalDate get() {

        Objects.requireNonNull(year);
        Objects.requireNonNull(month);
        Objects.requireNonNull(day);

        String dateString = year + "-" + month + "-" + day;

        return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public boolean appendYear(String year) {
        this.year = year;
        return true;
    }

    public boolean appendMonth(String month) {
        this.month = month;
        return true;
    }

    public boolean appendDay(String day) {
        this.day = day;
        return true;
    }
}

