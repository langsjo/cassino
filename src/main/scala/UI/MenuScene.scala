package UI

import joo.{CorruptedSaveException, Game, GameLoader, WrongCardException}
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Background, GridPane, HBox, RowConstraints}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

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

  //function to choose file to load
  def chooseFile(): Option[File] =
    val fileChooser = FileChooser()
    fileChooser.getExtensionFilters.add(new ExtensionFilter("Text Files", "*.txt"))
    fileChooser.initialDirectory = new File("./saves/")
    val chosenFile = fileChooser.showOpenDialog(stage)
    Option(chosenFile)

  //returns alert with info about the error when loading save
  def getSaveErrorAlert(message: String): Alert =
    new Alert(AlertType.Warning):
      title = "Error while loading save"
      headerText = message

  val loadButton = new Button(): //button to load save
    alignmentInParent = Pos.Center
    graphic = new ImageView(Image(FileInputStream("./assets/loadbutton.png")))
    padding = Insets(-5)

    onAction = event =>
      val chosenFile = chooseFile()
      chosenFile match
        case Some(file) =>
          val loadedGame =
            try
              GameLoader.load(file)
            catch //catch errors in game file and show alerts about them
              case e: CorruptedSaveException =>
                getSaveErrorAlert(e.getMessage).showAndWait()
                None

              case e: WrongCardException =>
                getSaveErrorAlert("Card in save file was written wrong.").showAndWait()
                None

              case e: IndexOutOfBoundsException =>
                getSaveErrorAlert("Save is missing data.").showAndWait()
                None
          loadedGame match 
            case Some(game) => //if a game was succesfully returned from the load method, switch scene to it
              val newScene = GameScene(game, stage)
              stage.setScene(newScene)
              newScene.ifAIThenPlayMove()
            case _ => //if there was an error in the file, dont do anything
        case _ =>
  grid.add(loadButton, 0, 2)


