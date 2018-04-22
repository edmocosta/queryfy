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
import org.bson.conversions.Bson;

public class MongodbEvaluationResult {

    private Bson filter;
    private Bson orderSpecifiers = new BasicDBObject();
    private Bson projection;
    private Long offset;
    private Long limit;

    public Bson getOrderSpecifiers() {
        return orderSpecifiers;
    }

    void setOrderSpecifiers(Bson orderSpecifiers) {
        this.orderSpecifiers = orderSpecifiers;
    }

    public Bson getProjection() {
        return projection;
    }

    void setProjection(Bson projection) {
        this.projection = projection;
    }

    public Bson getFilter() {
        return filter;
    }

    public Long getLimit() {
        return limit;
    }

    void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getOffset() {
        return offset;
    }

    void setOffset(Long offset) {
        this.offset = offset;
    }

    void addAnd(Bson andFilter) {
        if (filter != null) {
            filter = Filters.and(filter, andFilter);
            return;
        }

        filter = Filters.and(andFilter);
    }

    void addOr(Bson orFilter) {
        if (filter != null) {
            filter = Filters.or(filter, orFilter);
            return;
        }
        filter = Filters.or(orFilter);
    }
}
