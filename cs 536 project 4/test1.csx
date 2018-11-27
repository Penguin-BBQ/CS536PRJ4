## test1.undeclared ##
## use of undeclared name as field/local, methods ## 

class Test{
    int zzz;

	int more() {
		int yyy;
		return 0;
	}

    void Testing(int temp) {
	foo();                          ## method name ## 
	K: while (zzz != 0) {
		if (zzz > 0)
			break r;	## use in break ##
		else {
			zzz = zzz + 1;
			continue r;     ## use in continue ##
		}
	}
	if (true != test) {             ## use in condition ## 
          int xxx;
	  return;
	} 
	else {
	  xxx = xxx;			## declared in a previous scope ## 
  	  if (undecl == 0) {            ## used in a condition ## 
	     yyy = 1;			## declared in a previous function ## 
	     while (a+b*c/d-y < (~1)*x) {   ## uses in a complex expr ## 
	        Testing(g);       ## actuals ## 
	     }
	  }
	}
	read(b); 	## use in read ##

	## uses in all kinds of expressions ## 
	a = !a;
	a = a && a;
	a = a || a;
	a = a == a;
	a = a != a;
	a = a < a;
	a = a <= a;
	a = a > a;
	a = a >= a;

	print(a);	## use in a print stmt ## 
	a();			## use as method name ## 

    }

	void main() {
		int Test;
		print(Test*5+zzz);
	}
}
