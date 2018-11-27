//
//You may use this symbol table implementation or your own (from project 1)
//
import java.util.*;



//import jdk.nashorn.internal.ir.Labels;

import java.io.*;
class SymbolTable {
	
   class Scope {
      Hashtable<String,Symb> currentScope;
      
      Scope next;
      Scope() {
         currentScope = new Hashtable<String,Symb>();
         next = null;
      }
      Scope(Scope scopes) {
         currentScope = new Hashtable<String,Symb>();
         next = scopes;
      }
   }
   
   List<identNode> labels;
   private Scope top;
   private Scope bottom;
   public methodDeclNode currentMethod;

   public Scope nextScope() {
	   return top.next;
   }
   
   SymbolTable() {
	   top = new Scope(); 
	   bottom = top;
	   labels = new ArrayList<identNode>();
	   }
   
   public void addLabel(identNode label) {
	   labels.add(label);
   }
   
   public void removeLavel(identNode label) {
	   labels.remove(label);
   }
   
   public void openScope() {
      top = new Scope(top); 
      }
   public void openScope(methodDeclNode node) {
	   	  currentMethod = node;
	      top = new Scope(top); }

   public void closeScope() throws EmptySTException {
      if (top == null)
         throw new EmptySTException();
      else top = top.next;
   }
   
   public Scope getBottom() {
	   return bottom;
   }
   
   public Symb findBottomSymbol(String key) {
	   return findSymbol(key, bottom);
   }
   
   public Symb findSymbol(String key, Scope scope) {
	   key = key.toLowerCase();
	   return (SymbolInfo) scopeLookup(key, scope);
   }

   public void insert(Symb s)
         throws DuplicateException{//, EmptySTException {
      String key = (s.name().toLowerCase());
      //if (top == null)
        // throw new EmptySTException();
      SymbolInfo checkKey = (SymbolInfo) localLookup(key);
      if (checkKey != null)
      {
    	  //Add method overloading
    	 if(checkKey.kind == ASTNode.Kinds.Method)
    	 {
    		 SymbolInfo sSI = (SymbolInfo) s;
    		 if(sSI.methodArgs.size() != checkKey.methodArgs.size()) {
    			 checkKey.overLoadedMethods.add((SymbolInfo) s);
        		 return;
    		 }
    		 for (int i = 0; i < sSI.methodArgs.size(); i++) {
    			 argDeclNode getExisting = checkKey.methodArgs.get(i);
    			 argDeclNode getNew = sSI.methodArgs.get(i);
    			 if(!getExisting.equalsType(getNew)) {
    				 checkKey.overLoadedMethods.add((SymbolInfo) s);
            		 return; 
    			 }
    			 
    		 }
    		 //We went through the whole list all arguments were the same, so duplicate
    		 throw new DuplicateException();
    	 }
         throw new DuplicateException();
      }
      top.currentScope.put(key,s);
   }

   public Symb scopeLookup(String s, Scope scope) {
	   String key = s.toLowerCase();
	      if (top == null)
	         return null;
	      Symb ans =scope.currentScope.get(key);
	      return ans;
   }
   
   public Symb localLookup(String s) {
      String key = s.toLowerCase();
      if (top == null)
         return null; 
      Symb ans =top.currentScope.get(key);
      return ans;
   }

   public Symb globalLookup(String s) {
      String key = s.toLowerCase();
      Scope top = this.top;
      while (top != null) {
         Symb ans = top.currentScope.get(key);
         if (ans != null)
            return ans;
         else top = top.next;
      }
      return null;
   }

   public String toString() {
      String ans = "";
      Scope top = this.top;
      while (top != null) {
         ans = ans +  top.currentScope.toString()+"\n";
         top = top.next;
      }
      return ans;
   }

   void dump(PrintStream ps) {
     ps.print(toString());
   }
public boolean containsMain() {
	SymbolInfo main = (SymbolInfo) top.currentScope.get("main");
	if(main != null) {
		if(main.type == ASTNode.Types.Void)
		{
			return true;
		}
	}
	return false;
}
}
