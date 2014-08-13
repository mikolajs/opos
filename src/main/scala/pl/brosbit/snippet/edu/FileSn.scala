/*
 * Copyright (C) 2013   Mikołaj Sochacki mikolajsochacki AT gmail.com
 *   epodrecznik.edu.pl
 *  License: AGPL, see <http://www.gnu.org/licenses/>.
 */

package pl.brosbit.snippet.edu

    import scala.xml.{ NodeSeq, Text }
import _root_.net.liftweb._
import util._
import common._
import Helpers._
import http.{ S, SHtml, FileParamHolder, RequestVar }
import java.util.Date
import java.awt.image.BufferedImage
import java.awt.Image
import javax.imageio.ImageIO
import java.io.{ File, ByteArrayInputStream, FileOutputStream }
import com.mongodb.gridfs._
import mongodb._
import java.io.ByteArrayOutputStream
    /**
     * snipet ma dostarczać grafikę na stronę oraz obsługiwać zapis zdjęć i dostarczenie
     * ich do edytora - w przysłości obsługa plików statycznych
     */
    class FileSn {

      object linkpath extends RequestVar[String]("")
     
      
      def addBigImg() = addImg(800)
      
      def addSmallImg() = addImg(300)
      
      private def addImg(maxSize:Int)= {
        var fileHold: Box[FileParamHolder] = Empty
        var mimeType = ""
        var mimeTypeFull = ""
         def isCorrect = fileHold match {
          case Full(FileParamHolder(_, mime, _, data)) if mime.startsWith("image/") => {
            mimeType = "." + mime.split("/")(1)
            mimeTypeFull = mimeType.toString()
            S.notice(mime.toString) 
            if (mimeType == ".png" || mimeType == ".jpeg" || mimeType == ".gif") true
            else {
              println("Nieprawidłowy format pliku!")
              S.error("Nieprawidłowy format pliku!")
              false
            }
          }
          case Full(_) => {
            println("Nieprawidłowy format pliku!")
            S.error("Nieprawidłowy format pliku!")
            false
          }
          case _ => {
            println("Brak pliku")
            S.error("Brak pliku?")
            false
          }
        }
        
        def save() {
          if (isCorrect){
           var fileName =  fileHold.get.fileName.split('.').dropRight(1).mkString
           if (fileName.isEmpty) fileName = scala.util.Random.nextLong.toString
           var imageBuf: BufferedImage = ImageIO.read(new ByteArrayInputStream(fileHold.get.file))
           var mime = mimeType(1)
           val imBox:Box[BufferedImage] = getImageBox(maxSize, imageBuf,mime)
           var outputStream = new ByteArrayOutputStream()
           ImageIO.write(imBox.get, mimeType.substring(1),outputStream)
           val inputStream = new ByteArrayInputStream(outputStream.toByteArray())
           MongoDB.use(DefaultMongoIdentifier) { db =>
             val fs = new GridFS(db)
             val inputFile = fs.createFile(inputStream)
             inputFile.setContentType(mimeTypeFull)
             inputFile.setFilename(fileName + mimeType)
             inputFile.save
             linkpath("/image/" + inputFile.getId().toString() + mimeType)   
           }
          }
          
        } 
        
        "img [src]" #> linkpath.is &
        "#linkpath" #> SHtml.text(linkpath.is,x=>x,"type"->"hidden") &
        "#file" #> SHtml.fileUpload(x => fileHold = Full(x)) &
        "#submit" #> SHtml.submit("Dodaj!", save) 
      } 
      
      
      private def getImageBox(maxSize:Int, imageBufIn: BufferedImage,mime:Char):Box[BufferedImage] = {
         var imageBuf: BufferedImage = imageBufIn
            //tutaj przeskalowanie
            var imBox: Box[BufferedImage] = Empty
            val w = imageBuf.getWidth
            val h = imageBuf.getHeight
            val bufferedImageTYPE = if(mime == 'j') BufferedImage.TYPE_INT_RGB else BufferedImage.TYPE_INT_ARGB
            if (w > maxSize || h > maxSize) {
              if (w > h) {
                val im: java.awt.Image = imageBuf.getScaledInstance(maxSize, (h.toDouble * maxSize.toDouble / w.toDouble).toInt, Image.SCALE_SMOOTH)
                //val graf2D = imageBuf.createGraphics
                //graf2D.scale(1.0, 500.0/w.toDouble)
                //imageBuf.getGraphics.translate(500, (h.toDouble * 500.0/w.toDouble).toInt)
                imBox = Full(new BufferedImage(maxSize, (h.toDouble * maxSize.toDouble / w.toDouble).toInt, bufferedImageTYPE))
                imBox.get.getGraphics.drawImage(im, 0, 0, null)
                //imageBuf = im.asInstanceOf[BufferedImage]
              } else {
                val im: java.awt.Image = imageBuf.getScaledInstance((w.toDouble * maxSize.toDouble / h.toDouble).toInt, maxSize, Image.SCALE_SMOOTH)
                imBox = Full(new BufferedImage((w.toDouble * maxSize.toDouble / h.toDouble).toInt, maxSize, bufferedImageTYPE))
                imBox.get.getGraphics.drawImage(im, 0, 0, null)
              }
            } else {
              imBox = Full(new BufferedImage(w, h, bufferedImageTYPE))
              imBox.get.getGraphics.drawImage(imageBuf, 0, 0, null)
            }
         imBox
      }
      
      
       lazy val serverPath = {
        S.request match {
          case Full(r) => r.hostAndPath
          case _ => "add host name"
        }
      }

     
    }
    
    