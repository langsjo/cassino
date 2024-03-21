package UI

import joo.{Card, Game, Player, Suit}
import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.Scene
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.{Button, Label, RadioButton, ToggleButton, ToggleGroup}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Background, ColumnConstraints, GridPane, HBox, Pane, RowConstraints, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color.*
import scalafx.scene.text.Font
import scala.collection.mutable.Set
import java.io.FileInputStream

def getTableButton(card: Card): ToggleButton =
  val button = new ToggleButton():

      //toggleGroup = tGroup
      val view = ImageView(Image(FileInputStream(s"./assets/${card.toRepr}.png")))
      view.setFitHeight(joo.CardHeight + 20)
      view.setPreserveRatio(true)
      graphic = view
      padding = Insets(2)
      selected = false
      userData = card
      style = //".toggle-button { -fx-background-color: transparent; }" +
              //s".toggle-button:selected { -f-background-color: $color; }" +
              "-fx-background-radius: 10px;" +
              //s"-fx-background-color: $color;" +
              "-fx-faint-focus-radius: 0px;" +
              s"-fx-faint-focus-color: transparent;"

      this.getStylesheets.add(getClass.getResource("style.css").toExternalForm)

  button

def getHandButton(card: Card, tGroup: ToggleGroup): ToggleButton =
  val button = new ToggleButton():
    toggleGroup = tGroup
    val view = ImageView(Image(FileInputStream(s"./assets/${card.toRepr}.png")))
    view.setFitHeight(joo.CardHeight + 20)
    view.setPreserveRatio(true)
    graphic = view
    padding = Insets(2)
    selected = false
    userData = card
    style = "-fx-background-radius: 10px;" +
            //"-fx-background-insets: 0px;" +
            "-fx-faint-focus-radius: 0px;" +
            "-fx-faint-focus-color: transparent;"

    this.getStylesheets.add(getClass.getResource("style.css").toExternalForm)
  button

def getBackCard: ImageView =
  val view = ImageView(Image(FileInputStream(s"./assets/back.png")))
  view.setFitHeight(joo.CardHeight + 20)
  view.setPreserveRatio(true)
  view

def clampX(x: Double, minX: Double, maxX: Double): Double = math.max(math.min(maxX, x), minX)

object Main extends JFXApp3:

  val game = Game(1)
  val player1 = Player(game, "Player 1")
  val player2 = Player(game, "Player 2")

  game.addPlayer(player1)
  game.addPlayer(player2)
  game.newRound()

  val root = GridPane()
  val bottomBox = HBox()
  val middleBox = HBox()
  val buttonBox = HBox()
  val topBox = HBox()

  val handCards = scala.collection.mutable.Buffer[ToggleButton]()
  val tableCards = scala.collection.mutable.Buffer[ToggleButton]()
  val cardGroup = ToggleGroup()
  def update(): Unit =
      handCards.clear()
      for card <- game.currentPlayer.hand do
        val button = getHandButton(card, cardGroup)
        handCards += button
      bottomBox.children = handCards

      tableCards.clear()
      for card <- game.table do
        val button = getTableButton(card)
        tableCards += button
      middleBox.children = tableCards

      topBox.children.clear()
      for i <- 0 until game.nextPlayer.hand.size do
        topBox.children += getBackCard

  def start() =



    stage = new JFXApp3.PrimaryStage:
      title = "UniqueProjectName"
      width = 850
      height = 700



    val scene = Scene(parent = root)
    stage.scene = scene

    root.setBackground
    root.setOnScroll( event =>
      middleBox.setTranslateX(clampX(middleBox.getTranslateX + event.getDeltaY, tableCards.size * -50, tableCards.size * 50)) )



    root.add(bottomBox, 0, 3)
    root.add(buttonBox, 0, 2)
    root.add(middleBox, 0, 1)
    root.add(topBox, 0, 0)



    val row0 = new RowConstraints:
      percentHeight = 33
    val row1 = new RowConstraints:
      percentHeight = 29
    val row2 = new RowConstraints:
      percentHeight = 5
    val row3 = new RowConstraints:
      percentHeight = 33
    val column0 = new ColumnConstraints:
      percentWidth = 100

    root.rowConstraints = Array(row0, row1, row2, row3)
    root.columnConstraints = Array(column0)
    bottomBox.setAlignment(Pos.BottomCenter)
    buttonBox.setAlignment(Pos.TopCenter)
    middleBox.setAlignment(Pos.BottomCenter)
    topBox.setAlignment(Pos.TopCenter)

    val casinoGreen = Color.rgb(1, 117, 1)
    bottomBox.background = Background.fill(casinoGreen)
    buttonBox.background = Background.fill(casinoGreen)
    middleBox.background = Background.fill(casinoGreen)
    topBox.background = Background.fill(casinoGreen)
    root.background = Background.fill(casinoGreen)

    bottomBox.setSpacing(5)
    bottomBox.setPadding(Insets(5))
    buttonBox.setSpacing(5)
    middleBox.setSpacing(5)
    middleBox.setPadding(Insets(5))
    topBox.setSpacing(5)
    topBox.setPadding(Insets(5))



    val playMoveButton = new Button("Play move"):

      onAction = event =>
        val playedCard = handCards.find( button => button.isSelected ).flatMap( x => Some(x.userData) )

        val chosenCards: Set[Card] = Set[Card]() ++ tableCards.filter(button => button.isSelected).toSet.map( btn => btn.userData ).map{
          case card: Card => card
        }
        playedCard match
          case Some(card: Card) =>
            if game.currentPlayer.playMove(card, chosenCards) then
              game.newTurn()
              println(game.currentPlayer.pile.mkString(" ,"))
              println()
              update()

            else
              println("Illegal move!")

          case None =>
            println("You haven't chosen a card!")

    val addToTableButton = new Button("Put on table"):

      onAction = event =>
        val chosenCard = handCards.find( button => button.isSelected ).flatMap( x => Some(x.userData) )


        chosenCard match
          case Some(card: Card) =>
            if game.currentPlayer.addCardToTable(card) then
              game.newTurn()
              update()
            else
              throw IllegalStateException("Sul ei oo sit korttii?!?!?")
          case None =>
            println("You haven't chosen a card!")



    buttonBox.children += playMoveButton
    buttonBox.children += addToTableButton
    update()
  end start



end Main

