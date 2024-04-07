package UI

import joo.{AIPlayer, Card, Game, GameLoader, Player, Suit}
import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Group, Node, Scene}
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, Label, RadioButton, ToggleButton, ToggleGroup, Tooltip}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Background, Border, BorderImage, BorderStroke, BorderStrokeStyle, BorderWidths, ColumnConstraints, CornerRadii, GridPane, HBox, Pane, Priority, Region, RowConstraints, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color.*
import scalafx.scene.text.{Font, FontWeight}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

import scala.collection.mutable.Set
import java.io.{File, FileInputStream}

object Menu:

  val grid = GridPane()
  val scene = Scene(parent = grid)
  val stage = Main.stage

  this.grid.setBackground(Background.fill(joo.CasinoGreen))

  val row0 = new RowConstraints:
    percentHeight = 40
  val row1 = new RowConstraints:
    percentHeight = 20
  val row2 = new RowConstraints:
    percentHeight = 20
  val row3 = new RowConstraints:
    percentHeight = 20
  this.grid.rowConstraints = Array(row0, row1, row2, row3)

  val logo = new HBox():
    children += new ImageView(Image(FileInputStream("./assets/logo.png")))
  grid.add(logo, 0, 0)

  val playButton = new Button(): //button to change scene to setting up the game
    onAction = event =>
      val newScene = SetupScene(stage)
      stage.scene = newScene
    alignmentInParent = Pos.Center
    graphic = new ImageView(Image(FileInputStream("./assets/playbutton.png")))
    padding = Insets(-5)
  grid.add(playButton, 0, 1)

  def chooseFile(): Option[File] =
    val fileChooser = FileChooser()
    fileChooser.getExtensionFilters.add(new ExtensionFilter("Text Files", "*.txt"))
    fileChooser.initialDirectory = new File("./saves/")
    val chosenFile = fileChooser.showOpenDialog(stage)
    Option(chosenFile)

  val loadButton = new Button(): //button to load save
    alignmentInParent = Pos.Center
    graphic = new ImageView(Image(FileInputStream("./assets/loadbutton.png")))
    padding = Insets(-5)

    onAction = event =>
      val chosenFile = chooseFile()
      chosenFile match
        case Some(file) =>
          val loadedGame = GameLoader.load(file)
          loadedGame match
            case Some(game) =>
              val newScene = GameScene(game, stage)
              stage.setScene(newScene)
              newScene.ifAIThenPlayMove()
            case _ => // TODO some feedback

        case _ =>


  grid.add(loadButton, 0, 2)


