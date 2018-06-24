package jp.fukui.controllergenerator.pattern;

/** トークン */
public class Token {
	public Token(TokenType type, String data) {
		this.type = type;
		this.data = data;
	}

	public TokenType type;
	public String    data;
}
