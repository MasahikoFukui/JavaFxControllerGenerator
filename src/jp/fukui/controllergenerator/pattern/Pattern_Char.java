package jp.fukui.controllergenerator.pattern;

import java.io.*;

/** 字句パターン（文字パターン） */
public class Pattern_Char extends Pattern {
	private StringBuffer buf = new StringBuffer();

	public Pattern_Char(Observer o) {
		super(o);
	}

	public boolean comp(Reader in) throws IOException {
		int c;

		in.mark(1);
		c = in.read();
		if(c != '\'') {
			in.reset();
			return(false);
		}

		c = in.read();
		if(c == '\\') {
			this.buf.append('\\');
			c = in.read();
		} else if((char) c == '\r' || (char) c == '\n' || (char) c == -1) {
			throw new RuntimeException("String is  broken!");
		}

		this.buf.append((char) c);

		c = in.read();
		if(c != '\'')
			throw new RuntimeException("Quotation is broken!");

		this.fire(new Token(TokenType.CHAR, buf.toString()));
		return(true);
	}
}
