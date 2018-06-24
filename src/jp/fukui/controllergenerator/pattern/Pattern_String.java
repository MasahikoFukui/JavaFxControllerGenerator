package jp.fukui.controllergenerator.pattern;

import java.io.*;

/** 字句パターン（文字列パターン） */
public class Pattern_String extends Pattern {
	private StringBuffer buf = new StringBuffer();

	public Pattern_String(Observer o) {
		super(o);
	}

	public boolean comp(Reader in) throws IOException {
		int c;

		this.buf.setLength(0);

		in.mark(1);
		c = in.read();
		if(c != '"') {
			in.reset();
			return(false);
		}

		for(;;) {
			c = in.read();
			if(c == '"') {
				this.fire(new Token(TokenType.STRING, buf.toString()));
				return(true);
			}

			if(c == '\\') {
				this.buf.append('\\');
				c = in.read();
			} else if((char) c == '\r' || (char) c == '\n' || (char) c == -1) {
				throw new RuntimeException("String is  broken!");
			}

			this.buf.append((char) c);
		}
	}
}
