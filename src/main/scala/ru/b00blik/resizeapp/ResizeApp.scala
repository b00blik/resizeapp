package ru.b00blik.resizeapp

import java.io

import _root_.ru.b00blik.resizeapp.event.PathChangedEvent
import _root_.ru.b00blik.resizeapp.ImageProcessor

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.event.Event
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, Slider}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.{DirectoryChooser, FileChooser, Stage}

object ResizeApp extends JFXApp {

  println("Starting ResizeApp")

  var element : Option[io.File] = None

  stage = new JFXApp.PrimaryStage {
    title.value = "ResizeApp"
    width = 600
    height = 350
    var labelHBox = createLabelBox()
    var scaleHBox = createScaleBox()
    var compressHBox = createCompressBox()
    var selectbtHBox = createSelectBtBox()
    scene = new Scene {
      root = new VBox() {
        children = Seq(
          labelHBox,
          scaleHBox,
          compressHBox,
          selectbtHBox
        )
      }
    }
  }

  def showSelectFileDialog(): Unit = {
    val fileChooser = new FileChooser()
    val stage = new Stage() {
      title.value = "Select element to process"
    }
    this.element = Option(fileChooser.showOpenDialog(
      stage
    ))
    Event.fireEvent(Event.NullSourceTarget, new PathChangedEvent(PathChangedEvent.Any))
  }

  def showSelectDirDialog(): Unit = {
    val directoryChooser = new DirectoryChooser()
    this.element = Option(directoryChooser.showDialog(
      new Stage() {
        title.value = "Select directory to process"
      }
    ))
    Event.fireEvent(Event.NullSourceTarget, new PathChangedEvent(PathChangedEvent.Any))
  }

  def createLabelBox():HBox = {
    var label = createLabel()
    var result = new HBox() {
      //label
      alignment = Pos.Center
      padding = Insets(10)
      children = label

      /*val filterEvent = filterEvent(PathChangedEvent.Any){
        (pe: PathChangedEvent) =>
          println("got pathChangedEvent!")
          label.text_=(element.get.getAbsolutePath)
      }*/


    }
    return result
  }

  def createScaleBox():HBox = {
    var result = new HBox() {
      //scale box
      alignment = Pos.Center
      padding = Insets(10)
      children = Seq(
        new Slider() {
          max = 100
          min = 1
        },
        new Button(){
          text = "Minimize"
          onAction = handle{ImageProcessor.resize(element.get, 0.5)}
        }
      )
    }
    return result
  }

  def createCompressBox():HBox = {
    var result = new HBox() {
      //compress box
      alignment = Pos.Center
      padding = Insets(10)
      children = Seq(
        new Button() {
          text = "Compress"
          onAction = handle{ImageProcessor.compress(element.get)}
        }
      )
    }
    return result
  }

  def createSelectBtBox():HBox = {
    val result = new HBox() {
      alignment = Pos.Center
      padding = Insets(10)
      spacing = 28
      children = Seq(
        new Button() {
          text = "Select File"
          onAction = handle {
            showSelectFileDialog()
          }
        },
        new Button() {
          text = "Select Directory"
          onAction = handle {
            showSelectDirDialog()
          }
        }
      )
    }
    return result
  }

  def createLabel() : Label = {
    return new Label {
      text = element match {
        case Some(e) => "Selected element: " + e.getPath
        case None => "Dir not selected"
      }
    }
  }

}
