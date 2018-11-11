import java.util.LinkedList;

/**
 * A class used to store information about an identifier, including name, type, line number
 * where the identifier was declared, and a list of line numbers where the identifier is used
 */
public class Variable {
	public String name; // Name of the identifier
	public String type; // Type of the identifier
	public int linenum; // Line number where identifier was declared
	public LinkedList<Integer> uses; // Line numbers where identifier is used
	
	public Variable(String name, String type, int linenum){
		this.name = name;
		this.type = type;
		this.linenum = linenum;
		this.uses = new LinkedList<Integer>();
	}
	
	public String getName(){
		return name;
	}
	
	public String getType(){
		return type;
	}
	
	public int getLine(){
		return linenum;
	}
	
	public LinkedList<Integer> getUseLines(){
		return uses;
	}
	
	public int getUseCount(){
		return uses.size();
	}
	
	public void addUse(int line){
		uses.add(line);
	}
	
	public String toString(){
		return name;
	}
}
