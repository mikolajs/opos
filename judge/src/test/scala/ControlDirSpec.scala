import eu.brosbit.ControlDir
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.nio.file.Paths

object CDD:
  val dataFile:String = "4 5 78 23 55 66 75 23 567 123"
  val id = "1234554"
  val cpp = "cpp"
  val cppFile:String =
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
      |  int s = 0;
      |  for (int i = 0; i < 10; i++) {
      |    wyniki[i] = (dane[i]*3) % 1000;
      |    s += dane[i];
      |  }
      |  ofstream wyjscie("wynik.txt");
      |  for (int i = 0; i < 10; i++) {
      |    wyjscie << wyniki[i] << endl;
      |  }
      |  cout << "suma = " << s << endl;
      |  return 0;
      |}
      |""".stripMargin


class ControlDirSpec extends AnyFlatSpec with Matchers:

  val dirControler = ControlDir(CDD.id)
  if(dirControler.mkPathObject(dirControler.mkRootDir).toFile.exists()) dirControler.deleteDir

  behavior of "Directory controler"

  it should "create test dir" in {
    dirControler.createDir
    Thread.sleep(100)
    val p = Paths.get(dirControler.mkRootDir)
    p.toFile.exists() should be(true)
  }

  it should "create test.cpp" in {
    dirControler.createSourceFile(CDD.cppFile, "cpp")
    val p = Paths.get(dirControler.mkFilePath("cpp"))
    p.toFile.exists() should be(true)
  }

  it should "create data file dane.cpp" in {
    dirControler.createDataFile("dane", CDD.dataFile )
    val p = Paths.get(dirControler.mkFilePath("txt", "dane"))
    p.toFile.exists() should be(true)
  }

  it should "compile file" in {
    dirControler.compileCpp("cpp")
    val p = Paths.get(dirControler.mkFilePath(""))
    p.toFile.exists() should be(true)
  }

  it should "run compiled program" in {
    val output = dirControler.runCpp
    output.trim should be("suma = 1019")
  }

  it should "read file data" in {
    val dataContent = dirControler.readFile("wynik.txt").split('\n').map(_.trim).mkString
    dataContent.size should be(26)
  }

end ControlDirSpec