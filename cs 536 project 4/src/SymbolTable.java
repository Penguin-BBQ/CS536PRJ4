//
//You may use this symbol table implementation or your own (from project 1)
//
import java.util.*;

import jdk.nashorn.internal.ir.Labels;

import java.io.*;
class SymbolTable {
	
   class Scope {
      Hashtable<String,Symb> currentScope;
      
      Scope next;
      Scope() {
         currentScope = new Hashtable<String,Symb>();
         next = null;
         labels = new ArrayList<identNode>();
      }
      Scope(Scope scopes) {
         currentScope = new Hashtable<String,Symb>();
         next = scopes;
         labels = new ArrayList<identNode>();
      }
   }
   
   List<identNode> labels;
   private Scope top;

   SymbolTable() {top = new Scope();}
   public void addLabel(identNode label) {
	   labels.add(label);
   }
   public void removeLavel(identNode label) {
	   labels.remove(label);
   }
   public void openScope() {
      top = new Scope(top); }

   public void closeScope() throws EmptySTException {
      if (top == null)
         throw new EmptySTException();
      else top = top.next;
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
    			 if(!getExisting.equals(getNew)) {
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
