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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

class NumberVar extends Var<Number> {

    public static final String FLOAT = "F";
    public static final String LONG = "L";
    public static final String INTEGER = "I";
    public static final String DOUBLE = "D";

    private Locale locale = Locale.getDefault();
    private String number;
    private String typeQualifier;

    public boolean setTypeQualifier(String typeQualifier) {
        this.typeQualifier = typeQualifier;
        return true;
    }

    private void buildQualifiedNumber() {
        if (typeQualifier.equalsIgnoreCase(FLOAT)) {
            set(Float.parseFloat(number));
            return;
        }

        if (typeQualifier.equalsIgnoreCase(LONG)) {
            set(Long.parseLong(number));
            return;
        }

        if (typeQualifier.equalsIgnoreCase(INTEGER)) {
            set(Integer.parseInt(number));
            return;
        }

        if (typeQualifier.equalsIgnoreCase(DOUBLE)) {
            set(Double.parseDouble(number));
            return;
        }

        throw new IllegalArgumentException(
                String.format("Invalid number qualifier '%s'", typeQualifier));
    }

    private void build() {
        try {

            if (typeQualifier != null) {
                buildQualifiedNumber();
                return;
            }

            Number value;
            if (number.contains(".")) {
                DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.US);
                decimalFormat.setParseBigDecimal(true);

                int decSymbolIndex = number.lastIndexOf(".");
                if (decSymbolIndex > -1) {
                    int precision = number.substring(decSymbolIndex, number.length() - 1).length();
                    decimalFormat.setMaximumFractionDigits(precision);
                }

                value = decimalFormat.parse(number);
            } else {
                value = NumberFormat.getInstance(locale).parse(number);
            }

            set(value);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("Invalid number '%s'", number));
        }
    }

    public boolean setNumber(String number) {
        this.number = number;
        return true;
    }

    @Override
    public Number get() {
        if (super.isNotSet()) {
            build();
        }
        return super.get();
    }
}
