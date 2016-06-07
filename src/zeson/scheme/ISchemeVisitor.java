package zeson.scheme;

public interface ISchemeVisitor {
	Value visit(SExp sexp);

	Value visit(IntegerExp integerExp);

	Value visit(IdentExp identExp);

}
