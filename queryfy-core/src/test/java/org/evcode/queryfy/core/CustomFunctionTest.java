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

package org.evcode.queryfy.core;

import org.evcode.queryfy.core.parser.ParserConfig;
import org.evcode.queryfy.core.parser.ast.FilterNode;
import org.evcode.queryfy.core.parser.functions.CustomFunctionInvoker;
import org.evcode.queryfy.core.parser.functions.DefaultCustomFunctionInvoker;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomFunctionTest {

    @Test
    public void testTemporalProvidedFunctions() {
        checkValue("selector = @today()", LocalDate.now());
        checkValue("selector = @endOfMonth()", LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        checkValue("selector = @plusDays(@today(), 1)", LocalDate.now().plusDays(1));
        checkValue("selector = @plusYears(@today(), 1)", LocalDate.now().plusYears(1));
        checkValue("selector = @plusMonths(@today(), 1)", LocalDate.now().plusMonths(1));
        checkValue("selector = @now()", new Object() {
            @Override
            public boolean equals(Object obj) {
                return obj instanceof LocalDateTime;
            }
        });
    }

    @Test
    public void testStringProvidedFunctions() {
        checkValue("selector = @upper('value')", "VALUE");
        checkValue("selector = @lower('VALUE')", "value");
        checkValue("selector = @replace('vxaxlxuxe', 'x', '')", "value");
        checkValue("selector = @lower(@replace(@upper('vxaxlxuxe'), 'X', ''))", "value");
        checkValue("selector = @substring('value', 1)", "alue");
        checkValue("selector = @substring('value', 0, 2)", "va");
    }

    @Test
    public void testCustomFunctions() {
        checkValue("selector = @sayHello()", "Hello function");
    }

    public void checkValue(String query, Object expectedValue) {
        FilterNode node = (FilterNode) Evaluator.parse(query,
                ParserConfig.builder()
                        .withCustomFunctionInvoker(DefaultCustomFunctionInvoker.with(new HelloFunctionInvoker()))
                        .build())
                .get(0);

        Assert.assertEquals(expectedValue, node.getArgs().get(0));
    }

    public static class HelloFunctionInvoker implements CustomFunctionInvoker {
        @Override
        public Object invoke(String function, Object... args) {
            return "Hello function";
        }

        @Override
        public boolean canHandle(String function, Object... args) {
            return function.equalsIgnoreCase("sayHello");
        }
    }
}
