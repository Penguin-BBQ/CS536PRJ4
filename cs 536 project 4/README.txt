Students: Calvin Price and Chayce Ririe

Implemented a type checker by using the parser we created in project 3 to create our AST, then visited the nodes of the AST in the TypeChecking class. The type checker starts at the class node of the AST, goes to memberdecls where it then walks through all the global declarations and method calls to build a list of them to be referenced in futher nodes. After this, it begins walking the AST by going into each method and visiting its children, and those children visit their own children, etc.

Most changes were made in TypeChecking.java. Other, more minor changes were made to SymbolTable.java, SymbolInfo.java, ast.java, P4.java, csx.cup, and build.xml.

For extra credit we type checked for loops. For loops allowed by our parser take the following forms:

optionallabel: for (fielddecl exp name INCREMENT/DECREMENT) stmt
optionallabel: for (SEMI SEMI) stmt

The type checker then goes futher, ensuring that the field declaration is the declaration and assignment of an integer scalar variable. A declaration lacking an assignment, a declaration of a non-scalar kind, a declaration of a non-integer type, and a declaration that is not a variable will log errors in the type checker. Otherwise, the errors that log are mostly the same as those in while loops, for example, the condition, break, and continue errors all log the same. 

A group of loop tests are in the fortest.csx file. Each loop in the file is commented to show whether or not it should be considered successful.