package zeson.scheme;

import org.junit.Assume;

class SchemeEvalError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SchemeEvalError(String message) {
		super(message);

	}

	public SchemeEvalError(String message, SourceLoc sourceLoc) {
		super(message + " at " + "line: " + sourceLoc.getLine() + ", column: "
				+ sourceLoc.getColumn());
	}

}

public class EvalVisitor implements ISchemeVisitor {

	Environment env;

	public EvalVisitor(Environment env) {
		super();
		this.env = env;
	}

	@Override
	public Value visit(SExp sexp) {

		if (sexp.args.size() == 2)
			return handleInvoke(sexp);

		if (sexp.args.size() > 0
				&& sexp.args.get(0).getClass() == IdentExp.class) {

			IdentExp identExp = (IdentExp) sexp.args.get(0);
			switch (identExp.name) {
			case "lambda":

				return handleLambda(sexp);
			case "let":

				return handleLet(sexp);

			default:
				break;
			}
		}
		if (sexp.args.size() == 3)
			return handleOp(sexp);

		throw new SchemeEvalError("unsupport S-exp format", sexp.getSourceLoc());

	}

	private Value handleLet(SExp sexp) {
		if (sexp.args.size() != 3)
			throw new SchemeEvalError("let exp format is not right",
					sexp.getSourceLoc());

		if (sexp.args.get(1).getClass() != SExp.class)
			throw new SchemeEvalError("let exp format is not right", sexp.args
					.get(1).getSourceLoc());

		SExp pre_arg = (SExp) sexp.args.get(1);

		if (pre_arg.args.size() != 1)
			throw new SchemeEvalError("let exp format is not right",
					pre_arg.getSourceLoc());

		if (pre_arg.args.get(0).getClass() != SExp.class)
			throw new SchemeEvalError("let exp format is not right",
					pre_arg.args.get(0).getSourceLoc());

		SExp arg = (SExp) pre_arg.args.get(0);

		if (arg.args.size() != 2)
			throw new SchemeEvalError("let exp format is not right",
					arg.getSourceLoc());

		if (arg.args.get(0).getClass() != IdentExp.class)
			throw new SchemeEvalError("let exp format is not right", arg.args
					.get(0).getSourceLoc());

		IdentExp arg_name = (IdentExp) arg.args.get(0);
		Value v = arg.args.get(1).accept(this);
		this.env.enterScope();
		this.env.addSymbol(arg_name.name, v);

		Value returnValue = sexp.args.get(2).accept(this);
		this.env.outScope();
		return returnValue;
	}

	private Value handleOp(SExp sexp) {

		if (sexp.args.get(0).getClass() != IdentExp.class)
			throw new SchemeEvalError("operator format is not right", sexp.args
					.get(0).getSourceLoc());

		IdentExp operator = (IdentExp) sexp.args.get(0);

		Value left = sexp.args.get(1).accept(this);
		Value right = sexp.args.get(2).accept(this);

		switch (operator.name) {
		case "+":
			return left.add(right);
		case "-":
			return left.sub(right);
		case "*":
			return left.mul(right);
		case "/":
			return left.div(right);

		default:
			throw new SchemeEvalError("unrecognized operator" + operator.name,
					operator.getSourceLoc());
		}
	}

	private Value handleLambda(SExp sexp) {
		if (sexp.args.size() != 3)
			throw new SchemeEvalError("lambda format is not right",
					sexp.getSourceLoc());

		if (sexp.args.get(1).getClass() != SExp.class)
			throw new SchemeEvalError("lambda format is not right", sexp.args
					.get(1).getSourceLoc());

		SExp arg = (SExp) sexp.args.get(1);

		if (arg.args.size() != 1)
			throw new SchemeEvalError("lambda format is not right",
					arg.getSourceLoc());

		if (arg.args.get(0).getClass() != IdentExp.class)
			throw new SchemeEvalError("lambda format is not right", arg.args
					.get(0).getSourceLoc());

		return new Closure(env.fork(), sexp);
	}

	private Value handleInvoke(SExp sexp) {
		Value v1 = sexp.args.get(0).accept(this);
		Value v2 = sexp.args.get(1).accept(this);

		if (v1.getClass() == Closure.class) {
			Closure closure = (Closure) v1;
			SExp arg = (SExp) closure.sexp.args.get(1);
			IdentExp arg_name = (IdentExp) arg.args.get(0);
			closure.env.enterScope();
			closure.env.addSymbol(arg_name.name, v2);
			Expr exp = closure.sexp.args.get(2);

			Value expValue = exp.accept(new EvalVisitor(closure.env));
			closure.env.outScope();
			return expValue;

		} else {
			// can not reach here
			Assume.assumeTrue(false);
		}
		// can not reach here
		Assume.assumeTrue(false);
		return null;
	}

	@Override
	public Value visit(IntegerExp integerExp) {

		return new IntegerValue(integerExp.value);
	}

	@Override
	public Value visit(IdentExp identExp) {
		Value v = env.getSymbol(identExp.name);

		if (v == null)
			throw new SchemeEvalError("undefined variable " + identExp.name,
					identExp.getSourceLoc());

		return v;
	}
}
