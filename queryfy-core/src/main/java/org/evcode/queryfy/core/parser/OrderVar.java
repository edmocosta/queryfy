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
package org.evcode.queryfy.core.parser;

import org.evcode.queryfy.core.operator.OrderOperatorType;
import org.evcode.queryfy.core.parser.ast.OrderNode;
import org.parboiled.support.Var;

import java.util.Objects;

class OrderVar extends Var<OrderNode.OrderSpecifier> {

    private String selector;
    private OrderOperatorType operator = OrderOperatorType.ASC;

    public String getSelector() {
        return selector;
    }

    public boolean setSelector(String selector) {
        this.selector = selector;
        return true;
    }

    public OrderOperatorType getOperator() {
        return operator;
    }

    public boolean setOperator(OrderOperatorType operator) {
        this.operator = operator;
        return true;
    }

    @Override
    public OrderNode.OrderSpecifier getAndClear() {
        this.operator = OrderOperatorType.ASC;
        return super.getAndClear();
    }

    @Override
    public boolean clear() {
        this.operator = OrderOperatorType.ASC;
        return super.clear();
    }

    @Override
    public OrderNode.OrderSpecifier get() {
        if (super.isNotSet()) {
            build();
        }
        return super.get();
    }

    private void build() {
        Objects.requireNonNull(selector);
        Objects.requireNonNull(operator);
        set(new OrderNode.OrderSpecifier(selector, operator));
    }
}
