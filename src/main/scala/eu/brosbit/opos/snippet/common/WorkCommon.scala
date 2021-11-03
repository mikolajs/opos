package eu.brosbit.opos.snippet.common

import eu.brosbit.opos.model.edu.{AnswerWorkItem, LessonContent, MessageItem, QuizQuestion, WorkAnswer}

import scala.xml.{Elem, Unparsed}
import net.liftweb.json.JsonDSL._

trait WorkCommon {
  protected def createMessages(items: List[AnswerWorkItem]):List[Elem] = {
    items.map(mkMessage)
  }

  protected def createQuestions(items: List[LessonContent], answer: WorkAnswer):List[Elem] = {
    val questionsInLesson = items.filter(_.what == "q").map(_.id.substring(1))
    val questions = QuizQuestion.findAll( "_id" -> ("$in" -> questionsInLesson))
    questionsInLesson.map(qinl => questions.find(_._id.toString == qinl)).filter(_.isDefined)
      .map(_.get).map(q => mkQuestion(q, answer))
    //questions.map(q => mkQuestion(q, answer))
  }

  protected def mkQuestion(question:QuizQuestion, answer:WorkAnswer): Elem = {
    <section class="question" id={"_" + question._id.toString } >
      <div class="panel panel-info">
        <div class="panel-heading">
          <span class="quizNr">Zadanie {question.nr.toString}</span>
          <a class="quizHint" onclick="workCommon.showHint(this);">
            <span class="glyphicon glyphicon-info-sign"></span> Pomoc</a>
          <textarea id="questHint" style="display:none;">{Unparsed(question.hint)}</textarea>
        </div>
        <div class="panel-body">
          <div class="questionText">
            {Unparsed(question.question)}
          </div>
          <div class="form-group">{createAnswers(question.answers, question.fake)}</div>
        </div>
        <div class="questMessages">{answer.answers.filter(_.qId.substring(1) == question._id.toString)
          .map(mkMessage)}</div>
        <div class="questMessagesForm">
          <textarea name="text"  style="overflow:hidden;" placeholder="Napisz odpowiedź lub wczytaj plik..."
                    class="writeMessage"
                    oninput='this.style.height = "";this.style.height = this.scrollHeight + "px";'></textarea>
          <div class="questMessagesActionsBox">
            <button class="btn btn-primary mboxl"> <span class="glyphicon glyphicon-paperclip"></span></button>
            <button class="btn btn-success mboxr" > <span class="glyphicon glyphicon-send"></span></button>
          </div>
        </div>
      </div>
    </section>
  }

  def createAllMessages(items: List[MessageItem]):Elem = {
    <section class="messages" >
        <div class="questMessages">{items.map(mkMessage)}</div>
        <div class="questMessagesForm">
          <textarea name="text"  style="overflow:hidden;" placeholder="Napisz odpowiedź lub wczytaj plik..."
                    class="writeMessage"
                    oninput='this.style.height = "";this.style.height = this.scrollHeight + "px";'></textarea>
          <div class="questMessagesActionsBox">
            <button class="btn btn-primary mboxl"> <span class="glyphicon glyphicon-paperclip"></span></button>
            <button class="btn btn-success mboxr" > <span class="glyphicon glyphicon-send"></span></button>
          </div>
       </div>
    </section>
  }
  protected def mkDescription(description:String) = {
    description.replace('\r', ' ').replace("\n", "<br/>")
  }

  private def createAnswers(good:List[String], fake:List[String]) = {
    val answers:List[Elem] = if(fake.nonEmpty) {
      (fake ++ good).sortWith(_ < _) .map(s => <ul class="answersList"><li>{Unparsed(s)} </li> </ul>)
    } else Nil
    <div class="answerBox">{answers}</div>
  }
  private def mkMessage(item: AnswerWorkItem) = {
    <div class={if(item.t) "bg bg-blue" else "bg bg-green" } name={"qr" + item.qId}>
      <pre>{if(item.l) <a href={Unparsed(item.m)} target="_blank">PLIK</a> else Unparsed(item.m)}</pre>
      <div class="messegeSign">
        <span class="glyphicon glyphicon-user"></span>
        <span class="msg-name">{item.a}</span>
        <span class="glyphicon glyphicon-calendar"></span>
        <span class="msg-date">{item.date}</span>
      </div>
    </div>
  }

  private def mkMessage(item: MessageItem) = {
    <div class={if(item.t) "bg bg-blue" else "bg bg-green" } >
      <pre>{if(item.l) <a href={Unparsed(item.m)} target="_blank">PLIK</a> else Unparsed(item.m)}</pre>
      <div class="messegeSign">
        <span class="glyphicon glyphicon-user"></span>
        <span class="msg-name">{item.a}</span>
        <span class="glyphicon glyphicon-calendar"></span>
        <span class="msg-date">{item.date}</span>
      </div>
    </div>
  }

}
