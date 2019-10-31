package ru.b00blik.resizeapp

import java.io.{BufferedInputStream, DataInputStream, File, FileInputStream}
import java.nio.file.{Path, Paths}

import com.sksamuel.scrimage.nio.{JpegWriter, PngWriter}
import com.sksamuel.scrimage.{Image, ScaleMethod}
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType

object ImageProcessor {

  val PROC_ERROR = "Error of processing file"
  val TYPE_ERROR = "Unknown type of file"

  def compress(toProcess: File): Unit = {
    echoInputType(toProcess)

    if (toProcess.isDirectory) {
      for (file <- toProcess.listFiles()) {
        compressSingleFile(file)
      }
    } else {
      compressSingleFile(toProcess)
    }
  }

  def echoInputType(toProcess: File): Unit = {
    println("Type for processing is: ".concat(if (toProcess.isDirectory) "directory" else "file"))
  }

  def compressSingleFile(toProcess: File): Unit = {
    val jpeg = isJPEG(toProcess)
    val png = isPNG(toProcess)

    val oldName = toProcess.getAbsolutePath
    val newName = insertSuffixCompressed(oldName)
    echoPathProcessing(toProcess)

    try {
      if (jpeg) {
        compressSingleJpeg(toProcess, newName)
      } else if (png) {
        compressSinglePng(toProcess, newName)
      } else {
        showErrorAlert(TYPE_ERROR)
      }
    } catch {
      case e: Exception => showErrorAlert(PROC_ERROR)
    }
  }

  def echoPathProcessing(f: File): Unit = {
    println("Processing file: " + f.getAbsolutePath)
  }

  def insertSuffixCompressed(oldName: String): Path = {
    val newName = Paths.get(oldName.replace(".", "_compressed."))
    newName
  }

  def isJPEG(toCheck: File): Boolean = {
    val ins = new DataInputStream(new BufferedInputStream(new FileInputStream(toCheck)))

    try
        if (ins.readInt == 0xffd8ffe0) {
          println("Checked file " + toCheck.getAbsolutePath + " is JPEG")
          true
        }
        else {
          println("Checked file " + toCheck.getAbsolutePath + " is NOT JPEG")
          false
        }
    finally ins.close()
  }

  def isPNG(toCheck: File): Boolean = {
    val ins = new DataInputStream(new BufferedInputStream(new FileInputStream(toCheck)))

    try
        if (ins.readInt == 0x89504e47) {
          println("Checked file " + toCheck.getAbsolutePath + " is PNG")
          true
        }
        else {
          println("Checked file " + toCheck.getAbsolutePath + " is NOT PNG")
          false
        }
    finally ins.close()
  }

  def compressSingleJpeg(toProcess: File, newName: Path): Unit = {
    val jpgWriter = JpegWriter(30, progressive = false)
    Image
      .fromFile(toProcess)
      .forWriter(jpgWriter)
      .write(newName)
    new Alert(AlertType.Information, "Image processing finished!").showAndWait()
  }

  def compressSinglePng(toProcess: File, newName: Path): Unit = {
    val pngWriter = PngWriter(9)
    Image.fromFile(toProcess).forWriter(pngWriter).write(newName)
    new Alert(AlertType.Information, "Image processing finished!").showAndWait()
  }

  def showErrorAlert(message: String): Unit = {
    new Alert(AlertType.Error, message).showAndWait()
  }

  def resize(toProcess: File, scaleValue: Double): Unit = {
    echoPathProcessing(toProcess)
    echoInputType(toProcess)
    println("Image scale rate is " + scaleValue)

    if (toProcess.isDirectory) {
      for (file <- toProcess.listFiles()) {
        resizeSingleFile(file, scaleValue)
      }
    } else {
      resizeSingleFile(toProcess, scaleValue)
    }
  }

  def resizeSingleFile(toResize: File, scaleVal: Double): Unit = {
    val jpeg = isJPEG(toResize)
    val png = isPNG(toResize)

    try {
      if (jpeg)
        resizeSingleJpeg(toResize, 75, scaleVal)
      else if (png)
        resizeSinglePng(toResize, 9, scaleVal)
      else
        showErrorAlert(TYPE_ERROR)
    } catch {
      case e: Exception => showErrorAlert(PROC_ERROR)
    }
  }

  def resizeSingleJpeg(toResize: File, compressionValue: Int, scaleVal: Double): Unit = {
    val jpegWriter = JpegWriter().withCompression(75)
    val oldName = toResize.getAbsolutePath
    val newName = insertSuffixResized(oldName)
    echoPathProcessing(toResize)
    Image.fromFile(toResize).scale(scaleVal, ScaleMethod.BSpline).output(newName)(jpegWriter)
    new Alert(AlertType.Information, "Image processing finished!").showAndWait()
  }

  def resizeSinglePng(toResize: File, compressionValue: Int, scaleVal: Double): Unit = {
    val pngWriter = new PngWriter(9)
    val oldName = toResize.getAbsolutePath
    val newName = insertSuffixResized(oldName)
    echoPathProcessing(toResize)
    Image.fromFile(toResize).scale(scaleVal, ScaleMethod.BSpline).output(newName)(pngWriter)
    new Alert(AlertType.Information, "Image processing finished!").showAndWait()
  }

  def insertSuffixResized(oldName: String): Path = {
    val newName = Paths.get(oldName.replace(".", "_resized."))
    newName
  }

}
