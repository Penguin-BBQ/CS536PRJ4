import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * A data structure that is used to store all the cross reference information for a CSX Lite program 
 */
public class ReferenceBuilder {
	LinkedList<Hashtable<String, Variable>> idTable; // List of symbol tables for each scope
	LinkedList<Hashtable<Integer, LinkedList<String>>> illegal; // List of tables of illegal declarations
	Hashtable<String, LinkedList<Integer>> undeclared; // Table of undeclared variable usages
	
	public ReferenceBuilder(){
		idTable = new LinkedList<Hashtable<String, Variable>>();
		illegal = new LinkedList<Hashtable<Integer, LinkedList<String>>>();
		undeclared = new Hashtable<String, LinkedList<Integer>>();
	}
	
	public void addIdTable(Hashtable<String, Variable> table){
		idTable.addLast(table);
	}
	
	/**
	 * Adds a scope's illegally declared variables to the master list of illegal variables
	 * @param table - Hashtable with the line numbers of illegally declared variables and the associated identifiers 
	 */
	public void addIllegal(Hashtable<Integer, LinkedList<String>> table){
		if (table == null){ //sanity check
			table = new Hashtable<Integer, LinkedList<String>>();
		}
		
		illegal.addLast(table);
	}
	
	/**
	 * Adds a scope's undeclared variables to the master list of undeclared variables
	 * @param table - Hashtable with undeclared variables and the lines they are used
	 */
	public void addUndeclared(Hashtable<String, LinkedList<Integer>> table){
		Set<String> keys = table.keySet();
		for (String key : keys){
			LinkedList<Integer> uses = undeclared.get(key);
			if (uses == null){ // have not yet added this identifier, simply put the list of uses in the hashtable
				undeclared.put(key, table.get(key));
			}
			else{ // have already added this identifier in another scope, append the list of uses to the hashtable
				uses.addAll(table.get(key));
			}
		}
	}
	
	/**
	 * Builds the string to display the cross reference information
	 * Format: linenumber: identifier(type): use1,use2,...
	 */
	public String toString(){
		
		Iterator<Hashtable<String, Variable>> idIterator = idTable.iterator();
		Iterator<Hashtable<Integer,LinkedList<String>>> illegalIterator =  illegal.iterator();
		TreeSet<Integer> masterLines = new TreeSet<Integer>();
		Hashtable<Integer, String> stringMap = new Hashtable<Integer, String>();
		
		// loop through the scopes and generate analysis output for each linenum
		while (idIterator.hasNext()){
			Hashtable<String, Variable> table = idIterator.next(); // symbol table for the scope
			Set<String> keys = table.keySet(); // each value in the table
			Hashtable<Integer, LinkedList<String>> map = new Hashtable<Integer, LinkedList<String>>(); // map of linenums to identifiers
			TreeSet<Integer> lines = new TreeSet<Integer>(); // sorted set of line numbers with declarations for this scope
			
			// map each line number to all the identifiers declared on that line
			for (String key : keys){
				Variable decl = table.get(key);
				int line = decl.getLine();
				lines.add(line);
				
				// add this identifier to the list at the appropriate line number, creating the list if empty
				LinkedList<String> varList = map.get(line);
				if (varList == null){
					varList = new LinkedList<String>();
					map.put(line, varList);
				}
				varList.add(key);
			}
			
			// get the illegal redeclaration list for this scope and add its lines so we check them as well
			Hashtable<Integer, LinkedList<String>> illegalTable = illegalIterator.next(); // illegal table for the scope
			Set<Integer> illegalKeys = illegalTable.keySet();
			for (int key : illegalKeys){
				lines.add(key);
			}
			
			// generate all the output for each line number in this scope
			for (int line: lines){
				String builderString = "";
				LinkedList<String> varList = map.get(line); // all the identifiers declared on a single line
				// generate the lines for the valid identifier declarations
				if (varList != null){ // if the identifier list is null, it's because this line only has illegal redeclarations
					for (String name : varList){
						Variable var = table.get(name);
						String type = var.getType();
						LinkedList<Integer> uses = var.getUseLines(); // pull use lines from the variable object
						Collections.sort(uses); // sort the lines, just in case they aren't already sorted
						builderString = builderString + line + ": " + name + "(" + type + ")";
						boolean started = false;
						for (int i : uses){
							// either add a comma or colon to the beginning depending on if this is the first use
							if (started){
								builderString = builderString + ",";
							}
							else{
								started = true;
								builderString = builderString + ": ";
							}
							builderString = builderString + i; // add the line number to the string
						}
						builderString = builderString + "\n";
					}
				}
				// generate the liens for the illegal redeclarations, if there are any
				LinkedList<String> illegalList = illegalTable.get(line);
				if (illegalList != null){
					for (String name : illegalList){
						builderString = builderString + line + ": " + name + "(illegal redeclaration)" + "\n";
					}
				}
				masterLines.add(line); // add linenum to sorted master line set
				stringMap.put(line, builderString); // add generated string for this linenum to master string map with key of the linenum
			}
			// block has been processed, remove its symbol table
			idIterator.remove();
			illegalIterator.remove();
		}
		
		String builderString = "";
		
		// Add all the line summaries together in the correct order, regardless of scope
		for (int line : masterLines){
			builderString = builderString + stringMap.get(line);
		}
		
		// Add undeclared identifiers, if they exist
		Set<String> undeclaredKeys = undeclared.keySet();
		if (undeclaredKeys == null){ // no undeclared identifiers - return what we already have
			return builderString;
		}
		boolean first = true;
		for (String key : undeclaredKeys){ // build each undeclared identifier line by identifier name, not linenum
			if (first){
				builderString = builderString + "Undeclared Identifiers:\n";
				first = false;
			}
			builderString = builderString + "\t" + key + "(undeclared): ";
			LinkedList<Integer> lines = undeclared.get(key); // list of lines where identifier is used
			boolean started = false;
			Collections.sort(lines); // sanity check - should already be sorted
			
			for (int i : lines){
				if (started){
					builderString = builderString + ",";
				}
				else{
					started = true;
				}
				builderString = builderString + i;
			}
			builderString = builderString + "\n";
		}
		
		return builderString;
	}
}
