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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Objects;

class DateTimeVar extends Var<Object> {

    private String year;
    private String month;
    private String dayOfMonth;
    private String hour = "00";
    private String minute = "00";
    private String second = "00";
    private String nanosecond;
    private String zoneId;
    private String zoneOffset;

    public void build() {

        Objects.requireNonNull(year);
        Objects.requireNonNull(month);
        Objects.requireNonNull(dayOfMonth);
        Objects.requireNonNull(hour);
        Objects.requireNonNull(minute);

        StringBuilder dateString = new StringBuilder();
        dateString.append(year);
        dateString.append("-");
        dateString.append(month);
        dateString.append("-");
        dateString.append(dayOfMonth);
        dateString.append("T");
        dateString.append(hour);
        dateString.append(":");
        dateString.append(minute);
        dateString.append(":");
        dateString.append(second);

        if (nanosecond != null) {
            dateString.append(".").append(nanosecond);
        }

        if (zoneOffset != null) {
            dateString.append(zoneOffset);
        }

        if (zoneId != null) {
            dateString.append(zoneId.trim());
        }

        boolean isZonedDateTime = zoneOffset != null || zoneId != null;
        Temporal dateTime = isZonedDateTime ?
                ZonedDateTime.parse(dateString.toString(), DateTimeFormatter.ISO_ZONED_DATE_TIME)
                : LocalDateTime.parse(dateString.toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        set(dateTime);
    }

    @Override
    public Object get() {
        if (super.isNotSet()) {
            build();
        }
        return super.get();
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
        this.dayOfMonth = day;
        return true;
    }

    public boolean appendHour(String hours) {
        this.hour = hours;
        return true;
    }

    public boolean appendMinute(String minutes) {
        this.minute = minutes;
        return true;
    }

    public boolean appendSecond(String seconds) {
        this.second = seconds;
        return true;
    }

    public boolean appendNanosecond(String nanoseconds) {
        this.nanosecond = nanoseconds;
        return true;
    }

    public boolean appendZoneOffset(String zoneOffset) {
        this.zoneOffset = zoneOffset;
        return true;
    }

    public boolean appendZoneId(String zoneId) {
        this.zoneId = zoneId;
        return true;
    }
}