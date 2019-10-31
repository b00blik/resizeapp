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
    width = 500
    height = 350
    var labelHBox: HBox = createLabelBox()
    var scaleVBox: VBox = createScaleBox()
    var compressHBox: HBox = createCompressBox()
    var selectbtHBox: HBox = createSelectBtBox()

    scene = new Scene {
      root = new VBox() {
        children = Seq(
          labelHBox,
          scaleVBox,
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

    val labelBox = new HBox() {
      alignment = Pos.Center
      padding = Insets(10)
      children = label
    }

    labelBox.handleEvent(PathChangedEvent.Any) {
      pe: PathChangedEvent => {
        if (this.element.get == null)
          label.setText(NOT_SELECTED)
        else
          label.setText(SELECTED + this.element.get.getAbsolutePath)
      }
    }

    labelBox
  }

  def createScaleBox(): VBox = {
    var scaleBox = new VBox() {
      //scale box
      alignment = Pos.Center
      padding = Insets(10)

      var scaleLabel: Label = new Label {
        text = "Select downscale ratio from 0 to 1"
      }

      var innerHbox: HBox = new HBox {
        alignment = Pos.Center
        var slider: Slider = new Slider() {
          max = 1
          min = 0.0
          onMouseClicked = handle {
            showAlertRatioChanged(slider.value())
          }
        }

        slider showTickMarks_= true
        slider showTickLabels_= true

        children = Seq(
          slider,
          new Button(){
            text = "Downscale"
            onAction = handle{
              if (element != None)
                ImageProcessor.resize(element.get, slider.value())
              else
                showAlertToSelect()
            }
          }
        )
      }

      children = Seq(
        scaleLabel,
        innerHbox
      )


    }
    scaleBox
  }

  def createCompressBox(): HBox = {
    var compressBox = new HBox() {
      //compress box
      alignment = Pos.Center
      padding = Insets(10)
      children = Seq(
        new Button() {
          text = "Compress"
          onAction = handle {
            if (element.isDefined)
              ImageProcessor.compress(element.get)
            else
              showAlertToSelect()
          }
        }
      )
    }
    compressBox
  }

  def createSelectBtBox(): HBox = {
    val selectButtonBox = new HBox() {
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
    selectButtonBox
  }

  def createLabel() : Label = {
    new Label {
      text = NOT_SELECTED
    }
  }

  def showAlertToSelect(): Unit ={
    new Alert(AlertType.Error, "Please select file(s) to process!").showAndWait()
  }

  def showAlertRatioChanged(value: Double): Unit ={
    new Alert(AlertType.Information, "Ratio for downscale changed to: " + value).showAndWait()
  }
}
