import java.util.*;

import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager.NameMap;

// The following methods type check  AST nodes used in CSX Lite
//  You will need to complete the methods after line 238 to type check the
//   rest of CSX
//  Note that the type checking done for CSX lite may need to be extended to
//   handle full CSX (for example binaryOpNode).

public class TypeChecking extends Visitor { 

//	static int typeErrors =  0;     // Total number of type errors found 
//  	public static SymbolTable st = new SymbolTable(); 	
	int typeErrors;     // Total number of type errors found 
	SymbolTable st;	
	
	TypeChecking(){
		typeErrors = 0;
		st = new SymbolTable();
	}
	public static ArrayList<argDeclNode> buildArgList(methodDeclsNode node){
		ArrayList<argDeclNode> argList = new ArrayList<argDeclNode>();
		if (!node.thisDecl.args.isNull()) {
			argDeclsNode args = (argDeclsNode) node.thisDecl.args;
			while(true) {
				argDeclNode arg = args.thisDecl;
				argList.add(arg);
				if(args.moreDecls.isNull()) {
					break;
				}
			 args = (argDeclsNode) args.moreDecls;
			}
		}
		return argList;
	}
	private boolean checkOverloadedTypes(methodDeclNode thisDecl) {
		SymbolInfo master = (SymbolInfo) st.localLookup(thisDecl.name.idname);
		if(master != null) {
			if(master.type != thisDecl.returnType.type) {
				typeErrors++;
				System.out.println(error(thisDecl) 
						+ thisDecl.name.idname + " is already declared");
				return false; //return false for non-matching overloads
			}
			
		}
		
		return true;
		
	}
	boolean isScalar(ASTNode.Kinds kind){
		return (kind == ASTNode.Kinds.Var || kind == ASTNode.Kinds.Value
				|| kind == ASTNode.Kinds.ScalarParm);
	}
	
	boolean isUnchangeable(ASTNode.Kinds kind) {
		return (kind == ASTNode.Kinds.Value || kind == ASTNode.Kinds.Method || kind == ASTNode.Kinds.HiddenLabel
				|| kind == ASTNode.Kinds.VisibleLabel);
	}
	
	boolean isTypeCorrect(csxLiteNode n) {
        	this.visit(n);
        	return (typeErrors == 0);
	}
	
	boolean isTypeCorrect(classNode n) {
    	this.visit(n);
    	return (typeErrors == 0);
}
	
	static void assertCondition(boolean assertion){  
		if (! assertion)
			 throw new RuntimeException();
	}
	 void typeMustBe(ASTNode.Types testType,ASTNode.Types requiredType,String errorMsg) {
		 if ((testType != ASTNode.Types.Error) && (testType != requiredType)) {
                        System.out.println(errorMsg);
                        typeErrors++;
                }
        }
	 boolean typesMustBeEqual(ASTNode.Types type1,ASTNode.Types type2,String errorMsg) {
		 if ((type1 != ASTNode.Types.Error) && (type2 != ASTNode.Types.Error) &&
                     (type1 != type2)) {
                        System.out.println(errorMsg);
                        typeErrors++;
                        return false;
                }
		 return true;
        }
	 void typeMustBeIn(ASTNode.Types testType,LinkedList<ASTNode.Types> requiredTypes,String errorMsg) {
		 if (testType == ASTNode.Types.Error){
			 return;
		 }
		 for (ASTNode.Types type : requiredTypes ){
			 if (testType == type){
				 return;
			 }
		 }
		 System.out.println(errorMsg);
     	 typeErrors++;
     }
	String error(ASTNode n) {
		return "Error (line " + n.linenum + "): ";
        }

	static String opToString(int op) {
		switch (op) {
			case sym.PLUS:
				return(" + ");
			case sym.MINUS:
				return(" - ");
			case sym.EQ:
				return(" == ");
			case sym.NOTEQ:
				return(" != ");
			case sym.COR:
				return(" || ");
			case sym.CAND:
				return(" && ");
			case sym.OR:
				return(" | ");
			case sym.AND:
				return(" & ");
			case sym.LT:
				return(" < ");
			case sym.GT:
				return(" > ");
			case sym.LEQ:
				return(" <= ");
			case sym.GEQ:
				return(" >= ");
			case sym.TIMES:
				return(" * ");
			case sym.SLASH:
				return(" / ");
			case sym.NOT:
				return(" ! ");
			default:
				assertCondition(false);
				return "";
		}
	}


// Extend this to handle all CSX binary operators
	static void printOp(int op) {
		switch (op) {
			case sym.PLUS:
				System.out.print(" + ");
				break;
			case sym.MINUS:
				System.out.print(" - ");
				break;
			case sym.EQ:
				System.out.print(" == ");
				break;
			case sym.NOTEQ:
				System.out.print(" != ");
				break;
			case sym.COR:
				System.out.print(" || ");
				break;
			case sym.CAND:
				System.out.print(" && ");
				break;
			case sym.OR:
				System.out.print(" | ");
				break;
			case sym.AND:
				System.out.print(" & ");
				break;
			case sym.LT:
				System.out.print(" < ");
				break;
			case sym.GT:
				System.out.print(" > ");
				break;
			case sym.LEQ:
				System.out.print(" <= ");
				break;
			case sym.GEQ:
				System.out.print(" >= ");
				break;
			case sym.TIMES:
				System.out.print(" * ");
				break;
			case sym.SLASH:
				System.out.print(" / ");
				break;
			case sym.NOT:
				System.out.print(" ! ");
				break;
			default:
				throw new Error();
		}
	}

	
	 void visit(csxLiteNode n){
		this.visit(n.progDecls);
		this.visit(n.progStmts);
	}
	
	void visit(fieldDeclsNode n){
			this.visit(n.thisField);
			this.visit(n.moreFields);
	}
	void visit(nullFieldDeclsNode n){}

	void visit(stmtsNode n){
		  //System.out.println ("In stmtsNode\n");
		  this.visit(n.thisStmt);
		  this.visit(n.moreStmts);

	}
	void visit(nullStmtsNode n){}

// Extend varDeclNode's method to handle initialization
	void visit(varDeclNode n){
		if (checkDecl(n, n.varName, n.varType.type, false, ASTNode.Kinds.Var, -1)) {
			if (!n.initValue.isNull() && (n.varType.type != ((exprNode) n.initValue).type)) {
        		typeErrors++;
        		System.out.println(error(n) + "The initializer must be of type " + n.varType.type);
        	}
		}
	}
	
	void visit(nullTypeNode n){}
	
	void visit(intTypeNode n){
		//no type checkingneeded}
	}
	void visit(boolTypeNode n){
		//no type checking needed}
	}
	void visit(identNode n){
		SymbolInfo    id;
    	id =  (SymbolInfo) st.globalLookup(n.idname);
    	if (id == null) {
           	System.out.println(error(n) +  n.idname + " is not declared.");
            typeErrors++;
            n.type = ASTNode.Types.Error;
        } 
    	else {
            n.type = id.type;
            n.kind = id.kind;
            n.idinfo = id; // Save ptr to correct symbol table entry
    	}
	}

// Extend nameNode's method to handle subscripts
	void visit(nameNode n){
		this.visit(n.varName); 
        n.type=n.varName.type;
        n.kind=n.varName.kind;
        if(!n.subscriptVal.isNull()) {
        	if (isScalar(n.kind)) {
        		typeErrors++;
				System.out.println(error(n) + "Only arrays can be subscripted.");
        	}
        	else if (((exprNode) n.subscriptVal).type != ASTNode.Types.Integer && ((exprNode) n.subscriptVal).type != ASTNode.Types.Character){
        		typeErrors++;
        		System.out.println(error(n) + "Array subscripts must be integer or character expressions.");
        	}
		}
	}

	void visit(asgNode n){
		this.visit(n.target);
		this.visit(n.source);
		if (n.target.varName.idinfo == null) {
		}
		else if(isScalar(n.target.kind)&& !n.target.subscriptVal.isNull()) {}
		else if (isUnchangeable(n.target.varName.idinfo.kind)) {
			typeErrors++;
			System.out.println(error(n) + "Target of assignment can't be changed.");
		}
		else if (n.target.type == ASTNode.Types.Character && n.target.kind == ASTNode.Kinds.Array
        		&& n.source.kind == ASTNode.Kinds.String){
        	if (n.target.varName.idinfo != null) {
        		strLitNode str = (strLitNode) n.source;
        		int len = 0;
        		for (int i = 1; i < str.strval.length()-1; i++) {
        			len++;
        			if (str.strval.charAt(i) == '\\'){
        				i++;
        			}
        		}
        		if (n.target.varName.idinfo.arraysize != len) {
        			typeErrors++;
					System.out.println(error(n) + "Source and target of the assignment must have the same length.");
        		}
        	}
        }
		else if (isScalar(n.target.kind) && isScalar(n.source.kind)){
			if (n.target.type == ASTNode.Types.Integer && n.source.type == ASTNode.Types.Character) {}
			else if (n.target.type == ASTNode.Types.Character && n.source.type == ASTNode.Types.Integer) {}
			else {
				typesMustBeEqual(n.source.type, n.target.type,
	                    error(n) + "Right hand side of an assignment is not assignable to left hand side.");
			}
		}
        else{
        	if (!typesMustBeEqual(n.source.type, n.target.type,
                    error(n) + "Right hand side of an assignment is not assignable to left hand side.")) {}
        	else if(n.target.kind == ASTNode.Kinds.Array && n.source.kind == ASTNode.Kinds.Array){
        		if (n.target.varName.idinfo != null) {
        			nameNode src = (nameNode) n.source;
        			if (src.varName.idinfo != null) {
        				if (n.target.varName.idinfo.arraysize != src.varName.idinfo.arraysize) {
        					typeErrors++;
        					System.out.println(error(n) + "Source and target of the assignment must have the same length.");
        				}
        			}
        		}
        	}
        	else {
        		System.out.println(error(n) + "Right hand side of an assignment is not assignable to left hand side.");
        		typeErrors++;
        	}
        }
	}

// Extend ifThenNode's method to handle else parts
	void visit(ifThenNode n){
		  this.visit(n.condition);
        	  typeMustBe(n.condition.type, ASTNode.Types.Boolean,
                	error(n) + "The control expression of an" +
                          	" if must be a bool.");

		  this.visit(n.thenPart);
		  // No else parts in CSXlite
	}
	  
	 void visit(printNode n){
		 this.visit(n.outputValue);
		 String errorMsg = error(n) + "Only integers, booleans, strings, characters and character arrays may be written.";
		 if (n.outputValue.kind == ASTNode.Kinds.Array){
			 typeMustBe(n.outputValue.type, ASTNode.Types.Character, errorMsg);
		 }
		 else if (n.outputValue.kind == ASTNode.Kinds.String){}
		 else{
			 LinkedList<ASTNode.Types> types = new LinkedList<ASTNode.Types>();
			 types.add(ASTNode.Types.Integer);
			 types.add(ASTNode.Types.Character);
			 types.add(ASTNode.Types.Boolean);
			 typeMustBeIn(n.outputValue.type, types, errorMsg);
		 }
		 this.visit(n.morePrints);
	  }
	  
	  void visit(blockNode n){
		// open a new local scope for the block body
			st.openScope();
			this.visit(n.decls);
			this.visit(n.stmts);
			// close this block's local scope
			try { st.closeScope();
			}  catch (EmptySTException e) 
	                      { /* can't happen */ }
	  }

	
	  void visit(binaryOpNode n){
		  
		assertCondition(n.operatorCode== sym.PLUS||n.operatorCode==sym.MINUS 
        			|| n.operatorCode== sym.EQ||n.operatorCode==sym.NOTEQ
        			|| n.operatorCode== sym.COR||n.operatorCode==sym.CAND
        			|| n.operatorCode== sym.OR||n.operatorCode==sym.AND
        			|| n.operatorCode== sym.LT||n.operatorCode==sym.GT
        			|| n.operatorCode== sym.LEQ||n.operatorCode== sym.GEQ
        			|| n.operatorCode==sym.TIMES||n.operatorCode== sym.SLASH);
		this.visit(n.leftOperand);
		this.visit(n.rightOperand);
		n.kind = ASTNode.Kinds.Value;
		
		if (!isScalar(n.leftOperand.kind)) {
			typeErrors++;
			System.out.println(error(n) + "Left operand of" + opToString(n.operatorCode) + "must be a scalar.");
		}
		if(!isScalar(n.rightOperand.kind)) {
			typeErrors++;
			System.out.println(error(n) + "Right operand of" + opToString(n.operatorCode) + "must be a scalar.");
		}
		if (n.operatorCode== sym.PLUS||n.operatorCode==sym.MINUS
        			||n.operatorCode== sym.TIMES||n.operatorCode==sym.SLASH){
        		n.type = ASTNode.Types.Integer;
        		LinkedList<ASTNode.Types> opTypes = new LinkedList<ASTNode.Types>();
        		opTypes.add(ASTNode.Types.Integer);
        		opTypes.add(ASTNode.Types.Character);
        		typeMustBeIn(n.leftOperand.type, opTypes,
                	error(n) + "Left operand of" + opToString(n.operatorCode) 
                         	+  "must be arithmetic.");
        		typeMustBeIn(n.rightOperand.type, opTypes,
                	error(n) + "Right operand of" + opToString(n.operatorCode) 
                         	+  "must be arithmetic.");
        	}
        	else if(n.operatorCode == sym.OR || n.operatorCode == sym.AND){
        		if (n.leftOperand.type == ASTNode.Types.Integer){
        			typeMustBe(n.rightOperand.type, ASTNode.Types.Integer,
        					error(n) + "Both operands of" + opToString(n.operatorCode)
        					+ "must have the same type.");
        		}
        		else if(n.leftOperand.type == ASTNode.Types.Boolean){
        			typeMustBe(n.rightOperand.type, ASTNode.Types.Boolean,
        					error(n) + "Both operands of" + opToString(n.operatorCode)
        					+ "must have the same type.");
        		}
        		else{
        			String errorMsg = error(n) + "Left operand of" + opToString(n.operatorCode)
        					+ "must be an int or a bool";
        			System.out.println(errorMsg);
        	     	typeErrors++;
        		}
        	}
        	else if(n.operatorCode == sym.COR || n.operatorCode == sym.CAND){
        		n.type = ASTNode.Types.Boolean;
        		String errorMsg = " operand of"+
                           opToString(n.operatorCode)+"must be a bool.";
        		if (n.leftOperand.type != ASTNode.Types.Boolean){
        			typeErrors++;
        			System.out.println(error(n) + "Left" + errorMsg);
        		}
        		if(n.rightOperand.type != ASTNode.Types.Boolean) {
        			typeErrors++;
        			System.out.println(error(n) + "Right" + errorMsg);
        		}	
        	}
        	else { // Must be a comparison operator
        		n.type = ASTNode.Types.Boolean;
        		String errorMsg = error(n)+"Operands of"+
                        opToString(n.operatorCode)+"must both be arithmetic or both must be boolean.";
        		if (n.leftOperand.type != ASTNode.Types.Integer && 
        				n.leftOperand.type != ASTNode.Types.Boolean){
        			System.out.println(errorMsg);
        			typeErrors++;
        		}
        		else{
        			typesMustBeEqual(n.leftOperand.type,n.rightOperand.type,errorMsg);
        		}
        	}
	  }

	
	
	void visit(intLitNode n){
	//      All intLits are automatically type-correct
	}
	
// Extend these unparsing methods to correctly unparse CSX AST nodes
	 
	 void visit(classNode n){
		 this.visit(n.members);
		 //No type checking needed 
		}

	 void  visit(memberDeclsNode n){
		 this.visit(n.fields);
		 //Build list of methods for us to check against later
		 LinkedList<ASTNode.Types> requiredTypes = new LinkedList<ASTNode.Types>();
		 requiredTypes.add(ASTNode.Types.Boolean);
		 requiredTypes.add(ASTNode.Types.Void);
		 requiredTypes.add(ASTNode.Types.Character);
		 requiredTypes.add(ASTNode.Types.Integer);
		 methodDeclsNode temp = (methodDeclsNode) n.methods;
		 while (true) {
			 ArrayList<argDeclNode> argList = TypeChecking.buildArgList(temp);
			 try {
				 if(this.checkOverloadedTypes(temp.thisDecl)) {
					 st.insert(new SymbolInfo(temp.thisDecl.name.idname, ASTNode.Kinds.Method, temp.thisDecl.returnType.type, argList));
				 }
				 else {
					 if(temp.moreDecls.isNull()) {
						 break;
					 }
					 temp = (methodDeclsNode) temp.moreDecls;
					 continue;
				 }
				 
			 } catch (DuplicateException e) {
				 typeErrors ++;
				 System.out.println(error(temp.thisDecl) + temp.thisDecl.name.idname + 
						 " is already declared.");
				 
			 }
			 this.typeMustBeIn(temp.thisDecl.returnType.type, requiredTypes, "Illegal method return type");
			 
			 if(temp.moreDecls.isNull()) {
				 break;
			 }
			 temp = (methodDeclsNode) temp.moreDecls;
		 }
		 if(!st.containsMain()) {
			 typeErrors ++;
			 System.out.println(error(n) + "Class must contain a main method of type void");
		 }
		 this.visit(n.methods);
	 }
	 
	 
	void  visit(methodDeclsNode n){
		 this.visit(n.thisDecl);
		 this.visit(n.moreDecls);
		 //Type checking not needed here, these are just declarations and we already created 
		 //declarations when we walked the tree methods section of the tree the first time in memberDeclsNode
	 }
	 
	 void visit(nullStmtNode n){}
	 
	 void visit(nullReadNode n){}

	 void visit(nullPrintNode n){}

	 void visit(nullExprNode n){}

	 void visit(nullMethodDeclsNode n){}

	 void visit(methodDeclNode n){
		 st.openScope();
		 this.visit(n.args);
		 this.visit(n.returnType);
		 this.visit(n.decls);
		 this.visit(n.stmts);
	 }
	 
	 void scalarErrorCheck(stmtNode n, ASTNode.Kinds kind, String operator) {
		 if (!isScalar(kind)) {
			  typeErrors++;
			  String errorMsg = error(n) + "Operand of " + operator + " must be a scalar.";
			  System.out.println(errorMsg);
		  }
	 }
	 
	 void visit(incrementNode n){
		 this.visit(n.target);
			LinkedList<ASTNode.Types> types = new LinkedList<ASTNode.Types>();
			types.add(ASTNode.Types.Integer);
			types.add(ASTNode.Types.Character);
			typeMustBeIn(n.target.type, types,
                	error(n) + "Operand of ++ must be arithmetic.");
			if (n.target.varName.idinfo != null){
				scalarErrorCheck(n, n.target.varName.idinfo.kind, "++");
				if (isUnchangeable(n.target.varName.idinfo.kind)) {
					typeErrors++;
					System.out.println(error(n) + "Target of ++ can't be changed.");
				}
			}
	 }
	 void visit(decrementNode n){
		 this.visit(n.target);
		 LinkedList<ASTNode.Types> types = new LinkedList<ASTNode.Types>();
			types.add(ASTNode.Types.Integer);
			types.add(ASTNode.Types.Character);
			typeMustBeIn(n.target.type, types,
             	error(n) + "Operand of decrement must be arithmetic.");
			if (n.target.varName.idinfo != null){
				scalarErrorCheck(n, n.target.varName.idinfo.kind, "--");
				if (isUnchangeable(n.target.varName.idinfo.kind)) {
					typeErrors++;
					System.out.println(error(n) + "Target of -- can't be changed.");
				}
			}
	 }
	void visit(argDeclsNode n){
		this.visit(n.thisDecl);
		this.visit(n.moreDecls);
		//System.out.println("Type checking for argDeclsNode not yet implemented");
	}

	void visit(nullArgDeclsNode n){}

	
	void visit(valArgDeclNode n){
		visit(n.argType);
		visit(n.argName);
		n.type = n.argType.type;
		n.kind = ASTNode.Kinds.ScalarParm;
		try {
			st.insert(new SymbolInfo(n.argName.idname,n.argName.kind,n.argType.type));
		} catch (DuplicateException e) {
			typeErrors++;
			System.out.println(error(n) + n.argName.idname + " is already declared.");
		}
	}
	
	void visit(arrayArgDeclNode n){
		visit(n.elementType);
		visit(n.argName);
		n.type = n.elementType.type;
		n.kind = ASTNode.Kinds.ArrayParm;
		try {
			st.insert(new SymbolInfo(n.argName.idname,n.argName.kind,n.elementType.type));
		} catch (DuplicateException e) {
			typeErrors++;
			System.out.println(error(n) + n.argName.idname + " is already declared.");
		}
	}
	
	void visit(constDeclNode n){
		checkDecl(n, n.constName, n.constValue.type, true, ASTNode.Kinds.Value,-1);
	}
	
	boolean checkDecl(declNode n, identNode name, ASTNode.Types type, boolean constant, ASTNode.Kinds kind, int arraysize) {
		SymbolInfo id;
		id = (SymbolInfo) st.localLookup(name.idname);
		try {
			id = new SymbolInfo(name.idname, kind, type);
			name.type = type;
			name.kind = kind;
			name.idinfo=id;
			id.arraysize = arraysize;
			st.insert(id);
		} catch (DuplicateException d) {
			System.out.println(error(n) + name.idname + " is already declared.");
			typeErrors++;
			name.type = ASTNode.Types.Error;
			name.kind = ASTNode.Kinds.Other;
			return false;
		}
		return true;
	}
	
	 void visit(arrayDeclNode n){
		checkDecl(n, n.arrayName, n.elementType.type, false, ASTNode.Kinds.Array, n.arraySize.intval);
		this.visit(n.elementType);
		if (n.arraySize.intval <= 0) {
			typeErrors++;
			System.out.println(error(n) + n.arrayName.idname + " must have more than 0 elements.");
		}
	 }
	
	void visit(charTypeNode n){
		//Should never happen
		if(n.type != ASTNode.Types.Character) {
			typeErrors++;
			System.out.println(error(n) + "charTypeNode is not a Character type");
		}
	}
	void visit(voidTypeNode n){
		//Shouldn't need type checking
	}

	void visit(whileNode n){
		
		this.visit(n.condition);
		if(n.condition.type != ASTNode.Types.Boolean)
		{
			typeErrors++;
			System.out.println(error(n) + "Condition is not a boolean");
			
		}
		if(!isScalar(n.condition.kind)) {
			typeErrors++;
			System.out.println(error(n) + "Condition is not scalar");
		}
		if (!n.label.isNull()) {
			st.addLabel((identNode) n.label);
		}
		st.openScope();
		this.visit(n.loopBody);
		if (!n.label.isNull()) {
			st.removeLavel((identNode)n.label);
		}
		try {
			st.closeScope();
		} catch (EmptySTException e) {
			e.printStackTrace();
		}
	  }

	void visit(breakNode n){
		this.visit(n.label);
		System.out.println("Type checking for breakNode not yet implemented");
	}
	void visit(continueNode n){
		this.visit(n.label);
		System.out.println("Type checking for continueNode not yet implemented");
	}
	
	boolean isArgValid(argsNode args, int count, List<argDeclNode> methodArgs) {
		/*if (args.linenum == 103) {
			System.out.println("hi");
		}*/
		
		if (count >= methodArgs.size()) {
			return false;
		}
		
		ASTNode.Types type = args.argVal.type;
		ASTNode.Kinds kind = args.argVal.kind;
		argDeclNode node = methodArgs.get(count);
		if (isScalar(node.kind)) {
			return type.equals(node.type) && isScalar(kind);
		}
		else {
			return type.equals(node.type) && !isScalar(kind);
		}
	}
	
	boolean isMethodRight(SymbolInfo method, callNode n) {
		if (method.methodArgs == null) {
			return n.args.isNull();
		}
		else {
			int argsCount = 0;
			argsNodeOption args = n.args;
			while (!args.isNull()) {
				if (!isArgValid((argsNode) args, argsCount, method.methodArgs)) {
					return false;
				}
				argsCount++;
				args = ((argsNode) args).moreArgs; 
			}
			return argsCount == method.methodArgs.size();
		}
	}
	
	void printIsMethodRight(SymbolInfo method, callNode n) {
		if (method.methodArgs == null) {
			if (!n.args.isNull()) {
				typeErrors++;
				System.out.println(error(n) + n.methodName.idname + " requires 0 parameters.");
			}
		}
		else {
			int argsCount = 0;
			argsNodeOption args = n.args;
			while (!args.isNull()) {
				if (argsCount >= method.methodArgs.size()) {
					argsCount++;
					break;
				}
				if (!isArgValid((argsNode) args, argsCount, method.methodArgs)) {
					typeErrors++;
					System.out.println(error(n) + "In the call to " + n.methodName.idname + ", parameter " + (argsCount + 1) + " has incorrect type.");
				}
				argsCount++;
				args = ((argsNode) args).moreArgs; 
			}
			if (argsCount != method.methodArgs.size()) {
				typeErrors++;
				System.out.println(error(n) + n.methodName.idname + " requires " + method.methodArgs.size() + " parameters.");
			}
		}
	}
	
	SymbolInfo findRightMethod(SymbolInfo method, callNode n) {
		if (isMethodRight(method, n)) {
			return method;
		}
		for (SymbolInfo overload : method.overLoadedMethods) {
			if (isMethodRight(overload, n)) {
				return overload;
			}
		}
		return null;
	}
	
	void visit(callNode n){
		//calvin working on callNode
		this.visit(n.methodName);
		this.visit(n.args);
		SymbolInfo method = (SymbolInfo) st.findBottomSymbol(n.methodName.idname);
		if (method == null) {}// will already be handled by visiting
		else if (!method.kind.equals(ASTNode.Kinds.Method)) {
			typeErrors++;
			System.out.println(error(n) + n.methodName.idname + " isn't a method.");
		}
		else {
			if (method.overLoadedMethods.size() == 0) {
				printIsMethodRight(method, n);
			}
			else {
				SymbolInfo match = findRightMethod(method, n);
				if (match == null) {
					typeErrors++;
					System.out.println(error(n) + "None of the " + method.overLoadedMethods.size() + 1 + " definitions of method " + n.methodName + " match the parameters in this call.");
				}
			}
		}
	}

	  
	  void visit(readNode n){
		 this.visit(n.targetVar);
		 if (isUnchangeable(n.targetVar.kind) || n.targetVar.kind == ASTNode.Kinds.Array) {
			 typeErrors++;
			 System.out.println(error(n) + n.targetVar.varName.idname + " may not be assigned to.");
		 }
		 String errorMsg = error(n) + "Only integers and characters may be read.";
		 LinkedList<ASTNode.Types> types = new LinkedList<ASTNode.Types>();
		 types.add(ASTNode.Types.Integer);
		 types.add(ASTNode.Types.Character);
		 typeMustBeIn(n.targetVar.type, types, errorMsg);
		 this.visit(n.moreReads);
	  }
	  

	  void visit(returnNode n){
		  // calvin working on return node
		  this.visit(n.returnVal);
		  ASTNode.Types functionRet;
		  
		  if (n.returnVal.isNull()) {
			  /*
			 if (!functionRet.equals(ASTNode.Types.Void)) {
				 typeErrors++;
				 System.out.println(error(n) + "Return type of " + " is not void.");
			 }*/
		  }
		  else {
			  ASTNode.Types type = ((exprNode) n.returnVal).type;
		  }
	  }

	  
	  void visit(argsNode n){
		  //Shouldn't need type checking, handled lower
		  this.visit(n.argVal);
		  this.visit(n.moreArgs);
	  }
	  	
	  void visit(nullArgsNode n){}
		
	  void visit(castNode n){
		  this.visit(n.operand);
		  this.visit(n.resultType);
		  n.type = n.resultType.type;
		  n.kind = ASTNode.Kinds.Value;
	  }

	  void visit(fctCallNode n){
		  this.visit(n.methodName);
		  this.visit(n.methodArgs);
		System.out.println("Type checking for fctCallNode not yet implemented");
	  }
	  
	  void visit(unaryOpNode n){
		  n.kind = ASTNode.Kinds.Value;
		  this.visit(n.operand);
		  if (n.operand.type == ASTNode.Types.Integer){
			  n.type = ASTNode.Types.Integer;
		  }
		  else if(n.operand.type == ASTNode.Types.Boolean){
			  n.type = ASTNode.Types.Boolean;
		  }
		  else{
			 n.type = ASTNode.Types.Boolean;
			String errorMsg = error(n) + "Operand of" + opToString(n.operatorCode) 
	        	+  "must be arithmetic or must be bool.";
			System.out.println(errorMsg);
  			typeErrors++;
		  }
		  if (!isScalar(n.operand.kind)) {
			  typeErrors++;
			  String errorMsg = error(n) + "Operand of" + opToString(n.operatorCode) 
	        	+  "must be a scalar.";
			  System.out.println(errorMsg);
		  }
	  }

	
	void visit(charLitNode n){
		n.type = ASTNode.Types.Character;
	}
	  
	void visit(strLitNode n){
		n.kind = ASTNode.Kinds.String;
	}

	
	void visit(trueNode n){
		//System.out.println("Type checking for trueNode not yet implemented");
	}

	void visit(falseNode n){
		//System.out.println("Type checking for falseNode not yet implemented");
	}

	void visit(bitStringNode n){
		//System.out.println("Type checking for bitStringNode not yet implemented");
	}

	@Override
	void visit(forNode n) {
		// TODO Auto-generated method stub
		
	}
}
