package eu.brosbit.opos.lib

import java.nio.charset.StandardCharsets
import java.util.zip.{CRC32, Deflater, ZipEntry, ZipInputStream, ZipOutputStream}
import com.sun.xml.messaging.saaj.util.ByteOutputStream

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.nio.file.{Files, Paths}

class Zipper() {
  private val dir = "dane/"

  //not used yet
  def toZipOneFile(text: String, file:String): Array[Byte] = {
    val out = new ByteOutputStream()
    val zip = new ZipOutputStream(out)
    zip.setMethod(ZipOutputStream.DEFLATED)
    val bytes = text.getBytes(StandardCharsets.UTF_8)
    //val zipped = compressor.mkCompress(bytes)
    val entry = new ZipEntry(file)
    entry.setSize(bytes.length)
    entry.setCompressedSize(bytes.length)
    zip.putNextEntry(entry)
    zip.write(bytes, 0, bytes.length)
    zip.closeEntry()
    val zipData = out.getBytes
    zip.flush()
    zip.finish()
    zip.close()
    zipData
  }

  //not used yet
  def toZipFilesFromBytes(map: Map[String, Array[Byte]]): Array[Byte] = {
    val out = new ByteOutputStream()
    val zip = new ZipOutputStream(out)
    zip.setMethod(ZipOutputStream.DEFLATED)
    zip.setLevel(Deflater.NO_COMPRESSION)
    map.keys.map(key => {
      val bytes = map(key)
      val entry = new ZipEntry(key)
      zip.putNextEntry(entry)
      zip.write(bytes)
      zip.flush()
      // zip.write(bytes, 0, bytes.length)
      zip.closeEntry()
    })
    val zipData = out.getBytes
    out.close()
    zip.close()
    zipData
  }
  def toZipJsonAndBinary(jsonMap: Map[String, String], binMap: Map[String, Array[Byte]]): Array[Byte] = {
    val out = new ByteOutputStream()
    val zip = new ZipOutputStream(out)
    //TODO: trying to compress stored
    zip.setMethod(ZipOutputStream.DEFLATED)
    zip.setLevel(Deflater.DEFLATED)
    //val zipped = compressor.mkCompress(bytes)
    addTextFiles(jsonMap, zip)
    addBinFiles(binMap, zip)

    zip.flush()
    //zip.finish()
    val zipData = out.getBytes
    zip.close()
    zipData
  }

  private def addTextFiles(map: Map[String, String], zip: ZipOutputStream):Unit = {
    map.keys.map(key => {
      val bytes = map(key).getBytes(StandardCharsets.UTF_8)
      val entry = new ZipEntry(dir + key)
      zip.putNextEntry(entry)
      zip.write(bytes)
      zip.flush()
      zip.closeEntry()
    })
  }

  private def addBinFiles(map: Map[String, Array[Byte]], zip: ZipOutputStream): Unit= {
   // zip.setLevel(Deflater.NO_COMPRESSION)
    //testSaveFiles(map)
    map.keys.map(key => {
      val bytes = map(key)
      val entry = new ZipEntry(dir + "files/" + key)
      entry.setSize(bytes.length)
      zip.putNextEntry(entry)
      zip.write(bytes, 0, bytes.length)
      zip.flush()
     // zip.finish()
      zip.closeEntry()

    })
  }

  def fromZipJsonAndBinary(array: Array[Byte]): Map[String, Array[Byte]] = {
    val input = new ByteArrayInputStream(array)
    val zip = new ZipInputStream(input)
    var map = Map[String, Array[Byte]]()
    var entry:ZipEntry = zip.getNextEntry
    while(entry != null) {
      val key = entry.getName
      //println("READ ZIP : " + key)
      if(entry != null && !entry.isDirectory) {
        val d = zip.readAllBytes()
        map = map + (key -> d)
      }
      zip.closeEntry()
      entry = zip.getNextEntry
    }
    input.close()
    zip.close()
    //testSaveFiles(map)
    map
  }

  //not used yet
  def fromZipTextOnly(array: Array[Byte]): Map[String, String] = {
    val input = new ByteArrayInputStream(array)
    val zip = new ZipInputStream(input)
    var map = Map[String, String]()
    var entry:ZipEntry = null
    val buffer = new Array[Byte](array.length*10)
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
       // val b = compressor.mkDecompress(out.toByteArray)
        val b = out.toByteArray
        map = map + (key -> new String(b, 0, b.length, StandardCharsets.UTF_8))
        out.close()
      }
    } while (entry != null)
    input.close()
    zip.close()
    map
  }

  private def testSaveFiles(fMap: Map[String, Array[Byte]]) = {
    val testDir = "/home/ms/Dokumenty/opostest/"
    Files.createDirectory(Paths.get(testDir + "dane/"))
    Files.createDirectory(Paths.get(testDir + "dane/files/"))
    fMap.keys.foreach(k => {
      val path = Paths.get(testDir + k)
      Files.write(path, fMap(k))
    })
  }

  /*
  def createReport() = {
    try {
      val byteArrayOutputStream = new ByteArrayOutputStream()
      val zipOutputStream = new ZipArchiveOutputStream(byteArrayOutputStream)

      zipOutputStream.setMethod(ZipArchiveOutputStream.STORED);
      zipOutputStream.setEncoding(ENCODING);

      String text= "text";
      byte[] textBytes = text.getBytes(StandardCharsets.UTF_8);

      ArchiveEntry zipEntryReportObject = newStoredEntry("file.txt", textBytes);
      zipOutputStream.putArchiveEntry(zipEntryReportObject);
      zipOutputStream.write(textBytes);

      zipOutputStream.closeArchiveEntry();
      zipOutputStream.close();

      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      return null;
    }

    ArchiveEntry newStoredEntry(String name, byte[] data) {
      ZipArchiveEntry zipEntry = new ZipArchiveEntry(name);
      zipEntry.setSize(data.length);
      zipEntry.setCompressedSize(zipEntry.getSize());
      CRC32 crc32 = new CRC32();
      crc32.update(data);
      zipEntry.setCrc(crc32.getValue());
      return zipEntry;
    }

   */


}
