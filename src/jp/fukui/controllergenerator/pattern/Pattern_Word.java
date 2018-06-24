package jp.fukui.controllergenerator.pattern;

import java.io.*;

/** 字句パターン（名前、ID、パス） */
public class Pattern_Word extends Pattern {
	private StringBuffer buf = new StringBuffer();

	public Pattern_Word(Observer o) {
		super(o);
	}

	public boolean comp(Reader in) throws IOException {
		char c;

		this.buf.setLength(0);

		in.mark(1);
		c = (char) in.read();
		if(!('A' <= c && 'Z' >= c || 'a' <= c && 'z' >= c || '_' == c)) {
			in.reset();
			return(false);
		}
		this.buf.append(c);

		for(;;) {
			in.mark(1);
			c = (char) in.read();

			if(!('0' <= c && '9' >= c || 'A' <= c && 'Z' >= c || 'a' <= c && 'z' >= c || '_' == c || ':' == c || '.' == c || '*' == c)) {
				in.reset();
				fire(new Token(TokenType.WORD, buf.toString()));
				return(true);
			}

			this.buf.append(c);
		}
	}
}
