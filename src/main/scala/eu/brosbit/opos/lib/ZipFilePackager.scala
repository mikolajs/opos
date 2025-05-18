package eu.brosbit.opos.lib

//import java.io.{BufferedInputStream, ByteArrayOutputStream, FileInputStream, FileOutputStream}
import java.nio.charset.StandardCharsets
import java.util.zip.{ZipEntry, ZipInputStream, ZipOutputStream, Deflater, Inflater}
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
      val bytes = documents(key).getBytes(StandardCharsets.UTF_8)
      val compress = new Deflater()
      compress.setInput(bytes)
      val zipped = new Array[Byte](bytes.length)
      compress.finish()
      val size = compress.deflate(zipped)
      println(new String(zipped, 0, size, "UTF-8") + "\nSize: " + size)
      checkInflate(zipped.take(size))
      val entry = new ZipEntry(key+".json")
      entry.setSize(bytes.length)
      entry.setCompressedSize(size)
      zip.putNextEntry(entry)
      zip.write(zipped, 0, size)
      zip.closeEntry()
    })
    val bytes = out.toByteArray
    zip.flush()
    zip.finish()
    zip.close()
    testReadZip()
    bytes
  }

  override def fromZip(array: Array[Byte]): Map[String, String] = {

    val input = new ByteArrayInputStream(array)

    val zip = new ZipInputStream(input)

    var map = Map[String, String]()
    var entry:ZipEntry = null
    val buffer = new Array[Byte](100000)
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

  private def checkInflate(zipped: Array[Byte]):Unit = {
    import java.util.zip.Inflater
    val decompresser: Inflater = new Inflater
    decompresser.setInput(zipped, 0, zipped.length)
    val result: Array[Byte] = new Array[Byte](zipped.length*10)
    val resultLength: Int = decompresser.inflate(result)
    decompresser.end()
    println("Zipped length: " + zipped.length)
     println("Decompressed length: " + resultLength + "\n" + new String(result, 0, resultLength, StandardCharsets.UTF_8))
  }

  private def testReadZip(): Unit ={
    val path = "/home/ms/Pobrane/export.zip"
    val ss = scala.io.Source.fromFile(path, "UTF_8")
    val data = ss.map(_.toByte).toArray
    println("Test Read ZIP: ")
    println(data.length)
    println(new String(data, 0, data.length, StandardCharsets.UTF_8))
  }
}

object ZipFilePackager {
  def apply(): ZipFilePackager = new ZipFilePackagerImpl()
}
