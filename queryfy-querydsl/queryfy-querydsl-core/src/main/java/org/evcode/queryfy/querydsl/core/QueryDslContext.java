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

import com.mysema.query.types.EntityPath;
import com.mysema.query.types.Expression;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryDslContext {

    private final Map<String, Expression> queryPaths;
    private final Map<String, Expression> projectionPaths;
    private EntityPath entityPath;

    protected QueryDslContext(EntityPath entityPath, Map<String, Expression> queryPaths, Map<String,
            Expression> projectionPaths) {
        this.entityPath = entityPath;
        this.queryPaths = queryPaths;
        this.projectionPaths = projectionPaths;
    }

    public static Builder from(EntityPath type) {
        return new Builder(type);
    }

    public Expression resolveProjectionPath(String path) {
        Expression expression = projectionPaths.get(path);
        if (expression == null) {
            throw new IllegalArgumentException("Projection path " + path + " not found");
        }
        return expression;
    }

    public Expression resolveQueryPath(String path) {
        Expression expression = queryPaths.get(path);
        if (expression == null) {
            throw new IllegalArgumentException("Query path " + path + " not found");
        }
        return expression;
    }

    public EntityPath getEntityPath() {
        return entityPath;
    }

    public Map<String, Expression> getQueryPaths() {
        return Collections.unmodifiableMap(queryPaths);
    }

    public Map<String, Expression> getProjectionPaths() {
        return Collections.unmodifiableMap(projectionPaths);
    }

    public static class Builder {

        private Map<String, Expression> queryPaths = new HashMap<>();
        private Map<String, Expression> projectionPaths = new HashMap<>();
        private EntityPath entityPath;

        public Builder(EntityPath entityPath) {
            this.entityPath = entityPath;
        }

        public Builder withPath(String name, Expression path) {
            return withQueryPath(name, path).withProjectionPath(name, path);
        }

        public Builder withQueryPath(String name, Expression path) {
            queryPaths.put(name, path);
            return this;
        }

        public Builder withProjectionPath(String name, Expression path) {
            projectionPaths.put(name, path);
            return this;
        }

        public QueryDslContext build() {
            return new QueryDslContext(entityPath, queryPaths, projectionPaths);
        }
    }
}