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

import org.parboiled.MatcherContext;
import org.parboiled.matchers.CustomMatcher;

class SelectorMatcher extends CustomMatcher {

    protected SelectorMatcher() {
        super("SelectorMatcher");
    }

    @Override
    public final boolean isSingleCharMatcher() {
        return true;
    }

    @Override
    public final boolean canMatchEmpty() {
        return false;
    }

    @Override
    public boolean isStarterChar(char c) {
        return isAllowedChar(c);
    }

    @Override
    public final char getStarterChar() {
        return 'a';
    }

    @Override
    public final <V> boolean match(final MatcherContext<V> context) {
        if (!isAllowedChar(context.getCurrentChar())) {
            return false;
        }
        context.advanceIndex(1);
        context.createNode();
        return true;
    }

    protected boolean isAllowedChar(char c) {
        return Character.isJavaIdentifierPart(c) && c != 32;
    }
}
