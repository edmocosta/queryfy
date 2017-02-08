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
import org.evcode.queryfy.querydsl.core.QueryDslEvaluator;

import javax.persistence.EntityManager;

public class JPAQueryDslParser {

    private EntityManager em;

    public JPAQueryDslParser(EntityManager em) {
        this.em = em;
    }

    public JPAQuery parse(String expression, QueryDslContext context) {
        return parse(expression, context, ParserConfig.DEFAULT);
    }

    public JPAQuery parse(String expression, QueryDslContext context, ParserConfig config) {
        QueryDslEvaluator evaluator = new QueryDslEvaluator();
        evaluator.evaluate(expression, context, config);

        JPAQuery query = new JPAQuery(em)
                .from(context.getEntityPath());

        if (context.getPredicate() != null) {
            query.where(context.getPredicate());
        }

        if (context.getQueryModifiers() != null) {
            query.restrict(context.getQueryModifiers());
        }

        if (context.getOrderSpecifiers() != null && !context.getOrderSpecifiers().isEmpty()) {
            query.orderBy(context.getOrderSpecifiers().toArray(new OrderSpecifier[0]));
        }

        return query;
    }
}
