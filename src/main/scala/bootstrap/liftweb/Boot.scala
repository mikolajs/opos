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
import _root_.net.liftweb.mapper.{ DB, By, ConnectionManager, ConnectionIdentifier, Schemifier, DefaultConnectionIdentifier }
import java.sql.{ Connection, DriverManager }
import _root_.pl.brosbit.model._
import _root_.pl.brosbit.api._
import _root_.pl.brosbit.lib.MailConfig

import _root_.net.liftweb.mongodb._

object DBVendor extends ConnectionManager {
  def newConnection(name: ConnectionIdentifier): Box[Connection] = {
    try {
      //Class.forName(classOf[org.postgresql.Driver].getName)
      Class.forName("org.postgresql.Driver")
      val dm = DriverManager.getConnection("jdbc:postgresql:vregister", "vregister", "123test456")
      Full(dm)
    } catch {
      case e: Exception => e.printStackTrace; Empty
    }
  }
  def releaseConnection(conn: Connection) { conn.close }
}

class Boot {
  def boot {

    DB.defineConnectionManager(DefaultConnectionIdentifier, DBVendor)
    
    MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("127.0.0.1", 27017), "vregister"))

    // where to search snippet
    LiftRules.addToPackages("pl.brosbit")
    LiftRules.addToPackages("pl.brosbit.snippet.page")
    LiftRules.addToPackages("pl.brosbit.snippet.edu")
     LiftRules.addToPackages("pl.brosbit.snippet.teacher")
     LiftRules.addToPackages("pl.brosbit.snippet.secretariat")

    Schemifier.schemify(true, Schemifier.infoF _, User, 
      ClassModel, MarkMap, SubjectName)
      
     LiftRules.statelessDispatch.append({
      case Req("img" :: id :: Nil, _, GetRequest) => () => ImageLoader.image(id)
      case Req("file" :: id :: Nil, _, GetRequest) => () => FileLoader.file(id)
      case Req("getdocument" :: id :: Nil, _, GetRequest) => () => TemplateDocumentCreater.create(id)
    })
      
     


    if (DB.runQuery("select * from users where lastname = 'Administrator'")._2.isEmpty) {
      val u = User.create
      u.lastName("Administrator").role("a").password("123qwerty").email("mail@mail.org").validated(true).save
    }

    val loggedIn = If(() => User.loggedIn_? && User.currentUser.openOrThrowException("Not logged").validated.is,
      () => RedirectResponse("/user_mgt/login"))
      
    val isAdmin = If(() => User.loggedIn_? && (User.currentUser.openOrThrowException("Not logged").role.is == "a"),
      () => RedirectResponse("/user_mgt/login"))
      
    val isSecretariat = If(() => {
      User.currentUser match {
        case Full(user) => {
          val role = user.role.is
          role  == "a" || role == "s" 
        }
        case _ => false
      }
        } , () => RedirectResponse("/user_mgt/login"))
      
    val isTeacher = If(() => {
      User.currentUser match {
        case Full(user) => {
          val role = user.role.is
         role == "n" || role  == "a" || role == "s" || role == "d"
        }
        case _ => false
        }
      } , () => RedirectResponse("/user_mgt/login"))

    // Build SiteMap::
    def sitemap() = SiteMap(
      List(
        Menu("Strona główna") / "index"  / ** >> LocGroup("public"), // Simple menu form
        //Menu("Biuletyn Informacji Publicznej") / "bip" / ** >> LocGroup("public"),
        Menu("Galeria") / "gallery" / ** >> LocGroup("public"),
        Menu("Kontakt") / "contact" >> LocGroup("public"),
        Menu("Forum") / "forum" >> LocGroup("public"),
        Menu("Edycja wątku") / "editthread" / ** >> LocGroup("extra") >> loggedIn,
        Menu("Forum Post") / "forumpost" / ** >> LocGroup("extra"),
        //Menu("Dziennik") / "vregister" >> LocGroup("public"),
        Menu("Edytor bip") / "editpage" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Edytor artykułów") / "editarticle" / ** >> LocGroup("extra") >> isTeacher,
        Menu("Edytor GSF") / "gsfedit" / ** >> LocGroup("extra") >> loggedIn,
        Menu("Maile kontaktowe") / "admin" / "index" >> LocGroup("admin") >> isAdmin,
        Menu("Działy BIP") / "admin" / "pages" >> LocGroup("admin") >> isAdmin,
        Menu("Tagi aktualności") / "admin" / "tags" >> LocGroup("admin") >> isAdmin,
        Menu("Linki") / "admin" / "links" >> LocGroup("admin") >> isAdmin,
        Menu("Slajdy") / "admin" / "slides" >> LocGroup("admin") >> isAdmin,
        Menu("Skrzynka pocztowa") / "admin" / "emailconfig" >> LocGroup("admin") >> isAdmin,
        Menu("Edycja administratorów") / "admin" / "admins" >> LocGroup("admin") >> isAdmin,
        Menu("Sekretariat") / "admin" / "secretariat" >> LocGroup("admin") >> isAdmin,
        Menu("Indeksowanie Picasa") / "admin" / "picasaindex" >> LocGroup("admin") >> isAdmin,
        Menu("Eksporty stron") / "admin" / "pagesexport" >> LocGroup("admin") >> isAdmin,
        Menu("Ideksacja newsów") / "admin" / "reindexnews" >> LocGroup("admin") >> isAdmin,
        Menu("Img") / "imgstorage" >> LocGroup("extra") >> loggedIn,
        Menu("Thumb") / "thumbstorage" >> LocGroup("extra") >> loggedIn,
        Menu("File") / "filestorage" >> LocGroup("extra") >>loggedIn,
        Menu("Nauczyciele") / "secretariat" / "index" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Klasy") / "secretariat" / "classes" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Uczniowie") / "secretariat" / "pupils" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Przedmioty") / "secretariat" / "subjects" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Dzwonki") / "secretariat" / "bells" >> LocGroup("secretariat") >> isSecretariat,
        Menu("Wybór dziennika") / "teacher" / "index" / ** >> LocGroup("teacher") >> isTeacher,
        Menu("Uczniowie") / "teacher" / "pupil_data"  >> LocGroup("teacher") >> isTeacher,
        Menu("Rodzice") / "teacher" / "parent_data"  >> LocGroup("teacher") >> isTeacher,
        //Menu("Tematy") / "teacher" / "themes" >> LocGroup("teacher") >> isTeacher,
        //Menu("Obecności") / "teacher" / "absents"  >> LocGroup("teacher") >> isTeacher,
        Menu("Oceny") / "teacher" / "marks"  / ** >> LocGroup("teacher") >> isTeacher,
        Menu("Ogłoszenia") / "teacher" / "anounces"  >> LocGroup("teacher") >> isTeacher,
        Menu("Uwagi") / "teacher" / "opinions"  >> LocGroup("teacher") >> isTeacher,
        Menu("Plan") / "teacher" / "class_plan"  >> LocGroup("teacher") >> isTeacher,
        Menu("Kółka") / "teacher" / "extralessons" >> LocGroup("teacher") >> isTeacher,
        Menu("Rozkłady") / "teacher" / "themesplan" >> LocGroup("teacher") >> isTeacher,
        Menu("Dokumenty") / "teacher" / "doctemplate" / ** >> LocGroup("teacher") >> isTeacher,
        Menu("Szablon") / "teacher" / "createtemplate" / ** >> LocGroup("extra") >> isAdmin,
        Menu("Oceny") / "viewer" / "index" >> LocGroup("view") >> loggedIn,
        //Menu("Nieobecności") / "viewer" / "absents" >> LocGroup("view") >> loggedIn,
        Menu("Kursy") / "edu" /  "index" >> LocGroup("edu"),
        Menu("Lekcje") / "edu" /  "course" / ** >> LocGroup("extra"),
        Menu("Kursy")  / "resources" / "index" >> LocGroup("resource") >> isTeacher,
        Menu("Lekcje") / "resources" / "lessons" >> LocGroup("extra") >> isTeacher,  
        Menu("Hasła") / "resources" / "headwords"  >> LocGroup("resource") >> isTeacher,
        Menu("Artykuły") / "resources" / "documents" >> LocGroup("resource") >> isTeacher,
        //Menu("Testy") / "resources" / "quizes" >> LocGroup("resource") >> isTeacher,
        Menu("Zadania") / "resources" / "editquest" >> LocGroup("resource") >> isTeacher,
        Menu("Pliki") / "resources" / "files" >> LocGroup("resource") >> isTeacher,
        Menu("Filmy") / "resources" / "video" >> LocGroup("resource") >> isTeacher,
        //Menu("Prezentacje") / "resources" / "slides" >> LocGroup("resource") >> isTeacher,
        Menu("Ustawienia") / "resources" / "options" >> LocGroup("resource") >> isTeacher,
        Menu("Edycja lekcji") / "resources" / "editlesson" / ** >> LocGroup("extra") >> Hidden  >> isTeacher,
        Menu("Edycja tematów") / "resources" / "editheadword" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edycja Slajdów") / "resources" / "editslide" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edycja quizów") / "resources" / "editquiz" / ** >> LocGroup("extra") >> Hidden >> isTeacher,
        Menu("Edytuj książkę") / "resources" / "editdocument" / ** >> LocGroup("extra") >> Hidden >> isTeacher, 
         Menu("Pokaż lekcję") / "edu" / "lesson" / ** >> LocGroup("extra") >> Hidden,
        Menu("Pokaz") / "edu" / "slide" / ** >> LocGroup("extra") >> Hidden,
        Menu("Slajdy") / "edu" / "lesson-slides" / ** >> LocGroup("extra") >> Hidden,
        Menu("Hasło") / "edu" / "headword" / ** >> LocGroup("extra") >> Hidden,
        Menu("Czytaj dokument")  / "edu" / "document" / ** >> LocGroup("extra") >> Hidden,
        Menu("Quiz")  / "edu" / "quiz" / ** >> LocGroup("extra") >> Hidden,
        Menu("Wyszukiwanie") / "edu" / "search"  >> LocGroup("extra") >> Hidden,
        Menu("GC") / "admin" / "gc" >> LocGroup("admin") >> isAdmin,
        Menu("Test") / "test" >> LocGroup("extra")) :::
        // Menu entries for the User management stuff
        User.sitemap: _*)

    LiftRules.setSiteMapFunc(sitemap)
    
    LiftRules.statelessRewrite.prepend(NamedPF("ClassRewrite") {
        case RewriteRequest(
            ParsePath("gallery" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "gallery" ::  Nil, Map("id" -> id)  )	
         case RewriteRequest(
            ParsePath("bip" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "bip"  :: Nil, Map("id" -> id)  )
         case RewriteRequest(
            ParsePath("forumpost" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "forumpost" ::  Nil, Map("id" -> id)  )	
        case RewriteRequest(
            ParsePath("forum" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "forum" ::  Nil, Map("id" -> id)  )	
        case RewriteRequest(
            ParsePath("editthread" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "editthread" ::  Nil, Map("id" -> id)  )
        case RewriteRequest(
            ParsePath("editpage" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "editpage" ::  Nil, Map("id" -> id)  )
         case RewriteRequest(
            ParsePath("editarticle" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "editarticle" ::  Nil, Map("id" -> id)  )
		case RewriteRequest(
            ParsePath("teacher" :: "index" :: classSchool :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "teacher" :: "index" :: Nil, Map("class" -> classSchool)  )		
        case RewriteRequest(
            ParsePath("teacher" :: "marks" :: subjectId :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "teacher" :: "marks" :: Nil, Map("idS" -> subjectId  )	)
        case RewriteRequest(
            ParsePath("gsf" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "gsf"  :: Nil, Map("id" -> id)  )	
         case RewriteRequest(
            ParsePath("sps" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "sps"  :: Nil, Map("id" -> id)  )	
        case RewriteRequest(
            ParsePath("gsfedit" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
            "gsfedit"  :: Nil, Map("id" -> id)  )
         case RewriteRequest(
            ParsePath("teacher" :: "doctemplate" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
           "teacher" :: "doctemplate"  :: Nil, Map("id" -> id)  )
          case RewriteRequest(
            ParsePath("teacher" :: "createtemplate" :: id :: Nil, _, _,_), _, _) =>
          RewriteResponse(
           "teacher" :: "createtemplate"  :: Nil, Map("id" -> id)  )
            case RewriteRequest(
            ParsePath("index" :: w :: Nil, _, _,_), _, _) =>
          RewriteResponse(
           "index"  :: Nil, Map("w" -> w)  )
           case RewriteRequest(
        ParsePath("resources" :: "editslide" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "editslide" :: Nil, Map("id" -> subjectId))
      case RewriteRequest(
        ParsePath("resources" :: "editheadword" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "editheadword" :: Nil, Map("id" -> subjectId))
      case RewriteRequest(
        ParsePath("resources" :: "editdocument" :: entryId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "editdocument" :: Nil, Map("id" -> entryId))
      case RewriteRequest(
        ParsePath("resources" :: "editquiz" :: entryId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "editquiz" :: Nil, Map("id" -> entryId))
      case RewriteRequest(
        ParsePath("resources" :: "editlesson" :: entryId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "editlesson" :: Nil, Map("id" -> entryId))
      case RewriteRequest(
        ParsePath("resources" :: "lessons" :: courseId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "resources" :: "lessons" :: Nil, Map("id" -> courseId))
      case RewriteRequest(
        ParsePath("edu" :: "slide" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "edu" :: "slide" :: Nil, Map("id" -> subjectId))
      case RewriteRequest(
        ParsePath("edu" :: "lesson-slides" :: lessonId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
         "edu" ::  "lesson-slides" :: Nil, Map("id" -> lessonId))
      case RewriteRequest(
        ParsePath("edu" :: "headword" :: subjectId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "edu" :: "headword" :: Nil, Map("id" -> subjectId))
      case RewriteRequest(
        ParsePath("edu" :: "quiz" :: quizId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
          "edu" :: "quiz" :: Nil, Map("id" -> quizId))
      case RewriteRequest(
        ParsePath("edu" :: "course" :: lessonId :: Nil, _, _, _), _, _) =>
        RewriteResponse(
         "edu" ::  "course" :: Nil, Map("id" -> lessonId))
      })

    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    LiftRules.passNotFoundToChain = true
    LiftRules.maxMimeSize = 16 * 1024 * 1024
    LiftRules.maxMimeFileSize = 16 * 1024 * 1024
    
    { new MailConfig().autoConfigure() }

    LiftRules.liftRequest.append {
      case Req("extra" :: _, _, _) => false
    }

    S.addAround(DB.buildLoanWrapper)
  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
