import java.util.ArrayList;
import java.util.List;

/**************************************************
*  class used to hold information associated w/
*  Symbs (which are stored in SymbolTables)
*  Update to handle arrays and methods
*
****************************************************/

class SymbolInfo extends Symb {
 public ASTNode.Kinds kind; // Should always be Var in CSX-lite
 public ASTNode.Types type; // Should always be Integer or Boolean in CSX-lite
 public List<argDeclNode> methodArgs;
 public List<SymbolInfo> overLoadedMethods;
 public int arraysize;

 public SymbolInfo(String id, ASTNode.Kinds k, ASTNode.Types t){    
	super(id);
	kind = k; 
	type = t;
	methodArgs = null;
	overLoadedMethods = new ArrayList<SymbolInfo>();
	arraysize = -1;
	};

public SymbolInfo(String id, ASTNode.Kinds k, ASTNode.Types t, List<argDeclNode> args) {
	super(id);
	kind = k; 
	type = t;
	methodArgs = args;
	overLoadedMethods = new ArrayList<SymbolInfo>();
	arraysize = -1;
	}
 public String toString(){
             return "("+name()+": kind=" + kind+ ", type="+  type+")";};
}
