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
package org.evcode.queryfy.core.operator;

import org.evcode.queryfy.core.parser.QueryParser;
import org.evcode.queryfy.core.parser.ast.FilterNode;
import org.evcode.queryfy.core.utils.ExpressionParserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.parboiled.Parboiled;
import org.parboiled.errors.ParserRuntimeException;

@RunWith(JUnit4.class)
public class ListOperatorTest {

    @Test
    public void testListOperations() {
        testOperator("selector in (1, 2, 3)", ListOperatorType.IN);
        testOperator("selector not in (1, 2, 3)", ListOperatorType.NOT_IN);
        testOperator("selector !in (1, 2, 3)", ListOperatorType.NOT_IN);
    }

    @Test(expected = ParserRuntimeException.class)
    public void testValueType() {
        testOperator("selector !in (1, 2, '3')", ListOperatorType.NOT_IN);
    }

    public void testOperator(String query, Operator operator) {
        QueryParser expressionParser = Parboiled.createParser(QueryParser.class);
        FilterNode node = (FilterNode) ExpressionParserUtils.parse(query, expressionParser.Query()).pop();
        assertParse(node, operator);
    }

    private void assertParse(FilterNode node, Operator operator) {
        Assert.assertEquals("selector", node.getSelector());
        Assert.assertEquals(operator, node.getOperator());

        Assert.assertEquals(3, node.getArgs().size(), 3);
        Assert.assertEquals(1L, node.getArgs().get(0));
        Assert.assertEquals(2L, node.getArgs().get(1));
        Assert.assertEquals(3L, node.getArgs().get(2));
    }
}
