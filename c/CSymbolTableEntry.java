package lang.c;

import lang.SymbolTableEntry;

public class CSymbolTableEntry extends SymbolTableEntry {
    private CType type;         // 型
    private int size;           // メモリに確保スべきワード数
    private boolean constp;     // 定数宣言か
    private boolean isGrobal;   // globalか
    private int address;        // 変数のアドレス

    public CSymbolTableEntry(CType type, int size, boolean constp, boolean isGrobal, int address) {
        this.type = type;
        this.size = size;
        this.constp = constp;
        this.isGrobal = isGrobal;
        this.address = address;
    }

    @Override
    public String toExplainString() {
        return type.toString() + "," + size + (constp ? "定数" : "変数");
    }

    public boolean isConstant() {
        return constp;
    }

    public CType getType() {
        return type;
    }
}
