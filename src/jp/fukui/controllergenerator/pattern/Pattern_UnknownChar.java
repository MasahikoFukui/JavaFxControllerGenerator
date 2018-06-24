package jp.fukui.controllergenerator.pattern;

import java.io.*;

/** 字句パターン（不明な文字） */
public class Pattern_UnknownChar extends Pattern {
	public Pattern_UnknownChar(Observer o) {
		super(o);
	}

	public boolean comp(Reader in) throws IOException {
		String c = String.valueOf((char) in.read());
		fire(new Token(TokenType.UK, c));
		return(true);
	}
}
