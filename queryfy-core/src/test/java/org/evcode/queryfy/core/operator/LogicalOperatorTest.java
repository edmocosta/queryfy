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
import org.evcode.queryfy.core.parser.ast.AndNode;
import org.evcode.queryfy.core.parser.ast.FilterNode;
import org.evcode.queryfy.core.parser.ast.Node;
import org.evcode.queryfy.core.parser.ast.OrNode;
import org.evcode.queryfy.core.utils.ExpressionParserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.parboiled.Parboiled;

@RunWith(JUnit4.class)
public class LogicalOperatorTest {

    @Test
    public void testOrOperation() {
        Node node = parseNode("left = 1 or right = 2 or (left = 3 or right = 4)");
        Assert.assertEquals(OrNode.class, node.getClass());

        //root node
        OrNode rootOrNode = (OrNode) node;

        // or (left = 3 or right = 4)
        Assert.assertEquals(OrNode.class, rootOrNode.getRightOperation().getClass());
        OrNode rightOrNode = (OrNode) rootOrNode.getRightOperation();

        Assert.assertEquals(FilterNode.class, rightOrNode.getRightOperation().getClass());
        assertNodeValue((FilterNode) rightOrNode.getRightOperation(), "right", ComparisionOperatorType.EQUAL, 4L);

        Assert.assertEquals(FilterNode.class, rightOrNode.getLeftOperation().getClass());
        assertNodeValue((FilterNode) rightOrNode.getLeftOperation(), "left", ComparisionOperatorType.EQUAL, 3L);

        //left = 1 or right = 2
        Assert.assertEquals(OrNode.class, rootOrNode.getLeftOperation().getClass());
        OrNode leftOrNode = (OrNode) rootOrNode.getLeftOperation();

        Assert.assertEquals(FilterNode.class, leftOrNode.getRightOperation().getClass());
        assertNodeValue((FilterNode) leftOrNode.getRightOperation(), "right", ComparisionOperatorType.EQUAL, 2L);

        Assert.assertEquals(FilterNode.class, leftOrNode.getLeftOperation().getClass());
        assertNodeValue((FilterNode) leftOrNode.getLeftOperation(), "left", ComparisionOperatorType.EQUAL, 1L);
    }

    @Test
    public void testAndOperation() {
        Node node = parseNode("left = 1 and right = 2 and (left = 3 and right = 4)");
        Assert.assertEquals(AndNode.class, node.getClass());

        //root node
        AndNode rootOrNode = (AndNode) node;

        // and (left = 3 and right = 4)
        Assert.assertEquals(AndNode.class, rootOrNode.getRightOperation().getClass());
        AndNode rightOrNode = (AndNode) rootOrNode.getRightOperation();

        Assert.assertEquals(FilterNode.class, rightOrNode.getRightOperation().getClass());
        assertNodeValue((FilterNode) rightOrNode.getRightOperation(), "right", ComparisionOperatorType.EQUAL, 4L);

        Assert.assertEquals(FilterNode.class, rightOrNode.getLeftOperation().getClass());
        assertNodeValue((FilterNode) rightOrNode.getLeftOperation(), "left", ComparisionOperatorType.EQUAL, 3L);

        //left = 1 and right = 2
        Assert.assertEquals(AndNode.class, rootOrNode.getLeftOperation().getClass());
        AndNode leftOrNode = (AndNode) rootOrNode.getLeftOperation();

        Assert.assertEquals(FilterNode.class, leftOrNode.getRightOperation().getClass());
        assertNodeValue((FilterNode) leftOrNode.getRightOperation(), "right", ComparisionOperatorType.EQUAL, 2L);

        Assert.assertEquals(FilterNode.class, leftOrNode.getLeftOperation().getClass());
        assertNodeValue((FilterNode) leftOrNode.getLeftOperation(), "left", ComparisionOperatorType.EQUAL, 1L);
    }

    private void assertNodeValue(FilterNode node, String selector, Operator operator, Long value) {
        Assert.assertEquals(selector, node.getSelector());
        Assert.assertEquals(operator, node.getOperator());
        Assert.assertEquals(value, node.getArgs().get(0));
    }

    public Node parseNode(String query) {
        QueryParser expressionParser = Parboiled.createParser(QueryParser.class);
        return (Node) ExpressionParserUtils.parse(query, expressionParser.Query()).pop();
    }
}
