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

import org.evcode.queryfy.core.parser.QueryParser;
import org.evcode.queryfy.core.parser.ast.ProjectionNode;
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
public class ProjectionOperationTest {

    @Test
    public void testProjectionOperations() {
        testProjectionOperation("select path ,other, one.more limit 1,1", Arrays.asList("path", "other", "one.more"));
        testProjectionOperation("select path ,other,one.more", Arrays.asList("path", "other", "one.more"));
        testProjectionOperation("select path, other, one.more", Arrays.asList("path", "other", "one.more"));
    }

    private void testProjectionOperation(String query, List<String> expected) {
        QueryParser expressionParser = Parboiled.createParser(QueryParser.class);

        ValueStack<Object> nodes = ExpressionParserUtils.parse(query, expressionParser.Query());
        for (Object node : nodes) {
            if (node instanceof ProjectionNode) {
                assertProjections((ProjectionNode) node, expected);
                return;
            }
        }
        Assert.fail("Projection node not found");
    }

    public static void assertProjections(ProjectionNode node, List<String> expectedSelectors) {
        Assert.assertEquals(expectedSelectors.size(), node.getSelectors().size());
        expectedSelectors.forEach(p -> {
            if (!node.getSelectors().contains(p)) {
                Assert.fail("Selector " + p + " not found in projection fields");
            }
        });
    }
}
