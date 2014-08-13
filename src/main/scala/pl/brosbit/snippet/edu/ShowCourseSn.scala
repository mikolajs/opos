package pl.brosbit.snippet.edu

import java.util.Date
import java.util.Random
import scala.xml.{ Text, XML, Unparsed }
import _root_.net.liftweb._
import http.{ S, SHtml }
import common._
import util._
import mapper.{ OrderBy, Descending }
import pl.brosbit.model._
import edu._
import mapper.By
import json.JsonDSL._
import json.JsonAST.JObject
import json.JsonParser
import org.bson.types.ObjectId
import Helpers._

class ShowCourseSn {

    val courseId = S.param("id").openOr("0")
    val course = Course.find(courseId) match {
        case Some(course) => course
        case _ => Course.create
    }
    val lessons = LessonCourse.findAll(("courseId" -> course._id.toString), ("nr" -> 1));
    var firstLesson = if (!lessons.isEmpty) lessons.head else LessonCourse.create

    def show() = {
        if (course.title != "" || firstLesson.title == "") {
            "#subjectListLinks *" #> createLessonList &
                ".content *" #> this.showAsDocument(firstLesson)
        } else ".main *" #> <h1>Nie ma takiego kursu lub brak lekcji</h1>
    }

    private def showAsDocument(lesson: LessonCourse) = {
        val infoError = "section" #> <section><h1>Błąd - nie znaleziono materiału</h1></section>

        val (headWords, restOf) = lesson.contents.partition(x => x.what == "word")
        val (quests, restOf2) = restOf.partition(x => x.what == "quest")
        val (docum, videos) = restOf2.partition(x => x.what == "doc")
        val listHWid = headWords.map(hw => hw.link.drop(3))
        val listQid = quests.map(q => q.link.drop(3))
        val listVideos = videos.map(v => v.link.drop(3))
        val listDocs = docum.map(d => d.link.drop(3))
        val vs = Video.findAll(("_id" -> ("$in" -> listVideos)))
        val hws = HeadWord.findAll(("_id" -> ("$in" -> listHWid)))
        val qts = QuizQuestion.findAll(("_id" -> ("$in" -> listQid)))
        val docs = Document.findAll(("_id" -> ("$in" -> listDocs)))

        val content = lesson.contents.map(item => item.what match {
            case "word" => {
                val headW = hws.find(i => i._id.toString == item.link.drop(3)).
                    getOrElse(HeadWord.create)
                "<section class=\" headword\"/><h2>" + headW.title + "</h2>" + headW.content + "</section>"
            }
            case "quest" => {
                createQuest(qts.find(q => q._id.toString == item.link.drop(3)).
                    getOrElse(QuizQuestion.create))
            }
            case "video" => {
                vs.find(v => v._id.toString() == item.link.drop(3)) match {
                    case Some(video) => {
                        <section class="video">
                        <iframe width="853" height="480" src={ "//www.youtube.com/embed/" + video.link } frameborder="0" allowfullscreen=""></iframe>
                        </section>
                    }
                    case _ => <h4>Błąd - nie ma takiego filmu</h4>
                }
            }
            case "doc" => {
                val docModel = docs.find(i => i._id.toString == item.link.drop(3)).getOrElse(Document.create)
                "<section class=\"document\"> <h3>" + docModel.title + "</h3>\n " + docModel.content + "</section>"
            }
            case _ => <h4>Błąd - nie ma takiego typu zawartości</h4>
        }).mkString("\n")
        
        "#run-as-slides [href]" #> ("/lesson-slides/" + lesson._id.toString()) &
           ".page-header *" #> lesson.title &
            "#sections" #> Unparsed(content) &
            "#extra-info *" #> Unparsed(lesson.extraText)
    }

    private def createQuest(quest: QuizQuestion) = {
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

    private def createLessonList() = {
        var nr = 0
       lessons.map(les => {
           nr += 1
           if(nr == 1)
                <a href={ "/course/#" } 
           		class={ "list-group-item active" }>{ les.nr.toString + ". " + les.title }</a>	
           	else 
           	    <a href={ "/course/" + course._id.toString + "?l=" + les._id.toString } 
           		class={ "list-group-item" }>{ les.nr.toString + ". " + les.title }</a>	
            })
            
    }
        
     private def printInfo { println("idCourse = " + course._id.toString + " course title = "  + course.title )}

    def slideData = {

    }
    

}