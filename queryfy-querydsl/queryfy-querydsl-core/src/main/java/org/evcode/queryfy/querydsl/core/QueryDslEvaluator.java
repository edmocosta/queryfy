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
package org.evcode.queryfy.querydsl.core;

import com.mysema.query.QueryModifiers;
import com.mysema.query.types.Expression;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import org.evcode.queryfy.core.Evaluator;
import org.evcode.queryfy.core.parser.ParserConfig;
import org.evcode.queryfy.core.parser.ast.*;

import java.util.LinkedList;
import java.util.List;

public final class QueryDslEvaluator {

    private final QueryDslVisitor visitor = new QueryDslVisitor();

    public QueryDslEvaluationResult evaluate(String expression, QueryDslContext context) {
        return evaluate(expression, context, ParserConfig.DEFAULT);
    }

    public QueryDslEvaluationResult evaluate(String expression, QueryDslContext context, ParserConfig config) {
        List<Node> nodeList = Evaluator.parse(expression, config);
        QueryDslEvaluationResult eval = new QueryDslEvaluationResult();

        for (Node node : nodeList) {
            if (node instanceof LogicalNode) {
                Predicate filter = node.accept(visitor, context);
                if (node instanceof OrNode) {
                    eval.addOr(filter);
                } else {
                    eval.addAnd(filter);
                }
            } else if (node instanceof FilterNode) {
                Predicate filterNode = node.accept(visitor, context);
                eval.addAnd(filterNode);
            } else if (node instanceof OrderNode) {
                LinkedList order = (LinkedList<OrderSpecifier>) node.accept(visitor, context);
                eval.setOrderSpecifiers(order);
            } else if (node instanceof LimitNode) {
                QueryModifiers modifiers = (QueryModifiers) (Object) node.accept(visitor, context);
                eval.setQueryModifiers(modifiers);
            } else if (node instanceof ProjectionNode) {
                Expression projection = node.accept(visitor, context);
                eval.setProjection(projection);
            }
        }

        return eval;
    }
}
