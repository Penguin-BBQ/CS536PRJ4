## test7.condition ##
class test {
	int i;
	char c;
	bool b;
	int ia[3];
	char ca[3];
	bool ba[2];

	bool f1(int p1, char p2, bool p3) {
		if (p1) { // not a bool
			char p1;
			i = p1; // type mismatch
			c = p1;
			return p3;
			if (p2) { // not a bool
				bool p1;
				i = p1; // type mismatch
				c = p1; // type mismatch
				b = p1;
				return p3;
			}
		}
		if (p3) {
			int ii;
			return p3;
		}
		else {
			i = ii; // ii is undeclared
			return b;
		}

		while (p1) { // not a bool
			char p1;
			i = p1; // type mismatch
			c = p1;
			return p3;
			while (p2) { // not a bool
				bool p1;
				i = p1; // type mismatch
				c = p1; // type mismatch
				b = p1;
				return p3;
			}
		}
		while (p3) {
			int ii;
			return p3;
		}
		return b;
	}

	void f2(int p1[], char p2[], bool p3[]) {
		if (p1) // not a bool scalar
			return;
		if (p2) // not a bool scalar
			return;
		if (p3) // not a bool scalar
			return;

		while (p1) // not a bool scalar
			return;
		while (p2) // not a bool scalar
			return;
		while (p3) // not a bool scalar
			return;

		return;
	}

	void main() {
		if (i) // not a bool
			return;
		if (c) // not a bool
			return;
		if (b)
			return;
		if (ia) // not a bool
			return;
		if (ca) // not a bool
			return;
		if (ba) // not a bool
			return;
		if (f1) // not a bool
			return;
		if (main) // not a bool
			return;
		if (i == c)
			return;
		if (b < c) // wrong type for <
			return;
		if (i + c) // not a bool
			return;
		if (c * b) // right operand of *, not a bool for if
			return;

		while (i) // not a bool
			break l; // l undeclared, this while doesn't have a label
		while (c) // not a bool
			continue l; // l undeclared, this while doesn't have a lable

		break L; // L undeclared,  break can't appear outside while
		continue L; // L undeclared,  continue can't appear outside while

		L: while (b) {
			break L;
			continue L;
			break b; // b is not a label
			continue main; // main is not a label

			S: while (ia) { // not a bool
				break L;
				break S;
				continue L;
				continue S;
			}

			break S; // the label for this while is not S
			continue S; // the label for this while is not S
		}

		break L; // break can't appear outside while loop
		continue L; // continue can't appear outside while loop

		A: while (ca) {// not a bool
			break A;
			continue A;
			break L; // the label for this while is not L
			continue L; // the label for this while is not L
		}
		D: while (ba) {// not a bool
			int d;
			break D;
			continue D;
		}
		while (f1) // not a bool
			return;
		while (main) // not a bool
			return;
		while (i == c)
			return;
		while (b < c) // wrong type for <
			return;
		while (i + c) // not a bool
			return;
		while (c * b) // right operand of *, not a bool for while
			return;
		while (A) // not a bool
			return;

		if (L) // not a bool
			return;
	}
}
