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
package org.evcode.queryfy.querydsl.jpa;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.OrderSpecifier;
import org.evcode.queryfy.core.parser.ParserConfig;
import org.evcode.queryfy.querydsl.core.QueryDslContext;
import org.evcode.queryfy.querydsl.core.QueryDslEvaluationResult;
import org.evcode.queryfy.querydsl.core.QueryDslEvaluator;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;

public class JPAQueryDslParser {

    private EntityManager em;

    public JPAQueryDslParser(EntityManager em) {
        this.em = em;
    }

    public JPAEvaluatedQuery parse(String expression, QueryDslContext context) {
        return parse(expression, context, ParserConfig.DEFAULT);
    }

    public JPAEvaluatedQuery parse(String expression, QueryDslContext context, ParserConfig config) {
        QueryDslEvaluator evaluator = new QueryDslEvaluator();
        QueryDslEvaluationResult eval = evaluator.evaluate(expression, context, config);

        JPAQuery query = new JPAEvaluatedQuery(em)
                .from(context.getEntityPath());

        if (eval.getPredicate() != null) {
            query.where(eval.getPredicate());
        }

        if (eval.getQueryModifiers() != null) {
            query.restrict(eval.getQueryModifiers());
        }

        if (eval.getOrderSpecifiers() != null && !eval.getOrderSpecifiers().isEmpty()) {
            query.orderBy(eval.getOrderSpecifiers().toArray(new OrderSpecifier[0]));
        }

        return new JPAEvaluatedQuery(eval);
    }

    public static class JPAEvaluatedQuery extends JPAQuery {
        private QueryDslEvaluationResult evaluationResult;

        JPAEvaluatedQuery(QueryDslEvaluationResult evaluationResult) {
            this.evaluationResult = evaluationResult;
        }

        JPAEvaluatedQuery(EntityManager em) {
            super(em);
        }

        public QueryDslEvaluationResult getEvaluationResult() {
            return evaluationResult;
        }

        public <RT> List<RT> listProjectedFields() {
            Objects.requireNonNull(evaluationResult.getProjection());
            return super.list(evaluationResult.getProjection());
        }
    }
}
