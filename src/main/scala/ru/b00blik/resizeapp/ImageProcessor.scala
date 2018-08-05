package ru.b00blik.resizeapp

import java.io.File
import java.nio.file.{Path, Paths}

import com.sksamuel.scrimage.nio.{JpegWriter, PngWriter}
import com.sksamuel.scrimage.{Image, ScaleMethod}

import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

object ImageProcessor {

  def compress (toProcess: File ): Unit ={
    echoInputType(toProcess)
    //TODO: add check to image type and selecting Writer
    val writer = PngWriter(9)

    if (toProcess.isDirectory) {
      for (file <- toProcess.listFiles()) {
        val oldName = file.getAbsolutePath
        val newName = insertSuffixResized(oldName)
        echoPathProcessing(file)

        Image.fromFile(file).forWriter(writer).write(newName)
      }
    } else {
      val oldName = toProcess.getAbsolutePath
      val newName = insertSuffixResized(oldName)
      echoPathProcessing(toProcess)

      Image.fromFile(toProcess).forWriter(writer).write(newName)
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
        val newName = insertSuffixCompressed(oldName)
        echoPathProcessing(file)

        Image.fromFile(file).scale(scaleVal, ScaleMethod.BSpline).output(newName)
      }
    } else {
      val oldName = toProcess.getAbsolutePath
      val newName = insertSuffixCompressed(oldName)
      echoPathProcessing(toProcess)

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

}
