package zeson.scheme;

import Zeson.AZLRJ.common.EOFStream;
import Zeson.AZLRJ.common.Source;
import Zeson.AZLRJ.parsec.TermParsec;
import Zeson.AZLRJ.parsec.action.ParsecLiteralSemanticAction;

public class SchemeIdentifier extends TermParsec {

	public SchemeIdentifier(ParsecLiteralSemanticAction literalSemanticAction) {
		super("lisp identifier", literalSemanticAction);
	}

	@Override
	public String parseString(Source inputString) {
		StringBuilder str = new StringBuilder();

		while (true) {
			char c;
			try {
				c = inputString.getCurrentChar();
			} catch (EOFStream e) {
				break;
			}

			if (Character.isWhitespace(c) || c == '(' || c == ')' || c == '['
					|| c == ']')
				break;

			str.append(c);
			inputString.peek(1);

		}

		if (str.length() == 0) {
			return null;
		}
		inputString.column += str.length();
		return str.toString();
	}
}
