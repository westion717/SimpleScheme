package zeson.scheme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.junit.Assume;

import Zeson.AZLRJ.common.AbstractParsec;
import Zeson.AZLRJ.common.DefaultFailHandler;
import Zeson.AZLRJ.common.IParsec;
import Zeson.AZLRJ.common.Source;
import Zeson.AZLRJ.parsec.AndParsec;
import Zeson.AZLRJ.parsec.ClosureOrPlusParsec;
import Zeson.AZLRJ.parsec.IntegerParsec;
import Zeson.AZLRJ.parsec.OrParsec;
import Zeson.AZLRJ.parsec.action.ParsecLiteralSemanticAction;
import Zeson.AZLRJ.parsec.action.ParsecObjectSemanticAction;
import Zeson.AZLRJ.parsec.action.ParsecObjectsSemanticAction;
import Zeson.AZLRJ.parser.Parser;
import Zeson.AZLRJ.parser.WordBuilder;

public class SimpleScheme {

	static final IntegerParsec _integer = new IntegerParsec(
			new ParsecLiteralSemanticAction() {

				@Override
				public Expr doAction(String word, Source source) {
					return new IntegerExp(source.line, source.column,
							Integer.parseInt(word));
				}
			});
	static final SchemeIdentifier _ident = new SchemeIdentifier(
			new ParsecLiteralSemanticAction() {

				@Override
				public Expr doAction(String word, Source source) {
					return new IdentExp(source.line, source.column, word);
				}
			});
	static final WordBuilder wb = WordBuilder.get('\n', '\t', ' ', '\r');

	/*
	 * private static final IParsec<Expr> add = wb.createParsec("+"); private
	 * static final IParsec<Expr> sub = wb.createParsec("-");
	 * 
	 * private static final IParsec<Expr> mul = wb.createParsec("*"); private
	 * static final IParsec<Expr> div = wb.createParsec("/");
	 */

	static final IParsec left_p = wb.createParsec("(");
	static final IParsec right_p = wb.createParsec(")");
	static final IParsec left_b = wb.createParsec("[");
	static final IParsec right_b = wb.createParsec("]");

	static final IParsec Int = wb.createParsec(_integer);
	static final IParsec Ident = wb.createParsec(_ident);

	static final OrParsec left = new OrParsec(left_p, left_b);
	static final OrParsec right = new OrParsec(right_p, right_b);

	/*
	 * S-Exp --> ( Exp* ) Exp --> atom | S-Exp atom --> num | ident(op)
	 */

	static final OrParsec exp = new OrParsec(Int, Ident);

	static final IParsec exp_clousre = new ClosureOrPlusParsec(exp,
			new ParsecObjectsSemanticAction() {

				@Override
				public Object doAction(List<Object> resultObjects) {

					Vector<Expr> arg = new Vector<>();

					for (Object object : resultObjects) {
						Assume.assumeNotNull(object);
						Assume.assumeTrue(object instanceof Expr);
						arg.add((Expr) object);
					}

					return arg;
				}
			}, false);
	static final IParsec s_exp = new AndParsec(
			new ParsecObjectsSemanticAction() {

				@SuppressWarnings("unchecked")
				@Override
				public Object doAction(List<Object> resultObjects) {
					Assume.assumeNotNull(resultObjects.get(1));
					Assume.assumeTrue(resultObjects.get(1).getClass() == Vector.class);
					return new SExp((Vector<Expr>) resultObjects.get(1));

				}
			}, left, exp_clousre, right);

	static {
		exp.addParsec(s_exp);
	}

	static final OrParsec program = new OrParsec(
			new ParsecObjectSemanticAction() {

				@Override
				public Object doAction(Object resultObject) {
					Assume.assumeNotNull(resultObject);
					Assume.assumeTrue(resultObject instanceof Expr);
					program_exp = (Expr) resultObject;
					return null;
				}
			}, s_exp);

	static Expr program_exp = null;

	public static void main(String args[]) {

		DefaultFailHandler failHandler = new DefaultFailHandler(
				wb.getWhiteParsec()) {

			@Override
			public String getErrorMsg() {
				return failMsg.getMessage(Locale.CHINESE) + " at line:"
						+ failMsg.getLineNumber() + ", column:"
						+ failMsg.getColumnNumber();
			}

		};
		AbstractParsec.handler = failHandler;
		AbstractParsec.isMemoization = false;

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int count = 0;
		String line = null;
		try {
			System.out.print("SimpleScheme>");
			line = br.readLine();
			StringBuffer sb = new StringBuffer();

			while (line != null) {

				sb.append(line + "\n");
				for (char c : line.toCharArray()) {
					if (c == '(')
						count++;
					else if (c == ')') {

						count--;

					}

				}

				if (count <= 0) {
					Source source = new Source(sb);
					Parser parser = new Parser(source, program);

					if (parser.parse()) {
						Environment env = new Environment();
						EvalVisitor evalVisitor = new EvalVisitor(env);
						Value v = null;
						try {
							v = program_exp.accept(evalVisitor);
							System.out.print("SimpleScheme>");
							System.out.println(v);
						} catch (SchemeEvalError e) {
							System.out.println(e.getMessage());
						}

					} else {
						System.out.println(AbstractParsec.getFailMessage());
						

					}
					failHandler.reset();
					sb = new StringBuffer();
					System.out.print("SimpleScheme>");
				}

				line = br.readLine();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
