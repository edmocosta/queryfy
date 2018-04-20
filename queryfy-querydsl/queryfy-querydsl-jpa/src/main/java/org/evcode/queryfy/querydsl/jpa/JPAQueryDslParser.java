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

    public QueryDslEvaluationResult parse(String expression, QueryDslContext context) {
        return parse(expression, context, ParserConfig.DEFAULT);
    }

    public QueryDslEvaluationResult parse(String expression, QueryDslContext context, ParserConfig config) {
        QueryDslEvaluator evaluator = new QueryDslEvaluator();
        QueryDslEvaluationResult eval = evaluator.evaluate(expression, context, config);
        return eval;
    }

    public JPAEvaluatedQuery parseAndFind(String expression, QueryDslContext context) {
        return parseAndFind(expression, context, ParserConfig.DEFAULT);
    }

    public JPAEvaluatedQuery parseAndFind(String expression, QueryDslContext context, ParserConfig config) {
        QueryDslEvaluationResult evaluationResult = parse(expression, context, config);
        JPAEvaluatedQuery jpaQuery = new JPAEvaluatedQuery(em, evaluationResult);
        return apply(jpaQuery, context, evaluationResult);
    }

    public <T extends JPAQuery> T parseAndApply(T query, String expression, QueryDslContext context) {
        return parseAndApply(query, expression, context, ParserConfig.DEFAULT);
    }

    public <T extends JPAQuery> T parseAndApply(T query, String expression, QueryDslContext context, ParserConfig config) {
        QueryDslEvaluationResult evaluationResult = parse(expression, context, config);
        return apply(query, context, evaluationResult);
    }

    public <T extends JPAQuery> T apply(T query, QueryDslContext context, QueryDslEvaluationResult evaluationResult) {
        if (context.getEntityPath() != null) {
            query.from(context.getEntityPath());
        }

        if (evaluationResult.getPredicate() != null) {
            query.where(evaluationResult.getPredicate());
        }

        if (evaluationResult.getQueryModifiers() != null) {
            query.restrict(evaluationResult.getQueryModifiers());
        }

        if (evaluationResult.getOrderSpecifiers() != null && !evaluationResult.getOrderSpecifiers().isEmpty()) {
            query.orderBy(evaluationResult.getOrderSpecifiers().toArray(new OrderSpecifier[0]));
        }

        return query;
    }

    public static class JPAEvaluatedQuery extends JPAQuery {
        private QueryDslEvaluationResult evaluationResult;

        public JPAEvaluatedQuery(EntityManager em, QueryDslEvaluationResult evaluationResult) {
            super(em);
            this.evaluationResult = evaluationResult;
        }

        public QueryDslEvaluationResult getEvaluationResult() {
            return evaluationResult;
        }

        public <RT> List<RT> listWithProjections() {
            Objects.requireNonNull(evaluationResult.getProjection());
            return super.list(evaluationResult.getProjection());
        }
    }
}
