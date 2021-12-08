import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import java.io.File
import eu.brosbit.CommandExecutorHelper

class CommandExecHelperSpec extends AnyFlatSpec with Matchers:
  val commandExec1 = CommandExecutorHelper(1, "id_234534edcab34bsdc32994")
  val commandExec2 = CommandExecutorHelper(2, "id_134534edaab34bsdc32457")
  val commandExec3 = CommandExecutorHelper(3, "id_a3d534edc4b36bsdc32111")

  behavior of "Commad Executor helper"

  it should "have id_234534edcab34bsdc32994 content" in {
    commandExec1.mainPath should be ("/home/ms/Dokumenty/forJudgeDir/id_234534edcab34bsdc32994/")
  }

  it should "have max listo of tests found" in {
    commandExec1.getDataFilesNumbs should be (2)
  }

  it should "create docker name" in {
    commandExec1.createDockerImageName should be("opos-image-id1")
  }

  it should "make test creating all dalt in id worker 1" in {
    //commandExec1.make
  }

  it should "make test create data with PY file in" in {
    commandExec3.make
  }

