package eu.brosbit.opos.snippet.edu

import com.mongodb.gridfs.GridFS
import com.mongodb.util.JSONParseException
import eu.brosbit.opos.api.{Exports, ImportJsonList}
import eu.brosbit.opos.lib.Zipper
import eu.brosbit.opos.model.{Lesson, SubjectName, TestProblem, User}
import eu.brosbit.opos.model.edu.{Course, Document, LessonCourse, Presentation, QuestIndex, QuizQuestion, SubjectTeach, Video}
import eu.brosbit.opos.model.page.LastImports
import net.liftweb.http.{FileParamHolder, S, SHtml}
import net.liftweb.mongodb.{DefaultMongoIdentifier, MongoDB}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import org.bson.types.ObjectId
import net.liftweb.json.JsonDSL._

import java.nio.file.{Files, Paths}
import eu.brosbit.opos.api.JsonExportImport

import java.util.Date
import scala.xml.Unparsed

case class ElementsInZip(var docs:Int = 0, var presentations: Int = 0, var videos: Int = 0, var questions: Int = 0, var lessons: Int = 0,
                         var files: Int = 0, var problems: Int = 0, var courses: Int = 0, var subjectNotAdded:List[String] = Nil)
case class ElementsJson(var docs:String = "", var presentations: String = "", var videos: String = "", var questions: String = "",
                        var lessons: String = "", var files: String = "", var problems: String = "")


/* Not inserted Exams in json!!*/
class ImportSn {
  private val user = User.currentUser.getOrElse(S.redirectTo("/"))
  private val yourDir = "/home/"+user.userDirName + "/"
  private val elementsInZip = ElementsInZip()
  private val addDepartments:Map[Long, scala.collection.mutable.Set[String]] = Map[Long, scala.collection.mutable.Set[String]]()

  /*
  def importingSaved(): CssSel = {
    var subjectNamesActual = SubjectName.findAll().map(_.name.get).mkString(",")
    val data = readFileFromDisk("export.zip")
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
            //case k if k == Exports.JsonFileNames.Problems.toString => insertProblems(jsonData)
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
*/
  def lastImportedFiles: CssSel = {
    val lastImports = LastImports.findAll(("teacherId" -> user.id.get))
    ".importedFiles *" #> lastImports.map(imp => {
      if(imp.isJson){
        ".fileName *" #> imp.fileName &
        ".docs *" #> imp.docJson(Exports.JsonFileNames.Documents.toString) &
        ".presentations *" #> imp.docJson(Exports.JsonFileNames.Presentations.toString) &
        ".videos *" #> imp.docJson(Exports.JsonFileNames.Videos.toString) &
        ".lessons *" #> imp.docJson(Exports.JsonFileNames.Lessons.toString) &
        ".courses *" #> imp.docJson(Exports.JsonFileNames.Courses.toString) &
        ".questions *" #> imp.docJson(Exports.JsonFileNames.Questions.toString) &
       ".files *" #> "0"
      } else {
        ".fileName *" #> imp.fileName &
        ".docs *" #> imp.docJson(Exports.JsonFileNames.Documents.toString) &
        ".presentations *" #> "0" &
        ".videos *" #> "0" &
        ".lessons *" #> "0" &
        ".courses *" #> "0" &
        ".questions *" #> "0"&
        ".files" #> imp.objectNumber
      }

    })
  }

  def importingFile: CssSel = {
    var error = ""
    var snActual = SubjectName.findAll().map(sn => sn.name.get).mkString(";")
    var snToChange = ""
    var fileName = ""
    var zipData:Array[Byte] = Array()
    def mkUpload(zipFile:FileParamHolder): Unit = {
      fileName = zipFile.fileName
      zipData = zipFile.file
    }
    def emptyFun(): Unit ={
      if(fileName.split(".").last != "zip" || fileName.take(6) != "export") error = "Błędna nazwa pliku"
      else {
        val mapSubjectChange = snToChange.split(";").map(line => {
          val a = line.split(":")
          (a(1) -> a(0))
        }).toMap
        saveFile(zipData, fileName, mapSubjectChange)
      }
      S.notice(error)
    }
    "#subjectNotAdded *" #> Unparsed(elementsInZip.subjectNotAdded.mkString(" <br> ")) &
    "#subjectNamesActual" #> SHtml.text(snActual, snActual = _) &
    "#subjectNamesChange" #> SHtml.text(snToChange, snToChange = _) &
    "#filezip" #> SHtml.fileUpload( zipFile => mkUpload(zipFile)) &
    "#save"#> SHtml.submit("Wczytaj", () => emptyFun())
  }

  /*
  private def listYourImportedFiles() = {
    import scala.collection.JavaConverters._
    val files = Files.walk(Paths.get(yourDir), 1).toList.asScala
    val zipImportFiles = files.filter(p => Files.isRegularFile(p)).filter(p => {
      p.endsWith("zip") && p.getFileName.toString.length > 6 && p.getFileName.toString.take(6) == "import"
    })
    zipImportFiles
  }
  */


  //NEW!!
  private def saveFile(data: Array[Byte], nameFile:String, subjChangeMap:Map[String, String]): Unit = {
    val zipper = new Zipper()
    val mapFiles = zipper.fromZipJsonAndBinary(data)
    val (jsonFiles, otherFiles) = mapFiles.partition(e => e.key.split('.').last == "json")
    val lastImports = LastImports.findAll(("fileName" -> nameFile)~("teacherId" -> user.id.get))
    val lastImport = if(lastImports.nonEmpty) lastImports.head else LastImports.create
    val subjects = SubjectName.findAll().map(sn => (sn.name.get, sn.id.get)).toMap
    val subjectsNotFound = scala.collection.mutable.Set[String]()
    lastImport.fileName = nameFile
    lastImport.date = new Date().getTime
    lastImport.teacherId = user.id.get
    var subjNotAdded = List[String]()
    if(jsonFiles.nonEmpty) {
      val dataObj = ImportJsonList.convertJsonToObjects(jsonFiles)
      subjNotAdded = insertDocuments(dataObj.doc, subjChangeMap, subjects):::subjNotAdded
      subjNotAdded = insertPresentation(dataObj.pres, subjChangeMap, subjects):::subjNotAdded
      subjNotAdded = insertVideos(dataObj.vid, subjChangeMap, subjects):::subjNotAdded
      subjNotAdded = insertQuestions(dataObj.quest, subjChangeMap, subjects):::subjNotAdded
      insertLessons(dataObj.less)
      subjNotAdded = insertCourses(dataObj.cour, subjChangeMap, subjects):::subjNotAdded
      subjNotAdded = subjNotAdded.distinct
      elementsInZip.subjectNotAdded = subjNotAdded
      lastImport.isJson = true
      saveAddDepartments() //adding all registered departments during inserts
    } else {
      lastImport.isJson = false
      lastImport.objectNumber = otherFiles.size
      otherFiles.foreach(otherFile => {
        val pathAr = otherFile.key.split('/')
          saveFileToDB(pathAr.last, otherFile._2)
      })
    }
    lastImport.save
  }

  ///change Path to /home/opos/...
  private def saveFileToDisk(bytes: Array[Byte]) = {
    val path = Paths.get(yourDir + "export.zip")
    Files.write(path, bytes)
  }

  private def readFileFromDisk(fileName: String) = {
    val path = Paths.get(yourDir + fileName)
    Files.readAllBytes(path)
  }

  private def setInfo(json: ElementsJson): String = {
    s""" {${json.docs}, ${json.presentations}, ${json.videos},  ${json.questions}, ${json.lessons}, ${json.files}"""
  }

  private def findSubject(subjectName: String, subjMap: Map[String, String], subExist:Map[String, Long]):(String, Long) =
    if(subExist.contains(subjectName)) (subjectName, subExist(subjectName))
    else if(subjMap.contains(subjectName) && subjMap(subjectName).trim.nonEmpty) (subjMap(subjectName), subExist(subjMap(subjectName)))
    else ("", -1)

  private def insertDocuments(docs: List[JsonExportImport.DocumentsImport], subjMap: Map[String, String],
                              subExist:Map[String, Long]):List[String] = {
    var notAddedSubjects = List[String]()
    var nr = 0
    docs.foreach(docImp => {
      val subject = findSubject(docImp.subjectName, subjMap, subExist)
      if(subject._2 == -1){
        notAddedSubjects = subject._1::notAddedSubjects
      } else {
        val doc = Document.create
        doc._id = new ObjectId(docImp._id)
        doc.lev = docImp.lev
        doc.title = docImp.title
        doc.content = docImp.content
        doc.descript = docImp.descript
        doc.department = docImp.department
        doc.subjectName = subject._1
        val subjId = subject._2
        doc.subjectId = subjId
        addDepartments(subjId).add(docImp.department)
        doc.authorName = user.getFullName
        doc.authorId = user.id.get
        doc.save
        nr += 1
      }
    })
    elementsInZip.docs = nr
    notAddedSubjects
  }

  private def insertLessons(lessons: List[JsonExportImport.LessonImport]):Unit  = {
    var nr = 0
    lessons.foreach(lessImp =>{
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
      nr += 1
    })
    elementsInZip.lessons = nr
  }

  private def insertQuestions(questions: List[JsonExportImport.QuestionImport], subjMap: Map[String, String],
                              subjExist: Map[String, Long]): List[String] = {
    var notAddedSubjects = List[String]()
    var nr = 0
    val nrs: scala.collection.mutable.Map[Long, Int] = scala.collection.mutable.Map[Long, Int]()
    subjExist.values.foreach(id => {
      val qi = QuestIndex.find(("authorId" -> user.id.get) ~ ("subjectId" -> id))
        .getOrElse(QuestIndex.create(user.id.get, id))
      nrs += (qi.subjectId -> qi.nr)
    })
    questions.foreach(questImp => {
      val subject = findSubject(questImp.subjectName, subjMap, subjExist)
      if (subject._2 != -1) {
        val quest = QuizQuestion.create
        quest._id = new ObjectId(questImp._id)
        quest.hint = questImp.hint
        quest.question = questImp.question
        quest.fake = questImp.fake
        quest.answers = questImp.answers
        quest.department = questImp.department
        val subId = subject._2
        addDepartments(subId).add(questImp.department)
        quest.dificult = questImp.dificult
        quest.info = questImp.info
        quest.lev = questImp.lev
        quest.subjectId = subId
        quest.subjectName = subject._1
        nrs(quest.subjectId) = nrs(quest.subjectId) + 1
        quest.nr = nrs(quest.subjectId)
        quest.authorId = user.id.get
        quest.save
        nr += 1
        val qi = QuestIndex.find(("authorId" -> user.id.get) ~ ("subjectId" -> subject._2))
          .getOrElse(QuestIndex.create(user.id.get, subject._2))
        val numb = nrs(qi.subjectId)
        qi.nr = numb
        qi.save
      } else {
        notAddedSubjects = subject._1 :: notAddedSubjects
      }
    })
    elementsInZip.questions = nr
    notAddedSubjects
  }

  private def insertVideos(videos:List[JsonExportImport.VideoImport], subjMap: Map[String, String], subjExist: Map[String, Long]) = {
    var notAddedSubjects = List[String]()
    var nr = 0
    videos.foreach(vidImp => {
      val subject = findSubject(vidImp.subjectName, subjMap, subjExist)
      if(subject._2 != -1){
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
        val subId = subject._2
        addDepartments(subId).add(vidImp.department)
        vid.descript = vidImp.descript
        vid.subjectName = subject._1
        vid.subjectId = subId
        vid.save
        nr += 1
      } else {
        notAddedSubjects = subject._1::notAddedSubjects
      }

    })
    elementsInZip.videos = nr
    notAddedSubjects
  }
  private def insertPresentation(presentations:List[JsonExportImport.PresentationImport], subjMap: Map[String, String],
                                 subjExist: Map[String, Long]):List[String] = {
    var notAddedSubjects = List[String]()
    var nr = 0
    presentations.foreach(pi =>{
      val subject = findSubject(pi.subjectName, subjMap, subjExist)
      if(subject._2 != -1){
        val presBox = Presentation.find(pi._id)
        val pres = if(presBox.nonEmpty && presBox.get.authorId == user.id.get) presBox.get else Presentation.create
        pres._id = new ObjectId(pi._id)
        pres.authorId = user.id.get
        pres.lev = pi.lev
        pres.title = pi.title
        pres.department = pi.department
        val subId = subject._2
        addDepartments(subId).add(pi.department)
        pres.subjectId = subId
        pres.subjectName = subject._1
        pres.descript = pi.descript
        pres.slides = pi.slides
        pres.save
        nr += 1
      } else {
        notAddedSubjects = subject._1::notAddedSubjects
      }
    })
    elementsInZip.presentations = nr
    notAddedSubjects
  }

  private def insertCourses(courses:List[JsonExportImport.CourseImport], subjMap: Map[String, String],
                            subjExist:Map[String, Long]):List[String] = {
    var notAddedSubjects = List[String]()
    var nr = 0
    courses.foreach(ci => {
      val subject = findSubject(ci.subjectName, subjMap, subjExist)
      if(subject._2 != -1){
        val courBox = Course.find(ci._id)
        val cour = if(courBox.nonEmpty && courBox.get.authorId == user.id.get) courBox.get else Course.create
        cour._id = new ObjectId(ci._id)
        cour.authorId = user.id.get
        cour.title = ci.title
        cour.chapters = ci.chapters
        cour.descript = ci.descript
        cour.subjectName = subject._1
        cour.subjectId = subject._2
        cour.img  = ci.img
        cour.save
        nr += 1
      } else {
        notAddedSubjects = subject._1::notAddedSubjects
      }

    })
    elementsInZip.courses = nr
    notAddedSubjects
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
          inputFile.save()

      }
    }
  }
  private def saveAddDepartments():Unit = {
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
