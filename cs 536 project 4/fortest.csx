## main has argument ##
class test {
	bool b;
	int ar[100];
	int fun = 10;
	int f(int i[]) { // valid for loop
		for (int j = 0; j < 10; j++){
			int q;
			q = 10;
			q = j;
		}
		label: for(int i = 2; i < 20; i--){ // valid for loop (i is in the loop scope)
			int j;
			break label;
			continue label;
		}
		for (;;){ // valid for loop
			int i = 5;
			i++;
		}
		for (int q[100]; f < 100; f++){//invalid for loop (non-scalar init parm)
			int i = 5;
		}
		for (char c = 'c'; c < 20; c++){//invalid for loop (non-int init parm)
			int i = 5;
		}
		for (int c; c < 20; c++){//invalid for loop (init parm not assigned)
			int i = 5;
		}
		for (int i = 10; i +7; i++){//invalid for loop (condition is not a bool)
			int j = 10;
			break label;
			continue ccc;
		}
	}
	void main(char c, int x) {
		return;
	}
}
