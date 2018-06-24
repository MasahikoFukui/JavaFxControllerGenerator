package jp.fukui.controllergenerator.pattern;

import java.io.*;

/** 字句パターン（基底クラス） */
public abstract class Pattern {
	public interface Observer {
		public void tokenDetected(Token t);
	}

	private Observer o;

	public Pattern(Observer o) {
		this.o = o;
	}

	protected void fire(Token t) {
		this.o.tokenDetected(t);
	}

	public abstract boolean comp(Reader in) throws IOException;
}
