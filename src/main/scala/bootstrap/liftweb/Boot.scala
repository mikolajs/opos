/*
 * Copyright (C) 2012   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   This file is part of VRegister (Virtual Register)
*    Apache License Version 2.0, January 2004  http://www.apache.org/licenses/
 */

package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import _root_.net.liftweb.mapper.{ConnectionIdentifier, ConnectionManager, DB, DefaultConnectionIdentifier, Schemifier}

import java.sql.{Connection, DriverManager}
import eu.brosbit.opos.model._
import eu.brosbit.opos.api._
import eu.brosbit.opos.lib.MailConfig
import _root_.net.liftweb.mongodb._
import com.mongodb.MongoClient
import eu.brosbit.opos.comet.CronActor
import eu.brosbit.opos.lib.{ConfigLoader => CL}

object DBVendor extends ConnectionManager {
  def newConnection(name: ConnectionIdentifier): Box[Connection] = {
    try {
      //Class.forName(classOf[org.postgresql.Driver].getName)
      Class.forName("org.postgresql.Driver")
      val dm = DriverManager.getConnection("jdbc:postgresql:osp", CL.sqlDB, CL.sqlPassw)
      Full(dm)
    } catch {
      case e: Exception => e.printStackTrace; Empty
    }
  }

  def releaseConnection(conn: Connection) {
    conn.close
  }
}

//object OtherConnectionIdentifier extends MongoIdentifier {
//  def jndiName = "other"
//}

class Boot {
  def boot {
    CL.init

    DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)

    MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("127.0.0.1", 27017), CL.mongoDB))
//    val mongoClient = new MongoClient("127.0.0.1", 27017)
//    MongoDB.defineDb(OtherConnectionIdentifier, mongoClient, CL.mongoDB)

    // where to search snippet
    LiftRules.addToPackages("eu.brosbit.opos")
    LiftRules.addToPackages("eu.brosbit.opos.snippet.page")
    LiftRules.addToPackages("eu.brosbit.opos.snippet.edu")
    LiftRules.addToPackages("eu.brosbit.opos.snippet.register")
    LiftRules.addToPackages("eu.brosbit.opos.snippet.secretariat")
    LiftRules.addToPackages("eu.brosbit.opos.snippet.doc")

    Schemifier.schemify(true, Schemifier.infoF _, User, ClassModel, MarkMap, SubjectName)


    LiftRules.statelessDispatch.append({
      case Req("img" :: id :: Nil, _, GetRequest) => () => ImageLoader.image(id)
      case Req("file" :: id :: Nil, _, GetRequest) => () => FileLoader.file(id)
      case Req("getdocument" :: id :: Nil, _, GetRequest) => () => TemplateDocumentCreater.create(id)
      case Req("getimgslide" :: Nil,  _, GetRequest) => () => SlideImg.create
//      case Req("exportedu"  :: Nil, _, GetRequest) => () => Exports.export()
//      case Req("importedu" :: Nil, _, GetRequest) => () => Imports.importing()
    })

    LiftRules.dispatch.append({
      case Req("export" ::  Nil, _, GetRequest) => () =>  Exports.export()
      case Req("import" :: Nil, _, GetRequest) => () => Imports.importing()

    })




    if (DB.runQuery("select * from users where lastname = 'Administrator'")._2.isEmpty) {
      val u = User.create
      u.lastName("Administrator").role("a").password("123qwerty").email("mail@mail.org").validated(true).save
    }

    val loggedIn = If(() => User.loggedIn_? && User.currentUser.openOrThrowException("Not logged").validated.get,
      () => RedirectResponse("/login?r=" + S.uri))

    val isAdmin = If(() => User.loggedIn_? && (User.currentUser.openOrThrowException("Not logged").role.get == "a"),
      () => RedirectResponse("/login?r=" + S.uri))

    val isSecretariat = If(() => {
      User.currentUser match {
        case Full(user) => {
          val role = user.role.get
          role == "a" || role == "s"
        }
        case _ => false
      }
    }, () => RedirectResponse("/login?r=" + S.uri))



    val isTeacher = If(() => {
      User.currentUser match {
        case Full(user) => {
          val role = user.role.get
          role == "n" || role == "a" || role == "d"
        }
        case _ => false
      }
    }, () => RedirectResponse("/login?r=" + S.uri))

    // Build SiteMap::
    def sitemap() = SiteMap(
      List(
        Menu("Strona główna") / "index" / ** >> LocGroup("public"),
        Menu("Strony") / "page" / ** >> LocGroup("public"),
        Menu("Galeria") / "gallery" / ** >> LocGroup("public"),
        //Menu("Kontakt") / "contact" >> LocGroup("public"),
        Menu("Forum") / "forum" >> LocGroup("extra"),
        Menu("Dział Slajdy") / "slidedep" >> LocGroup("extra"),
        Menu("Slajdy z obrazków") / "slideimg" >> LocGroup("extra"),
        Menu("Slajdy z obrazków") / "editslideimg" >> LocGroup("extra") >> isTeacher,
        Menu("Edycja wątku") / "editthread" / ** >> LocGroup("extra") >> loggedIn,
        Menu("Forum Post") / "forumpost" / ** >> LocGroup("extra"),
        Menu("Login") / "login" >> LocGroup("extra"),
        Menu("Edycja galerii") / "galleryedit" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Galerie zdjęć") / "galleries" >> LocGroup("extra") >> isTeacher,
        Menu("Edytor artykułów") / "editarticle" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Pokaz") / "showslide" / ** >> LocGroup("extra") >> Hidden,
        Menu("Maile kontaktowe") / "admin" / "index" >> LocGroup("admin") >> isAdmin,
        Menu("Dane kontaktowe") / "admin" / "contact" >> LocGroup("admin") >> isAdmin,
        Menu("Działy") / "admin" / "pages" >> LocGroup("admin") >> isAdmin,
        Menu("Kafelki flash") / "admin" / "flashtile" >> LocGroup("admin") >> isAdmin,
        Menu("Kafelki linków") / "admin" / "linktiles" >> LocGroup("admin") >> isAdmin,
        Menu("Projektowanie menu") / "admin" / "menu" >> LocGroup("admin") >> isAdmin,
        //Menu("Slajdy") / "admin" / "slides" >> LocGroup("admin") >> isAdmin,
        Menu("Logo i favicon") / "admin" / "logo" >> LocGroup("admin") >> isAdmin,
        Menu("Edycja administratorów") / "admin" / "admins" >> LocGroup("admin") >> isAdmin,
        Menu("Sekretariat") / "admin" / "secretariat" >> LocGroup("admin") >> isAdmin,
        Menu("Edycja kodu Google wyszukiwania") / "admin" / "googlecode" >> LocGroup("admin") >> isAdmin,
        Menu("Eksporty stron") / "admin" / "pagesexport" >> LocGroup("admin") >> isAdmin,
        //Menu("Ideksacja newsów") / "admin" / "reindexnews" >> LocGroup("admin") >> isAdmin,
        Menu("Img") / "imgstorage" >> LocGroup("extra") >> loggedIn,
        Menu("Thumb") / "thumbstorage" >> LocGroup("extra") >> loggedIn,
        Menu("File") / "filestorage" >> LocGroup("extra") >> loggedIn,
        Menu("Nauczyciele") / "secretariat" / "index" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Klasy") / "secretariat" / "classes" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Import uczniów") / "secretariat" / "classimport" >> LocGroup("extra") >> isSecretariat,
        Menu("Uczniowie") / "secretariat" / "pupils" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Przedmioty") / "secretariat" / "subjects" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Dzwonki") / "secretariat" / "bells" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Wybór dziennika") / "register" / "index" / ** >> LocGroup("register") >> isTeacher,
        Menu("Uczniowie") / "register" / "pupil_data" >> LocGroup("register") >> isTeacher,
        Menu("Rodzice") / "register" / "parent_data" >> LocGroup("register") >> isTeacher,
        Menu("Tematy") / "register" / "themes" / ** >> LocGroup("register") >> isTeacher,
//        Menu("Oceny") / "register" / "marks" / ** >> LocGroup("register") >> isTeacher,
        Menu("Ogłoszenia") / "register" / "anounces" >> LocGroup("register") >> isTeacher,
        Menu("Uwagi") / "register" / "opinions" >> LocGroup("register") >> isTeacher,
        Menu("Plan") / "register" / "class_plan" >> LocGroup("register") >> isTeacher,
        //Menu("Wiadomości") / "documents" / "index" >> LocGroup("documents") >> isTeacher,
        Menu("Kółka") / "documents" / "extralessons" >> LocGroup("documents") >> isTeacher,
        Menu("Rozkłady") / "documents" / "themesplan" >> LocGroup("documents") >> isTeacher,
        Menu("PSO") / "documents" / "pso" >> LocGroup("documents") >> isTeacher,
        Menu("Dokumenty") / "documents" / "doctemplate" / ** >> LocGroup("documents") >> isTeacher,
        Menu("Szablon") / "documents" / "createtemplate" / ** >> LocGroup("extra") >> isAdmin,
        Menu("Kolejność") / "documents" / "orderdoc" / ** >> LocGroup("extra") >> isAdmin,
        //Menu("Wiadomości") / "view" / "index" >> LocGroup("view") >> loggedIn,
        Menu("Tematy") / "view" / "themes" / ** >> LocGroup("view") >> loggedIn,
        Menu("Kursy") / "view" / "courses" >> LocGroup("view") >> loggedIn,
        Menu("Sprawdziany") / "view" / "exams" >> LocGroup("view") >> loggedIn,
        Menu("Zobacz lekcję") / "view" / "course" / ** >> LocGroup("extra") >> loggedIn,
        Menu("MassagesWork") / "view" / "showmessageswork" / ** >> LocGroup("extra") >> loggedIn,
        Menu("Quiz") / "view" / "showquiz" / ** >> LocGroup("extra") >> Hidden >> loggedIn,
        Menu("Works") / "view" / "showwork" / ** >> LocGroup("extra") >> Hidden >> loggedIn,
        Menu("CheckedExam") / "view" / "showcheckedexam" / ** >> LocGroup("extra") >> Hidden >> loggedIn,
        Menu("CheckProblem") / "educontent" / "checkproblem" / ** >> LocGroup("extra") >> Hidden >> loggedIn,
        Menu("Kursy") / "educontent" / "index" >> LocGroup("edu") >> isTeacher,
        Menu("Tematy") / "educontent" / "works" >> LocGroup("edu") >> isTeacher,
        Menu("Sprawdziany") / "educontent" / "exams" >> LocGroup("edu") >> isTeacher,
        //Menu("Sprawdziany") / "educontent" / "quizzes" >> LocGroup("edu") >> isTeacher,
        Menu("Zadania") / "educontent" / "questions" >> LocGroup("edu") >> isTeacher,
        Menu("Artykuły") / "educontent" / "documents" >> LocGroup("edu") >> isTeacher,
        Menu("Filmy") / "educontent" / "video" >> LocGroup("edu") >> isTeacher,
        Menu("Prezentacje") / "educontent" / "presentations" >> LocGroup("edu") >> isTeacher,
        Menu("Problemy") / "educontent" / "problems" >> LocGroup("edu") >> isTeacher,
        Menu("Grupy") / "educontent" / "groups" >> LocGroup("edu") >> isTeacher,
        Menu("Ustawienia") / "educontent" / "options" >> LocGroup("edu") >> isTeacher,
        Menu("Lekcje") / "educontent" / "course" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Sprawdziany edycja") / "educontent" / "editexam" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Praca edycja") / "educontent" / "editwork" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Lista odpowiedzi") / "educontent" / "showexams" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Praca odpowiedzi") / "educontent" / "showworks" / ** >> LocGroup("extra") >> isTeacher,
        Menu("MassagesWork") / "educontent" / "showmessageswork" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Sprawdzian sprawdzanie") / "educontent" / "checkexam" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Praca sprawdzanie") / "educontent" / "checkwork" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Edycja lekcji") / "educontent" / "editlesson" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edycja prezentacji") / "educontent" / "editpresentation" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edycja quizów") / "educontent" / "editquiz" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edycja grup") / "educontent" / "groupedit" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edytuj dokument") / "educontent" / "editdocument" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Indeksuj wideo") / "educontent" / "indexvideo" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Import") / "educontent" / "import" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Slajdy") / "educontent" / "showlessonslides" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edycja problemów") / "educontent" / "editproblem" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Otwarte kursy") / "public" / "index" >> LocGroup("pub"),
        Menu("Otwarta lekcja") / "public" / "course" / ** >> LocGroup("pub"),
        Menu("GC") / "admin" / "gc" >> LocGroup("admin") >> isAdmin,
        //Menu("Test") / "test1234qwerty" >> LocGroup("extra"),
        Menu("Static") / "static" / **) :::
        // Menu entries for the User management stuff
        User.sitemap: _*)

    LiftRules.setSiteMapFunc(sitemap)

    LiftRules.statelessRewrite.prepend(NamedPF("ClassRewrite") {
      case RewriteRequest(
      ParsePath("gallery" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "gallery" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("forumpost" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "forumpost" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("forum" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "forum" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("editthread" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "editthread" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("editarticle" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "editarticle" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("register" :: "index" :: classSchool :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "register" :: "index" :: Nil, Map("class" -> classSchool))
      case RewriteRequest(
      ParsePath("register" :: "marks" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "register" :: "marks" :: Nil, Map("idS" -> subjectId))
      case RewriteRequest(
      ParsePath("register" :: "themes" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "register" :: "themes" :: Nil, Map("idS" -> subjectId))
      case RewriteRequest(
      ParsePath("documents" :: "doctemplate" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "documents" :: "doctemplate" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("documents" :: "createtemplate" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "documents" :: "createtemplate" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("documents" :: "orderdoc" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "documents" :: "orderdoc" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("index" :: w :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "index" :: Nil, Map("w" -> w))
      case RewriteRequest(
      ParsePath("page" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "page" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("galleryedit" :: galId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "galleryedit" :: Nil, Map("id" -> galId))
      case RewriteRequest(
      ParsePath("view" :: "themes" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "view" :: "themes" :: Nil, Map("idS" -> subjectId))
      case RewriteRequest(
      ParsePath("educontent" :: "course" :: courseId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "course" :: Nil, Map("id" -> courseId))
      case RewriteRequest(
      ParsePath("public" :: "course" :: courseId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "public" :: "course" :: Nil, Map("id" -> courseId))
      case RewriteRequest(
      ParsePath("educontent" :: "editpresentation" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "editpresentation" :: Nil, Map("id" -> subjectId))
      case RewriteRequest(
      ParsePath("educontent" :: "editdocument" :: entryId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "editdocument" :: Nil, Map("id" -> entryId))
      case RewriteRequest(
      ParsePath("educontent" :: "editquiz" :: entryId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "editquiz" :: Nil, Map("id" -> entryId))
      case RewriteRequest(
      ParsePath("educontent" :: "editlesson" :: entryId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "editlesson" :: Nil, Map("id" -> entryId))
      case RewriteRequest(
      ParsePath("showslide" :: slideId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "showslide" :: Nil, Map("id" -> slideId))
      case RewriteRequest(
      ParsePath("educontent" :: "checkproblem" :: problemId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "checkproblem" :: Nil, Map("id" -> problemId))
      case RewriteRequest(
      ParsePath("educontent" :: "showlessonslides" :: lessonId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "showlessonslides" :: Nil, Map("id" -> lessonId))
      case RewriteRequest(
      ParsePath("educontent" :: "editexam" :: examId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "editexam" :: Nil, Map("id" -> examId))
      case RewriteRequest(
      ParsePath("educontent" :: "editwork" :: workId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "editwork" :: Nil, Map("id" -> workId))
      case RewriteRequest(
      ParsePath("educontent" :: "groupedit" :: groupId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "groupedit" :: Nil, Map("id" -> groupId))
      case RewriteRequest(
      ParsePath("educontent" :: "showexams" :: examId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "showexams" :: Nil, Map("id" -> examId))
      case RewriteRequest(
      ParsePath("educontent" :: "showworks" :: workId  :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "showworks" :: Nil, Map("id" -> workId))
      case RewriteRequest(
      ParsePath("educontent" :: "showmessageswork" :: workId  :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "showmessageswork" :: Nil, Map("id" -> workId))
      case RewriteRequest(
      ParsePath("educontent" :: "checkexam" :: examId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "checkexam" :: Nil, Map("id" -> examId))
      case RewriteRequest(
      ParsePath("educontent" :: "checkwork" :: workId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "checkwork" :: Nil, Map("id" -> workId))
      case RewriteRequest(
      ParsePath("educontent" :: "editproblem" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "educontent" :: "editproblem" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("view" :: "showquiz" :: quizId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "view" :: "showquiz" :: Nil, Map("id" -> quizId))
      case RewriteRequest(
      ParsePath("view" :: "showwork" :: workId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "view" :: "showwork" :: Nil, Map("id" -> workId))
      case RewriteRequest(
      ParsePath("view" :: "showcheckedexam" :: id :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "view" :: "showcheckedexam" :: Nil, Map("id" -> id))
      case RewriteRequest(
      ParsePath("view" :: "course" :: lessonId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "view" :: "course" :: Nil, Map("id" -> lessonId))
      case RewriteRequest(
      ParsePath("view" :: "showmessageswork" :: workId  :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "view" :: "showmessageswork" :: Nil, Map("id" -> workId))
    })

    //DataTable.init

    LiftRules.early.append(makeUtf8)

    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    LiftRules.passNotFoundToChain = true
    LiftRules.maxMimeSize = 256 * 1024 * 1024
    LiftRules.maxMimeFileSize = 256 * 1024 * 1024

    {
      new MailConfig().autoConfigure()
    }

    LiftRules.liftRequest.append {
      case Req("extra" :: _, _, _) => false
    }

    //cron jobs for find zastepstwa
    //val cron = new CronActor()


    S.addAround(DB.buildLoanWrapper)
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
