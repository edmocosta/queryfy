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

package org.evcode.queryfy.mongodb.functions;

import org.bson.types.ObjectId;
import org.evcode.queryfy.core.parser.functions.AbstractFunctionInvoker;
import org.evcode.queryfy.core.parser.functions.CustomFunction;
import org.evcode.queryfy.core.parser.functions.DefaultFunctionInvoker;

public class ObjectIdCustomFunction extends CustomFunction {

    public ObjectIdCustomFunction() {
        super(new ObjectIdFunctionInvoker(), DefaultFunctionInvoker.PROVIDED_PRIORITY);
    }

    static class ObjectIdFunctionInvoker extends AbstractFunctionInvoker {

        public static String OID = "oid";

        @Override
        public Object invoke(String function, Object... args) {
            if (OID.equals(function)) {
                return new ObjectId(String.valueOf(args[0]));
            }

            throw new IllegalArgumentException("Function " + function + " is not supported.");
        }

        @Override
        public boolean canHandle(String function, Object... args) {
            return OID.equals(function) && args.length == 1;
        }
    }
}