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
package org.evcode.queryfy.core.lexer;

import org.evcode.queryfy.core.operator.*;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultGrammar implements Grammar {

    private final HashMap<Operator, Set<String>> operators = new HashMap<>();

    public DefaultGrammar() {
        initGrammarOperators();
    }

    protected final void registerOperator(final Operator operator, final Set<String> symbols) {
        operators.put(operator, symbols);
    }

    private Set<String> asSet(final String... values) {
        return Arrays.stream(values)
                .collect(Collectors.toSet());
    }

    protected void initGrammarOperators() {
        registerOperator(QueryOperatorType.SELECT, asSet("select"));
        registerOperator(QueryOperatorType.WHERE, asSet("where"));
        registerOperator(QueryOperatorType.LIMIT, asSet("limit"));

        registerOperator(QueryOperatorType.ORDER, asSet("order by"));
        registerOperator(OrderOperatorType.ASC, asSet("asc"));
        registerOperator(OrderOperatorType.DESC, asSet("desc"));

        registerOperator(LogicalOperatorType.AND, asSet("and", "&&"));
        registerOperator(LogicalOperatorType.OR, asSet("or", "||"));

        registerOperator(StringOperatorType.LIKE, asSet("like"));
        registerOperator(StringOperatorType.NOT_LIKE, asSet("not like", "!like"));

        registerOperator(ComparisionOperatorType.EQUAL, asSet("=", "=="));
        registerOperator(ComparisionOperatorType.NOT_EQUAL, asSet("!=", "<>"));
        registerOperator(ComparisionOperatorType.GREATER, asSet(">"));
        registerOperator(ComparisionOperatorType.GREATER_EQUAL, asSet(">="));
        registerOperator(ComparisionOperatorType.LOWER, asSet("<"));
        registerOperator(ComparisionOperatorType.LOWER_EQUAL, asSet("<="));

        registerOperator(SelectorOperatorType.IS_EMPTY, asSet("is empty"));
        registerOperator(SelectorOperatorType.IS_NOT_EMPTY, asSet("is not empty", "!empty"));
        registerOperator(SelectorOperatorType.IS_NULL, asSet("is null"));
        registerOperator(SelectorOperatorType.IS_NOT_NULL, asSet("is not null", "!null"));
        registerOperator(SelectorOperatorType.IS_TRUE, asSet("is true"));
        registerOperator(SelectorOperatorType.IS_FALSE, asSet("is false"));

        registerOperator(ListOperatorType.IN, asSet("in"));
        registerOperator(ListOperatorType.NOT_IN, asSet("not in", "!in"));
    }

    @Override
    public Set<String> getOperatorSymbols(final Operator type) {
        return operators.get(type);
    }

    @Override
    public Operator getOperator(final String operator) {
        List<Operator> matchedOperators = operators.entrySet().stream()
                .filter(p -> p.getValue().contains(operator))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (matchedOperators.isEmpty()) {
            throw new NoSuchElementException(String.format("Operator not found for symbol '%s'", operator));
        }

        if (matchedOperators.size() > 1) {
            throw new IllegalStateException(String.format("Multiple operators was defined for symbol '%s'", operator));
        }

        return matchedOperators.get(0);
    }
}
