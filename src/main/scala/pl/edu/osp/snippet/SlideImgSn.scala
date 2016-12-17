package pl.edu.osp.snippet

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.{ByteArrayOutputStream, ByteArrayInputStream}
import javax.imageio.ImageIO
import com.mongodb.gridfs.GridFS
import net.liftweb.mongodb._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.pl.edu.osp.model.page._
import _root_.pl.edu.osp.model._
import net.liftweb.http.{FileParamHolder, S, SHtml}
import Helpers._
import _root_.net.liftweb.json.JsonDSL._

class SlideImgSn {
  val user = User.currentUser.openOr(User.create)
  val userId = user.id.get
  var telebim = S.param("t").openOr("A")
   //teacher checked at Boot
  def edit() = {
    var time = ""
    var reload = ""

    var slides = if(user.isAdmin_? ) ImageSlides.findAll(
        ("code" -> telebim), ("order" -> 1))
        .map(_.src).mkString(";")
      else ImageSlides.findAll(
        ("code" -> telebim)~("author" -> userId),
        ("order" -> 1))
        .map(_.src).mkString(";")

    var fileHold: Box[FileParamHolder] = Empty
    var picturesDel = ""
    val timesData = MapExtraData.getMapData(telebim)
     if(timesData.isDefinedAt("t")) time = timesData("t")
     if(timesData.isDefinedAt("r")) reload = timesData("r")
    def run()  {
      //delete files
      val toDel = picturesDel.split(";")
      println("TO del files: " + toDel.mkString(", "))
      toDel.foreach(src => {
        ImageSlides.findAll("src" -> src).foreach(
          imgS =>
          if(imgS.author == userId || user.isAdmin_? ) imgS.delete)
      })

      //add new files
      val files : List[FileParamHolder] = S.request.map(_.uploadedFiles) openOr Nil
      val newImages = files.map(f =>  f.fileName )
      println("======Dodane: " + newImages.mkString(", "))
      var i = 1
      files.foreach(f => {
        val mime = if (f.mimeType.startsWith("image/"))
                      "." + f.mimeType.split('/').last.toLowerCase()
                   else ""
        if(!mime.isEmpty) {
          var src = ""
          val imageBuf: BufferedImage = ImageIO.read(new ByteArrayInputStream(f.file))
          val resizedImageBuf = resizeImageWithProportion(imageBuf, 1920, mime.substring(1))
          val outputStream = new ByteArrayOutputStream()
          ImageIO.write(resizedImageBuf, mime.substring(1), outputStream)
          val bais = new ByteArrayInputStream(outputStream.toByteArray())
          MongoDB.use(DefaultMongoIdentifier) {
            db =>
              val fs = new GridFS(db)
              val inputFile = fs.createFile(bais)
              inputFile.setContentType(mime) //check if correct
              inputFile.setFilename(f.fileName)
              inputFile.save
              src = "/img/" + inputFile.getId().toString() + mime
          }
          val imgSlide = ImageSlides.create
          imgSlide.code = telebim
          imgSlide.src = src
          imgSlide.order = i
          imgSlide.author = userId
          imgSlide.save
          i += 1
        }
      })
      MapExtraData.setMapData(telebim, Map("r" -> reload, "t" -> time))

    }
     val telebims = List(("A","A"), ("B", "B"), ("C", "C"), ("D","D"))

      "#pictures" #> SHtml.text(slides, slides = _) &
      "#telebim" #> SHtml.select(telebims, Full(telebim), telebim = _) &
     "#time" #> SHtml.text(time, time = _) &
     "#reload" #> SHtml.text(reload, reload = _ ) &
      "#picturesDel" #> SHtml.text(picturesDel, picturesDel = _) &
      "#files" #> SHtml.fileUpload(fileUploaded => fileHold = Full(fileUploaded)) &
      "#save" #> SHtml.submit("Zapisz", run)
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

  
  private def getImageType(mimeType:String) = if (mimeType == "jpeg" || mimeType == "jpg") BufferedImage.TYPE_INT_RGB
  else BufferedImage.TYPE_INT_ARGB
}