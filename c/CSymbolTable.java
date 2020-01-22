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

    private OneSymbolTable global = new OneSymbolTable();
    private OneSymbolTable local;

    public void setGlobalEntry(String name, CSymbolTableEntry e) {
        global.register(name, e);
    }

    public CSymbolTableEntry globalSearch(String name) {
        return global.get(name);
    }

    public void printGlobal() {
        global.show();
    }
}
