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

import org.evcode.queryfy.core.operator.OrderOperatorType;
import org.evcode.queryfy.core.Visitor;

import java.util.LinkedList;

public final class OrderNode implements Node {

    private LinkedList<OrderSpecifier> orderSpecifiers = new LinkedList<>();

    public OrderNode(final LinkedList<OrderSpecifier> orderSpecifiers) {
        this.orderSpecifiers = orderSpecifiers;
    }

    public LinkedList<OrderSpecifier> getOrderSpecifiers() {
        return orderSpecifiers;
    }

    @Override
    public <R, A> R accept(final Visitor<R, A> visitor, final A param) {
        return visitor.visit(this, param);
    }

    public static class OrderSpecifier {

        private final String selector;
        private final OrderOperatorType operator;

        public OrderSpecifier(final String selector, final OrderOperatorType operator) {
            this.selector = selector;
            this.operator = operator;
        }

        public String getSelector() {
            return selector;
        }

        public OrderOperatorType getOperator() {
            return operator;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OrderSpecifier that = (OrderSpecifier) o;

            if (selector != null ? !selector.equals(that.selector) : that.selector != null) return false;
            return operator == that.operator;
        }

        @Override
        public int hashCode() {
            int result = selector != null ? selector.hashCode() : 0;
            result = 31 * result + (operator != null ? operator.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "OrderSpecifier{" +
                    "selector='" + selector + '\'' +
                    ", operator=" + operator +
                    '}';
        }
    }
}
