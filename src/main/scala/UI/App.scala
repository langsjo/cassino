package UI

import joo.{AIPlayer, Card, Game, Player, Suit}
import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Group, Scene}
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, Label, RadioButton, ToggleButton, ToggleGroup}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Background, Border, BorderImage, BorderStroke, BorderStrokeStyle, BorderWidths, ColumnConstraints, CornerRadii, GridPane, HBox, Pane, Priority, RowConstraints, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color.*
import scalafx.scene.text.{Font, FontWeight}

import scala.collection.mutable.Set
import java.io.FileInputStream


object Main extends JFXApp3:
  def start() =

    stage = new JFXApp3.PrimaryStage:
      title = "UniqueProjectName"
      width = 850
      height = 700
    stage.setMinWidth(stage.getWidth)
    stage.setMinHeight(stage.getHeight)

    stage.scene = Menu.scene


  end start



end Main

