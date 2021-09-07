package eu.brosbit.opos.lib

//import java.io.{BufferedInputStream, ByteArrayOutputStream, FileInputStream, FileOutputStream}
import java.nio.charset.StandardCharsets
import java.util.zip.{ZipEntry, ZipInputStream, ZipOutputStream}
import java.io.{ByteArrayOutputStream, ByteArrayInputStream}

trait ZipFilePackager {
  /**
   *  Give Zip from text based documents
   * @param documents docName -> content
   * @return bytes of zipped document
   */
  def toZip(documents:Map[String, String]):Array[Byte]

  /**
   * Conver zip data bytes to  documents to text documents
   * @param array data
   * @return Map docName -> content
   */
  def fromZip(array: Array[Byte]):Map[String, String]
}

class ZipFilePackagerImpl extends ZipFilePackager {

  override def toZip(documents: Map[String, String]): Array[Byte] = {

    val out = new ByteArrayOutputStream()
    val zip = new ZipOutputStream(out)
    zip.setMethod(ZipOutputStream.DEFLATED)

    val keys = documents.keys
    keys.foreach(key => {
      val bytes = documents(key).getBytes("UTF8")
      val entry = new ZipEntry(key)
      entry.setSize(bytes.length)
      entry.setCompressedSize(bytes.length)
      zip.putNextEntry(entry)
      zip.write(bytes, 0, bytes.length)
      zip.closeEntry()
    })
    val bytes = out.toByteArray
    zip.flush()
    zip.finish()
    zip.close()
    bytes
  }

  override def fromZip(array: Array[Byte]): Map[String, String] = {

    val input = new ByteArrayInputStream(array)

    val zip = new ZipInputStream(input)

    var map = Map[String, String]()
    var entry:ZipEntry = null
    val buffer = new Array[Byte](10000)
    do {
      entry = zip.getNextEntry
      if(entry != null) {
        val out = new ByteArrayOutputStream()
        val key = entry.getName
        var len = 0
        do {
          len = zip.read(buffer)
          if(len > 0) {
            out.write(buffer, 0, len)
          }
        }
        while(len > 0)
        val b = out.toByteArray
        map = map + (key -> new String(b, StandardCharsets.UTF_8))
        out.close()
      }
    } while (entry != null)
    input.close()
    zip.close()
    map
  }
}

object ZipFilePackager {
  def apply(): ZipFilePackager = new ZipFilePackagerImpl()
}
