## test3.field-decl ##

class test {
	int ia[3];
	char ca[8];
	bool ba[288];

	int bad1[0]; ## array size must be positive ##
	char bad2[~4]; ## array size must be positive ##

	int i;
	char c;
	bool b;

	int i1 = 8;
	int i2 = 'c';	## initializer type error ##
	int i3 = true;	## initializer type error ##
	int i4 = i;
	int i5 = ia;	## initializer type error ##

	char c1 = 5;	## initializer type error ##
	char c2 = 's';
	char c3 = false;	## initializer type error ##
	char c4 = c;
	char c5 = ca;	## initializer type error ##
	char c6 = "leis";	## initializer type error ##

	bool b1 = 4;	## initializer type error ##
	bool b2 = 'k';	## initializer type error ##
	bool b3 = true;
	bool b4 = b;
	bool b5 = ba;	## initializer type error ##

	const coni = 3;
	const conc = 'i';
	const conb = false;
	const conv = b;
 	const con1 = ia;	## can't initialize const var with non-scalar ##
	const con2 = "flsk";	## can't initialize const var with non-scalar ##

	void f1(int p1, char p2, bool p3) {
		int ii = p1;
		char cc = p2;
		bool bb = p3;
		const vv = p3;

  		ii = 2;
		cc = 'd';
		bb = true;

		i = 3;
		c = 'e';
		b = false;

		i = coni;
		b = conb;
		c = conc;
		b = vv;

		i2 = 4;
		c6 = 'c';
		b5 = true;

		L: while (conb) {
			int L1 = L;	## initializer type error ##
			char L2 = L;	## initializer type error ##
			bool L3 = L;	## initializer type error ##
			const L4 = L;	## can't initialize const var with non-scalar ##
			break L;
		}

		return;
	}

	int f2() {
		return 8;
	}

	char f3() {
		return 'c';
	}

	bool f4() {
		return false;
	}

	void main() {
		int ii1 = f1;	## initializer type error ##
		char cc1 = f1;	## initializer type error ##
		bool bb1 = f1;	## initializer type error ##

		int ii2 = f2;	## initializer type error ##
		char cc2 = f3;	## initializer type error ##
		bool bb2 = f4;	## initializer type error ##

		return;
	}
}
