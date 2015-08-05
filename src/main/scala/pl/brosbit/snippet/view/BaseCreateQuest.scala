package pl.brosbit.snippet.view

import pl.brosbit.model.edu.QuizQuestion
import scala.xml.Unparsed

trait BaseCreateQuest {
  protected def createQuest(quest: QuizQuestion) = {

    if (quest.fake.length == 0) {
      if (quest.answers.length == 0) createPlainQuest(quest)
      else createInputQuest(quest)
    }
    else {
      if (quest.answers.length == 1) createSingleAnswerQuest(quest)
      else createMultiAnswerQuest(quest)
    }
  }

  protected def createPlainQuest(quest: QuizQuestion) = {
    mkQuestHTML('p', quest.question, "", scala.xml.NodeSeq.Empty)
  }

  protected def createSingleAnswerQuest(quest: QuizQuestion) = {
    val all = (quest.fake ++ quest.answers).sortWith(_ > _)
      .map(s => <li>
      <input type="radio" value={s} name={quest._id.toString}/> <label>
        {s}
      </label>
    </li>)
    val correctString = quest.answers.mkString(";;;")
    mkQuestHTML('s', quest.question, correctString, <ul>
      {all}
    </ul>)
  }

  protected def createInputQuest(quest: QuizQuestion) = {
    val all = <div>
      <label>Odpowiedź:</label> <input type="text" name={quest._id.toString}/>
    </div>
    val correctString = quest.answers.mkString(";;;")
    mkQuestHTML('i', quest.question, correctString, all)
  }

  protected def createMultiAnswerQuest(quest: QuizQuestion) = {
    val all = (quest.fake ++ quest.answers).sortWith(_ > _)
      .map(s => <li>
      <input type="checkbox" value={s} name={quest._id.toString}/> <label>
        {s}
      </label>
    </li>)
    val correctString = quest.answers.mkString(";;;")
    mkQuestHTML('m', quest.question, correctString, <ul>
      {all}
    </ul>)
  }

  protected def mkQuestHTML(questType: Char, question: String, correct: String, answers: scala.xml.NodeSeq) = {
    <section class="question">
      <div class="panel panel-info">
        <div class="panel-heading questionMark">
          <span class="glyphicon glyphicon-question-sign"></span>
          Zadanie</div>
        <div class="panel-body">
          <input type="hidden" class="questType" value={questType.toString}/>
          <input type="hidden" class="correct" value={correct}/>
          <div class="questionText">
            {Unparsed(question)}
          </div>{answers}{if (questType != 'p') <button onclick="showCourse.checkAnswer(this)">Sprawdź</button>
            else <span></span>}<p class="alertWell"></p>
        </div>
      </div>
    </section>
  }
}
