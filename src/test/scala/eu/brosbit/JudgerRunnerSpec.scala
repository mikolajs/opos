package eu.brosbit
import eu.brosbit.opos.judge.ControlDir
import eu.brosbit.opos.lib.ConfigLoader
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

object CDD {
  val dataFile: String = "2 5 8 9 12 14 15 26 28 35"
  val id = "1234554"
  val cpp = "cpp"
  val cppFile: String =
    """
      |#include <bits/stdc++.h>
      |using namespace std;
      |
      |int main() {
      |  int dane[10];
      |  int wyniki[10];
      |  ifstream plik("dane.txt");
      |  for(int i = 0; i < 10; i++) {
      |    plik >> dane[i];
      |  }
      |  for (int i = 0; i < 10; i++) {
      |    wyniki[i] = dane[i]*2;
      |  }
      |  for (int i = 0; i < 10; i++) {
      |    cout << wyniki[i] << endl;
      |  }
      |  return 0;
      |}
      |""".stripMargin
  val pyFile =
    """
      |f = open("dane.txt", "r")
      |dane = f.read().split(" ")
      |for i in dane:
      |  print(int(i)*2, "\n")
      |f.close()
      |""".stripMargin
}

class JudgerRunnerSpec extends AnyFlatSpec with Matchers {
  println("Judge Spec Start")
  ConfigLoader.init
  val cdir = new ControlDir(CDD.id, CDD.cppFile, CDD.cpp, List(CDD.dataFile), List("4 10 16 18 24 28 30 52 56 70"))
  val cdirPython = new ControlDir("34567898", CDD.pyFile, "py", List(CDD.dataFile), List("4 10 16 18 24 28 30 52 56 70"))
  "Judger" should "have path to directory " in {
    cdir.mainDir should be("/home/ms/Dokumenty/judgeDir/")
  }

  it should "good full path " in {
    cdir.fullPath should be("/home/ms/Dokumenty/judgeDir/test_"+CDD.id)
  }

  it should "finish test cpp program positive" in {
     cdir.run.trim.split("\n").mkString(" ") should be("4 10 16 18 24 28 30 52 56 70")
  }

  it should "finish test python program positive" in {
    cdirPython.run.trim.split("\n").mkString("") should be("4 10 16 18 24 28 30 52 56 70")
  }

}
