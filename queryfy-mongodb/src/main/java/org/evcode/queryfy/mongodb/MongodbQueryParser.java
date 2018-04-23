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

package org.evcode.queryfy.mongodb;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.evcode.queryfy.core.parser.ParserConfig;


public class MongodbQueryParser {

    public MongodbEvaluationResult parse(String expression, MongodbContext context) {
        return parse(expression, context, ParserConfig.DEFAULT);
    }

    public MongodbEvaluationResult parse(String expression, MongodbContext context, ParserConfig config) {
        MongodbEvaluator evaluator = new MongodbEvaluator();
        MongodbEvaluationResult evaluationResult = evaluator.evaluate(expression, context, config);
        return evaluationResult;
    }

    public <T> FindIterable<T> parseAndfind(MongoCollection<T> collection, String expression, MongodbContext context) {
        return parseAndfind(collection, expression, context, ParserConfig.DEFAULT);
    }

    public <T> FindIterable<T> parseAndfind(MongoCollection<T> collection, String expression, MongodbContext context, ParserConfig config) {
        FindIterable<T> query = collection.find();
        return parseAndApply(query, expression, context, config);
    }


    public <T extends FindIterable> T parseAndApply(T findIterable, String expression, MongodbContext context) {
        return parseAndApply(findIterable, expression, context, ParserConfig.DEFAULT);
    }

    public <T extends FindIterable> T parseAndApply(T findIterable, String expression, MongodbContext context, ParserConfig config) {
        MongodbEvaluationResult evaluationResult = parse(expression, context, config);
        return apply(findIterable, context, evaluationResult, null, null);
    }

    public <T extends FindIterable> T parseAndApply(T findIterable, String expression, MongodbContext context,
                                                    Bson initialAndFilter, Bson initialOrFilter) {
        return parseAndApply(findIterable, expression, context, ParserConfig.DEFAULT, initialAndFilter, initialOrFilter);
    }

    public <T extends FindIterable> T parseAndApply(T findIterable, String expression, MongodbContext context, ParserConfig config,
                                                    Bson initialAndFilter, Bson initialOrFilter) {
        MongodbEvaluationResult evaluationResult = parse(expression, context, (config != null ? config : ParserConfig.DEFAULT));
        return apply(findIterable, context, evaluationResult, initialAndFilter, initialOrFilter);
    }

    public <T extends FindIterable> T apply(T findIterable, MongodbContext context, MongodbEvaluationResult evaluationResult) {
        return apply(findIterable, context, evaluationResult, null, null);
    }

    public <T extends FindIterable> T apply(T findIterable, MongodbContext context, MongodbEvaluationResult evaluationResult, Bson initialAndFilter, Bson initialOrFilter) {

        if (evaluationResult.getFilter() != null) {
            Bson filter = evaluationResult.getFilter();
            if (initialAndFilter != null) {
                filter = Filters.and(initialAndFilter, filter);
            }
            if (initialOrFilter != null) {
                filter = Filters.or(initialOrFilter, filter);
            }
            findIterable.filter(filter);
        }

        if (evaluationResult.getLimit() != null) {
            findIterable.limit(evaluationResult.getLimit().intValue());
        }

        if (evaluationResult.getOffset() != null) {
            findIterable.skip(evaluationResult.getOffset().intValue());
        }

        if (evaluationResult.getProjection() != null) {
            findIterable.projection(evaluationResult.getProjection());
        }

        if (evaluationResult.getOrderSpecifiers() != null) {
            findIterable.sort(evaluationResult.getOrderSpecifiers());
        }

        return findIterable;
    }

}
