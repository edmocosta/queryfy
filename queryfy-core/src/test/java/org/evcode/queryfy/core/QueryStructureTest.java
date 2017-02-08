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
import org.evcode.queryfy.core.parser.ast.FilterNode;
import org.evcode.queryfy.core.parser.ast.LimitNode;
import org.evcode.queryfy.core.parser.ast.OrderNode;
import org.evcode.queryfy.core.parser.ast.ProjectionNode;
import org.evcode.queryfy.core.utils.ExpressionParserUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.parboiled.Parboiled;
import org.parboiled.support.ValueStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(JUnit4.class)
public class QueryStructureTest {

    @Test
    public void testQuery() {

        List<String> oneTwoThreeSelectors = Arrays.asList("one", "two", "three");

        testQuery("select one, two, three where one = 1 order by one limit 1,1",
                oneTwoThreeSelectors,
                1L,
                Arrays.asList(new OrderNode.OrderSpecifier("one", OrderOperatorType.ASC)),
                new LimitNode(1L, 1L));

        testQuery("select one, two, three where one = 1 order by one",
                oneTwoThreeSelectors,
                1L,
                Arrays.asList(new OrderNode.OrderSpecifier("one", OrderOperatorType.ASC)),
                null);

        testQuery("select one, two, three where one = 1 order by one asc, two limit 2,4",
                oneTwoThreeSelectors,
                1L,
                Arrays.asList(new OrderNode.OrderSpecifier("one", OrderOperatorType.ASC),
                        new OrderNode.OrderSpecifier("two", OrderOperatorType.ASC)),
                new LimitNode(2L, 4L));

        testQuery("select one, two, three where one = 1 order by one asc, two desc limit 1,1",
                oneTwoThreeSelectors,
                1L,
                Arrays.asList(new OrderNode.OrderSpecifier("one", OrderOperatorType.ASC),
                        new OrderNode.OrderSpecifier("two", OrderOperatorType.DESC)),
                new LimitNode(1L, 1L));

        testQuery("select one, two, three order by one asc, two desc limit 1,1",
                oneTwoThreeSelectors,
                null,
                Arrays.asList(new OrderNode.OrderSpecifier("one", OrderOperatorType.ASC),
                        new OrderNode.OrderSpecifier("two", OrderOperatorType.DESC)),
                new LimitNode(1L, 1L));

        testQuery("select one, two, three limit 1,1",
                oneTwoThreeSelectors, null, null, new LimitNode(1L, 1L));

        testQuery("select one, two, three", oneTwoThreeSelectors, null, null, null);

        testQuery("limit 1,1", null, null, null, new LimitNode(1L, 1L));

        testQuery("order by one", null, null, Arrays.asList(
                new OrderNode.OrderSpecifier("one", OrderOperatorType.ASC)), null);

        testQuery("order by one limit 1,1", null, null,
                Collections.singletonList(new OrderNode.OrderSpecifier("one", OrderOperatorType.ASC)),
                new LimitNode(1L, 1L));

        testQuery("select one, two order by one",
                Arrays.asList("one", "two"),
                null,
                Collections.singletonList(new OrderNode.OrderSpecifier("one", OrderOperatorType.ASC)),
                null);

        testQuery("select one, two limit 1,1",
                Arrays.asList("one", "two"),
                null,
                null,
                new LimitNode(1L, 1L));
    }

    private void testQuery(String query,
                           List<String> projections,
                           Object filterValue,
                           List<OrderNode.OrderSpecifier> orders,
                           LimitNode limit) {

        QueryParser expressionParser = Parboiled.createParser(QueryParser.class);
        ValueStack<Object> nodes = ExpressionParserUtils.parse(query, expressionParser.Query());

        boolean projectionTestOk = false;
        boolean filterTestOk = false;
        boolean orderTestOk = false;
        boolean limitTestOk = false;

        for (Object node : nodes) {
            if (node instanceof ProjectionNode) {
                if (projections == null || projections.isEmpty()) {
                    Assert.fail("Projection selectors was not expected");
                }
                ProjectionOperationTest.assertProjections((ProjectionNode) node, projections);
                projectionTestOk = true;
                continue;
            }
            if (node instanceof LimitNode) {
                if (limit == null) {
                    Assert.fail("Limit node was not expected");
                }
                Assert.assertEquals(limit, node);
                limitTestOk = true;
                continue;
            }
            if (node instanceof OrderNode) {
                if (orders == null || orders.isEmpty()) {
                    Assert.fail("Order node was not expected");
                }
                OrderOperationTest.assertOrders((OrderNode) node, orders);
                orderTestOk = true;
                continue;
            }
            if (node instanceof FilterNode) {
                if (filterValue == null) {
                    Assert.fail("Filter node was not expected");
                }
                Assert.assertEquals(filterValue, ((FilterNode) node).getArgs().get(0));
                filterTestOk = true;
                continue;
            }
            Assert.fail("Node type " + node.getClass() + " was not expected");
        }

        if (!projectionTestOk && projections != null) {
            Assert.fail("Projection node was not found");
        }
        if (!filterTestOk && filterValue != null) {
            Assert.fail("Filter node was not found");
        }
        if (!limitTestOk && limit != null) {
            Assert.fail("Limit node was not found");
        }
        if (!orderTestOk && orders != null) {
            Assert.fail("Order node was not found");
        }
    }
}
