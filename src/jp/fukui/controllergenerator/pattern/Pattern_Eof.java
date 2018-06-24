package jp.fukui.controllergenerator.pattern;

import java.io.*;

/** 字句パターン（ファイル終端） */
public class Pattern_Eof extends Pattern {
	public Pattern_Eof(Observer o) {
		super(o);
	}

	public boolean comp(Reader in) throws IOException {
		int c;

		in.mark(1);
		c = in.read();
		if(c != -1) {
			in.reset();
			return(false);
		}

		this.fire(new Token(TokenType.EOF, ""));
		return(true);
	}
}
