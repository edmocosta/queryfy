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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

class TimeVar extends Var<LocalTime> {

    private String hour = "00";
    private String minute = "00";
    private String second = "00";
    private String nanosecond;
    private String zoneOffset;

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

    private void build() {

        Objects.requireNonNull(hour);
        Objects.requireNonNull(minute);

        StringBuilder timeString = new StringBuilder();
        timeString.append(hour);
        timeString.append(":");
        timeString.append(minute);
        timeString.append(":");
        timeString.append(second);

        if (nanosecond != null) {
            timeString.append(".").append(nanosecond);
        }

        if (zoneOffset != null) {
            timeString.append(zoneOffset);
        }

        LocalTime time = zoneOffset != null ?
                LocalTime.parse(timeString.toString(), DateTimeFormatter.ISO_OFFSET_TIME) :
                LocalTime.parse(timeString.toString(), DateTimeFormatter.ISO_TIME);

        set(time);
    }

    @Override
    public LocalTime get() {
        if (super.isNotSet()) {
            build();
        }
        return super.get();
    }
}