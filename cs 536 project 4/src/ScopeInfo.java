import java.util.Hashtable;
import java.util.LinkedList;

//import java.io.*;

// ScopeInfo is the primary data structure used to hold information on identifier
//  declarations and uses. Each ScopeInfo node contains information for one scope.
// ScopeInfo nodes are linked together, in order of appearance of scopes in the CSX Lite
//  program. The entire list of nodes contains count information for the entire program.

public class ScopeInfo {
	int number; 	// sequence number of this scope (starting at 1)
	int line;   	// Source line this scope begins at
	int declsCount;	// Number of declarations in this scope 
	int usesCount;	// Number of identifier uses in this scope 
	ScopeInfo next; // Next ScopeInfo node (in list of all scopes found and processed)
	ScopeInfo prev; // Previous ScopeInfo node (in list of all scoped found and processed)
	Hashtable<String, Variable> vars; // symbol table for this scope
	Hashtable<Integer, LinkedList<String>> illegal; // contains the list of illegal declarations and the lines they occur at
	Hashtable<String, LinkedList<Integer>> undeclared; // contains the undeclared uses and each line they occur
	
	
	// A useful constructor
	ScopeInfo(int num, int l){
		number=num;
		line=l;
		declsCount=0;
		usesCount=0;
		next=null;
		vars = new Hashtable<String, Variable>();
		illegal = new Hashtable<Integer, LinkedList<String>>();
		undeclared = new Hashtable<String, LinkedList<Integer>>();
	}
	
	// A useful constructor
	ScopeInfo(int l){
		number=0;
		line=l;
		declsCount=0;
		usesCount=0;
		next=null;
		vars = new Hashtable<String, Variable>();
		illegal = new Hashtable<Integer, LinkedList<String>>();
		undeclared = new Hashtable<String, LinkedList<Integer>>();
	}
	
	// This method converts a list of ScopeInfo nodes into string form.
	// It controls what the caller sees as the result of the analysis after
	//  the ScopeInfo list is built.
	public String toString() {
		String thisLine="Scope "+number+ " (at line "+line+"): "+declsCount+" declaration(s), "+
	                     usesCount+" identifier use(s)"+"\n";
		if (next == null)
			return thisLine;
		else return thisLine+next.toString();
	   }
	
	// Method append follows list to its end. Then it appends newNode as the new end of list.
	// It also sets number in newNode to be one more than number in the previous
	//  end of list node. Thus append numbers list nodes in sequence as
	//  the list is built.
	public static void append(ScopeInfo list, ScopeInfo newNode){
		while (list.next != null){
			list=list.next;
		}
		list.next=newNode;
		newNode.number=list.number+1;
		newNode.prev=list; // set the previous list
		
	}

	/**
	 * Adds a declaration to the symbol table
	 * @param key - the name of the variable
	 * @param val - the line number of the declaration and the type of variable it is
	 * @return true if declaration successfully added, false if illegal
	 */
	public boolean addDecl(varDeclNode decl){
		String key = decl.varName.idname.toLowerCase();
		int line = decl.linenum;
		typeNode type = decl.varType;
		// check if already declared in this scope, which would be an illegal declaration
		if (vars.get(key) != null){
			// Add to list of illegal declarations made on this line
			LinkedList<String> illegalNames = illegal.get(line);
			if (illegalNames == null){
				illegalNames = new LinkedList<String>();
				illegal.put(line, illegalNames);
			}
			illegalNames.add(key);
			return false;
		}
		// if not already declared, add to the symbol table
		this.vars.put(key, new Variable(key, type.toString(), line));
		this.declsCount++;
		return true;
	}
	
	/**
	 * Documents a use of an identifier at a specific line
	 * @param key - the name of the identifier being used
	 * @param linenum - the line the identifier is being used at
	 */
	public void addUse(String key, int linenum){
		key = key.toLowerCase();
		// check if the variable is undeclared, add to undeclared list in this scope if so, otherwise add to uses in correct scope
		if (!addUse(key, this, linenum)){
			LinkedList<Integer> values = this.undeclared.get(key);
			if (values == null){
				values = new LinkedList<Integer>();
				this.undeclared.put(key, values);
			}
			values.add(linenum); 
		}
	}
	
	/**
	 * Recursively searches each scope for the variable. If found in the given scope, adds to use count and table, 
	 * otherwise searches the previous scope
	 * @param key - the name of the identifier being used
	 * @param scope - the scope to search
	 * @param linenum - the line the identifier is being used at
	 * @return True if the variable is found in a scope, false if not found in any scopes
	 */
	public boolean addUse(String key, ScopeInfo scope, int linenum){
		if (scope == null){ // Undeclared identifier, return false
			return false;
		}
		Variable var = scope.vars.get(key);
		if (var != null){ // Found the identifier in this scope
			// Add line number to uses list for the identifier
			var.addUse(linenum);
			scope.usesCount++;
			return true;
		}
		
		return addUse(key, scope.prev, linenum); // Not found in this scope, search another scope
	}
	
//  This is used only to test this class (during development or modification).
	public static void  main(String args[]) {
		ScopeInfo test = new ScopeInfo(1,1);
		System.out.println("Begin test of ScopeInfo");
		append(test,new ScopeInfo(2));
		append(test,new ScopeInfo(3));
		System.out.println(test);
		System.out.println("End test of ScopeInfo");
	}

}
