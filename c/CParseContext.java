package lang.c;

import lang.IOContext;
import lang.ParseContext;

public class CParseContext extends ParseContext {
	private CSymbolTable table;

	public CParseContext(IOContext ioCtx, CTokenizer tknz) {
		super(ioCtx, tknz);
		table = new CSymbolTable();
	}

	@Override
	public CTokenizer getTokenizer() {
		return (CTokenizer) super.getTokenizer();
	}

	public CSymbolTable getTable() {
		return table;
	}

	private int seqNo = 0;

	public int getSeqId() {
		return ++seqNo;
	}
}
