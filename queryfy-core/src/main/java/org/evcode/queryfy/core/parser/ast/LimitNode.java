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
package org.evcode.queryfy.core.parser.ast;

import org.evcode.queryfy.core.Visitor;

public final class LimitNode implements Node {

    private final Long offset;
    private final Long limit;

    public LimitNode(final Long offset, final Long limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Long getOffset() {
        return offset;
    }

    public Long getLimit() {
        return limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LimitNode limitNode = (LimitNode) o;

        if (offset != null ? !offset.equals(limitNode.offset) : limitNode.offset != null) return false;
        return limit != null ? limit.equals(limitNode.limit) : limitNode.limit == null;
    }

    @Override
    public int hashCode() {
        int result = offset != null ? offset.hashCode() : 0;
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        return result;
    }

    @Override
    public <R, A> R accept(final Visitor<R, A> visitor, final A param) {
        return visitor.visit(this, param);
    }
}
