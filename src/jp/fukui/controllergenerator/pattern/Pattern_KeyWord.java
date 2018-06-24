package jp.fukui.controllergenerator.pattern;

import java.io.*;

/** 字句パターン（キーワード） */
public class Pattern_KeyWord extends Pattern {
	private TokenType type;
	private String    str;

	public Pattern_KeyWord(Observer o, TokenType type, String str) {
		super(o);
		this.type = type;
		this.str  = str;
	}

	public boolean comp(Reader in) throws IOException {
		in.mark(this.str.length());
		for(int i = 0; i < this.str.length(); i++) {
			int c = in.read();
			if(this.str.charAt(i) != (char) c) {
				in.reset();
				return(false);
			}
		}

		this.fire(new Token(this.type, this.str));
		return(true);
	}
}
