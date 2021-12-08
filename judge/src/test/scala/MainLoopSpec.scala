import eu.brosbit.MainLoop
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.io.File


class MainLoopSpec extends AnyFlatSpec with Matchers:
  val mainLoop = MainLoop()

  behavior of "main loop"

  it should "have id_234534edcab34bsdc32994 content" in {
    val i = "id_234534edcab34bsdc32994"
    val c = mainLoop.mkTestFromDir(File(mainLoop.mainPath+i))
    c.lang should be("cpp")
    c.code.size should be > 10
    c.id should be(i)
    c.testDataInput.size should be > 0
  }

  it should "have id_134534edaab34bsdc32457 content" in {
    val i = "id_134534edaab34bsdc32457"
    val c = mainLoop.mkTestFromDir(File(mainLoop.mainPath+i))
    c.lang should be("cpp")
    c.code.size should be > 10
    c.id should be(i)
    c.testDataInput.size should be > 0
  }

  it should "have id_a3d534edc4b36bsdc32111 content" in {
    val i = "id_a3d534edc4b36bsdc32111"
    val c = mainLoop.mkTestFromDir(File(mainLoop.mainPath+i))
    c.lang should be("py")
    c.code.size should be > 10
    c.id should be(i)
    c.testDataInput.size should be > 0
  }

  it should "create all found tests" in {
    mainLoop.getTestFromDir()
    mainLoop.toTestQueue.size should be(3)
  }


