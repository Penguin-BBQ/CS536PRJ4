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
	 void typesMustBeEqual(ASTNode.Types type1,ASTNode.Types type2,String errorMsg) {
		 if ((type1 != ASTNode.Types.Error) && (type2 != ASTNode.Types.Error) &&
                     (type1 != type2)) {
                        System.out.println(errorMsg);
                        typeErrors++;
                }
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
		if (checkDecl(n, n.varName, n.varType.type, false, ASTNode.Kinds.Var)) {
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
		this.visit(n.varName); // Subscripts not allowed in CSX Lite
        	n.type=n.varName.type;

	}

	void visit(asgNode n){
		this.visit(n.target);
		if (n.target.varName.idinfo == null) {
		}
		else if (isUnchangeable(n.target.varName.idinfo.kind)) {
			typeErrors++;
			System.out.println(error(n) + "Target of assignment can't be changed.");
		}
		else if (n.target.type == ASTNode.Types.Character && n.target.kind == ASTNode.Kinds.Array
        		&& n.source.kind == ASTNode.Kinds.String){
        	//verify array length same as string
        }
        else{
        	typesMustBeEqual(n.source.type, n.target.type,
                    error(n) + "Right hand side of an assignment is not assignable to left hand side.");
        	if(n.target.kind == ASTNode.Kinds.Array && n.source.kind == ASTNode.Kinds.Array){
        		//verify each array has the same length
        	}
        }
		this.visit(n.source);
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
		 String errorMsg = error(n) + "Only int, bool, char, char arrays, and string literal"
		 		+ " values may be printed in CSX.";
		 if (n.outputValue.kind == ASTNode.Kinds.Array){
			 typeMustBe(n.outputValue.type, ASTNode.Types.Character, errorMsg);
		 }
		 else if (n.outputValue.kind == ASTNode.Kinds.String){}
		 else{
			 LinkedList<ASTNode.Types> types = new LinkedList<ASTNode.Types>();
			 types.add(ASTNode.Types.Integer);
			 types.add(ASTNode.Types.Character);
			 types.add(ASTNode.Types.Boolean);
			 typeMustBeIn(n.outputValue.type, types,
					error(n) + "Only int values may be printed in CSX-lite.");
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
        	if (n.operatorCode== sym.PLUS||n.operatorCode==sym.MINUS
        			||n.operatorCode== sym.TIMES||n.operatorCode==sym.SLASH){
        		n.type = ASTNode.Types.Integer;
        		LinkedList<ASTNode.Types> opTypes = new LinkedList<ASTNode.Types>();
        		opTypes.add(ASTNode.Types.Integer);
        		opTypes.add(ASTNode.Types.Character);
        		typeMustBeIn(n.leftOperand.type, opTypes,
                	error(n) + "Left operand of" + opToString(n.operatorCode) 
                         	+  "must be an int or a char.");
        		typeMustBeIn(n.rightOperand.type, opTypes,
                	error(n) + "Right operand of" + opToString(n.operatorCode) 
                         	+  "must be an int or a char.");
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
        		String errorMsg = error(n)+"Both operands of"+
                           opToString(n.operatorCode)+"must have the same type.";
        		if (n.leftOperand.type == ASTNode.Types.Integer){
        			n.type = ASTNode.Types.Integer;
        			typesMustBeEqual(n.leftOperand.type,n.rightOperand.type,errorMsg);
        		}
        		else if (n.leftOperand.type == ASTNode.Types.Boolean){
        			n.type = ASTNode.Types.Boolean;
        			typesMustBeEqual(n.leftOperand.type,n.rightOperand.type,errorMsg);
        		}
        		else{
        			errorMsg = error(n) + "Left operand of" + opToString(n.operatorCode)
					+ "must be an int or a bool";
        			System.out.println(errorMsg);
        			typeErrors++;
        		}		
        	}
        	else { // Must be a comparison operator
        		n.type = ASTNode.Types.Boolean;
        		String errorMsg = error(n)+"Both operands of"+
                           opToString(n.operatorCode)+"must have the same type.";
        		if (n.leftOperand.type != ASTNode.Types.Integer || 
        				n.leftOperand.type != ASTNode.Types.Boolean){
        			errorMsg = error(n) + "Left operand of" + opToString(n.operatorCode)
					+ "must be an int or a bool";
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
		 st.openScope();
		 this.visit(n.members);
		 //No type checking needed 
		 
		}

	 void  visit(memberDeclsNode n){
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
				 st.insert(new SymbolInfo(temp.thisDecl.name.idname, ASTNode.Kinds.Method, temp.thisDecl.returnType.type, argList));
			 } catch (DuplicateException e) {
				 typeErrors ++;
				 System.out.println(error(temp.thisDecl) + "Method name already used");
				 
			 }
			 this.typeMustBeIn(temp.thisDecl.returnType.type, requiredTypes, "Illegal method return type");
			 if(temp.moreDecls.isNull()) {
				 break;
			 }
			 temp = (methodDeclsNode) temp.moreDecls;
		 }
		 this.visit(n.fields);
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
		 
		 //System.out.println("Type checking for valArgDeclNode not yet implemented");
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
		System.out.println("Type checking for valArgDeclNode not yet implemented");
	}

	void visit(nullArgDeclsNode n){}

	
	void visit(valArgDeclNode n){
		try {
			st.insert(new SymbolInfo(n.argName.idname,n.argName.kind,n.argType.type));
		} catch (DuplicateException e) {
			typeErrors++;
			System.out.println(error(n) + ": Duplicate variable");
		}
	}
	
	void visit(arrayArgDeclNode n){
		try {
			st.insert(new SymbolInfo(n.argName.idname,n.argName.kind,n.elementType.type));
		} catch (DuplicateException e) {
			typeErrors++;
			System.out.println(error(n) + ": Duplicate variable");
		}
	}
	
	void visit(constDeclNode n){
		checkDecl(n, n.constName, n.constValue.type, true, ASTNode.Kinds.Value);
	}
	
	boolean checkDecl(declNode n, identNode name, ASTNode.Types type, boolean constant, ASTNode.Kinds kind) {
		SymbolInfo id;
		id = (SymbolInfo) st.localLookup(name.idname);
		try {
			id = new SymbolInfo(name.idname, kind, type);
			name.type = type;
			name.kind = kind;
			name.idinfo=id;
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
		checkDecl(n, n.arrayName, n.elementType.type, false, ASTNode.Kinds.Array);
		if (n.arraySize.intval <= 0) {
			typeErrors++;
			System.out.println(error(n) + n.arrayName.idname + " must have more than 0 elements.");
		}
	 }
	
	void visit(charTypeNode n){
		System.out.println("Type checking for charTypeNode not yet implemented");
	}
	void visit(voidTypeNode n){
		System.out.println("Type checking for voidTypeNode not yet implemented");
	}

	void visit(whileNode n){
		this.visit(n.label);
		this.visit(n.condition);
		this.visit(n.loopBody);
		System.out.println("Type checking for whileNode not yet implemented");
	  }

	void visit(breakNode n){
		this.visit(n.label);
		System.out.println("Type checking for breakNode not yet implemented");
	}
	void visit(continueNode n){
		this.visit(n.label);
		System.out.println("Type checking for continueNode not yet implemented");
	}
	  
	void visit(callNode n){
		this.visit(n.methodName);
		this.visit(n.args);
		System.out.println("Type checking for callNode not yet implemented");
	}

	  
	  void visit(readNode n){
		 this.visit(n.targetVar);
		 String errorMsg = error(n) + "Only int, and char values may be read";
		 LinkedList<ASTNode.Types> types = new LinkedList<ASTNode.Types>();
		 types.add(ASTNode.Types.Integer);
		 types.add(ASTNode.Types.Character);
		 typeMustBeIn(n.targetVar.type, types, errorMsg);
		 this.visit(n.moreReads);
	  }
	  

	  void visit(returnNode n){
		  this.visit(n.returnVal);
		System.out.println("Type checking for returnNode not yet implemented");
	  }

	  
	  void visit(argsNode n){
		  this.visit(n.argVal);
		  this.visit(n.moreArgs);
		System.out.println("Type checking for argsNode not yet implemented");
	  }
	  	
	  void visit(nullArgsNode n){}
		
	  void visit(castNode n){
		  this.visit(n.operand);
		  this.visit(n.resultType);
		System.out.println("Type checking for castNode not yet implemented");
	  }

	  void visit(fctCallNode n){
		  this.visit(n.methodName);
		  this.visit(n.methodArgs);
		System.out.println("Type checking for fctCallNode not yet implemented");
	  }
	  
	  void visit(unaryOpNode n){
		  this.visit(n.operand);
		  if (!isScalar(n.operand.kind)) {
			  typeErrors++;
			  String errorMsg = error(n) + "Operand of" + opToString(n.operatorCode) 
	        	+  "must be a scalar.";
			  System.out.println(errorMsg);
		  }
		  if (n.operand.type == ASTNode.Types.Integer){
			  n.type = ASTNode.Types.Integer;
		  }
		  else if(n.operand.type == ASTNode.Types.Boolean){
			  n.type = ASTNode.Types.Boolean;
		  }
		  else{
			String errorMsg = error(n) + "Operand of" + opToString(n.operatorCode) 
	        	+  "must be an int or a char.";
			System.out.println(errorMsg);
  			typeErrors++;
		  }
	  }

	
	void visit(charLitNode n){
		System.out.println("Type checking for charLitNode not yet implemented");
	}
	  
	void visit(strLitNode n){
		System.out.println("Type checking for strLitNode not yet implemented");
	}

	
	void visit(trueNode n){
		System.out.println("Type checking for trueNode not yet implemented");
	}

	void visit(falseNode n){
		System.out.println("Type checking for falseNode not yet implemented");
	}

	void visit(bitStringNode n){
		System.out.println("Type checking for bitStringNode not yet implemented");
	}

	@Override
	void visit(forNode n) {
		// TODO Auto-generated method stub
		
	}
}
