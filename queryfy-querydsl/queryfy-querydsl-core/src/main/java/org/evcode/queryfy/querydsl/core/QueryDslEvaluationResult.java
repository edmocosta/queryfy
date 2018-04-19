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

package org.evcode.queryfy.querydsl.core;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.QueryModifiers;
import com.mysema.query.types.Expression;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;

import java.util.LinkedList;

public class QueryDslEvaluationResult {

    private final BooleanBuilder predicateBuilder = new BooleanBuilder();
    private Expression projection;
    private QueryModifiers queryModifiers;
    private LinkedList<OrderSpecifier> orderSpecifiers = new LinkedList<>();

    QueryDslEvaluationResult() {
    }

    void addAnd(Predicate predicate) {
        predicateBuilder.and(predicate);
    }

    void addOr(Predicate predicate) {
        predicateBuilder.or(predicate);
    }

    public Predicate getPredicate() {
        return predicateBuilder.getValue();
    }

    public Expression getProjection() {
        return projection;
    }

    void setProjection(Expression projection) {
        this.projection = projection;
    }

    public LinkedList<OrderSpecifier> getOrderSpecifiers() {
        return orderSpecifiers;
    }

    void setOrderSpecifiers(LinkedList<OrderSpecifier> orderSpecifiers) {
        this.orderSpecifiers = orderSpecifiers;
    }

    public QueryModifiers getQueryModifiers() {
        return queryModifiers;
    }

    void setQueryModifiers(QueryModifiers queryModifiers) {
        this.queryModifiers = queryModifiers;
    }
}
