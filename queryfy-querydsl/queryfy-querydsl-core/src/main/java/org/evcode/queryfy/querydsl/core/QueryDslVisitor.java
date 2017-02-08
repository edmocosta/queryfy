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
import com.mysema.query.types.*;
import com.mysema.query.types.expr.*;
import org.evcode.queryfy.core.Visitor;
import org.evcode.queryfy.core.operator.*;
import org.evcode.queryfy.core.parser.ast.*;

import java.util.*;

public class QueryDslVisitor implements Visitor<Predicate, QueryDslContext> {

    @Override
    public Expression visit(ProjectionNode node, QueryDslContext context) {
        Map<Path, List<Expression>> expressions = new HashMap<>();
        for (String selector : node.getSelectors()) {
            Expression extractedPath = ExpressionUtils.extract(context.resolveProjectionPath(selector));

            if (extractedPath instanceof Path) {
                Path path = (Path) extractedPath;

                if (path.getMetadata().isRoot()) {
                    expressions.putIfAbsent(path, new ArrayList<>());
                    expressions.get(path).add(path);
                    continue;
                }

                Path parentPath = path.getMetadata().getParent();
                expressions.putIfAbsent(parentPath, new ArrayList<>());
                expressions.get(parentPath).add(path);
            }
        }

        List<Expression> projections = expressions.getOrDefault(context.getEntityPath(),
                new LinkedList<>());

        expressions.remove(context.getEntityPath());
        for (Map.Entry<Path, List<Expression>> rootPath : expressions.entrySet()) {
            projections.add(Projections.fields(rootPath.getKey().getType(),
                    rootPath.getValue().toArray(new Expression[0]))
                    .as(rootPath.getKey().getMetadata().getName()));
        }

        return Projections.fields(context.getEntityPath().getType(),
                projections.toArray(new Expression[0]));
    }

    @Override
    public Predicate visit(AndNode node, QueryDslContext context) {
        Predicate right = node.getRightOperation().accept(this, context);
        Predicate left = node.getLeftOperation().accept(this, context);
        return ExpressionUtils.and(left, right);
    }

    @Override
    public Predicate visit(OrNode node, QueryDslContext context) {
        Predicate right = node.getRightOperation().accept(this, context);
        Predicate left = node.getLeftOperation().accept(this, context);
        return ExpressionUtils.or(left, right);
    }

    @Override
    public Predicate visit(FilterNode node, QueryDslContext context) {
        Expression path = context.resolveQueryPath(node.getSelector());

        if (path instanceof BooleanExpression && node.getOperator() instanceof SelectorOperatorType) {
            BooleanExpression expression = (BooleanExpression) path;
            if (node.getOperator() == SelectorOperatorType.IS_TRUE) {
                return expression.isTrue();
            }
            if (node.getOperator() == SelectorOperatorType.IS_FALSE) {
                return expression.isFalse();
            }
        }

        boolean isStringOrSelectorOperator = node.getOperator() instanceof StringOperatorType ||
                node.getOperator() instanceof SelectorOperatorType;

        if (path instanceof StringExpression && isStringOrSelectorOperator) {
            StringExpression expression = (StringExpression) path;

            if (node.getOperator() == StringOperatorType.LIKE) {
                return expression.like((String) getNodeValue(path, node, 0));
            }
            if (node.getOperator() == StringOperatorType.NOT_LIKE) {
                return expression.notLike((String) getNodeValue(path, node, 0));
            }
            if (node.getOperator() == SelectorOperatorType.IS_EMPTY) {
                return expression.isEmpty();
            }
            if (node.getOperator() == SelectorOperatorType.IS_NOT_EMPTY) {
                return expression.isNotEmpty();
            }
        }

        if (path instanceof NumberExpression) {
            NumberExpression expression = (NumberExpression) path;

            if (node.getOperator() == ComparisionOperatorType.GREATER) {
                return expression.gt((Number) getNodeValue(path, node, 0));
            }
            if (node.getOperator() == ComparisionOperatorType.GREATER_EQUAL) {
                return expression.goe((Number) getNodeValue(path, node, 0));
            }
            if (node.getOperator() == ComparisionOperatorType.LOWER) {
                return expression.lt((Number) getNodeValue(path, node, 0));
            }
            if (node.getOperator() == ComparisionOperatorType.LOWER_EQUAL) {
                return expression.loe((Number) getNodeValue(path, node, 0));
            }
        }

        if (path instanceof ComparableExpression) {
            ComparableExpression expression = (ComparableExpression) path;

            if (node.getOperator() == ComparisionOperatorType.GREATER) {
                return expression.gt((Comparable) getNodeValue(path, node, 0));
            }
            if (node.getOperator() == ComparisionOperatorType.GREATER_EQUAL) {
                return expression.goe((Comparable) getNodeValue(path, node, 0));
            }
            if (node.getOperator() == ComparisionOperatorType.LOWER) {
                return expression.lt((Comparable) getNodeValue(path, node, 0));
            }
            if (node.getOperator() == ComparisionOperatorType.LOWER_EQUAL) {
                return expression.loe((Comparable) getNodeValue(path, node, 0));
            }
        }

        if (path instanceof SimpleExpression) {
            SimpleExpression expression = (SimpleExpression) path;

            if (node.getOperator() == ComparisionOperatorType.EQUAL) {
                return expression.eq(getNodeValue(path, node, 0));
            }
            if (node.getOperator() == ComparisionOperatorType.NOT_EQUAL) {
                return expression.ne(getNodeValue(path, node, 0));
            }
            if (node.getOperator() == ListOperatorType.IN) {
                return expression.in(getNodeValues(path, node));
            }
            if (node.getOperator() == ListOperatorType.NOT_IN) {
                return expression.in(getNodeValues(path, node));
            }
            if (node.getOperator() == SelectorOperatorType.IS_NULL) {
                return expression.isNull();
            }
            if (node.getOperator() == SelectorOperatorType.IS_NOT_NULL) {
                return expression.isNotNull();
            }
        }

        throw new UnsupportedOperationException("Operation not supported '" + node.getOperator().name() + "'");
    }

    protected List getNodeValues(Expression path, FilterNode node) {
        List args = new LinkedList();
        for (int i = 0; i < node.getArgs().size(); i++) {
            args.add(getNodeValue(path, node, i));
        }
        return args;
    }

    protected Object getNodeValue(Expression path, FilterNode node, Integer valueIndex) {
        if (path instanceof EnumExpression) {
            Object value = node.getArgs().get(valueIndex);
            for (Object item : path.getType().getEnumConstants()) {
                Enum enumValue = (Enum) item;
                if (value instanceof String && enumValue.name().equalsIgnoreCase(value.toString())) {
                    return item;
                } else if (value instanceof Number && ((Number) value).intValue() == enumValue.ordinal()) {
                    return item;
                }
            }
        }
        return node.getArgs().get(valueIndex);
    }

    @Override
    public List<OrderSpecifier> visit(OrderNode node, QueryDslContext context) {
        List<OrderSpecifier> orders = new LinkedList<>();

        node.getOrderSpecifiers().forEach(p -> {
            Order order = p.getOperator() == OrderOperatorType.DESC ? Order.DESC : Order.ASC;
            orders.add(new OrderSpecifier(order, context.resolveQueryPath(p.getSelector())));
        });

        return orders;
    }

    @Override
    public QueryModifiers visit(LimitNode node, QueryDslContext context) {
        return new QueryModifiers(node.getLimit(), node.getOffset());
    }
}
