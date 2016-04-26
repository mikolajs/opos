package pl.edu.osp.api

import net.liftweb._
import common._
import http._
import pl.edu.osp.model.edu.{Slide, SlideContent}

import java.util.zip.{ZipOutputStream, ZipEntry, ZipInputStream}
import java.io.{FileOutputStream, File, FileInputStream, ByteArrayInputStream, ByteArrayOutputStream}
import com.mongodb.gridfs._
import net.liftweb.mongodb._
import net.liftweb.util.Helpers._
import scala.xml._


object ImportExportSlides {

  def zip(): Box[LiftResponse] = {

    val baos = mkExport
    val inputStream = new ByteArrayInputStream(baos.toByteArray)
    if(inputStream.available() < 10){
      Full(NotFoundResponse("Not found"))
    }
    else {
      Full(StreamingResponse(inputStream, () => (), inputStream.available().toLong,
        ("Content-Type", "application/zip") :: ("Content-Disposition", "attachment; filename=\"export.zip\"") :: Nil,
        Nil, 200))
    }
  }

  def loadZip(fileBox:Box[FileParamHolder]):Boolean = {

      if(isCorrect(fileBox)) {
        val fph = fileBox.openOrThrowException("Imposible, file must be present!")
        val bArray = fph.file
        val bais = new ByteArrayInputStream(bArray)
        val baos = new ByteArrayOutputStream()
        val zis = new ZipInputStream(bais)
        var entry = zis.getNextEntry
        val buffer = new Array[Byte](4096)
        var len = 1
        var name = ""
        val idChange = scala.collection.mutable.Map[String, String]()
        var slidesStr = ""
        while(entry != null) {


          //while(len > 0) {
          //    len = zis.read(buffer)
          //  baos.write(buffer, 0, len)???? un
          //}
          name = entry.getName
          println(s"Zip entry name: $name" )
          /*if(name == "export.xml") {
            slidesStr = baos.toString("UTF-8")
          }
          else {
            val extension = name.split('.').last.toLowerCase
            if(extension == "png" || extension == "jpg" || extension == "gif" || extension == "jpeg")
                idChange(name.split('.').head) = saveImage(baos.toByteArray, name)
          }
          */
          baos.reset()

          zis.closeEntry()
          entry = zis.getNextEntry
        }
        zis.closeEntry()
        bais.close()
        //insertSlides(slidesStr, idChange)
        zis.close()
      }
    true
    }


  private def mkExport = {
    val baos = new ByteArrayOutputStream();
    val zos = new ZipOutputStream(baos);
    val slides = Slide.findAll.map(sh => {
      val sc = SlideContent.find(sh.slides).getOrElse(SlideContent.create)
      val reg = """ src=\"(.*)\"""".r
      val srcs = reg.findAllIn(sc.slides)
      var imgs:List[String] = Nil
      while(srcs.hasNext) {
        val ar = srcs.next.split('"')
        if(ar.length > 1) imgs = ar(1) :: imgs
      }
      imgs.foreach(img => {
        if(img.startsWith("/") && !img.startsWith("//")) {
          val p = img.split('/')
          if(p.length > 2) {
            println("++++ " + p(2))
            val array = p(2).split('.')
            val outStream = getImages(array(0))
            val e = new ZipEntry(array.mkString("."))
            val binary = outStream.toByteArray()
            if(binary.length > 100) {
              zos.putNextEntry(e)
              zos.write(binary, 0, binary.length)
              zos.closeEntry()
            } else println("!!!!! emtpy output stream" )
          }
        }
      })
      <slide>
        <title>{ sh.title }</title>
        <content>{Unparsed(sc.slides.replaceAll("&.{2,4};", ""))}</content>
      </slide>
    })

    val html = <slides>{ slides }</slides>
    val xmlStr = toXML(html.toString)
    val xml = <root>{xmlStr}</root>
    val e = new ZipEntry("slides.xml")
    zos.putNextEntry(e)
    val ar = xml.toString.getBytes
    zos.write(ar, 0, ar.length)
    zos.closeEntry()
    zos.close()
    baos
  }

  private def getImages(id: String) = {
    var outputStream = new ByteArrayOutputStream()
    if (id.nonEmpty) {
      MongoDB.use(DefaultMongoIdentifier) { db =>
        val fs = new GridFS(db)
        val foundFile = fs.findOne(new org.bson.types.ObjectId(id))
        if (foundFile == null) println("null!!!")
        else {
          foundFile.writeTo(outputStream)
        }
      }
    }
    outputStream
  }

  private def toXML(str:String) = {
    import org.jsoup.Jsoup
    import org.jsoup.safety.Whitelist
    import scala.collection.JavaConversions._
    val par = org.jsoup.parser.Parser.xmlParser()
    val doc = Jsoup.parse(str, "", par)
    doc.toString()
  }

  private def isCorrect(fileHold:Box[FileParamHolder]) = {

    fileHold match {

      case Full(FileParamHolder(_, mime, _, _)) => {
        if(mime == "application/zip") true
        else false
      }
      case Full(_) => {
        println("Error, File import failed")
        false
      }
      case _ => {
        println("Error, File not fount")
        false
      }
    }
  }

  private def saveImage(data:Array[Byte], name:String) = {

    val mimeType = "image/" + name.last.toLower
    var fileId = ""
    MongoDB.use(DefaultMongoIdentifier) {
      db =>
        val fs = new GridFS(db)
        val inputFile = fs.createFile(new ByteArrayInputStream(data))
        inputFile.setContentType(mimeType)
        inputFile.setFilename(name)
        inputFile.save
        fileId = inputFile.getId().toString()
    }
    fileId
  }

  private def insertSlides(slidesStr: String, idChange: scala.collection.mutable.Map[String, String]) {

    var newSliesStr = slidesStr
    val reg = """src="(.*)" """.r
    val hits = reg.findAllIn(slidesStr)
    hits.foreach(hit => {
      val ar = hit.split('"')
      if (ar.length > 1) {
        val src = ar(1)
        if (!src.startsWith("http") && !src.startsWith("//")) {
          val paths = src.split('/')
          if(paths.length > 2){
            val a = paths(2).split('.')
            if(a.length > 0){
              val id = a(0)
              if(idChange.contains(id)){
                newSliesStr = newSliesStr.replace(id, idChange(id))
              }
            }
          }
        }
      }
    })
    val slides = XML.loadString(newSliesStr)
    ////dalej odczyt slajdów i zapis do bazy
  }

}