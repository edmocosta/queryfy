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
package org.evcode.queryfy.core;

import org.evcode.queryfy.core.parser.ast.*;

public interface Visitor<R, P> {

    <T> T visit(ProjectionNode node, P param);

    R visit(AndNode node, P param);

    R visit(OrNode node, P param);

    R visit(FilterNode node, P param);

    <T> T visit(OrderNode node, P param);

    <T> T visit(LimitNode node, P param);
}