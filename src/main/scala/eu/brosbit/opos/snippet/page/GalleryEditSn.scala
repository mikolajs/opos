package eu.brosbit.opos.snippet.page

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File}
import java.nio.file.{Files, Paths}
import javax.imageio.ImageIO

import net.liftweb.common.{Box, Empty, Full}
import net.liftweb.http.{FileParamHolder, S, SHtml}
import net.liftweb.util.Helpers._
import eu.brosbit.opos.model.User
import eu.brosbit.opos.model.page.{Gallery, Photo}
import _root_.net.liftweb.json.JsonDSL._

class GalleryEditSn {
  val user = User.currentUser.openOrThrowException("No USER!")
  var videoItems: scala.collection.mutable.ListBuffer[File] = scala.collection.mutable.ListBuffer()
  println("start index photos")
  lazy val pathGallery = "/home/osp/"
  lazy val  pathMedia =  S.hostAndPath.split('/').take(3)
    .mkString("/").split(':').take(2).mkString(":")  + "/osp/"

 def listGallery() = {
   val gal = if(user.role.is == "a")  Gallery.findAll("owner" -> user.id.is)
      else Gallery.findAll("owner" -> user.id.is)
   "a" #> gal.map(g =>
     <a href={"/galleryedit/"+ g._id.toString}>{g.title }<br/><small>{g.description}</small></a>)
 }

 def editGallery() = {
   val id = S.param("id").getOrElse("0")
   val gal = Gallery.find(id).getOrElse(Gallery.create)

   var fileHold: Box[FileParamHolder] = Empty
   var picturesDel = ""

   var title = gal.title
   var descript = gal.description
   var pictures = gal.photos.map(ph =>
     pathMedia + gal._id.toString + "/" + ph.thumbnail).mkString(";")


   def save() {
     if(gal.owner == 0 || gal.owner == user.id.get || user.isAdmin_?){
       if(title.trim.isEmpty) return
       val files : List[FileParamHolder] = S.request.map(_.uploadedFiles) openOr Nil
       val newPhotos = files.map(f =>
         Photo("t_" + f.fileName, f.fileName )).filterNot(p => p.full.trim.isEmpty)
       println("======Dodane: " + newPhotos.mkString)
       gal.title = title.trim
       gal.owner = user.id.get
       gal.description = descript.trim
       gal.photos = newPhotos ++ rmFromList(gal.photos, picturesDel)
       gal.save
       deletePicturesFromDisk(getPhotosFromPicturesStr(picturesDel), gal._id.toString)
       savePicturesOnDisk(files, gal._id.toString)
     }
     S.redirectTo("/galleryedit/"+ gal._id.toString)
   }

   def delete() {
     if(gal.owner == 0 || gal.owner == user.id.is || user.isAdmin_?) {
       deleteDirOfPictures(gal._id.toString )
       gal.delete
       S.redirectTo("/galleries")
     }
   }

   "#title" #> SHtml.text(title, title = _) &
   "#descript" #> SHtml.textarea(descript, descript = _) &
   "#pictures" #> SHtml.text(pictures, pictures = _) &
   "#picturesDel" #> SHtml.text(picturesDel, picturesDel = _) &
   "#files" #> SHtml.fileUpload(fileUploaded => fileHold = Full(fileUploaded)) &
   "#save" #> SHtml.submit("Zapisz", save) &
   "#delete" #> SHtml.submit("Usuń", delete)
 }

 private def getPhotosFromPicturesStr(pict:String) = {
   pict.split(";").filter(!_.isEmpty).map(p => {
     Photo(p, p.substring(2))
   }).toList
  }

 private def rmFromList(photos:List[Photo], toRm: String) = {
    val delPh  = getPhotosFromPicturesStr(toRm)
    photos.filterNot(p => delPh.exists(dp => dp.full == p.full))
  }

 private def deletePicturesFromDisk(photos:List[Photo], dir: String) {
    val directory = new File(pathGallery + dir)
    if(directory.exists()) directory.listFiles().foreach(fil => {
      if(fil.exists() && photos.exists(p => fil.getName == p.full || fil.getName == p.thumbnail)) fil.delete()
    })
 }

 private def deleteDirOfPictures(dir: String) {
    val directory = new File(pathGallery + dir)
    if(directory.isDirectory) {
      println("======== delete dir " + dir)
      directory.listFiles().foreach(f => f.delete())
      directory.delete()
    }
 }

 private def savePicturesOnDisk(photos:List[FileParamHolder], dir:String) {
   val directory = new File(pathGallery + dir)
   if(!directory.exists()) directory.mkdir()
   photos.filterNot(p => p.fileName.trim.isEmpty).foreach(ph => {

    println("========= wczytany plik: " + ph.fileName)
    val fFull = new File(directory.getAbsolutePath + "/" + ph.fileName)
    val fThumb =  new File(directory.getAbsolutePath + "/t_" + ph.fileName)
     if(!fFull.getName.trim.isEmpty){
       if(!fFull.exists()) fFull.createNewFile()
       if(!fThumb.exists()) fThumb.createNewFile()
       val mime = readImage(ph)
       if(mime != "ERROR") {
         val mimeLast = mime.split('/').last
         val imageBuf: BufferedImage = ImageIO.read(new ByteArrayInputStream(ph.file))
         val resizedImageBuf = resizeImageWithProportion(imageBuf, 800, mimeLast)
         val outputStream = new ByteArrayOutputStream()
         ImageIO.write(resizedImageBuf, mimeLast, outputStream)
         val path = Paths.get(fFull.getAbsolutePath)
         Files.write(path, outputStream.toByteArray())
         outputStream.close()
         val resizedImageBuf2 = resizeImageThumbSquare(imageBuf, 100, mimeLast)
         val outputStream2 = new ByteArrayOutputStream()
         ImageIO.write(resizedImageBuf2, mimeLast, outputStream2)
         val path2 = Paths.get(fThumb.getAbsolutePath)
         Files.write(path2, outputStream2.toByteArray())
         outputStream2.close()
       }
     }
   })
 }

  private def readImage(fileHold: FileParamHolder): String = {
    fileHold match {
      case FileParamHolder(_, mime, _, data) => {
        if (mime.startsWith("image/")) {
          println("mimetype is good " + mime)
          mime
        } else {
          println("mimetype is wrong")
          "ERROR"
        }
      }
      case _ => {
        println("Brak pliku")
        "ERROR"
      }
    }
  }

  private def resizeImageWithProportion(imageBuf: BufferedImage, maxDimension: Int, mime:String): BufferedImage = {
    val bufferedImageTYPE = getImageType(mime)
    var imageBufferOut: BufferedImage = imageBuf
    val width = imageBuf.getWidth
    val height = imageBuf.getHeight
    if (width > maxDimension || height > maxDimension) {
      if (width > height) {
        val image: java.awt.Image = imageBuf.getScaledInstance(maxDimension,
          (height.toDouble * maxDimension.toDouble / width.toDouble).toInt, Image.SCALE_SMOOTH)
        imageBufferOut = new BufferedImage(maxDimension,
          (height.toDouble * maxDimension.toDouble / width.toDouble).toInt, bufferedImageTYPE)
        imageBufferOut.getGraphics.drawImage(image, 0, 0, null)
        //imageBuf = im.asInstanceOf[BufferedImage]
      } else {
        val image: java.awt.Image =
          imageBuf.getScaledInstance((width.toDouble * maxDimension / height.toDouble).toInt,
            maxDimension, Image.SCALE_SMOOTH)
        imageBufferOut = new BufferedImage((width.toDouble * maxDimension.toDouble / height.toDouble).toInt,
          maxDimension, bufferedImageTYPE)
        imageBufferOut.getGraphics.drawImage(image, 0, 0, null)
      }
    } else {
      imageBufferOut = new BufferedImage(width, height, bufferedImageTYPE)
      imageBufferOut.getGraphics.drawImage(imageBuf, 0, 0, null)
    }
    imageBufferOut
  }

  private def resizeImageThumbSquare(imageBuf: BufferedImage, dimension: Int, mime:String): BufferedImage = {
    val bufferedImageTYPE = getImageType(mime)
    val width = imageBuf.getWidth
    val height = imageBuf.getHeight
    var imageBufferOut: BufferedImage = if(width > height) {
      val start = (width - height) / 2
      imageBuf.getSubimage(start, 0,  width - 2*start, height)
    } else {
      val start = (height - width) / 2
      imageBuf.getSubimage(0, start,  width, height - 2*start)
    }

    val image: java.awt.Image = imageBufferOut.getScaledInstance(dimension, dimension, Image.SCALE_SMOOTH)
    imageBufferOut = new BufferedImage(dimension, dimension, bufferedImageTYPE)
    imageBufferOut.getGraphics.drawImage(image, 0, 0, null)


    imageBufferOut
  }


  private def getImageType(mimeType:String) = if (mimeType == "jpeg" || mimeType == "jpg") BufferedImage.TYPE_INT_RGB
  else BufferedImage.TYPE_INT_ARGB
}
