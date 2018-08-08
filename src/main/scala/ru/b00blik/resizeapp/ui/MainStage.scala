package ru.b00blik.resizeapp.ui

import java.io

import ru.b00blik.resizeapp.ImageProcessor
import ru.b00blik.resizeapp.ResizeApp.stage
import ru.b00blik.resizeapp.event.PathChangedEvent
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, Label, Slider}
import scalafx.scene.layout.{HBox, VBox}
import scalafx.stage.{DirectoryChooser, FileChooser, Stage}

class MainStage extends PrimaryStage {

  var element : Option[io.File] = None
  val NOT_SELECTED = "Nothing selected"
  val SELECTED = "Selected: "

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

    delegate.getScene.getRoot.getChildrenUnmodifiable.get(0).fireEvent(new PathChangedEvent(PathChangedEvent.Any))

  }

  def showSelectDirDialog(): Unit = {
    val directoryChooser = new DirectoryChooser()

    this.element = Option(directoryChooser.showDialog(
      new Stage() {
        title.value = "Select directory to process"
      }
    ))

    delegate.getScene.getRoot.getChildrenUnmodifiable.get(0).fireEvent(new PathChangedEvent(PathChangedEvent.Any))

  }

  def createLabelBox():HBox = {
    val label = createLabel()

    val result = new HBox() {
      alignment = Pos.Center
      padding = Insets(10)
      children = label
    }

    result.handleEvent(PathChangedEvent.Any){
      pe: PathChangedEvent => {
        if (this.element.get == null)
          label.setText(NOT_SELECTED)
        else
          label.setText(SELECTED + this.element.get.getAbsolutePath)
      }
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
          onAction = handle{
            if (element != None)
              ImageProcessor.resize(element.get, 0.5)
            else
              showAlertToSelect()
          }
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
          onAction = handle{
            if (element != None)
              ImageProcessor.compress(element.get)
            else
              showAlertToSelect()
          }
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
        text = NOT_SELECTED
      }
  }

  def showAlertToSelect(): Unit ={
    new Alert(AlertType.Error, "Please select file(s) to process!").showAndWait()
  }
}
