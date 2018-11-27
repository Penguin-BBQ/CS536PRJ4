## test8.method ##
class test {
	int i;
	char c;
	bool b;
	int ia[3];
	char ca[9];
	bool ba[3];

	int f1() {
		return; // can't return without expression
		return i;
		f1(); // can use a function here
		if (f3())  
			return ia; // wrong return type
		else
			return ca; // wrong return type
	}

	char f2() {
		return c;
		if (b) {
			return "string"; // wrong return type
			c = f2();
			i = f1();
			if (!b)
				return i; // wrong return type
			else
				return b; // wrong return type
		}
		return ia; // wrong return type
	}

	bool f3() {
		while (b) {
			return b;
			return ba; // wrong return type
		}

		while (!b) {
		b = f3() != ba; // wrong type for !=
		return i; // wrong return type
		}
		return ca; // wrong return type
	}

	void f4() {
		return;
		return b; // can't return with expression
		i = f4(); // can't call procedure here
		f4();
	}

	int g1(int p1, int p2[]) {
		return p1;
		return c; // wrong type return;
		return b; // wrong type return;
		return p2; // wrong type return;
		return ba; // wrong type return;
	}

	char g2(char p1, bool p2, char p3[]) {
		while (true) {
			return p1;
			while (p2)
				if (b)
					return p2; // wrong type return
				else
					return g2(c, b, ca);
			while (false) {
				return ca; // wrong type return
				return p3; // wrong type return
			}
		}
		return ba; // wrong type return
	}

	bool g3(int p1[], char p2[], bool p3[]) {
		return p3; // wrong type return
		return p1; // wrong type return
		return p2; // wrong type return
		return c; // wrong type return;
		return ia; // wrong type return
	}

	void g4(int p1, bool p2) {
		int f4;
		bool f2;
		f4(); // f4 is not a method
		c = f2(); // f2 is not a method
		return p1; // can't return with expression
		return;
	}

	char g1(char p1[]) { // Illegal overload
		g4(i, b);
	}

	int f3() { // Illegal overload
		f4();
		return 4;
	}

	void main() {
		i = f1();
		c = f2();
		b = f3();
		i = f3(); // type mismatch
		ia = f1(); // type mismatch
		ca = f2(); // type mismatch
		ba = f3(); // type mismatch

		f4();
		g4(i, b);

		i = g1(i, ia);
		c = g1(ca); // wrong # of argument
		c = g2(c, b, ca);
		c = g2(c, b, "st"); // "st" has wrong type
		b = g3(ia, ca, ba);
		b = g3(i, c, b); // i, c, b has wrong type

		g4(i); // wrong # of arguments
		g4(i, b, c); // wrong # of arguments
		g4(b); // wrong # of arguments
		g4(i, c, b); // wrong # of arguments
		g4(x, y, z); // x, y, z are undeclared, wrong # of arguments

		c = g2(ca, b, ca); // first argument has wrong type
		c = g2(i, c, ca); // i, c has wrong type
		c = g2(ca, i, b); // ca, i, b has wrong type
		c = g2(b, i, c); // b, i, c has wrong type

		main();
		main(i); // wrong # of argument
		return;
	}
}
