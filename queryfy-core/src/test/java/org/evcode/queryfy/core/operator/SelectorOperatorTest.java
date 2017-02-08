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

@RunWith(JUnit4.class)
public class SelectorOperatorTest {

    @Test
    public void testSelectorOperations() {
        testOperator("selector is null", SelectorOperatorType.IS_NULL);
        testOperator("selector is not null", SelectorOperatorType.IS_NOT_NULL);
        testOperator("selector !null", SelectorOperatorType.IS_NOT_NULL);
        testOperator("selector is empty", SelectorOperatorType.IS_EMPTY);
        testOperator("selector is not empty", SelectorOperatorType.IS_NOT_EMPTY);
        testOperator("selector !empty", SelectorOperatorType.IS_NOT_EMPTY);
        testOperator("selector is true", SelectorOperatorType.IS_TRUE);
        testOperator("selector is false", SelectorOperatorType.IS_FALSE);
    }

    public void testOperator(String query, Operator operator) {
        QueryParser expressionParser = Parboiled.createParser(QueryParser.class);
        FilterNode node = (FilterNode) ExpressionParserUtils.parse(query, expressionParser.Query()).pop();
        assertParse(node, operator);
    }

    private void assertParse(FilterNode node, Operator operator) {
        Assert.assertEquals("selector", node.getSelector());
        Assert.assertEquals(operator, node.getOperator());
        Assert.assertEquals(0, node.getArgs().size());
    }
}
