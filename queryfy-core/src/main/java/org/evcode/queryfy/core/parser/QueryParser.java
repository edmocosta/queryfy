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

import org.evcode.queryfy.core.operator.*;
import org.evcode.queryfy.core.parser.ast.*;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.support.StringBuilderVar;
import org.parboiled.support.StringVar;

import java.util.*;
import java.util.stream.Collectors;

public class QueryParser extends BaseParser<Object> {

    final ParserConfig config;

    public QueryParser() {
        this.config = ParserConfig.DEFAULT;
    }

    public QueryParser(ParserConfig config) {
        this.config = config;
    }

    //Common rules
    Rule WS() {
        return OneOrMore(Ch(' '));
    }

    Rule OptionalWS() {
        return ZeroOrMore(Ch(' '));
    }

    Rule Minus() {
        return Ch('-');
    }

    Rule Plus() {
        return Ch('+');
    }

    Rule Comma() {
        return Ch(',');
    }

    Rule ArgumentsSeparator() {
        return String(config.getGrammar().getArgsSeparator());
    }

    Rule Integer() {
        return OneOrMore(Digit());
    }

    Rule Dot() {
        return Ch('.');
    }

    Rule Colon() {
        return Ch(':');
    }

    Rule FourDigits() {
        return NTimes(4, Digit());
    }

    Rule ThreeDigits() {
        return NTimes(3, Digit());
    }

    Rule TwoDigits() {
        return NTimes(2, Digit());
    }

    Rule Digit() {
        return CharRange('0', '9');
    }

    Rule Date() {
        LocalDateVar date = new LocalDateVar();
        return Sequence(
                FourDigits(), date.appendYear(match()),
                Minus(), TwoDigits(), date.appendMonth(match()),
                Minus(), TwoDigits(), date.appendDay(match()),
                push(date.get())
        );
    }

    Rule ZoneOffset() {
        return FirstOf(IgnoreCase('Z'),
                Sequence(FirstOf(Minus(), Plus()),
                        FirstOf(Sequence(TwoDigits(), Optional(Colon()), TwoDigits(), Optional(Colon(), TwoDigits())),
                                Sequence(TwoDigits(), Optional(Colon()), TwoDigits()),
                                TwoDigits(),
                                Digit()
                        )
                )
        );
    }

    Rule ZoneId() {
        return Sequence(Ch('['),
                ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\"\\[]")), ANY)),
                Ch(']'));
    }

    Rule DateTime() {
        DateTimeVar dateTime = new DateTimeVar();
        return Sequence(
                FourDigits(), dateTime.appendYear(match()),
                Minus(), TwoDigits(), dateTime.appendMonth(match()),
                Minus(), TwoDigits(), dateTime.appendDay(match()),
                Ch('T'), TwoDigits(), dateTime.appendHour(match()),
                Colon(), TwoDigits(), dateTime.appendMinute(match()),
                Optional(Colon(), TwoDigits(), dateTime.appendSecond(match())),
                Optional(Sequence(FirstOf(Comma(), Dot()), Sequence(ThreeDigits(), dateTime.appendNanosecond(match())))),
                Optional(ZoneOffset(), dateTime.appendZoneOffset(match())),
                Optional(OptionalWS(), ZoneId(), dateTime.appendZoneId(match())),
                push(dateTime.get())
        );
    }

    Rule Time() {
        TimeVar time = new TimeVar();
        return Sequence(
                TwoDigits(), time.appendHour(match()), Colon(),
                TwoDigits(), time.appendMinute(match()),
                Optional(Colon(), TwoDigits(), time.appendSecond(match())),
                Optional(Sequence(FirstOf(Comma(), Dot()), Sequence(ThreeDigits(), time.appendNanosecond(match())))),
                Optional(ZoneOffset(), time.appendZoneOffset(match())),
                push(time.get()));
    }

    Rule Locale() {
        return Sequence(NTimes(2, CharRange('a', 'z')),
                Minus(),
                NTimes(2, CharRange('A', 'Z')));
    }

    Rule InsideBrackets(Rule rule) {
        return Sequence(Ch('['), rule, Ch(']'));
    }

    Rule NumericTypeQualifier() {
        return FirstOf(
                IgnoreCase(NumberVar.FLOAT),
                IgnoreCase(NumberVar.LONG),
                IgnoreCase(NumberVar.INTEGER),
                IgnoreCase(NumberVar.DOUBLE)
        );
    }

    Rule DoubleQuoteString() {
        StringBuilderVar str = new StringBuilderVar();
        return Sequence('"', ZeroOrMore(FirstOf(Escape(str),
                Sequence(TestNot(AnyOf("\r\n\"\\")), ANY, str.append(match())))).suppressSubnodes(), '"',
                push(str.getString()));
    }

    Rule SingleQuoteString() {
        StringBuilderVar str = new StringBuilderVar();
        return Sequence('\'', ZeroOrMore(FirstOf(Escape(str),
                Sequence(TestNot(AnyOf("\r\n'\\")), ANY, str.append(match())))).suppressSubnodes(), '\'',
                push(str.getString()));
    }

    Rule Escape(StringBuilderVar str) {
        return Sequence('\\', FirstOf(AnyOf("btnfr\"\'\\"), OctalEscape(), UnicodeEscape()), str.append(match()));
    }

    Rule OctalEscape() {
        return FirstOf(
                Sequence(CharRange('0', '3'), CharRange('0', '7'), CharRange('0', '7')),
                Sequence(CharRange('0', '7'), CharRange('0', '7')),
                CharRange('0', '7')
        );
    }

    Rule UnicodeEscape() {
        return Sequence(OneOrMore('u'), HexDigit(), HexDigit(), HexDigit(), HexDigit());
    }

    Rule HexDigit() {
        return FirstOf(CharRange('a', 'f'), CharRange('A', 'F'), CharRange('0', '9'));
    }

    Rule Arguments(ListVar listValues) {
        return Sequence(
                Value(),
                listValues.add(pop()),
                ZeroOrMore(Sequence(OptionalWS(),
                        ArgumentsSeparator(),
                        OptionalWS(),
                        Value(),
                        listValues.add(pop()))
                )
        );
    }

    //Grammar operators
    Rule ComparisionOperator() {
        return FirstOf(
                toOperator(ComparisionOperatorType.LOWER_EQUAL),
                toOperator(ComparisionOperatorType.LOWER),
                toOperator(ComparisionOperatorType.GREATER_EQUAL),
                toOperator(ComparisionOperatorType.GREATER)
        );
    }

    Rule EqualOperator() {
        return FirstOf(
                toOperator(ComparisionOperatorType.EQUAL),
                toOperator(ComparisionOperatorType.NOT_EQUAL)
        );
    }

    Rule IsNullOperator() {
        return Sequence(toOperator(SelectorOperatorType.IS_NULL),
                push(SelectorOperatorType.IS_NULL));
    }

    Rule IsNotNullOperator() {
        return Sequence(toOperator(SelectorOperatorType.IS_NOT_NULL),
                push(SelectorOperatorType.IS_NOT_NULL));
    }

    Rule IsTrueOperator() {
        return Sequence(toOperator(SelectorOperatorType.IS_TRUE),
                push(SelectorOperatorType.IS_TRUE));
    }

    Rule IsFalseOperator() {
        return Sequence(toOperator(SelectorOperatorType.IS_FALSE),
                push(SelectorOperatorType.IS_FALSE));
    }

    Rule IsEmptyOperator() {
        return Sequence(toOperator(SelectorOperatorType.IS_EMPTY),
                push(SelectorOperatorType.IS_EMPTY));
    }

    Rule IsNotEmptyOperator() {
        return Sequence(toOperator(SelectorOperatorType.IS_NOT_EMPTY),
                push(SelectorOperatorType.IS_NOT_EMPTY));
    }

    Rule InOperator() {
        return Sequence(toOperator(ListOperatorType.IN), push(ListOperatorType.IN));
    }

    Rule NotInOperator() {
        return Sequence(toOperator(ListOperatorType.NOT_IN), push(ListOperatorType.NOT_IN));
    }

    Rule LikeOperator() {
        return Sequence(toOperator(StringOperatorType.LIKE), push(StringOperatorType.LIKE));
    }

    Rule NotLikeOperator() {
        return Sequence(toOperator(StringOperatorType.NOT_LIKE), push(StringOperatorType.NOT_LIKE));
    }

    //Value rules
    Rule Numeric() {
        NumberVar number = new NumberVar();
        return Sequence(
                Sequence(Sequence(Optional(Sequence(Minus(), OptionalWS())), Integer(), Optional(Dot(), Integer())),
                        number.setNumber(match())),
                Optional(Sequence(NumericTypeQualifier(), number.setTypeQualifier(match()))),
                push(number.get()));
    }

    Rule True() {
        return Sequence(String(config.getGrammar().getTrueValue()), push(Boolean.TRUE));
    }

    Rule False() {
        return Sequence(String(config.getGrammar().getFalseValue()), push(Boolean.FALSE));
    }

    Rule String() {
        return FirstOf(SingleQuoteString(), DoubleQuoteString());
    }

    Rule Temporal() {
        return FirstOf(DateTime(), Date(), Time());
    }

    Rule Value() {
        return FirstOf(Temporal(), Numeric(), String(), UserFunctionValue());
    }

    Rule UserFunctionValue() {
        ListVar<Object> list = new ListVar<>(true);
        StringVar function = new StringVar();
        return Sequence(config.getGrammar().getUserFunctionPrefix(), Sequence(Selector(), function.set(match()), drop()),
                OptionalWS(),
                Ch('('), OptionalWS(), Optional(Arguments(list)), OptionalWS(), Ch(')'),
                push(new UserFunctionNode(function.getAndClear(), list.isSet() ? list.getAndClear().toArray() : new Object[]{})));
    }

    //Operations
    Rule SelectorOperation() {
        return Sequence(Selector(), WS(),
                FirstOf(IsNotNullOperator(), IsNullOperator(), IsTrueOperator(), IsFalseOperator(),
                        IsNotEmptyOperator(), IsEmptyOperator()),
                pushSelectorOperation());
    }

    Rule InOperation() {
        ListVar<Object> list = new ListVar<>();
        return Sequence(Selector(), WS(), FirstOf(NotInOperator(), InOperator()),
                OptionalWS(),
                Ch('('),
                OptionalWS(), Arguments(list), OptionalWS(),
                Ch(')'), pushListOperation(list.get()));
    }

    Rule LikeOperation() {
        return Sequence(Selector(), WS(), FirstOf(NotLikeOperator(), LikeOperator()),
                WS(), String(), pushFilterOperation());
    }

    Rule EqualOperation() {
        return Sequence(Selector(), OptionalWS(), EqualOperator(), pushOperator(),
                OptionalWS(), FirstOf(True(), False(), Value()), pushFilterOperation());
    }

    Rule ComparisionOperation() {
        return Sequence(Selector(), OptionalWS(), ComparisionOperator(), pushOperator(),
                OptionalWS(), Value(), pushFilterOperation());
    }

    Rule Operations() {
        return FirstOf(EqualOperation(), ComparisionOperation(), SelectorOperation(), LikeOperation(), InOperation());
    }

    Rule LogicalOperation() {
        return FirstOf(Sequence(Ch('('), OptionalWS(), OrOperation(), OptionalWS(), Ch(')')), Operations());
    }

    Rule AndOperation() {
        return Sequence(LogicalOperation(), ZeroOrMore(Sequence(WS(), toOperator(LogicalOperatorType.AND), WS(),
                LogicalOperation(), pushLogicalOperation(LogicalOperatorType.AND))));
    }

    Rule OrOperation() {
        return Sequence(AndOperation(), ZeroOrMore(Sequence(WS(), toOperator(LogicalOperatorType.OR), WS(),
                AndOperation(), pushLogicalOperation(LogicalOperatorType.OR))));
    }

    //Query structure
    Rule Select() {
        return Sequence(toOperator(QueryOperatorType.SELECT), WS());
    }

    Rule QualifiedSelector() {
        return Sequence(SelectorPattern(), ZeroOrMore(Dot(), SelectorPattern()));
    }

    Rule Selector() {
        return Sequence(QualifiedSelector(), push(match()));
    }

    @MemoMismatches
    Rule SelectorPattern() {
        return OneOrMore(new SelectorMatcher());
    }

    Rule ProjectionSelectors() {
        ListVar<String> fields = new ListVar<>();
        return Sequence(
                Sequence(QualifiedSelector(), push(match())),
                fields.add((String) pop()),
                ZeroOrMore(Sequence(OptionalWS(),
                        String(config.getGrammar().getArgsSeparator()),
                        OptionalWS(),
                        Sequence(QualifiedSelector(), push(match())),
                        fields.add((String) pop()))
                ),
                pushProjectionSelectors(fields.get())
        );
    }

    Rule Where() {
        return Sequence(OptionalWS(), toOperator(QueryOperatorType.WHERE), WS());
    }

    Rule Limit() {
        return Sequence(toOperator(QueryOperatorType.LIMIT), WS(),
                Integer(), push(match()), OptionalWS(), ArgumentsSeparator(), OptionalWS(), Integer(),
                push(match()), pushLimitOperation());
    }

    Rule OrderSpecifier(OrderVar orderVar) {
        return Sequence(QualifiedSelector(), orderVar.setSelector(match()),
                Optional(WS(), FirstOf(toOperator(OrderOperatorType.ASC), toOperator(OrderOperatorType.DESC)),
                        orderVar.setOperator((OrderOperatorType) config.getGrammar().getOperator(match()))));
    }

    Rule Order() {
        ListVar<OrderNode.OrderSpecifier> list = new ListVar<>();
        OrderVar orderVar = new OrderVar();

        return Sequence(
                toOperator(QueryOperatorType.ORDER),
                WS(),
                OrderSpecifier(orderVar),
                list.add(orderVar.get()),
                orderVar.clear(),
                ZeroOrMore(Sequence(OptionalWS(),
                        String(config.getGrammar().getArgsSeparator()),
                        OptionalWS(),
                        OrderSpecifier(orderVar),
                        list.add(orderVar.get()),
                        orderVar.clear()
                )), pushOrderByOperation(list.get()));
    }

    //Parser entry point
    public Rule Query() {
        return Sequence(Optional(Select(), ProjectionSelectors(), Optional(FirstOf(Where(), Order(), Limit()))),
                Optional(OrOperation()),
                Optional(OptionalWS(), Order(), Optional(Limit())),
                Optional(OptionalWS(), Limit()), EOI);
    }

    //AST builder operations
    boolean pushOrderByOperation(LinkedList<OrderNode.OrderSpecifier> orderList) {
        push(new OrderNode(orderList));
        return true;
    }

    boolean pushLimitOperation() {
        Long limit = Long.parseLong(pop().toString());
        Long offset = Long.parseLong(pop().toString());
        return push(new LimitNode(offset, limit));
    }

    boolean pushProjectionSelectors(List<String> selectors) {
        ProjectionNode projection = new ProjectionNode(selectors
                .stream()
                .collect(Collectors.toSet()));
        return push(projection);
    }

    boolean pushOperator() {
        String value = match();
        Operator operator = config.getGrammar().getOperator(value);
        return push(operator);
    }

    boolean pushListOperation(List<Object> list) {
        ListOperatorType operator = (ListOperatorType) pop();
        String selector = (String) pop();

        List<Object> parsedValues = list.stream().map(this::parseValue)
                .collect(Collectors.toList());

        FilterNode node = new FilterNode(operator, selector, parsedValues);
        return push(node);
    }

    boolean pushFilterOperation() {
        Object value = parseValue(pop());
        Operator operator = (Operator) pop();
        String selector = (String) pop();
        FilterNode node = new FilterNode(operator, selector, Collections.singletonList(value));
        return push(node);
    }

    boolean pushSelectorOperation() {
        SelectorOperatorType operator = (SelectorOperatorType) pop();
        String selector = (String) pop();
        FilterNode node = new FilterNode(operator, selector, Collections.emptyList());
        return push(node);
    }

    boolean pushLogicalOperation(LogicalOperatorType operator) {
        Node rightNode = (Node) pop();
        Node leftNode = (Node) pop();

        Node logicalNode = operator == LogicalOperatorType.AND ?
                new AndNode(rightNode, leftNode) :
                new OrNode(rightNode, leftNode);

        return push(logicalNode);
    }

    Object parseValue(Object value) {
        if (value instanceof UserFunctionNode) {
            Objects.requireNonNull(config.getUserFunctionInvoker());
            UserFunctionNode functionValue = (UserFunctionNode) value;

            if (functionValue.getArguments() != null && functionValue.getArguments().length > 0) {
                for (int i = 0; i < functionValue.getArguments().length; i++) {
                    if(functionValue.getArguments()[i] instanceof UserFunctionNode){
                        functionValue.getArguments()[i] = parseValue(functionValue.getArguments()[i]);
                    }
                }
            }

            Object parsedValue = config.getUserFunctionInvoker()
                    .invoke(functionValue.getFunction(), functionValue.getArguments());

            return parsedValue;
        }

        return value;
    }

    Rule toOperator(Operator operatorType) {
        Object[] parts = operatorParts(operatorType);

        if (parts.length == 1) {
            if (parts[0] instanceof Rule)
                return (Rule) parts[0];

            throw new RuntimeException("General error");
        }

        return Sequence(parts);
    }

    Object[] operatorParts(Operator operatorType, Object... moreRules) {

        Set<String> symbols = operatorType.getOperatorSymbols(config.getGrammar());

        LinkedList<Rule> symbolsRule = new LinkedList<>();
        for (String symbol : symbols) {
            symbolsRule.add(operatorSymbolRule(symbol));
        }

        Rule rule = FirstOf(symbolsRule.toArray(new Object[0]));

        LinkedList<Object> allRules = new LinkedList<>();
        allRules.add(rule);
        allRules.addAll(Arrays.asList(moreRules));

        return allRules.toArray(new Object[0]);
    }

    Rule operatorSymbolRule(String operatorSymbol) {
        if (!operatorSymbol.contains(" ")) {
            return operatorSymbol.length() == 1 ?
                    Ch(operatorSymbol.charAt(0)) :
                    String(operatorSymbol);
        }

        LinkedList<Rule> symbolParts = new LinkedList<>();
        String[] parts = operatorSymbol.split(" ");
        for (int i = 0; i < parts.length; i++) {
            Rule operatorValue = parts[i].length() == 1 ?
                    Ch(parts[i].charAt(0)) :
                    String(parts[i]);

            symbolParts.add(operatorValue);

            if ((i + 1) < parts.length) {
                symbolParts.add(Ch(' '));
            }
        }

        return Sequence(symbolParts.toArray(new Object[0]));
    }
}
