package eu.brosbit
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class JudgerRunnerSpec extends AnyFlatSpec with Matchers {
  println("Judge Spec Start")
  val f = false
  "Judger" should "start " in {
    println("Not created specs")
    f should be (true)
  }
}
