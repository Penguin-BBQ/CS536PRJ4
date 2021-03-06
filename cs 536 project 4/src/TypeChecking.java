import java.util.*;

//import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager.NameMap;

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
	
	public static void setArgKindAndType(argDeclNode n, TypeChecking tc){
		if (n instanceof arrayArgDeclNode){
			tc.visit(((arrayArgDeclNode)n).elementType);
			n.type = ((arrayArgDeclNode)n).elementType.type;
			n.kind = ASTNode.Kinds.ArrayParm;
		}
		else if(n instanceof valArgDeclNode){
			tc.visit(((valArgDeclNode)n).argType);
			n.type = ((valArgDeclNode)n).argType.type;
			n.kind = ASTNode.Kinds.ScalarParm;
		}
	}
	
	public static ArrayList<argDeclNode> buildArgList(methodDeclsNode node, TypeChecking tc){
		ArrayList<argDeclNode> argList = new ArrayList<argDeclNode>();
		if (!node.thisDecl.args.isNull()) {
			argDeclsNode args = (argDeclsNode) node.thisDecl.args;
			while(true) {
				argDeclNode arg = args.thisDecl;
				setArgKindAndType(arg, tc);
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
						+ thisDecl.name.idname + " is already declared.");
				return false; //return false for already declared method calls
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
    	System.out.print("Error count = " + typeErrors);
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
	 boolean typeMustBeIn(ASTNode.Types testType,LinkedList<ASTNode.Types> requiredTypes,String errorMsg) {
		 for (ASTNode.Types type : requiredTypes ){
			 if (testType == type){
				 return true;
			 }
		 }
		 System.out.println(errorMsg);
     	 typeErrors++;
     	 return false;
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
			case sym.DECREMENT:
				return(" -- ");
			case sym.INCREMENT:
				return(" ++ ");
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

// Extend varDeclNode's method to handle initialization - CRR - Looks like it has been modified, not sure if finished
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

// Extend nameNode's method to handle subscripts - done
	void visit(nameNode n){
		this.visit(n.varName); 
        n.type=n.varName.type;
        n.kind=n.varName.kind;
        if(!n.subscriptVal.isNull()) {
        	this.visit(n.subscriptVal);
        	if (isScalar(n.kind)) {
        		typeErrors++;
				System.out.println(error(n) + "Only arrays can be subscripted.");
        	}
        	else if (((exprNode) n.subscriptVal).type != ASTNode.Types.Integer && ((exprNode) n.subscriptVal).type != ASTNode.Types.Character){
        		typeErrors++;
        		System.out.println(error(n) + "Array subscripts must be integer or character expressions.");
        	}
        	else if(isScalar(((exprNode)n.subscriptVal).kind)) {
        		n.kind=((exprNode)n.subscriptVal).kind;
        	}
		}
	}

	void visit(asgNode n){
		this.visit(n.target);
		this.visit(n.source);
		
		// verify source and target match appropriately 
		if (n.target.varName.idinfo == null) { //nothing to be done if we didn't find the target in the symbol table
		}
		else if(isScalar(n.target.kind)&& !n.target.subscriptVal.isNull()) {} // error handled in namenode
		else if (isUnchangeable(n.target.varName.idinfo.kind)) {
			typeErrors++;
			System.out.println(error(n) + "Target of assignment can't be changed.");
		}
		else if (n.target.type == ASTNode.Types.Character && n.target.kind == ASTNode.Kinds.Array
        		&& n.source.kind == ASTNode.Kinds.String){ // verify string length when assigning to char array
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
		else if (isScalar(n.target.kind) && isScalar(n.source.kind)){ // verify types are correct for scalar assignments
			if (n.target.type == ASTNode.Types.Integer && n.source.type == ASTNode.Types.Character) {}
			else if (n.target.type == ASTNode.Types.Character && n.source.type == ASTNode.Types.Integer) {}
			else {
				typesMustBeEqual(n.source.type, n.target.type,
	                    error(n) + "Right hand side of an assignment is not assignable to left hand side.");
			}
		}
        else{ // other cases
        	if (!typesMustBeEqual(n.source.type, n.target.type,
                    error(n) + "Right hand side of an assignment is not assignable to left hand side.")) {}//verify types are equal
        	else if(n.target.kind == ASTNode.Kinds.Array && n.source.kind == ASTNode.Kinds.Array){ // make sure assigning arrays are the same size
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
        	else { // if we've got here, we skipped all the acceptable cases
        		System.out.println(error(n) + "Right hand side of an assignment is not assignable to left hand side.");
        		typeErrors++;
        	}
        }
	}

// Extend ifThenNode's method to handle else parts - Done
	void visit(ifThenNode n){
		  this.visit(n.condition);
        	  typeMustBe(n.condition.type, ASTNode.Types.Boolean,
                	error(n) + "The control expression of an" +
                          	" if must be a bool.");
          st.openScope();
		  this.visit(n.thenPart);
		  try {
			  st.closeScope();
		  } catch (EmptySTException e) {
			  //Can't happen
		  }
		  st.openScope();
		  this.visit(n.elsePart);
		  try {
			  st.closeScope();
		  } catch (EmptySTException e) {
			  //Can't happen
		}	
		
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

	  boolean isErrorType(ASTNode.Types type1, ASTNode.Types type2) {
		  return type1.equals(ASTNode.Types.Error) || type2.equals(ASTNode.Types.Error);
	  }
	  
	  void visit(binaryOpNode n){
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
        		if (!isErrorType(n.leftOperand.type, n.rightOperand.type)) {
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
        	}
        	else if(n.operatorCode == sym.OR || n.operatorCode == sym.AND){
        		if (isErrorType(n.leftOperand.type, n.rightOperand.type)) {}//error found further down the line, skipping
        		else if (n.leftOperand.type == ASTNode.Types.Integer){
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
        		if (isErrorType(n.leftOperand.type, n.rightOperand.type)) {}//error found further down the line, skipping
        		else {
	        		if (n.leftOperand.type != ASTNode.Types.Boolean){
	        			typeErrors++;
	        			System.out.println(error(n) + "Left" + errorMsg);
	        		}
	        		if(n.rightOperand.type != ASTNode.Types.Boolean) {
	        			typeErrors++;
	        			System.out.println(error(n) + "Right" + errorMsg);
	        		}	
        		}
        	}
        	else { // Must be a comparison operator
        		n.type = ASTNode.Types.Boolean;
        		String errorMsg = error(n)+"Operands of"+
                        opToString(n.operatorCode)+"must both be arithmetic or both must be boolean.";
        		if (isErrorType(n.leftOperand.type, n.rightOperand.type)) {}//error found further down the line, skipping
        		else if (n.leftOperand.type != ASTNode.Types.Integer && 
        				n.leftOperand.type != ASTNode.Types.Boolean && n.leftOperand.type != ASTNode.Types.Character){
        			System.out.println(errorMsg);
        			typeErrors++;
        		}
        		else if (n.leftOperand.type == ASTNode.Types.Boolean) {
        			typesMustBeEqual(n.leftOperand.type,n.rightOperand.type,errorMsg);
        		}
        		else{//we know the left operand is an int or a char
        			LinkedList<ASTNode.Types> opTypes = new LinkedList<ASTNode.Types>();
            		opTypes.add(ASTNode.Types.Integer);
            		opTypes.add(ASTNode.Types.Character);
        			typeMustBeIn(n.rightOperand.type,opTypes,errorMsg);
        		}
        	}
	  }

	
	
	void visit(intLitNode n){
	//      All intLits are automatically type-correct
	}
	 
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
			 ArrayList<argDeclNode> argList = TypeChecking.buildArgList(temp, this);
			 try {
				 if(this.checkOverloadedTypes(temp.thisDecl)) {
					 //Maybe do arg duplicate checking here?
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
			 this.typeMustBeIn(temp.thisDecl.returnType.type, requiredTypes, 
					 error(temp.thisDecl) + temp.thisDecl.name.idname 
					 + " has an illegal method return type");
			 
			 if(temp.moreDecls.isNull()) {
				 break;
			 }
			 temp = (methodDeclsNode) temp.moreDecls;
		 }
		 if(!st.containsMain()) {
			 typeErrors ++;
			 System.out.println(error(n) + "Class must contain a main method of type void");
		 }
		 //Start type checking the args
		 temp = (methodDeclsNode) n.methods;
		 while (true) {
			 ArrayList<argDeclNode> argList = TypeChecking.buildArgList(temp, this);
			 this.CheckArgs(argList, error(temp));
			 if(temp.moreDecls.isNull()) {
				 break;
			 }
			 temp = (methodDeclsNode) temp.moreDecls;
		 }
		 this.visit(n.methods);
	 }
	 
	 
	private void CheckArgs(ArrayList<argDeclNode> argList, String errorLine) {
		Hashtable<String,ASTNode.Types> tableOfArgs = new Hashtable<String,ASTNode.Types>();
		for(int i = 0; i < argList.size(); i++) {
			argDeclNode currentArg = argList.get(i);
			if(currentArg.getClass() == valArgDeclNode.class) {
				valArgDeclNode argNode = (valArgDeclNode) currentArg;
				if(tableOfArgs.containsKey(argNode.argName.idname)) {
					System.out.println(errorLine + argNode.argName.idname + " is already declared.");
					typeErrors++;
				}
				tableOfArgs.put(argNode.argName.idname, argNode.argType.type);
			}
			if(currentArg.getClass() == arrayArgDeclNode.class) {
				arrayArgDeclNode argNode = (arrayArgDeclNode) currentArg;
				if(tableOfArgs.containsKey(argNode.argName.idname)) {
					System.out.println(errorLine + argNode.argName.idname + " is already declared.");
					typeErrors++;
				}
				tableOfArgs.put(argNode.argName.idname, argNode.elementType.type);
			}
		}
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
		 st.openScope(n);
		 this.visit(n.args);
		 this.visit(n.decls);
		 this.visit(n.stmts);
		 try {
			st.closeScope();
		} catch (EmptySTException e) {
			//Can't happen
		}
	 }
	 
	 // check that the statement for an operation is a scalar
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
             	error(n) + "Operand of -- must be arithmetic.");
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
	}

	void visit(nullArgDeclsNode n){}

	
	void visit(valArgDeclNode n){
		visit(n.argType);
		n.type = n.argType.type;
		n.kind = ASTNode.Kinds.ScalarParm;
		try {
			st.insert(new SymbolInfo(n.argName.idname,n.kind,n.type));
		} catch (DuplicateException e) {
			
		}
	}
	
	void visit(arrayArgDeclNode n){
		visit(n.elementType);
		n.type = n.elementType.type;
		n.kind = ASTNode.Kinds.ArrayParm;
		try {
			st.insert(new SymbolInfo(n.argName.idname,n.kind,n.type));
		} catch (DuplicateException e) {
			
		}
	}
	
	void visit(constDeclNode n){
		checkDecl(n, n.constName, n.constValue.type, true, ASTNode.Kinds.Value,-1);
	}
	
	//do basic decl checks that are the same for all decls
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
		st.openScope();
		if (!n.label.isNull()) {
			st.addLabel((identNode) n.label);
			try {
				st.insert(new SymbolInfo(((identNode) n.label).idname, ASTNode.Kinds.VisibleLabel, ASTNode.Types.Character));
			}	catch (DuplicateException d) {}
		}
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

	boolean areNullsValid(declNode init, exprNodeOption condition, exprNodeOption variableChange){
		if (init.isNull()){
			return condition.isNull() && variableChange.isNull();
		}
		return !condition.isNull() && !variableChange.isNull();
	}
	
	void visit(forNode n) {
		st.openScope();
		if (!areNullsValid(n.initialization, n.condition, n.variableChange)){
			typeErrors++;
			System.out.println(error(n) + "Either all parameters of for loop must be null or none can be null.");
		}
		else{
			if (!n.initialization.isNull()){
				this.visit(n.initialization);
				if (! (n.initialization instanceof varDeclNode)){
					typeErrors++;
					System.out.println(error(n) + "Initializing parameter of for loop must be an int scalar variable.");
				}
				else{
					varDeclNode init = (varDeclNode) n.initialization;
					if (!init.varType.type.equals(ASTNode.Types.Integer)){
						typeErrors++;
						System.out.println(error(n) + "Initializing parameter of for loop must be an int.");
					}
					if (!isScalar(init.varName.kind)){
						typeErrors++;
						System.out.println(error(n) + "Initializing parameter of for loop must be a scalar.");
					}
					if (init.initValue.isNull()){
						typeErrors++;
						System.out.println(error(n) + "Initializing parameter of for loop must be assigned a value.");
					}
				}
			}
			if (!n.condition.isNull()){
				this.visit(n.condition);
				if(((exprNode)n.condition).type != ASTNode.Types.Boolean)
				{
					typeErrors++;
					System.out.println(error(n) + "Condition is not a boolean");
					
				}
				if(!isScalar(((exprNode)n.condition).kind)) {
					typeErrors++;
					System.out.println(error(n) + "Condition is not scalar");
				}
			}
			if (!n.variableChange.isNull()){
				this.visit(n.variableChange);
			}
		}
		if (!n.label.isNull()) {
			st.addLabel((identNode) n.label);
			try {
				st.insert(new SymbolInfo(((identNode) n.label).idname, ASTNode.Kinds.VisibleLabel, ASTNode.Types.Character));
			}	catch (DuplicateException d) {}
		}
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
		SymbolInfo label = (SymbolInfo) st.findSymbol(n.label.idname, st.nextScope());
		if (label == null) {
			typeErrors++;
			System.out.println(error(n) + n.label.idname + " doesn't label an enclosing while loop.");
		}
	}
	void visit(continueNode n){
		SymbolInfo label = (SymbolInfo) st.findSymbol(n.label.idname, st.nextScope());
		if (label == null) {
			typeErrors++;
			System.out.println(error(n) + n.label.idname + " doesn't label an enclosing while loop.");
		}
	}
	
	boolean isArgValid(argsNode args, int count, List<argDeclNode> methodArgs) {
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
	
	  // returns whether or not a specific method matches a call
	boolean isMethodRight(SymbolInfo method, callNode n) {
		if (method.methodArgs == null) {
			return n.args.isNull();
		}
		else {
			int argsCount = 0;
			argsNodeOption args = n.args;
			while (!args.isNull()) {
				if (!isArgValid((argsNode) args, argsCount, method.methodArgs)) {
					return false; //invalid arg, can't be this method
				}
				argsCount++;
				args = ((argsNode) args).moreArgs; 
			}
			return argsCount == method.methodArgs.size(); // all of the args we checked were valid, was it the right number?
		}
	}
	
	//check if a method matches a call, and print errors if not
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
	
	// find and return a method matching all the same specs as the call
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
		this.visit(n.methodName);
		this.visit(n.args);
		SymbolInfo method = (SymbolInfo) st.findBottomSymbol(n.methodName.idname);
		if (method == null) {}// will already be handled by visiting
		else if (!method.kind.equals(ASTNode.Kinds.Method)) {
			typeErrors++;
			System.out.println(error(n) + n.methodName.idname + " isn't a method.");
		}
		else if (!method.type.equals(ASTNode.Types.Void)) {
			typeErrors++;
			System.out.println(error(n) + n.methodName.idname + " is called as a procedure and must therefore return void.");
		}
		else { // we found a method with the same name
			if (method.overLoadedMethods.size() == 0) {
				printIsMethodRight(method, n);
			}
			else { //need to check overloaded methods
				SymbolInfo match = findRightMethod(method, n);
				if (match == null) {
					typeErrors++;
					System.out.println(error(n) + "None of the " + (method.overLoadedMethods.size() + 1) + " definitions of method " + n.methodName.idname + " match the parameters in this call.");
				}
			}
		}
	}

	  
	  void visit(readNode n){
		 this.visit(n.targetVar);
		 
		 if (isUnchangeable(n.targetVar.kind) || n.targetVar.kind == ASTNode.Kinds.Array 
				 || n.targetVar.kind == ASTNode.Kinds.Method) {
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
		  this.visit(n.returnVal);
		  if (n.returnVal.isNull()) {
			 if (!st.currentMethod.returnType.type.equals(ASTNode.Types.Void)) {
				 typeErrors++;
				 System.out.println(error(n) + "Return type of " + st.currentMethod.name.idname +" is not void."); 
			 }
		  }
		  else {
			  ASTNode.Types type = ((exprNode) n.returnVal).type;
			  if(!type.equals(st.currentMethod.returnType.type)) {
				  typeErrors++;
				  System.out.println(error(n) + "Return type of " + st.currentMethod.name.idname + 
						  " is " + st.currentMethod.returnType.type.toString());
			  }
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
		  
		  LinkedList<ASTNode.Types> types = new LinkedList<ASTNode.Types>();
		  types.add(ASTNode.Types.Integer);
		  types.add(ASTNode.Types.Character);
		  types.add(ASTNode.Types.Boolean);
		  String errorMsg = error(n) + "Operand of cast must be an integer, character or boolean.";
		  typeMustBeIn(n.operand.type, types, errorMsg);
	  }
		
	  // returns whether or not a specific method matches a call
		boolean isFctMethodRight(SymbolInfo method, fctCallNode n) {
			if (method.methodArgs == null) {
				return n.methodArgs.isNull();
			}
			else {
				int argsCount = 0;
				argsNodeOption args = n.methodArgs;
				while (!args.isNull()) {
					if (!isArgValid((argsNode) args, argsCount, method.methodArgs)) {
						return false; //invalid arg, can't be this method
					}
					argsCount++;
					args = ((argsNode) args).moreArgs; 
				}
				return argsCount == method.methodArgs.size();// all of the args we checked were valid, was it the right number?
			}
		}
		
		//check if a method matches a call, and print errors if not
		void printIsFctMethodRight(SymbolInfo method, fctCallNode n) {
			if (method.methodArgs == null) {
				if (!n.methodArgs.isNull()) {
					typeErrors++;
					System.out.println(error(n) + n.methodName.idname + " requires 0 parameters.");
				}
			}
			else {
				int argsCount = 0;
				argsNodeOption args = n.methodArgs;
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
		
		// find and return a method matching all the same specs as the call
		SymbolInfo findRightFctMethod(SymbolInfo method, fctCallNode n) {
			if (isFctMethodRight(method, n)) {
				return method;
			}
			for (SymbolInfo overload : method.overLoadedMethods) {
				if (isFctMethodRight(overload, n)) {
					return overload;
				}
			}
			return null;
		}
		
		void visit(fctCallNode n){
			this.visit(n.methodName);
			this.visit(n.methodArgs);
			SymbolInfo method = (SymbolInfo) st.findBottomSymbol(n.methodName.idname);
			if (method == null) {}// error will already be handled by visiting
			else if (!method.kind.equals(ASTNode.Kinds.Method)) {
				typeErrors++;
				System.out.println(error(n) + n.methodName.idname + " isn't a method.");
			}
			else { // we found a method with the same name
				n.type = method.type;
				n.kind = ASTNode.Kinds.Value;
				if (method.overLoadedMethods.size() == 0) { // only need to check this method
					printIsFctMethodRight(method, n);
				}
				else { // need to check all overloaded methods
					SymbolInfo match = findRightFctMethod(method, n);
					if (match == null) {
						typeErrors++;
						System.out.println(error(n) + "None of the " + (method.overLoadedMethods.size() + 1) + " definitions of method " + n.methodName.idname + " match the parameters in this call.");
					}
				}
			}
		}
	  
	  void visit(unaryOpNode n){
		  n.kind = ASTNode.Kinds.Value;
		  this.visit(n.operand);
		  if (n.operand.type == ASTNode.Types.Integer || n.operand.type == ASTNode.Types.Character){
			  n.type = ASTNode.Types.Integer;
		  }
		  else if(n.operand.type == ASTNode.Types.Boolean){
			  n.type = ASTNode.Types.Boolean;
		  }
		  else{
			 n.type = ASTNode.Types.Boolean;
			String errorMsg = error(n) + "Operand of" + opToString(n.operatorCode) 
	        	+  "must be boolean.";
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
		//shouldn't need type checking
	}

	void visit(falseNode n){
		//shouldn't need type checking
	}

	void visit(bitStringNode n){
		//shouldn't need type checking
	}
}
