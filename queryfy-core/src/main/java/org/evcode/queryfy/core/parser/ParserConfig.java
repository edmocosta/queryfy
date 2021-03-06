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

import org.evcode.queryfy.core.lexer.DefaultGrammar;
import org.evcode.queryfy.core.lexer.Grammar;
import org.evcode.queryfy.core.parser.functions.DefaultFunctionInvoker;
import org.evcode.queryfy.core.parser.functions.FunctionInvoker;

public final class ParserConfig {

    public static final ParserConfig DEFAULT = ParserConfig.builder().build();
    private final Grammar grammar;
    private final FunctionInvoker functionInvoker;

    private ParserConfig(Grammar grammar, FunctionInvoker functionInvoker) {
        this.grammar = grammar;
        this.functionInvoker = functionInvoker;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public FunctionInvoker getFunctionInvoker() {
        return functionInvoker;
    }

    public static class Builder {

        private Grammar grammar = new DefaultGrammar();
        private FunctionInvoker functionInvoker = new DefaultFunctionInvoker();

        public Builder withGrammar(final Grammar grammar) {
            this.grammar = grammar;
            return this;
        }

        public Builder withFunctionInvoker(final FunctionInvoker resolver) {
            this.functionInvoker = resolver;
            return this;
        }


        public ParserConfig build() {
            return new ParserConfig(grammar, functionInvoker);
        }
    }
}