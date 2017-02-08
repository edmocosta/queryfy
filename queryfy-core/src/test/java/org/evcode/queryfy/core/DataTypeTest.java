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
package org.evcode.queryfy.core;

import org.evcode.queryfy.core.operator.ComparisionOperatorType;
import org.evcode.queryfy.core.parser.QueryParser;
import org.evcode.queryfy.core.parser.ast.FilterNode;
import org.evcode.queryfy.core.utils.ExpressionParserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.parboiled.Parboiled;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;

import static java.time.format.DateTimeFormatter.*;

@RunWith(JUnit4.class)
public class DataTypeTest {

    @Test
    public void testDataType() {
        checkValue("selector = 999999999", new Long("999999999"));
        checkValue("selector = 99999999999999999999999999999999", new Double("99999999999999999999999999999999"));
        checkValue("selector = 18968.36", new BigDecimal(18968.36).setScale(2, RoundingMode.DOWN));
        checkValue("selector = 18968.4536", new BigDecimal(18968.4536).setScale(4, RoundingMode.DOWN));
        checkValue("selector = 1i", Integer.parseInt("1"));
        checkValue("selector = 1l", Long.parseLong("1"));
        checkValue("selector = 1f", Float.parseFloat("1"));
        checkValue("selector = 1d", Double.parseDouble("1"));

        checkValue("selector = 'string'", "string");
        checkValue("selector = 'string with (like) operators = like or'", "string with (like) operators = like or");
        checkValue("selector = 'John Doe\\'s strings are escaped!'", "John Doe's strings are escaped!");

        checkValue("selector = 2017-01-01", LocalDate.parse("2017-01-01", ISO_DATE));
        checkValue("selector = 2017-01-01T12:10:10", LocalDateTime.parse("2017-01-01T12:10:10", ISO_DATE_TIME));
        checkValue("selector = 2017-01-01T12:10:10+03:00[America/Sao_Paulo]",
                ZonedDateTime.parse("2017-01-01T12:10:10+03:00[America/Sao_Paulo]", ISO_ZONED_DATE_TIME));
        checkValue("selector = 18:10:10.056+01:00", LocalTime.parse("18:10:10.056+01:00", ISO_OFFSET_TIME));
    }

    public void checkValue(String query, Object expectedValue) {
        QueryParser expressionParser = Parboiled.createParser(QueryParser.class);
        FilterNode node = (FilterNode) ExpressionParserUtils.parse(query, expressionParser.Query()).pop();
        assertValueAndType(node, expectedValue);
    }

    private void assertValueAndType(FilterNode node, Object expectedValue) {
        Assert.assertEquals("selector", node.getSelector());
        Assert.assertEquals(ComparisionOperatorType.EQUAL, node.getOperator());
        Assert.assertEquals(expectedValue.getClass(), node.getArgs().get(0).getClass());
        Assert.assertEquals(expectedValue, node.getArgs().get(0));
    }
}
