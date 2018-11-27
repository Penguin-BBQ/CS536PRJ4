## test2.multiple-decl ##
## multiple decls of field, param, param & local, local, method ##

class Test{
    int field1;
    int field1;	## field/field ##

    void g(int p1, int p2, int p1, int p2) { ## param/param ##
        char Testing;
	int Testing;	## local/local ##
	p1: while(true) ## param/label ##
		print(field1);
	return;
    }

    void f(char S[]) {
        char S[3];	## param/local ##
	int K;
	K: while(true) { ## local/label ##
		int K;
		print(1);
	}
	return;
    }

    void Tests() {
	L: while(true) {
		break L;
	  }

	L: while (false) { ## label/label ##
		continue L; ## invalid continue ##
	   }
		return;
    }

    void Tests() {  ## illegal overloading ##
		return;
    }

	void field1() { ## field/method ##
		int a;
		bool a; ## local/local even in bad method ##
		return;
	}
	void main() { return; }
}
