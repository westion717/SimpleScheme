package zeson.scheme;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.junit.Assume;

public class Environment {

	Stack<Map<String, Value>> symbols = new Stack<>();

	public Environment() {

		this.symbols.add(new HashMap<String, Value>());

	}

	public Value getSymbol(String name) {

		for (int i = symbols.size() - 1; i >= 0; i--) {
			Value v = symbols.get(i).get(name);
			if (v != null)
				return v;
		}
		return null;
	}

	public boolean addSymbol(String name, Value v) {

		Assume.assumeNotNull(symbols.lastElement());
		symbols.lastElement().put(name, v);
		return true;
	}

	public void enterScope() {
		symbols.push(new HashMap<String, Value>());
	}

	public void outScope() {
		symbols.pop();

	}

	@SuppressWarnings("unchecked")
	public Environment fork() {
		Environment environment = new Environment();
		environment.symbols = (Stack<Map<String, Value>>) symbols.clone();
		return environment;
	}

}
