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

import org.evcode.queryfy.core.parser.ParserConfig;
import org.evcode.queryfy.core.parser.QueryParser;
import org.evcode.queryfy.core.parser.ast.Node;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import org.parboiled.support.ValueStack;

import java.util.LinkedList;
import java.util.List;

import static org.parboiled.errors.ErrorUtils.printParseErrors;

public final class Evaluator {

    public static List<Node> parse(String query) {
        return parse(query, ParserConfig.DEFAULT);
    }

    public static List<Node> parse(String query, ParserConfig config) {
        QueryParser expressionParser = Parboiled.createParser(QueryParser.class, config);
        ValueStack<Object> nodes = parse(query, expressionParser.Query());
        List<Node> nodeList = new LinkedList<>();

        for (Object node : nodes) {
            if (node instanceof Node) {
                nodeList.add((Node) node);
            }
        }
        return nodeList;
    }

    private static ValueStack<Object> parse(String query, Rule ruleTree) {
        ReportingParseRunner<Object> runner = new ReportingParseRunner<>(ruleTree);
        ParsingResult<Object> result = runner.run(query);

        if (!result.matched || result.hasErrors()) {
            throw new IllegalArgumentException("Invalid query: " + printParseErrors(result));
        }

        return result.valueStack;
    }
}
