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

import org.bson.conversions.Bson;
import org.evcode.queryfy.core.Evaluator;
import org.evcode.queryfy.core.parser.ParserConfig;
import org.evcode.queryfy.core.parser.ast.*;

import java.util.List;

public class MongodbEvaluator {

    private final MongodbVisitor visitor = new MongodbVisitor();

    public MongodbEvaluationResult evaluate(String expression, MongodbContext context) {
        return evaluate(expression, context, ParserConfig.DEFAULT);
    }

    public MongodbEvaluationResult evaluate(String expression, MongodbContext context, ParserConfig config) {
        List<Node> nodeList = Evaluator.parse(expression, config);
        MongodbEvaluationResult eval = new MongodbEvaluationResult();

        for (Node node : nodeList) {
            if (node instanceof LogicalNode) {
                Bson filter = node.accept(visitor, context);
                if (node instanceof OrNode) {
                    eval.addOr(filter);
                } else {
                    eval.addAnd(filter);
                }
            } else if (node instanceof FilterNode) {
                Bson filterNode = node.accept(visitor, context);
                eval.addAnd(filterNode);
            } else if (node instanceof OrderNode) {
                Bson order = node.accept(visitor, context);
                eval.setOrderSpecifiers(order);
            } else if (node instanceof LimitNode) {
                LimitNode modifiers = (LimitNode) (Object) node.accept(visitor, context);
                eval.setLimit(modifiers.getLimit());
                eval.setOffset(modifiers.getOffset());
            } else if (node instanceof ProjectionNode) {
                Bson projection = node.accept(visitor, context);
                eval.setProjection(projection);
            }
        }

        return eval;
    }
}
