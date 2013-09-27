// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.java_cup.runtime;

final class DefaultSymbolFactory implements SymbolFactory {

    public Symbol newSymbol(String name, int id, Symbol left, Symbol right, Object value) {
        return new Symbol(id, left.line, left.column, value);
    }

    public Symbol newSymbol(String name, int id, Symbol left, Symbol right) {
        return newSymbol(name, id, left, right, null);
    }

    public Symbol startSymbol(String name, int id, int state) {
        return new Symbol(id, -1, -1, null, state);
    }

    public Symbol newSymbol(String name, int id) {
        return new Symbol(id, -1, -1, null);
    }

    public Symbol newSymbol(String name, int id, Object value) {
        return new Symbol(id, -1, -1, value);
    }
}
