package lang.c;

import lang.SymbolTable;

public class CSymbolTable {
    private class OneSymbolTable extends SymbolTable<CSymbolTableEntry> {

        @Override
        public CSymbolTableEntry register(String name, CSymbolTableEntry cSymbolTableEntry) {
            return put(name, cSymbolTableEntry);
        }

        @Override
        public CSymbolTableEntry search(String name) {
            return get(name);
        }
    }

    private OneSymbolTable global;
    private OneSymbolTable local;
}
