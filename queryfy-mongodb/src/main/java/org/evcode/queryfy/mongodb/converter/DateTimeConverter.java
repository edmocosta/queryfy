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

package org.evcode.queryfy.mongodb.converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateTimeConverter implements TypeConverter {

    @Override
    public boolean isSupported(Class<?> type) {
        return type.isAssignableFrom(LocalDate.class) ||
                type.isAssignableFrom(LocalDateTime.class) ||
                type.isAssignableFrom(ZonedDateTime.class);
    }

    @Override
    public Object convert(Object value) {
        if (value instanceof LocalDate) {
            Date date = Date.from(((LocalDate) value).atStartOfDay(ZoneOffset.UTC).toInstant());
            return date;
        }

        if (value instanceof LocalDateTime) {
            Date date = Date.from(((LocalDateTime) value).atZone(ZoneOffset.UTC).toInstant());
            return date;
        }

        if (value instanceof ZonedDateTime) {
            Date date = Date.from(((ZonedDateTime) value).toInstant());
            return date;
        }

        return value;
    }
}
