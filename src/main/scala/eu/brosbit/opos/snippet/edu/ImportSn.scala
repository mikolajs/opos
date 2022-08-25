package eu.brosbit.opos.snippet.edu

import com.mongodb.gridfs.GridFS
import eu.brosbit.opos.api.{Exports, ImportJsonList}
import eu.brosbit.opos.lib.Zipper
import eu.brosbit.opos.model.{Lesson, SubjectName, TestProblem, User}
import eu.brosbit.opos.model.edu.{Course, Document, LessonCourse, Presentation, QuestIndex, QuizQuestion, SubjectTeach, Video}
import net.liftweb.common.Box.box2Iterable
import net.liftweb.http.{FileParamHolder, S, SHtml}
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import org.bson.types.ObjectId
import net.liftweb.json.JsonDSL._

import java.nio.file.{Files, Paths}
import net.liftweb.mapper._

case class ElementsInZip(var docs:Int = 0, var presentations: Int = 0, var videos: Int = 0, var questions: Int = 0, var lessons: Int = 0,
                         var files: Int = 0, var problems: Int = 0, var courses: Int = 0)
case class ElementsJson(var docs:String = "", var presentations: String = "", var videos: String = "", var questions: String = "",
                        var lessons: String = "", var files: String = "", var problems: String = "")



class ImportSn {
  val user = User.currentUser.getOrElse(S.redirectTo("/"))
  val pathName = "/home/ms/export_user_" + user.id.get +".zip"
  val elementsInZip = ElementsInZip()
  var addDepartments:Map[Long, scala.collection.mutable.Set[String]] = Map[Long, scala.collection.mutable.Set[String]]()

  def importingSaved(): CssSel = {
    var subjectNamesActual = SubjectName.findAll().map(_.name.get).mkString(",")
    val data = readFileFromDisk()
    val zipper = new Zipper()
    val mapFiles = zipper.fromZipJsonAndBinary(data)
    var subjectNamesImported = ImportJsonList.getSubjects(mapFiles, elementsInZip).mkString(",")
    var subjectNamesToSave = ""
    val (jsonFiles, otherFiles) = mapFiles.partition(e => e.key.split('/').length == 2)
    elementsInZip.files = otherFiles.size
    def mkImport(): Unit = {
      val subjects = subjectNamesToSave.split(',').map(s => s.split(':')).filter(s => s.length == 2).map(s => (s.head, s.last))
      .toList.map(sub =>  {
        (sub._1,SubjectName.find(By(SubjectName.name, sub._2)))
      }).filter(elem => elem._2.nonEmpty).map(elem => (elem._1 -> elem._2.openOrThrowException("filtered"))).toMap
      addDepartments = subjects.map(s => (s._2.id.get, scala.collection.mutable.Set[String]()))
      val inserted = jsonFiles.map(jf => (jf._1.split('/').last, jf._2)).map(mapElem  => {
        val jsonFile = mapElem._1
        val jsonData = mapElem._2
        //println("JSON FILE: " + jsonFile)
        //println("JSON DATA: " + new String(jsonData, StandardCharsets.UTF_8))
          jsonFile match {
            case k if k == Exports.JsonFileNames.Documents.toString => insertDocuments(jsonData, subjects)
            case k if k == Exports.JsonFileNames.Presentations.toString => insertPresentation(jsonData, subjects)
            case k if k == Exports.JsonFileNames.Videos.toString => insertVideos(jsonData, subjects)
            case k if k == Exports.JsonFileNames.Questions.toString => insertQuestions(jsonData, subjects)
            case k if k == Exports.JsonFileNames.Lessons.toString => insertLessons(jsonData)
            case k if k == Exports.JsonFileNames.Problems.toString => insertProblems(jsonData)
            case k if k == Exports.JsonFileNames.Courses.toString => insertCourses(jsonData, subjects)
            case _ => 0
          }
      }).sum
      otherFiles.foreach(otherFile => {
        val pathAr = otherFile.key.split('/')
        if (pathAr.length == 3) {
          saveFileToDB(pathAr.last, otherFile._2)
        }
      })
      saveAddDepartments()
      S.notice("importData", "Zapisano " + inserted + " dokumentów")
    }
    "#docs *" #> elementsInZip.docs.toString &
      "#presentations *" #> elementsInZip.presentations.toString &
      "#videos *" #> elementsInZip.videos.toString &
      "#questions *" #> elementsInZip.questions.toString &
      "#lessons *" #> elementsInZip.lessons.toString &
      "#problems *" #> elementsInZip.problems.toString &
      "#courses *" #> elementsInZip.courses.toString &
      "#files *" #> elementsInZip.files.toString &
      "#subjectNamesImported" #> SHtml.text(subjectNamesImported, subjectNamesImported = _) &
      "#subjectNamesActual" #> SHtml.text(subjectNamesActual, subjectNamesActual = _) &
      "#subjectNamesToSave" #> SHtml.text(subjectNamesToSave, subjectNamesToSave = _) &
      "#saveLoadedHidden" #> SHtml.submit("Importuj", mkImport)
  }

  def importingFile: CssSel = {
    var error = ""
    var subjectNames = ""
    def mkUpload(zipFile:FileParamHolder): Unit = {
      if(zipFile.fileName != "export.zip") error = "Błędna nazwa pliku"
      else {
        saveFileToDisk(zipFile.file)
      }
    }
    def emptyFun(): Unit ={
      println("EMPTY FUNCTION!!!")
    }
    "#filezip" #> SHtml.fileUpload( zipFile => mkUpload(zipFile)) &
    "#save"#> SHtml.submit("Wczytaj", () => emptyFun())
  }

  ///change Path to /home/opos/...
  def saveFileToDisk(bytes: Array[Byte]) = {
    val path = Paths.get(pathName)
    Files.write(path, bytes)
  }

  def readFileFromDisk() = {
    val path = Paths.get(pathName)
    Files.readAllBytes(path)
  }

  private def setInfo(json: ElementsJson): String = {
    s""" {${json.docs}, ${json.presentations}, ${json.videos},  ${json.questions}, ${json.lessons}, ${json.files}"""
  }
  private def insertDocuments(bytes: Array[Byte], subj: Map[String, SubjectName]) = {
    val arr = ImportJsonList.documentList(bytes)
    arr.foreach(docImp => {
      val doc = Document.create
      doc._id = new ObjectId(docImp._id)
      doc.lev = docImp.lev
      doc.title = docImp.title
      doc.content = docImp.content
      doc.descript = docImp.descript
      doc.department = docImp.department
      doc.subjectName = subj(docImp.subjectName).name.get
      val subjId = subj(docImp.subjectName).id.get
      doc.subjectId = subjId
      addDepartments(subjId).add(docImp.department)
      doc.authorName = user.getFullName
      doc.authorId = user.id.get
      doc.save
    })
    arr.length
  }
  private def insertLessons(bytes: Array[Byte])  = {
    val arr = ImportJsonList.lessonList(bytes)
    arr.foreach(lessImp =>{
      val less = LessonCourse.create
      less._id = new ObjectId(lessImp._id)
      less.nr = lessImp.nr
      less.title = lessImp.title
      less.descript = lessImp.descript
      less.courseId = new ObjectId(lessImp.courseId)
      less.contents = lessImp.contents
      less.authorId = user.id.get
      less.chapter = lessImp.chapter
      less.save
    })
    arr.length
  }
  private def insertQuestions(bytes: Array[Byte], subj: Map[String, SubjectName]) = {
    val arr = ImportJsonList.questionList(bytes)
    import scala.collection.mutable.Map
    val nrs:Map[Long, Int] = scala.collection.mutable.Map[Long, Int]()
    subj.map(s => s._2.id.get).foreach(id => {
      val qi = QuestIndex.find(("authorId" -> user.id.get) ~ ("subjectId" -> id))
        .getOrElse(QuestIndex.create(user.id.get, id))
      nrs += (qi.subjectId -> qi.nr)
    })
    arr.foreach(questImp => {
      val quest = QuizQuestion.create
      quest._id = new ObjectId(questImp._id)
      quest.hint = questImp.hint
      quest.question = questImp.question
      quest.fake = questImp.fake
      quest.answers = questImp.answers
      quest.department = questImp.department
      val subId = subj(questImp.subjectName).id.get
      addDepartments(subId).add(questImp.department)
      quest.dificult = questImp.dificult
      quest.info = questImp.info
      quest.lev = questImp.lev
      quest.subjectId = subId
      quest.subjectName = subj(questImp.subjectName).name.get
      nrs(quest.subjectId) = nrs(quest.subjectId) + 1
      quest.nr = nrs(quest.subjectId)
      quest.authorId = user.id.get
      quest.save
    })
    subj.map(s => s._2.id.get).foreach(id => {
      val qi = QuestIndex.find(("authorId" -> user.id.get) ~ ("subjectId" -> id))
        .getOrElse(QuestIndex.create(user.id.get, id))
      val nr = nrs(qi.subjectId)
      qi.nr = nr
      qi.save
    })
    arr.length

  }
  //TODO: Implement!
  private def insertVideos(bytes: Array[Byte], subj: Map[String, SubjectName]) = {
    val arr = ImportJsonList.videoList(bytes)
    arr.foreach(vidImp => {
      val vidBox = Video.find(vidImp._id)
      val vid = if(vidBox.nonEmpty && vidBox.get.authorId == user.id.get) vidBox.get else Video.create
      vid._id = new ObjectId(vidImp._id)
      vid.authorId = user.id.get
      vid.lev = vidImp.lev
      vid.title = vidImp.title
      vid.oldPath = vidImp.oldPath
      vid.mime = vidImp.mime
      vid.onServer = vidImp.onServer
      vid.link = vidImp.link
      vid.department = vidImp.department
      val subId = subj(vidImp.subjectName).id.get
      addDepartments(subId).add(vidImp.department)
      vid.descript = vidImp.descript
      vid.subjectName = subj(vidImp.subjectName).name.get
      vid.subjectId = subId
      vid.save
    })
    arr.length
  }
  private def insertPresentation(bytes: Array[Byte], subj: Map[String, SubjectName]) = {
    val arr = ImportJsonList.presentationList(bytes)
    arr.foreach(pi =>{
      val presBox = Presentation.find(pi._id)
      val pres = if(presBox.nonEmpty && presBox.get.authorId == user.id.get) presBox.get else Presentation.create
      pres._id = new ObjectId(pi._id)
      pres.authorId = user.id.get
      pres.lev = pi.lev
      pres.title = pi.title
      pres.department = pi.department
      val subId = subj(pi.subjectName).id.get
      addDepartments(subId).add(pi.department)
      pres.subjectId = subId
      pres.subjectName = subj(pi.subjectName).name.get
      pres.descript = pi.descript
      pres.slides = pi.slides
      pres.save
    })
    arr.length
  }
  private def insertProblems(bytes: Array[Byte]) = {
    val arr = ImportJsonList.problemList(bytes)
    arr.foreach(pi => {
      val probBox = TestProblem.find(pi._id)
      val prob = if(probBox.nonEmpty && probBox.get.author == user.id.get) probBox.get else TestProblem.create
      prob._id = new ObjectId(pi._id)
      prob.author = user.id.get
      prob.info = pi.info
      prob.title = pi.title
      prob.description = pi.description
      prob.expectedOutputs = pi.expectedOutputs
      prob.inputs = pi.inputs
      prob.save
    })
    arr.length
  }

  //TODO: Implement!
  private def insertCourses(bytes: Array[Byte], subj: Map[String, SubjectName]) = {
    val arr = ImportJsonList.courseList(bytes)
    arr.foreach(ci => {
      val courBox = Course.find(ci._id)
      val cour = if(courBox.nonEmpty && courBox.get.authorId == user.id.get) courBox.get else Course.create
      cour._id = new ObjectId(ci._id)
      cour.authorId = user.id.get
      cour.title = ci.title
      cour.chapters = ci.chapters
      cour.descript = ci.descript
      cour.subjectName = subj(ci.subjectName).name.get
      cour.subjectId = subj(ci.subjectName).id.get
      cour.img  = ci.img
      cour.save
    })
    arr.length
  }

  private def saveFileToDB(nameWithId:String, data:Array[Byte]): Unit = {
    val nameArr = nameWithId.split('.')
    if(nameArr.length > 1) {
      val idStr = nameArr.head
      val nameFile = nameArr.drop(1).mkString(".")
      val mime = nameArr.last.toLowerCase
      //println("SAVE FILE: " + nameFile)
      MongoDB.use(DefaultMongoIdentifier) {
        db =>
          val fs = new GridFS(db)
          val inputFile = fs.createFile(data)
          inputFile.setContentType(mime)
          inputFile.setFilename(nameFile)
          inputFile.setId(new ObjectId(idStr))
          inputFile.save

      }
    }
  }
  private def saveAddDepartments() = {
   /* println("SAVE ADD DEPARTMENTS")
    addDepartments.foreach(ad => {
      println(s"${ad._1} : ${ad._2.mkString(", ")}")
    }) */
    SubjectTeach.findAll("authorId"->user.id.get).foreach( st => {
      if(addDepartments.exists(d => d._1 == st.id)){
        val departs = (st.departments ::: addDepartments(st.id).toList).distinct
        st.departments = departs
        st.save
      }
    })
  }

}
