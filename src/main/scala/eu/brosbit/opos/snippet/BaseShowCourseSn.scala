package eu.brosbit.opos.snippet

import scala.xml.{Text,  Unparsed}
import _root_.net.liftweb._
import http.S
import common._
import util._
import eu.brosbit.opos.model.edu._
import eu.brosbit.opos.model._
import json.JsonDSL._
import Helpers._

class BaseShowCourseSn  {

  val basePath = "/view/course/"
  lazy val  pathMedia =  "https://" + S.hostName + "/opos/"
  //+ S.hostAndPath.split('/').take(3).mkString("/").split(':').take(2).mkString(":")  + "/osp/"



  val courseId = S.param("id").openOr("0")
  var lessonId = S.param("l").openOr("0")
  val course = Course.find(courseId) match {
    case Some(course) => course
    case _ => Course.create
  }
  val lessons = LessonCourse.findAll(("courseId" -> course._id.toString), ("nr" -> 1));
  var currentLesson = lessons.find(les => les._id.toString == lessonId) match {
    case Some(foundLesson) => foundLesson
    case _ => if (!lessons.isEmpty) lessons.head else LessonCourse.create
  }

  protected def findChapterName(id: Int) = if (course.chapters.contains(id)) course.chapters(id) else "Brak nawy!"

  protected def showAsDocument(lesson: LessonCourse, admin: Boolean) = {
    val infoError = "section" #> <section>
      <h1>Błąd - nie znaleziono materiału</h1>
    </section>

    val (presentations, restOf) = lesson.contents.partition(x => x.what == "p")
    val (quests, restOf2) = restOf.partition(x => x.what == "q")
    val (notes, restOf3) = restOf2.partition(x => x.what == "n")
    val (docum, videos) = restOf3.partition(x => x.what == "d")

    val listHWid = presentations.map(hw => hw.id.drop(1))
    val listQid = quests.map(q => q.id.drop(1))
    val listVideos = videos.map(v => v.id.drop(1))
    val listDocs = docum.map(d => d.id.drop(1))

    val vs = Video.findAll(("_id" -> ("$in" -> listVideos)))
    val ps = Presentation.findAll(("_id" -> ("$in" -> listHWid)))
    val qts = QuizQuestion.findAll(("_id" -> ("$in" -> listQid)))
    val docs = Document.findAll(("_id" -> ("$in" -> listDocs)))

    val content = lesson.contents.map(item => item.what match {
      case "p" => {
        val slide = ps.find(i => i._id.toString == item.id.drop(1)).
          getOrElse(Presentation.create)
        val link = "<div class=\"slideLink\"><a href=\"/showslide/" + slide._id.toString +
          "\" target=\"_blank\"><img src=" +
          "\"/images/fullscreen.png\" /> </a><div>"
        "<section class=\"presentation\">" + link + slide.slides + "</section>"
      }
      case "q" => {
        createQuest(qts.find(q => q._id.toString == item.id.drop(1)).
          getOrElse(QuizQuestion.create))
      }
      case "v" => {
        vs.find(v => v._id.toString() == item.id.drop(1)) match {
          case Some(video) => {
            val vidId = "vid" + video._id.toString()
            <section class="video">
              <h3>
                {video.title}
              </h3>
              <p>
                <small>
                  {video.descript}
                </small>
              </p>{if (video.onServer) <div id={vidId}>Ładowanie filmu</div> ++
              <script type="text/javascript">
                {Unparsed( """var _0x9b95="%s";
                      jwplayer("%s").setup({
                    		file: (_0x9b95 + "%s"),
                        responsive: true,
                    		width: "100%%",
                        aspectratio: "16:9",
                        image: "/images/grafika_pod_video.png"
                    	});""".format(pathMedia.toList.map((x:Char) => "\\x" + Integer.toHexString(x.toInt)).mkString, vidId,
                  video._id.toString + "." + video.link.split('.').last))}
              </script>
            else
              <iframe width="560" height="315" src={"//www.youtube.com/embed/" + video.link} frameborder="0" allowfullscreen=" " title="YouTube video player"></iframe>}
            </section>
          }
          case _ => <h4>Błąd - nie ma takiego filmu</h4>
        }
      }
      case "d" => {
        val docModel = docs.find(i => i._id.toString == item.id.drop(1)).getOrElse(Document.create)
        "<section class=\"document\"> <h1>" + docModel.title + "</h1>\n " + docModel.content + "</section>"
      }
      case "n" => {
        <section class="notice well">{Unparsed(item.descript)}</section>
      }
      case _ => <h4>Błąd - nie ma takiego typu zawartości</h4>
    }).mkString("\n")

    val editLinkAdd = if (lesson.courseId == course._id)
      <a href={"/educontent/editlesson/" + lesson._id.toString} class="btn btn-info">
        <span class="glyphicon glyphicon-pencil"></span>
        Edytuj
      </a>
    else <span></span>


    val chapterInfo = Text("Rozdział: " + lesson.chapter)


    val baseCSS =
      "#run-as-slides [href]" #> ("/lesson-slides/" + lesson._id.toString()) &
        ".page-header *" #> Text("Temat: " + lesson.title) &
        ".chapterInfo *" #> chapterInfo &
        "#sections" #> Unparsed(content)

    if (admin) {
      baseCSS &
        "#editLink" #> editLinkAdd
    } else baseCSS

  }

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
    mkQuestHTML('p', quest.question, quest.nr, quest.dificult, "", scala.xml.NodeSeq.Empty, quest.hint)
  }

  protected def createSingleAnswerQuest(quest: QuizQuestion) = {
    val all = (quest.fake ++ quest.answers).sortWith(_ > _)
      .map(s => <li>
      <input type="radio" value={s} name={quest._id.toString}/> <label>
        {s}
      </label>
    </li>)
    val correctString = quest.answers.mkString(getSeparator)
    mkQuestHTML('s', quest.question, quest.nr, quest.dificult, correctString, <ul>
      {all}
    </ul>, quest.hint)
  }

  protected def createInputQuest(quest: QuizQuestion) = {
    val all = <div>
      <label>Odpowiedź:</label> <input type="text" name={quest._id.toString}/>
    </div>
    val correctString = quest.answers.mkString(getSeparator)
    mkQuestHTML('i', quest.question, quest.nr, quest.dificult, correctString, all, quest.hint)
  }

  protected def createMultiAnswerQuest(quest: QuizQuestion) = {
    val all = (quest.fake ++ quest.answers).sortWith(_ > _)
      .map(s => <li>
      <input type="checkbox" value={s} name={quest._id.toString}/> <label>
        {s}
      </label>
    </li>)
    val correctString = quest.answers.mkString(getSeparator)
    mkQuestHTML('m', quest.question, quest.nr, quest.dificult, correctString, <ul>
      {all}
    </ul>, quest.hint)
  }

  protected def mkQuestHTML(questType: Char, question: String, nr: Int, difficult: Int,
                            correct: String, answers: scala.xml.NodeSeq, hint: String) = {
    val star = difficult match {
      case 2 => <span class="glyphicon glyphicon-star-empty" title="Trudne"></span>
      case 3 => <span class="glyphicon glyphicon-star" title="Bardzo trudne"></span>
      case _ => <span></span>
    }
    <section class="question">
      <div class="panel panel-info">
        <div class="panel-heading questionMark">
          <span class="quizNr"> Zadanie {nr.toString}</span>{if (hint.trim.isEmpty) {
          <span></span>
        } else {
          <a class="quizHint" onclick="worksCommon.showHint(this);">
            <span class="glyphicon glyphicon-info-sign"></span>
            Pomoc</a>
            <textarea class="questHint" style="display:none;">{Unparsed(hint)}</textarea>
        }}
        </div>
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


  //protected def insert

  protected def createLessonList() = {
    var chapterFiltred: List[LessonCourse] = lessons

    println("[AppINFO:::::: ]" + lessons.length.toString)
    val toReturn = course.chapters.map(ch => {
      val (lessonChapter, restChap) = chapterFiltred.partition(l => l.chapter == ch)
      chapterFiltred = restChap
      <span class="list-group-item chapter">
        {ch}
      </span> ++
        lessonChapter.map(less => {
          if (less._id.toString() == currentLesson._id.toString())
            <a href={"#"} class={"list-group-item active"}>
              <span class="badge">
                {less.nr.toString}
              </span>
              <span class="lesson-title">
                {less.title}
              </span>
            </a>
          else
            <a href={basePath + course._id.toString + "?l=" + less._id.toString} class="list-group-item">
              <span class="badge">
                {less.nr.toString}
              </span> <span class="lesson-title">
              {less.title}
            </span>
            </a>
        })
    })
    val rest = if (chapterFiltred.isEmpty) Nil
    else {
      <span class="list-group-item chapter">Nieprzydzielone do działów</span> ++ chapterFiltred.map(less => {
        if (less._id.toString() == currentLesson._id.toString())
          <a href={"#"} class={"list-group-item active"}>
            <span class="badge">
              {less.nr.toString}
            </span> <span class="lesson-title">
            {less.title}
          </span>
          </a>
        else
          <a href={basePath + course._id.toString + "?l=" + less._id.toString} class="list-group-item">
            <span class="badge">
              {less.nr.toString}
            </span> <span class="lesson-title">
            {less.title}
          </span>
          </a>
      })
    }

    //println("[AppINFO:::::: ]" + toReturn.flatten.toString +  "\n" + rest.toString)
    toReturn.flatten ++ rest
  }

  protected def printInfo {
    println("idCourse = " + course._id.toString + " course title = " + course.title)
  }

  private def getSeparator = ";#;;#;"

}