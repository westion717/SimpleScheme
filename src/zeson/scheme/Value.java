package zeson.scheme;

import org.junit.Assume;

public abstract class Value {
	public abstract Type getValueType();

	public Value add(Value v) {
		// can not go here
		Assume.assumeTrue(false);
		return null;
	}

	public Value sub(Value v) {
		// can not go here
		Assume.assumeTrue(false);
		return null;
	}

	public Value mul(Value v) {
		// can not go here
		Assume.assumeTrue(false);
		return null;
	}

	public Value div(Value v) {
		// can not go here
		Assume.assumeTrue(false);
		return null;
	}

	protected Value added(IntegerValue v) {
		// can not go here
		Assume.assumeTrue(false);
		return null;
	}

	protected Value subed(IntegerValue v) {
		// can not go here
		Assume.assumeTrue(false);
		return null;
	}

	protected Value muled(IntegerValue v) {
		// can not go here
		Assume.assumeTrue(false);
		return null;
	}

	protected Value dived(IntegerValue v) {
		// can not go here
		Assume.assumeTrue(false);
		return null;
	}

}

class Closure extends Value {
	Environment env;
	SExp sexp;

	public Closure(Environment env, SExp sexp) {
		super();
		this.env = env;
		this.sexp = sexp;
	}

	@Override
	public Type getValueType() {

		return Type.Lambda;
	}

}

class IntegerValue extends Value {
	int val;
	boolean isInital = false;

	public IntegerValue(int val) {
		super();
		this.val = val;
		this.isInital = true;
	}

	public IntegerValue() {

	}

	@Override
	public Type getValueType() {

		return Type.Int;
	}

	@Override
	public String toString() {
		return val + "";
	}

	@Override
	public Value add(Value v) {
		return v.added(this);
	}

	@Override
	public Value sub(Value v) {
		return v.subed(this);
	}

	@Override
	public Value mul(Value v) {
		return v.muled(this);
	}

	@Override
	public Value div(Value v) {
		return v.dived(this);
	}

	@Override
	protected Value added(IntegerValue v) {

		return new IntegerValue(v.val + this.val);
	}

	@Override
	protected Value subed(IntegerValue v) {
		return new IntegerValue(v.val - this.val);
	}

	@Override
	protected Value muled(IntegerValue v) {
		return new IntegerValue(v.val * this.val);
	}

	@Override
	protected Value dived(IntegerValue v) {

		return new IntegerValue(v.val / this.val);
	}

}
