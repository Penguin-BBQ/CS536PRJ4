/*  This defines AST classes for CSX
 *  Little, if any, of this needs to be changed
 * 
 */ 
// abstract superclass; only subclasses are actually created  
abstract class ASTNode {

	public  int 	linenum;
	public  int	colnum;
	
	// Type codes used in CSX
		 public enum Types { 
			    Character,
			    Integer,
			    Boolean,
			    Error,
			    Unknown,
			    Void
			   }
		
		 // Kinds of identifiers found in CSX
		public enum Kinds {
				Var,	
			    Value,
			    Array,
			    Method,
			    ArrayParm,
			    ScalarParm,
			    String,
			    VisibleLabel,
			    HiddenLabel,
			    Other
			   }
	
	ASTNode(){linenum=-1;colnum=-1;}
	ASTNode(int l,int c){linenum=l;colnum=c;}
	boolean   isNull(){return false;}; // Is this node null?

    abstract void accept(Visitor v);// Will be defined in sub-classes    
    	
    // default action on an AST node is to record no declarations and no identifier uses
   	 void countDeclsAndUses(ScopeInfo currentScope){ 
   		return;
   	}
};



//This node is used to root only CSXlite programs 
class csxLiteNode extends ASTNode {
	
	public final fieldDeclsOption	progDecls;
	public final stmtsOption 	progStmts;
	private ScopeInfo  		 scopeList;
	
	csxLiteNode(fieldDeclsOption decls, stmtsOption stmts, int line, int col){      
		super(line,col);
		progDecls=decls;
		progStmts=stmts;
		scopeList=null;
	}; 
	
	
	void accept(Visitor u){ u.visit(this); }
	
	// This method begins the count declarations and uses analysis.
	//  It first creates a ScopeInfo node for the entire program.
	//  It then passes this ScopeInfo node to the declarations subtree and then
	//   the statements subtree. Visiting these two subtrees causes all identifier uses and
	//     declarations to be recognized and recorded in the list rooted by the ScopeInfo node.
	//  Finally, the information stored in the ScopeInfo list is converted to string form
	//   and returned to the caller of the analysis.
	
	 String countDeclsAndUses(){
		 scopeList = new ScopeInfo(1,linenum);
		 progDecls.countDeclsAndUses(scopeList);
		 progStmts.countDeclsAndUses(scopeList);
		 return scopeList.toString();
	 }
	 
	/**
	 * Builds the cross reference information for the CSX Lite program by traversing the scope list
	 * and obtaining all the information about declarations and usages
	 * @return A string with all identifier declarations and uses for the program
	 */
	String buildCrossReferences(){
		ReferenceBuilder references = new ReferenceBuilder();
		countDeclsAndUses(); // Count uses to build the symbol tables
		
		// traverse the scopes to collect all the identifier information
		ScopeInfo tmpScope = scopeList;
		while (tmpScope != null){
			references.addIdTable(tmpScope.vars);
			references.addIllegal(tmpScope.illegal);
			references.addUndeclared(tmpScope.undeclared);
			tmpScope = tmpScope.next;
		}
		return references.toString();
	}
	 
};

// Root of all ASTs for CSX
class classNode extends ASTNode {

        public final identNode       className;
        public final memberDeclsNode members;

        classNode(identNode id, memberDeclsNode m, int line, int col){
                super(line,col);
                className=id;
                members=m;
        }

	void accept(Visitor u){u.visit(this); }
		//System.out.println("In classnode accept\n");
		
};

class memberDeclsNode extends ASTNode {

        fieldDeclsOption 				fields;
        public final methodDeclsOption	methods;

        memberDeclsNode(fieldDeclsOption f, methodDeclsOption m,
                        int line, int col){
                super(line,col);
                fields=f;
                methods=m;
        }

	void accept(Visitor u){ u.visit(this); }
};


abstract class fieldDeclsOption extends ASTNode{
	fieldDeclsOption(int line,int column){
		super(line,column);
	}
	fieldDeclsOption(){ super(); }

	static nullFieldDeclsNode NULL = new nullFieldDeclsNode();
};

class fieldDeclsNode extends fieldDeclsOption {

	public final declNode		thisField;
	public final fieldDeclsOption 	moreFields;
	
	fieldDeclsNode(declNode d, fieldDeclsOption f, int line, int col){
		super(line,col);
		thisField=d;
		moreFields=f;
	}

	void accept(Visitor u){ u.visit(this);}

	void countDeclsAndUses(ScopeInfo currentScope){
		thisField.countDeclsAndUses(currentScope);
		moreFields.countDeclsAndUses(currentScope);
		return;
	}
};

class nullFieldDeclsNode extends fieldDeclsOption {
	
	nullFieldDeclsNode(){};

	boolean   isNull(){return true;};

	void accept(Visitor u){ u.visit(this);}

	void countDeclsAndUses(ScopeInfo currentScope){
			return;
		}
};

// abstract superclass; only subclasses are actually created
abstract class declNode extends ASTNode {
	declNode(){super();};
	declNode(int l,int c){super(l,c);};
};


class varDeclNode extends declNode { 
	
	public final	identNode	varName;
	public 			typeNode 	varType;
	public final	exprNodeOption 	initValue;
	
	varDeclNode(identNode id, typeNode t, exprNodeOption e,
			int line, int col){
		super(line,col);
		varName=id;
		varType=t;
		initValue=e;
	}
	
	void accept(Visitor u){ u.visit(this);}
	
	// This node represents a variable declaration, so we increment the declarations
		//  count by 1
		void countDeclsAndUses(ScopeInfo currentScope){
			// add declaration to scope's symbol table. If already exists, reset the type to illegal
			if (!currentScope.addDecl(this)){
				varType = new illegalTypeNode(linenum, colnum);
			}
			return;
		}
};

class constDeclNode extends declNode {

        public final identNode       constName;
        public final exprNode        constValue;

        constDeclNode(identNode id,  exprNode e, int line, int col){
                super(line,col);
                constName=id;
                constValue=e;
        }

	void accept(Visitor u) { u.visit(this);}
};


class arrayDeclNode extends declNode {

        public final identNode       arrayName;
        public final typeNode        elementType;
        public final intLitNode      arraySize;

        arrayDeclNode(identNode id, typeNode t, intLitNode lit, int line, int col){
                super(line,col);
                arrayName=id;
                elementType=t;
                arraySize=lit;
        }

	void accept(Visitor u) { u.visit(this);}
};


abstract class typeNodeOption extends ASTNode {
// abstract superclass; only subclasses are actually created
	public Types type;
	typeNodeOption(){super();};
	typeNodeOption(int l,int c){super(l,c);};
	static nullTypeNode NULL = new nullTypeNode();
};

abstract class typeNode extends typeNodeOption {
	// abstract superclass; only subclasses are actually created
	     // Used for typechecking -- the type of this typeNode
		typeNode(){super();};
		typeNode(int l,int c, Types t){super(l,c);type = t;};
	};

class nullTypeNode extends typeNodeOption {
	nullTypeNode(){};

	boolean   isNull(){return true;};

	void accept(Visitor u){ u.visit(this); }
	
	public String toString(){
		return "null";
	}
};


class intTypeNode extends typeNode {
	intTypeNode(int line, int col){
		super(line,col,  Types.Integer);
	}

	void accept(Visitor u){ u.visit(this); }
	
	public String toString(){
		return "int";
	}
};


class boolTypeNode extends typeNode {
	boolTypeNode(int line, int col){
		super(line,col,  Types.Boolean);
	}

	void accept(Visitor u){ u.visit(this); }
	
	public String toString(){
		return "bool";
	}
};

class illegalTypeNode extends typeNode {
	illegalTypeNode(int line, int col){
		super(line,col, Types.Error);
	}

	void accept(Visitor u){ u.visit(this); }
};

class charTypeNode extends typeNode {
        charTypeNode(int line, int col){
                super(line,col, Types.Character);
        }

	void accept(Visitor u) { u.visit(this); }
};

class voidTypeNode extends typeNode {
        voidTypeNode(int line, int col){
                super(line,col, Types.Void);
        }

	void accept(Visitor u) { u.visit(this); }
};

//abstract superclass; only subclasses are actually created
abstract class methodDeclsOption extends ASTNode {
	methodDeclsOption(){super();};
	methodDeclsOption(int l,int c){super(l,c);};
	
	boolean   isNull(){return false;};

        static nullMethodDeclsNode NULL = new nullMethodDeclsNode();
};

class methodDeclsNode extends methodDeclsOption {

        public final methodDeclNode         thisDecl;
        public final methodDeclsOption      moreDecls;
        

        methodDeclsNode(methodDeclNode m, methodDeclsOption ms, int line, int col){
                super(line,col);
                thisDecl=m;
                moreDecls=ms;
        }

	void accept(Visitor u){ u.visit(this); }
};

class nullMethodDeclsNode extends methodDeclsOption {

        nullMethodDeclsNode(){};

        boolean   isNull(){return true;};

	void accept(Visitor u) { u.visit(this); }
};

class methodDeclNode extends ASTNode {
       
        public final identNode         name;
        public final argDeclsOption    args;
        public final typeNodeOption    returnType;
        public final fieldDeclsOption  decls;
        public final stmtsOption         stmts;
        
        methodDeclNode(identNode id, argDeclsOption a, typeNodeOption t,
                fieldDeclsOption f, stmtsOption s, int line, int col){
        	super(line,col);
        	name=id;
        	args=a;
        	returnType=t;
        	decls=f;
        	stmts=s;
		}

	void accept(Visitor u){ u.visit(this); }
};


// abstract superclass; only subclasses are actually created
abstract class argDeclNode extends ASTNode {
		public ASTNode.Types type;
		public ASTNode.Kinds kind;
        argDeclNode(){super();};
        argDeclNode(int l,int c){super(l,c);};
        public boolean equalsType(Object o) {
        	return false;
        }
};


abstract class argDeclsOption extends ASTNode{

	argDeclsOption(int line,int column){
		super(line,column);
	}
	argDeclsOption(){ super(); }

        static nullArgDeclsNode NULL = new nullArgDeclsNode();
};


class argDeclsNode extends argDeclsOption {

        public final argDeclNode     thisDecl;
        public final argDeclsOption    moreDecls;

        argDeclsNode(argDeclNode arg, argDeclsOption args,
                        int line, int col){
                super(line,col);
                thisDecl=arg;
                moreDecls=args;
        }

	void accept(Visitor u) { u.visit(this); }
};

class nullArgDeclsNode extends argDeclsOption {

        nullArgDeclsNode(){};

        boolean   isNull(){return true;};

	void accept(Visitor u) { u.visit(this); }
};


class arrayArgDeclNode extends argDeclNode {

        public final identNode       argName;
        public final typeNode        elementType;

        arrayArgDeclNode(identNode id, typeNode t, int line, int col){
                super(line,col);
                argName=id;
                elementType=t;
        }

	void accept(Visitor u) { u.visit(this); }
	public boolean equalsType(Object n) {
		if(n.getClass() != this.getClass()) {
			return false;
		}
		arrayArgDeclNode temp = (arrayArgDeclNode) n;
		if(temp.elementType.equals(this.elementType)) {
			return false;
		}
			
		return true;
	}
};


class valArgDeclNode extends argDeclNode {

        public final identNode       argName;
        public final typeNode        argType;

        valArgDeclNode(identNode id, typeNode t, int line, int col){
                super(line,col);
                argName=id;
                argType=t;
        }

	void accept(Visitor u) { u.visit(this); }
	public boolean equalsType(Object n) {
		if(n.getClass() != this.getClass()) {
			return false;
		}
		valArgDeclNode temp = (valArgDeclNode) n;
		if(!temp.argType.type.equals(this.argType.type)) {
			return false;
		}
			
		return true;
	}
};



//abstract superclass; only subclasses are actually created
abstract class stmtOption extends ASTNode {
	stmtOption(){super();};
	stmtOption(int l,int c){super(l,c);};
	static nullStmtNode NULL = new nullStmtNode();
};

// abstract superclass; only subclasses are actually created
abstract class stmtNode extends stmtOption {
	stmtNode(){super();};
	stmtNode(int l,int c){super(l,c);};
};

class nullStmtNode extends stmtOption {
	nullStmtNode(){};
	boolean   isNull(){return true;};
	void accept(Visitor u){ u.visit(this);}
	void countDeclsAndUses(ScopeInfo currentScope){return;}
};

abstract class stmtsOption extends ASTNode{
	stmtsOption(int line,int column){
		super(line,column);
	}
	stmtsOption(){ super(); }

	static nullStmtsNode NULL = new nullStmtsNode();
};

class stmtsNode extends stmtsOption { 
	public final stmtNode	    	thisStmt;
	public final stmtsOption 	moreStmts;

	stmtsNode(stmtNode stmt, stmtsOption stmts, int line, int col){
		super(line,col);
		thisStmt=stmt;
		moreStmts=stmts;
	};
	
	void accept(Visitor u){ u.visit(this);}
	
	void countDeclsAndUses(ScopeInfo currentScope){
		 // Count decls and uses in both subtrees:
			 thisStmt.countDeclsAndUses(currentScope);
			 moreStmts.countDeclsAndUses(currentScope);
			}

};


class nullStmtsNode extends stmtsOption {
	nullStmtsNode(){};

	boolean   isNull(){return true;};

	void accept(Visitor u){ u.visit(this);}
	
	void countDeclsAndUses(ScopeInfo currentScope){return;}
};

class asgNode extends stmtNode {      

	public final nameNode	target;
	public final exprNode 	source;
	
	asgNode(nameNode n, exprNode e, int line, int col){       
		super(line,col);
		target=n;
		source=e;
	};
	
	void accept(Visitor u){ u.visit(this);}
	
	void countDeclsAndUses(ScopeInfo currentScope){
		// The target of the assign counts as 1 use
		currentScope.addUse(target.varName.idname, linenum);
		// Visit the source expression to include the identifiers in it
		source.countDeclsAndUses(currentScope);
		}
};

class incrementNode extends stmtNode {      

	public final nameNode	target;
	
	incrementNode(nameNode n, int line, int col){       
		super(line,col);
		target=n;
	};
	
	void accept(Visitor u){ u.visit(this);}
};
class decrementNode extends stmtNode {      

	public final nameNode	target;
	
	decrementNode(nameNode n, int line, int col){       
		super(line,col);
		target=n;
	};
	
	void accept(Visitor u){ u.visit(this);}
};


class ifThenNode extends stmtNode {
	
	public final exprNode 		condition;
	public final stmtNode 		thenPart;
	public final stmtOption 	elsePart;
	
	ifThenNode(exprNode e, stmtNode s1, stmtOption s2, int line, int col){
		super(line,col);
		condition=e;
		thenPart=s1;
		elsePart=s2;
	};

	ifThenNode(exprNode e, stmtNode s1, int line, int col){
		super(line,col);
		condition=e;
		thenPart=s1;
		elsePart=stmtNode.NULL;
	};
	
	
	void accept(Visitor u){ u.visit(this);}
	
	void countDeclsAndUses(ScopeInfo currentScope){
		// Count identifier uses in control expression and then statement.
		// In CSX Lite the else statement is always null
		condition.countDeclsAndUses(currentScope);
		thenPart.countDeclsAndUses(currentScope);
		}
};

class whileNode extends stmtNode {

        public final exprNodeOption  label;
        public final exprNode        condition;
        public final stmtNode        loopBody;

        whileNode(exprNodeOption i, exprNode e, stmtNode s, int line, int col){
                super(line,col);
                label=i;
                condition=e;
                loopBody=s;
        }

	void accept(Visitor u) { u.visit(this);}
};

class forNode extends stmtNode {

    public final exprNodeOption  label;
    public final stmtOption		 initialization;
    public final exprNodeOption  condition;
    public final exprNodeOption	 variableChange;
    public final boolean		 inc;		 
    public final stmtNode        loopBody;

    forNode(exprNodeOption i, stmtOption init, exprNodeOption e, exprNodeOption change,
    		boolean increment, stmtNode s, int line, int col){
            super(line,col);
            label=i;
            initialization=init;
            condition=e;
            variableChange=change;
            inc = increment;
            loopBody=s;
    }

void accept(Visitor u) { 
	u.visit(this);}
};

abstract class readNodeOption extends stmtNode{
	readNodeOption(int line,int column){ super(line,column); }

	readNodeOption(){ super(); }

        static nullReadNode NULL = new nullReadNode();
};

class readNode extends readNodeOption {

        public final nameNode        targetVar;
        public final readNodeOption  moreReads;

        readNode(nameNode n, readNodeOption rn, int line, int col){
                super(line,col);
                 targetVar=n;
                 moreReads=rn;
        }

	void accept(Visitor u) { u.visit(this);}
};


class nullReadNode extends readNodeOption {

        nullReadNode(){};

        boolean   isNull(){return true;};

	void accept(Visitor u){ u.visit(this);}
};

abstract class printNodeOption extends stmtNode{
	printNodeOption(int line,int column){ super(line,column); }

	printNodeOption(){ super(); }

        static nullPrintNode NULL = new nullPrintNode();
};

class printNode extends printNodeOption {

        public final exprNode       	outputValue;
        public final printNodeOption	morePrints;

        printNode(exprNode val, printNodeOption pn, int line, int col){
                super(line,col);
                outputValue=val;
                morePrints=pn;
        }

	void accept(Visitor u) { u.visit(this);}
};


class nullPrintNode extends printNodeOption {
        nullPrintNode(){};

        boolean   isNull(){return true;};

	void accept(Visitor u){ u.visit(this);}
};


class callNode extends stmtNode {

        public final identNode       methodName;
        public final argsNodeOption  args;

        callNode(identNode id, argsNodeOption a, int line, int col){
                super(line,col);
                methodName=id;
                args=a;
        }

	void accept(Visitor u) { u.visit(this);}
};

class returnNode extends stmtNode {

        public final exprNodeOption	returnVal;

        returnNode(exprNodeOption e, int line, int col){
                super(line,col);
                returnVal=e;
        }
	
	 void accept(Visitor u) { u.visit(this);}

};


class blockNode extends stmtNode {
	
	public final fieldDeclsOption 	decls;  
	public final stmtsOption 	stmts;
	
	blockNode(fieldDeclsOption f, stmtsOption s, int line, int col){
		super(line,col);
		decls=f;
		stmts=s;
	}
	
	 void accept(Visitor u){ u.visit(this);}

	 
	 void countDeclsAndUses(ScopeInfo currentScope){
			/* A block opens a new scope, so a new ScopeInfo node is created.
			   It is appended to the end of the ScopeInfo list.
			   The new scope is used to record local declarations and uses in the block
			*/ 
			 ScopeInfo  localScope = new ScopeInfo(linenum);
			 ScopeInfo.append(currentScope,localScope);
			 decls.countDeclsAndUses(localScope);
			 stmts.countDeclsAndUses(localScope);
		}
};

class breakNode extends stmtNode {

        public final identNode       label;

        breakNode(identNode i, int line, int col){
                super(line,col);
                label=i;
        }
	
	 void accept(Visitor u) { u.visit(this);}
};

class continueNode extends stmtNode {

        public final identNode       label;

        continueNode(identNode i, int line, int col){
                super(line,col);
                label=i;
        }
	
	 void accept(Visitor u) { u.visit(this);}
};


//abstract superclass; only subclasses are actually created
abstract class argsNodeOption extends ASTNode {
	argsNodeOption(){super();};
	argsNodeOption(int l,int c){super(l,c);};
	boolean isNull() {return false;};
        static nullArgsNode NULL = new nullArgsNode();
};

class argsNode extends argsNodeOption {

        public final exprNode        	argVal;
        public final argsNodeOption     moreArgs;

        argsNode(exprNode e, argsNodeOption a, int line, int col){
                super(line,col);
                argVal=e;
                moreArgs=a;
        }

	void accept(Visitor u){ u.visit(this);}
};


class nullArgsNode extends argsNodeOption {

        nullArgsNode(){};

        boolean   isNull(){return true;};

	void accept(Visitor u){ u.visit(this);}
};

class strLitNode extends exprNode {

        public final String  strval;

        strLitNode(String stringval, int line, int col){
                super(line,col);
                strval=stringval;
        }

	void accept(Visitor u)  { u.visit(this);}
};


//abstract superclass; only subclasses are actually created
abstract class exprNodeOption extends ASTNode {
	exprNodeOption(){super();};
	exprNodeOption(int l,int c){super(l,c);};
	boolean isNull() {return false;}

	static nullExprNode NULL = new nullExprNode();
};

//abstract superclass; only subclasses are actually created
abstract class exprNode extends exprNodeOption {
	 protected Types   type; // Used for typechecking: the type of this node
  protected Kinds   kind; // Used for typechecking: the kind of this node

	exprNode(){super();};
	exprNode(int l,int c){
             super(l,c);
		type=Types.Error;
		kind=Kinds.Other;
     };
     
	exprNode(int l,int c,Types t,Kinds k) {
		super(l,c);
             type = t; kind = k;
     };

};

class nullExprNode extends exprNodeOption {
	nullExprNode(){super();};

	boolean   isNull(){return true;};

	void accept(Visitor u){}
};

class binaryOpNode extends exprNode {
	
	public final exprNode 	leftOperand;
	public final exprNode 	rightOperand;
	public final int	operatorCode; // Token code of the operator
	
	binaryOpNode(exprNode e1, int op, exprNode e2, int line, int col){
		super(line,col);
		operatorCode=op;
		leftOperand=e1;
		rightOperand=e2;
	};

	void accept(Visitor u){ u.visit(this);}

	// Count identifier uses in left and right operands
	 void countDeclsAndUses(ScopeInfo currentScope){
			leftOperand.countDeclsAndUses(currentScope);
			rightOperand.countDeclsAndUses(currentScope);
		}
};


class unaryOpNode extends exprNode {

        public final exprNode        operand;
        public final int             operatorCode; // Token code of the operator
        unaryOpNode(int op, exprNode e, int line, int col){
                super(line,col);
                operand=e;
                operatorCode=op;
        }

	void accept(Visitor u) { u.visit(this);}
};

class castNode extends exprNode {

        public final exprNode        operand;
        public final typeNode        resultType;

        castNode(typeNode t, exprNode e, int line, int col){
                super(line,col);
                operand=e;
                resultType=t;
        }

	void accept(Visitor u) { u.visit(this);}
};

class fctCallNode extends exprNode {

        public final identNode       methodName;
        public final argsNodeOption  methodArgs;

        fctCallNode(identNode id, argsNodeOption a, int line, int col){
                super(line,col);
                methodName=id;
                methodArgs=a;
        }

	void accept(Visitor u) { u.visit(this);}
};


class identNode extends exprNode {
	
	public final String 	idname;
	public  SymbolInfo  	idinfo; // symbol table entry for this ident
	private boolean		nullFlag;
	
	identNode(String identname, int line, int col){
		super(line,col,Types.Unknown, Kinds.Var);
		idname   = identname;
		nullFlag = false;
	};

	identNode(boolean flag){
		super(0,0,Types.Unknown, Kinds.Var);
		idname   = "";
		nullFlag = flag;
	};
	
	boolean   isNull(){return nullFlag;}; // Is this node null?

	static identNode NULL = new identNode(true);

	void accept(Visitor u){ u.visit(this);}
	
	//One identifier used here:
	void countDeclsAndUses(ScopeInfo currentScope){
		currentScope.addUse(idname, linenum);
	}
};

class nameNode extends exprNode {

        public final identNode    	  		varName;
        public final exprNodeOption       	subscriptVal;

        nameNode(identNode id, exprNodeOption expr, int line, int col){
                super(line,col);
                varName=id;
                subscriptVal=expr;
        };

        nameNode(identNode id, int line, int col){
                super(line,col);
                varName=id;
                subscriptVal=exprNode.NULL;
        };

	void accept(Visitor u){ u.visit(this);}

};




class intLitNode extends exprNode {
	public final int 	intval;
	intLitNode(int val, int line, int col){
		super(line,col);
		intval=val;
		type = Types.Integer;
		kind = Kinds.Value;
	}

	void accept(Visitor u){ u.visit(this);}
};

class bitStringNode extends exprNode {
	public final int 	intValue;
	public final String 	bitString;
	bitStringNode(int val, String bitStr, int line, int col){
		super(line,col);
		intValue=val;
		bitString=bitStr;
		type = Types.Integer;
		kind = Kinds.String;
	}

	void accept(Visitor u){ u.visit(this);}
};

class charLitNode extends exprNode {

        public final char    charval;

        charLitNode(char val, int line, int col){
                super(line,col);
                 charval=val;
                 type = Types.Character;
         		kind = Kinds.Value;
        }

	void accept(Visitor u) { u.visit(this);}
};


class trueNode extends exprNode {
        trueNode(int line, int col){
                super(line,col);
                type = Types.Boolean;
         		kind = Kinds.Value;
        }

	void accept(Visitor u) { u.visit(this);}
};


class falseNode extends exprNode {
        falseNode(int line, int col){
                super(line,col);
                type = Types.Boolean;
         		kind = Kinds.Value;
        }

	void accept(Visitor u) { u.visit(this);}
};

