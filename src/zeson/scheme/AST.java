package zeson.scheme;

import java.util.Vector;

import org.junit.Assume;

class SourceLoc {
	private int line;
	private int column;

	public SourceLoc(int line, int column) {
		super();
		this.line = line;
		this.column = column;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	@Override
	public String toString() {
		return "SourceLoc [line=" + line + ", column=" + column + "]";
	}

}

public abstract class AST {

}

abstract class Expr {
	public abstract SourceLoc getSourceLoc();

	public abstract Value accept(ISchemeVisitor v);

	public abstract void print();
}

enum Type {
	Int, Double, String, Bool, Void, Lambda
}

class SExp extends Expr {

	Vector<Expr> args;

	public SExp(Vector<Expr> args) {
		this.args = args;
	}

	@Override
	public void print() {
		System.out.print("(");
		for (Expr expr : args) {
			expr.print();
			System.out.print(" ");
		}
		System.out.print(")");
	}

	@Override
	public SourceLoc getSourceLoc() {
		Assume.assumeTrue(false);
		return null;
	}

	@Override
	public Value accept(ISchemeVisitor v) {

		return v.visit(this);
	}

}

class IntegerExp extends Expr {
	int value;
	SourceLoc sourceLoc;

	public IntegerExp(int line, int column, int value) {
		this.value = value;
		sourceLoc = new SourceLoc(line, column);
	}

	@Override
	public void print() {
		System.out.print(value);

	}

	@Override
	public SourceLoc getSourceLoc() {
		return sourceLoc;
	}

	@Override
	public Value accept(ISchemeVisitor v) {
		return v.visit(this);
	}

}

class IdentExp extends Expr {
	String name;
	SourceLoc sourceLoc;

	public IdentExp(int line, int column, String name) {
		this.name = name;
		sourceLoc = new SourceLoc(line, column);
	}

	@Override
	public void print() {
		System.out.print(name);
	}

	@Override
	public SourceLoc getSourceLoc() {

		return sourceLoc;
	}

	@Override
	public Value accept(ISchemeVisitor v) {
		return v.visit(this);
	}

}