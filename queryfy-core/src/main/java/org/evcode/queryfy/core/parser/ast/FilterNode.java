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

import org.evcode.queryfy.core.operator.Operator;
import org.evcode.queryfy.core.Visitor;

import java.util.List;

public final class FilterNode implements Node {

    private final Operator operator;
    private final String selector;
    private final List<Object> args;

    public FilterNode(final Operator operator, final String selector, final List<Object> args) {
        this.operator = operator;
        this.selector = selector;
        this.args = args;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getSelector() {
        return selector;
    }

    public List<Object> getArgs() {
        return args;
    }

    @Override
    public <R, A> R accept(final Visitor<R, A> visitor, A param) {
        return visitor.visit(this, param);
    }
}
