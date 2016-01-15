package pl.edu.osp.snippet.admin

import net.liftweb.http.{FileParamHolder, S, SHtml}
import  net.liftweb.util.Helpers._
import net.liftweb.common.{Box, Full, Empty}
import java.io.ByteArrayOutputStream
import java.nio.file.{Paths, Files}

/**
 * Created by ms on 04.01.16.
 */
class AdminLogoSn {
  def show() = {
    val  pathMedia =  S.hostAndPath.split('/').take(3).mkString("/").split(':').take(2).mkString(":")  + "/osp/"
    val logoPath = "/home/osp/logo.png"
    val faviconPath = "/home/osp/favicon.png"
    var fphBoxLogo:Box[FileParamHolder] = Empty
    var fphBoxFav:Box[FileParamHolder] = Empty
    def save() {
      //println("Logo action!!!!!!!!!!!!!!!!!!")
      if(!fphBoxLogo.isEmpty) {
        val fph = fphBoxLogo.openOrThrowException("Niemożliwe")
        println("LOGO Start " + fph.fileName)
        if(fph.mimeType.toLowerCase == "image/png") {
          val outputStream = new ByteArrayOutputStream()
          outputStream.write(fph.file)
          val path = Paths.get(logoPath)
          //println("LOGO "  + path.toString)
          Files.write(path, outputStream.toByteArray)
        }
      }
      if(!fphBoxFav.isEmpty) {
        val fph = fphBoxFav.openOrThrowException("Niemożliwe")
        println("favicon Start " + fph.fileName)
        if(fph.mimeType.toLowerCase == "image/png") {
          val outputStream = new ByteArrayOutputStream()
          outputStream.write(fph.file)
          val path = Paths.get(faviconPath)
          //println("Favicon " + path.toString)
          Files.write(path, outputStream.toByteArray)
        }
      }


    }

    "#logoImg [src]" #> (pathMedia + "logo.png") &
    "#faviconImg [src]" #> (pathMedia + "favicon.png") &
    "#logo" #> SHtml.fileUpload(fileParamHold => fphBoxLogo = Full(fileParamHold))  &
    "#favicon" #> SHtml.fileUpload(fileParamHold => fphBoxFav = Full(fileParamHold)) &
    "#save" #> SHtml.submit("Zapisz", save)
  }
}
