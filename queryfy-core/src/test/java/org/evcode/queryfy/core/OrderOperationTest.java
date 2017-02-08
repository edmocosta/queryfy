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

import org.evcode.queryfy.core.operator.OrderOperatorType;
import org.evcode.queryfy.core.parser.QueryParser;
import org.evcode.queryfy.core.parser.ast.OrderNode;
import org.evcode.queryfy.core.utils.ExpressionParserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.parboiled.Parboiled;
import org.parboiled.support.ValueStack;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
public class OrderOperationTest {

    public static void assertOrders(OrderNode node, List<OrderNode.OrderSpecifier> expectedOrders) {
        Assert.assertEquals(expectedOrders.size(), node.getOrderSpecifiers().size());
        expectedOrders.forEach(p -> {
            if (!node.getOrderSpecifiers().contains(p)) {
                Assert.fail("Order specifier " + p + " not found in order node");
            }
        });
    }

    @Test
    public void testOrderByOperations() {
        testOrderOperation("order by one", Arrays.asList(
                new OrderNode.OrderSpecifier("one", OrderOperatorType.ASC)));

        testOrderOperation("order by one, two", Arrays.asList(
                new OrderNode.OrderSpecifier("one", OrderOperatorType.ASC),
                new OrderNode.OrderSpecifier("two", OrderOperatorType.ASC)));

        testOrderOperation("order by one desc, two asc", Arrays.asList(
                new OrderNode.OrderSpecifier("one", OrderOperatorType.DESC),
                new OrderNode.OrderSpecifier("two", OrderOperatorType.ASC)));

        testOrderOperation("order by one desc, two asc, three desc", Arrays.asList(
                new OrderNode.OrderSpecifier("one", OrderOperatorType.DESC),
                new OrderNode.OrderSpecifier("two", OrderOperatorType.ASC),
                new OrderNode.OrderSpecifier("three", OrderOperatorType.DESC)));
    }

    private void testOrderOperation(String query, List<OrderNode.OrderSpecifier> expected) {
        QueryParser expressionParser = Parboiled.createParser(QueryParser.class);

        ValueStack<Object> nodes = ExpressionParserUtils.parse(query, expressionParser.Query());
        for (Object node : nodes) {
            if (node instanceof OrderNode) {
                assertOrders((OrderNode) node, expected);
                return;
            }
        }
        Assert.fail("Order node not found");
    }


}
