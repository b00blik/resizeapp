package ru.b00blik.resizeapp

import java.io.{BufferedInputStream, DataInputStream, File, FileInputStream}
import java.nio.file.{Path, Paths}

import com.sksamuel.scrimage.nio.{JpegWriter, PngWriter}
import com.sksamuel.scrimage.{Image, ScaleMethod}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

object ImageProcessor {

  def compress (toProcess: File ): Unit ={
    echoInputType(toProcess)
    //TODO: add check to image type and selecting Writer
    val pngWriter = PngWriter(9)
    val jpgWriter = JpegWriter(75, true)


    if (toProcess.isDirectory) {
      for (file <- toProcess.listFiles()) {

        val jpeg = isJPEG(file)
        val png = isPNG(file)

        val oldName = file.getAbsolutePath
        val newName = insertSuffixCompressed(oldName)
        echoPathProcessing(file)

        if (jpeg == true)
          Image.fromFile(file).forWriter(jpgWriter).write(newName)
        else
          Image.fromFile(file).forWriter(pngWriter).write(newName)
      }
    } else {

      val jpeg = isJPEG(toProcess)
      val png = isPNG(toProcess)

      val oldName = toProcess.getAbsolutePath
      val newName = insertSuffixCompressed(oldName)
      echoPathProcessing(toProcess)

      if (jpeg == true)
        Image.fromFile(toProcess).forWriter(jpgWriter).write(newName)
      else
        Image.fromFile(toProcess).forWriter(pngWriter).write(newName)
    }
    new Alert(AlertType.Information,"Image compressing finished!").showAndWait()
  }

  def resize (toProcess: File, scaleVal: Double): Unit ={
    echoPathProcessing(toProcess)
    echoInputType(toProcess)

    val jpegWriter = JpegWriter().withCompression(75)
    val pngWriter = new PngWriter(9)

    if (toProcess.isDirectory) {
      for (file <- toProcess.listFiles()) {
        val oldName = file.getAbsolutePath
        val newName = insertSuffixResized(oldName)
        echoPathProcessing(file)

        Image.fromFile(file).scale(scaleVal, ScaleMethod.BSpline).output(newName)
      }
    } else {
      val oldName = toProcess.getAbsolutePath
      val newName = insertSuffixResized(oldName)
      echoPathProcessing(toProcess)

      val jpeg = isJPEG(toProcess)
      val png = isPNG(toProcess)

      Image.fromFile(toProcess).scale(scaleVal, ScaleMethod.BSpline).output(newName)
    }
    new Alert(AlertType.Information,"Image processing finished!").showAndWait()
  }

  def echoPathProcessing(f: File): Unit = {
    println("Processing file: " + f.getAbsolutePath.toString)
  }

  def insertSuffixResized(oldName: String): Path = {
    val newName = Paths.get(oldName.replace(".", "_resized."))
    return newName
  }

  def insertSuffixCompressed(oldName: String): Path = {
    val newName = Paths.get(oldName.replace(".", "_compressed."))
    return newName
  }

  def echoInputType(toProcess: File): Unit = {
    println("Type for processing is: ".concat(if (toProcess.isDirectory) "directory" else "file"))
  }

  def isJPEG(toCheck: File): Boolean = {
    val ins = new DataInputStream(new BufferedInputStream(new FileInputStream(toCheck)))

    try
        if (ins.readInt == 0xffd8ffe0)
        {
          println("Checked file " + toCheck.getAbsolutePath + " is JPEG")
          return true
        }
        else
        {
          println("Checked file " + toCheck.getAbsolutePath + " is NOT JPEG")
          return false
        }
    finally ins.close
  }

  def isPNG(toCheck: File): Boolean = {
    val ins = new DataInputStream(new BufferedInputStream(new FileInputStream(toCheck)))

    try
        if (ins.readInt == 0x89504e47)
        {
          println("Checked file " + toCheck.getAbsolutePath + " is PNG")
          return true
        }
        else
        {
          println("Checked file " + toCheck.getAbsolutePath + " is NOT PNG")
          return false
        }
    finally ins.close
  }

}
