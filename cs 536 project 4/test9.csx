## test13.misc ##
class test {
	int i;
	char c;
	bool b;
	const ic = 9;
	const cc = 's';
	const bc = true;
	int ia[3];
	char ca[5];
	bool ba[2];

	int f1(int p1, char p2, bool p3) {
		int aa[9];

		aa[p1] = i;
		c = aa[p2]; // type mismatch
		i = aa[p3]; // subscript error

		i = (int)p2;
		ia = (int)p3; // type mismatch
		c = (char)p1;
		ca = (char)p3; // type mismatch
		b = (bool)p1;
		ba = (bool)p2; // type mismatch

		return 3;
	}

	char f2(int p1[], char p2[], bool p3[]) {
		char bb[9];

		bb[p1] = c; // subscript error
		c = bb[p2]; // subscript error
		c = bb[p3]; // subscript error

		i = (int)p2; // can't cast array
		c = (char)p3; // can't cast array
		b = (bool)p1; // can't cast array

		return '3';
	}

	bool f3() {
		i = ia[9];
		c = ca['s'];
		i = ia[i + c];

		i = (int)bc;
		c = (char)ic;
		b = (bool)cc;

		i = (int)'c';
		c = (char)true;
		b = (bool)4;
		i = (int)"a"; // can't cast stringLit
		c = (char)(false);
		b = (bool)f2(ia, ca, ba);
		i = (int)f3();
		c = (char)f1(i, c, b);
		
		return false;
	}

	void main() {
		bool bb[9];

		L: while(bb[2]) {
			break L;
		}

		ba = bb[i]; // type mismatch
		b = bb[c];
		b = bb[b]; // subscript error
		b = bb[ia]; // subscript error
		b = bb[ca]; // subscript error
		b = bb[ba]; // subscript error
		b = bb[f1]; // subscript error
		b = bb[f2]; // subscript error
		b = bb[f3]; // subscript error
		b = bb[main]; // subscript error
		b = bb[L]; // subscript error

		i = (int)c;
		c = (char)b;
		b = (bool)i;
		i = (int)ba; // can't cast array
		c = (char)ia; // can't cast array
		b = (bool)ca; // can't cast array
		i = (int)f2; // can't cast method
		c = (char)f3; // can't cast method
		b = (bool)f1; // can't cast method
		i = (int)main; // can't cast method
		c = (char)L; // can't cast label

		i = (c + b) - i; // only error for +
		i = c * (b / i); // only error for /
		b = (c && b) || b; // only error for &&
		b = b && (b || i); // only error for ||

		return;
	}
}
