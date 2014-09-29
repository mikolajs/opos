package pl.brosbit.snippet

import java.util.Date
import java.util.Random
import scala.xml.{ Text, XML, Unparsed }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model.edu._
import pl.brosbit.model._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

class BaseShowCourseSn {

  val user = User.currentUser match {
    case Full(user) => user
    case _ => S.redirectTo("/login")
  }

  val courseId = S.param("id").openOr("0")
  var lessonId = S.param("l").openOr("0")
  val course = Course.find(courseId) match {
    case Some(course) => course
    case _ => Course.create
  }
  val lessons = LessonCourse.findAll(("courseId" -> course._id.toString), ("chapterId" -> 1) ~ ("nr" -> 1));
  var currentLesson = lessons.find(les => les._id.toString == lessonId) match {
    case Some(foundLesson) => foundLesson
    case _ => if (!lessons.isEmpty) lessons.head else LessonCourse.create
  }
  
  protected def findChapterName(id:Int) = if(course.chapters.contains(id)) course.chapters(id) else "Brak nawy!"

  protected def showAsDocument(lesson: LessonCourse, admin: Boolean) = {
    val infoError = "section" #> <section><h1>Błąd - nie znaleziono materiału</h1></section>

    val (headWords, restOf) = lesson.contents.partition(x => x.what == "w")
    val (quests, restOf2) = restOf.partition(x => x.what == "q")
    val (files, restOf3) = restOf2.partition(x => x.what == "f")
    val (docum, videos) = restOf3.partition(x => x.what == "d")

    val listHWid = headWords.map(hw => hw.id.drop(1))
    val listQid = quests.map(q => q.id.drop(1))
    val listVideos = videos.map(v => v.id.drop(1))
    val listDocs = docum.map(d => d.id.drop(1))
    val listFiles = docum.map(f => f.id.drop(1))
    val vs = Video.findAll(("_id" -> ("$in" -> listVideos)))
    val hws = HeadWord.findAll(("_id" -> ("$in" -> listHWid)))
    val qts = QuizQuestion.findAll(("_id" -> ("$in" -> listQid)))
    val docs = Document.findAll(("_id" -> ("$in" -> listDocs)))
    val fs = FileResource.findAll("_id" -> ("$in" -> listFiles))

    val content = lesson.contents.map(item => item.what match {
      case "w" => {
        val headW = hws.find(i => i._id.toString == item.id.drop(1)).
          getOrElse(HeadWord.create)
        "<section class=\" headword\"/><h2>" + headW.title + "</h2>" + headW.content + "</section>"
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
              <h3>{ video.title }</h3>
              <p><small>{ video.descript }</small></p>
              {
                if (video.onServer) <div id={ vidId }>Ładowanie filmu</div> ++
                  <script type="text/javascript">
                    {
                      Unparsed("""jwplayer("%s").setup({
                    		file: "%s",
                    		width: 853,
                    		height:  480
                    	});""".format(vidId,
                        "http://video.epodrecznik.edu.pl/" + video._id.toString + "." + video.link.split('.').last))
                    }
                  </script>
                else
                  <iframe width="853" height="480" src={ "//www.youtube.com/embed/" + video.link } frameborder="0" allowfullscreen=""></iframe>
              }
            </section>
          }
          case _ => <h4>Błąd - nie ma takiego filmu</h4>
        }
      }
      case "d" => {
        val docModel = docs.find(i => i._id.toString == item.id.drop(1)).getOrElse(Document.create)
        "<section class=\"document\"> <h3>" + docModel.title + "</h3>\n " + docModel.content + "</section>"
      }
      case "f" => {
        val fileMod = fs.find(f => f._id.toString == item.id.drop(1)).getOrElse(FileResource.create(ObjectId.get))
        "<section class=\"file\"> <h3>" + fileMod.title + "</h3>\n <a href=\"/file/" + fileMod.fileId.toString() +
          "\">" + fileMod.descript + "</a></section>"
      }
      case _ => <h4>Błąd - nie ma takiego typu zawartości</h4>
    }).mkString("\n")

    val editLinkAdd = if (lesson.courseId == course._id)
      <a href={ "/educontent/editlesson/" + lesson._id.toString } class="btn btn-info">
        <span class="glyphicon glyphicon-pencil"></span>
        Edytuj
      </a>
    else <span></span>
      
  
    val chapterInfo =  Text( "Rozdział: "  + lesson.chapter)
    	  

    val baseCSS = 
       "#run-as-slides [href]" #> ("/lesson-slides/" + lesson._id.toString()) &
        ".page-header *" #> Text("Temat: " +  lesson.title) &
        ".chapterInfo *" #> chapterInfo &
        "#sections" #> Unparsed(content) &
        "#extra-info *" #> Unparsed(lesson.extraText) 
        
    if (admin) {
       baseCSS &
        "#editLink" #> editLinkAdd
    } else baseCSS
   
  }

  protected def createQuest(quest: QuizQuestion) = {
    val rand = new Random
    val witch = rand.nextInt(quest.fake.length + 1)
    val all = quest.fake.take(witch) ++ (quest.answer :: quest.fake.drop(witch))
    var n = -1
    var qId = quest._id.toString
    <section class="question">
      <h4>Zadanie</h4>
      <input type="hidden" class="correct" value={ witch.toString }/>
      <div class="questionText">{ Unparsed(quest.question) } </div>
      <ul>	{
        all.map(a => {
          n += 1
          <li><input type="radio" name={ quest._id.toString } class={ "answer" + n }/> <label>{ a }</label></li>
        })
      } </ul>
      <button onclick="checkAnswer(this)">Sprawdź</button>
      <p class="alertWell"></p>
    </section>
  }

  protected def createLessonList() = {
    var chapterFiltred:List[LessonCourse] = lessons
    course.chapters.map(ch => {
      val (lessonChapter, restChap) = chapterFiltred.partition(l => l.chapter == ch)
       chapterFiltred = restChap
       <span class="list-group-item chapter">{ ch }</span> ++
      chapterFiltred.map(less => {
        if (less._id.toString() == currentLesson._id.toString())
        <a href={ "#" } class={ "list-group-item active" }>
                        { less.title }
                        <span class="badge">{ less.nr.toString }</span>
             </a>
      else
        <a href={ "/view/course/" + course._id.toString + "?l=" + less._id.toString } class="list-group-item" >
                        { less.title }
                        <span class="badge">{ less.nr.toString }</span>
                      </a>
      })
    })
  }

  protected def printInfo { println("idCourse = " + course._id.toString + " course title = " + course.title) }

}