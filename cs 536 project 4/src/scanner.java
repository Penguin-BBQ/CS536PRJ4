/* This probably doesn't need to be changed */

import java_cup.runtime.*;
import java.io.*;
class Scanner {
	private	static Yylex lex = null;

	public static void init(java.io.FileInputStream yyin){
		if (lex == null)
			lex = new Yylex(yyin); 
		else 	System.err.println("Scanner is already initialized.");
	}

	public static Symbol next_token() throws IOException{
		if (lex == null) {
			System.err.println("Scanner is not yet initialized.");
			System.exit(-1);
		} else	return lex.yylex();
		return null;// To appease javac

	}
}
