package corpus.creator;

import java.util.ArrayList;

import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class MethodVisitor extends VoidVisitorAdapter {

	public ArrayList<String> methods;

	public MethodVisitor() {
		this.methods = new ArrayList<>();
	}

	@Override
	public void visit(MethodDeclaration m, Object arg) {
		//capture the method
		this.methods.add(m.toString());
	}

	@Override
	public void visit(ConstructorDeclaration c, Object arg) {
		//capture the constructor
		this.methods.add(c.toString());
	}
}
