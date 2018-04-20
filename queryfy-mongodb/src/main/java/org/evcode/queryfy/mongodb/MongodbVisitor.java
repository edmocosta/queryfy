/*
 *  Copyright 2018 EVCode
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.evcode.queryfy.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.BsonNull;
import org.bson.conversions.Bson;
import org.evcode.queryfy.core.Visitor;
import org.evcode.queryfy.core.operator.*;
import org.evcode.queryfy.core.parser.ast.*;

import java.util.regex.Pattern;

public class MongodbVisitor implements Visitor<Bson, MongodbContext> {

    @Override
    public Bson visit(ProjectionNode node, MongodbContext param) {
        BasicDBObject projection = new BasicDBObject();
        for (String selector : node.getSelectors()) {
            projection.append(param.resolveProjectionPath(selector), 1);
        }
        return Projections.fields(projection);
    }

    @Override
    public Bson visit(AndNode node, MongodbContext param) {
        Bson right = node.getRightOperation().accept(this, param);
        Bson left = node.getLeftOperation().accept(this, param);
        return Filters.and(left, right);
    }

    @Override
    public Bson visit(OrNode node, MongodbContext param) {
        Bson right = node.getRightOperation().accept(this, param);
        Bson left = node.getLeftOperation().accept(this, param);
        return Filters.or(left, right);
    }

    @Override
    public Bson visit(FilterNode node, MongodbContext param) {
        String path = param.resolveQueryPath(node.getSelector());

        //String types
        if (node.getOperator() == StringOperatorType.LIKE) {
            return Filters.regex(path, asLikeRegex(String.valueOf(node.getArgs().get(0))));
        }
        if (node.getOperator() == StringOperatorType.NOT_LIKE) {
            return Filters.not(Filters.regex(path, asLikeRegex(String.valueOf(node.getArgs().get(0)))));
        }

        //Comparision types
        if (node.getOperator() == ComparisionOperatorType.EQUAL) {
            return Filters.eq(path, node.getArgs().get(0));
        }

        if (node.getOperator() == ComparisionOperatorType.NOT_EQUAL) {
            return Filters.ne(path, node.getArgs().get(0));
        }

        if (node.getOperator() == ComparisionOperatorType.GREATER) {
            return Filters.gt(path, node.getArgs().get(0));
        }

        if (node.getOperator() == ComparisionOperatorType.GREATER_EQUAL) {
            return Filters.gte(path, node.getArgs().get(0));
        }

        if (node.getOperator() == ComparisionOperatorType.LOWER) {
            return Filters.lt(path, node.getArgs().get(0));
        }

        if (node.getOperator() == ComparisionOperatorType.LOWER_EQUAL) {
            return Filters.lte(path, node.getArgs().get(0));
        }

        //List types
        if (node.getOperator() == ListOperatorType.IN) {
            return Filters.in(path, node.getArgs());
        }

        if (node.getOperator() == ListOperatorType.NOT_IN) {
            return Filters.nin(path, node.getArgs());
        }

        //Selector operator types
        if (node.getOperator() == SelectorOperatorType.IS_TRUE ||
                node.getOperator() == SelectorOperatorType.IS_FALSE) {
            return Filters.eq(path, node.getOperator() == SelectorOperatorType.IS_TRUE);
        }

        if (node.getOperator() == SelectorOperatorType.IS_EMPTY) {
            return Filters.eq(path, "");
        }

        if (node.getOperator() == SelectorOperatorType.IS_NOT_EMPTY) {
            return Filters.ne(path, "");
        }

        if (node.getOperator() == SelectorOperatorType.IS_NULL) {
            return Filters.eq(path, BsonNull.VALUE);
        }

        if (node.getOperator() == SelectorOperatorType.IS_NOT_NULL) {
            return Filters.ne(path, BsonNull.VALUE);
        }

        throw new UnsupportedOperationException("Operation not supported '" + node.getOperator().name() + "'");
    }

    @Override
    public Bson visit(OrderNode node, MongodbContext param) {
        BasicDBObject order = new BasicDBObject();
        for (OrderNode.OrderSpecifier specifier : node.getOrderSpecifiers()) {
            Object key = param.resolveProjectionPath(specifier.getSelector());
            order.append(key.toString(), specifier.getOperator() == OrderOperatorType.ASC ? 1 : -1);
        }
        return order;
    }

    @Override
    public LimitNode visit(LimitNode node, MongodbContext param) {
        return node;
    }

    protected Pattern asLikeRegex(String value) {
        final StringBuilder rv = new StringBuilder(value.length() + 4);
        if (!value.startsWith("%")) {
            rv.append('^');
        }

        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == '.' || ch == '*' || ch == '?') {
                rv.append('\\');
            } else if (ch == '%') {
                rv.append(".*");
                continue;
            } else if (ch == '_') {
                rv.append('.');
                continue;
            }
            rv.append(ch);
        }

        if (!value.endsWith("%")) {
            rv.append('$');
        }

        //TODO: Should it receive the magic constants from context?
        return Pattern.compile(rv.toString());
    }
}
