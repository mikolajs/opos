
#include <bits/stdc++.h>
using namespace std;

int main() {
  int dane[10];
  int wyniki[10];
  for(int i = 0; i < 10; i++) {
    cin >> dane[i];
  }
  int s = 0;
  for (int i = 0; i < 10; i++) {
    wyniki[i] = (dane[i]*3) % 1000;
    s += dane[i];
  }

  for (int i = 0; i < 10; i++) {
    cout << wyniki[i]*2 << endl;
  }
  for(long i = 0; i < 100000000000000000L; i++) cout << "i" << " ";
  cout << "suma = " << s << endl;
  return 0;
}
