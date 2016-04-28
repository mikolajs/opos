
import java.util.zip.{ZipOutputStream, ZipEntry, ZipInputStream}
import java.io.{FileOutputStream, File, FileInputStream, ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.file.Files;
import java.nio.file.Paths;





val file = new File("./test.zip")
val inputFile = new FileInputStream(file)

val allSize = inputFile.available
val inpArr = new Array[Byte](allSize)
inputFile.read(inpArr)
val bais = new ByteArrayInputStream(inpArr)
inputFile.close()
val baos = new ByteArrayOutputStream()
val zis = new ZipInputStream(bais)
val buffer = new Array[Byte](4096)

var len = 1
var name = ""
val idChange = scala.collection.mutable.Map[String, String]()
var slidesStr = ""

var entry = zis.getNextEntry
while(entry != null) {
  name = entry.getName
  val size = entry.getSize
  println(s"Zip entry name: $name, size: $size" )
  len = 1
  while(size > 0 && len > 0) {
    len = zis.read(buffer)
    if(len > 0) baos.write(buffer, 0, len)
  }
  val extension = name.split('.').last.toLowerCase
  //if(extension == "png" || extension == "jpg" || extension == "gif" || extension == "jpeg")
   if(!entry.isDirectory) Files.write(Paths.get("./"+name), baos.toByteArray);

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
//bais.close()
//insertSlides(slidesStr, idChange)
zis.close()
