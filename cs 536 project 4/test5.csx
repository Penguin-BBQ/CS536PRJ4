## test5.operation ##
class test {
	int i;
	char c;
	bool b;
	int ia[3];
	char ca[4];
	bool ba[4];

	int f1(int p1, char p2, bool p3) {
		i = p1 + p2;
		i = p1 - p2;
		i = p1 * p2;
		i = p1 / p2;

		b = p3 && p3;
		b = p3 || p3;

		b = p1 == p2;
		b = p3 == p3;
		b = p1 != p2;
		b = p3 != p3;
		b = p1 < p2;
		b = p3 > p3;
		b = p1 <= p2;
		b = p3 <= p3;
		b = p1 >= p2;
		b = p3 >= p3;
		return 2;
	}

	char f2(int p1[], char p2[], bool p3[]) {
		i = p1 + p2; // both
		i = p1 - p2; // both
		i = p1 * p2; // both
		i = p1 / p2; // both

		b = p3 && p3; // both
		b = p3 || p3; // both

		b = p1 == p2; // wrong
		b = p3 == p3; // wrong
		b = p1 != p2; // wrong
		b = p3 != p3; // wrong
		b = p1 < p2; // wrong
		b = p3 > p3; // wrong
		b = p1 <= p2; // wrong
		b = p3 <= p3; // wrong
		b = p1 >= p2; // wrong
		b = p3 >= p3; // wrong
		return 'd';
	}

	bool f3() {
		return false;
	}

	void main() {
		L: while (b) {
			break L;
		}

		i = i + 1;
		i = 1 + i;
		i = i + 's';
		i = 's' + i;
		i = i + true; // right
		i = true + i; // left
		i = i + "s"; // right
		i = "s" + i; // left
		i = i + i;
		i = i + b; // right
		i = b + i; // left
		i = b + b; // both
		i = b + c; // left
		i = c + b; // right
		i = i + c;
		i = c + i;
		i = c + c;
		i = f1 + f2; // both
		i = ca + ia; // both
		i = main + L; // both

		i = i - 1;
		i = 1 - i;
		i = i - 's';
		i = 's' - i;
		i = i - true; // right
		i = true - i; // left
		i = i - "s"; // right
		i = "s" - i; // left
		i = i - i;
		i = i - b; // right
		i = b - i; // left
		i = b - b; // both
		i = b - c; // left
		i = c - b; // right
		i = i - c;
		i = c - i;
		i = c - c;
		i = f1 - f2; // both
		i = ca - ia; // both
		i = main - L; // both

		i = i * 1;
		i = 1 * i;
		i = i * 's';
		i = 's' * i;
		i = i * true; // right
		i = true * i; // left
		i = i * "s"; // right
		i = "s" * i; // left
		i = i * i;
		i = i * b; // right
		i = b * i; // left
		i = b * b; // both
		i = b * c; // left
		i = c * b; // right
		i = i * c;
		i = c * i;
		i = c * c;
		i = f1 * f2; // both
		i = ca * ia; // both
		i = L * main; // both

		i = i / 1;
		i = 1 / i;
		i = i / 's';
		i = 's' / i;
		i = i / true; // right
		i = true / i; // left
		i = i / "s"; // right
		i = "s" / i; // left
		i = i / i;
		i = i / b; // right
		i = b / i; // left
		i = b / b; // both
		i = b / c; // left
		i = c / b; // right
		i = i / c;
		i = c / i;
		i = c / c;
		i = f1 / f2; // both
		i = ca / ia; // both
		i = L / main; // both

		b = b && 1; // right
		b = 1 && b; // left
		b = b && 's'; // right
		b = 's' && b; // left
		b = b && true;
		b = true && b;
		b = b && "s"; // right
		b = "s" && b; // left
		b = i && i; // both
		b = i && b; // left
		b = b && i; // right
		b = b && b;
		b = b && c; // right
		b = c && b; // left
		b = i && c; // both
		b = c && i; // both
		b = c && c; // both
		b = f3 && f3; // both
		b = ba && ba; // both
		b = main && L; // both

		b = b || 1; // right
		b = 1 || b; // left
		b = b || 's'; // right
		b = 's' || b; // left
		b = b || true;
		b = true || b;
		b = b || "s"; // right
		b = "s" || b; // left
		b = i || i; // both
		b = i || b; // left
		b = b || i; // right
		b = b || b;
		b = b || c; // right
		b = c || b; // left
		b = i || c; // both
		b = c || i; // both
		b = c || c; // both
		b = f3 || f3; // both
		b = ba || ba; // both
		b = L || main; // both

		b = i == i;
		b = i == b; // wrong
		b = b == i; // wrong
		b = b == b;
		b = b == c; // wrong
		b = c == b; // wrong
		b = i == c;
		b = c == i;
		b = c == c;
		b = f1 == f2; // wrong
		b = ba == ba; // wrong
		b = L == main; // wrong

		b = i != i;
		b = i != b; // wrong
		b = b != i; // wrong
		b = b != b;
		b = b != c; // wrong
		b = c != b; // wrong
		b = i != c;
		b = c != i;
		b = c != c;
		b = f1 != f2; // wrong
		b = ba != ba; // wrong
		b = main != L; // wrong

		b = i < i;
		b = i < b; // wrong
		b = b < i; // wrong
		b = b < b;
		b = b < c; // wrong
		b = c < b; // wrong
		b = i < c;
		b = c < i;
		b = c < c;
		b = f1 < f2; // wrong
		b = ba < ba; // wrong
		b = main < L; // wrong

		b = i > i;
		b = i > b; // wrong
		b = b > i; // wrong
		b = b > b;
		b = b > c; // wrong
		b = c > b; // wrong
		b = i > c;
		b = c > i;
		b = c > c;
		b = f1 > f2; // wrong
		b = ba > ba; // wrong
		b = main > L; // wrong

		b = i <= i;
		b = i <= b; // wrong
		b = b <= i; // wrong
		b = b <= b;
		b = b <= c; // wrong
		b = c <= b; // wrong
		b = i <= c;
		b = c <= i;
		b = c <= c;
		b = f1 <= f2; // wrong
		b = ba <= ba; // wrong
		b = L <= main; // wrong

		b = i >= i;
		b = i >= b; // wrong
		b = b >= i; // wrong
		b = b >= b;
		b = b >= c; // wrong
		b = c >= b; // wrong
		b = i >= c;
		b = c >= i;
		b = c >= c;
		b = f1 >= f2; // wrong
		b = ba >= ba; // wrong
		b = L >= main; // wrong

		b = !i; // wrong
		b = !c; // wrong
		b = !b;
		b = !f3; // wrong
		b = !ba; // wrong
		b = !L; // wrong
		b = !main; // wrong
	}
}
