package UI

import joo.{Card, Game, Player, Suit}
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

class GameView(val game: Game, val roott: StackPane = StackPane()) extends Scene(parent = roott):

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

  def getDeckImage: ImageView =
    val view = ImageView(Image(FileInputStream(s"./assets/deck.png")))
    view.setFitHeight(joo.CardHeight + 20)
    view.setPreserveRatio(true)
    view

  def getCountLabel(fontSize: Int, count: Int): Label =
    val label = Label(s"×${count}")
    label.font = Font.font("Times New Roman", FontWeight.Bold, fontSize)
    label.textFill = White
    label

  def clampX(x: Double, minX: Double, maxX: Double): Double = math.max(math.min(maxX, x), minX)
  val grid = GridPane()
  val bottomBox = HBox()
  val middleBox = HBox()
  val buttonBox = HBox()
  val topBox = HBox()
  val playerPile = HBox()
  val playerPileGroup = Group(playerPile)
  val enemyPile = HBox()
  val enemyPileGroup = Group(enemyPile)

  val deckPile = HBox()
  val deckPileGroup = Group(deckPile)

  val handCards = scala.collection.mutable.Buffer[ToggleButton]()
  val tableCards = scala.collection.mutable.Buffer[ToggleButton]()
  val cardGroup = ToggleGroup()

  def update(): Unit =
      handCards.clear()
      for card <- game.currentPlayer.hand do
        val button = getHandButton(card, cardGroup)
        handCards += button
      bottomBox.children = handCards
      playerPile.children = Array(getDeckImage, getCountLabel(40, this.game.currentPlayer.pile.size))

      tableCards.clear()
      for card <- game.table do
        val button = getTableButton(card)
        tableCards += button
      middleBox.children = tableCards
      deckPile.children = Array(getDeckImage, getCountLabel(40, this.game.deck.cardsLeft))

      topBox.children.clear()
      for i <- game.nextPlayer.hand.indices do
        topBox.children += getBackCard

      enemyPile.children = Array(getDeckImage, getCountLabel(40, this.game.nextPlayer.pile.size))
      
  grid.setOnScroll(event =>
    middleBox.setTranslateX(clampX(middleBox.getTranslateX + event.getDeltaY, tableCards.size * -50, tableCards.size * 50)) )

  roott.children = Array(grid, playerPileGroup, deckPileGroup, enemyPileGroup)
  grid.add(bottomBox, 0, 3)
  grid.add(buttonBox, 0, 2)
  grid.add(middleBox, 0, 1)
  grid.add(topBox, 0, 0)


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

  grid.rowConstraints = Array(row0, row1, row2, row3)
  grid.columnConstraints = Array(column0)
  bottomBox.setAlignment(Pos.BottomCenter)
  buttonBox.setAlignment(Pos.TopCenter)
  middleBox.setAlignment(Pos.BottomCenter)
  topBox.setAlignment(Pos.TopCenter)


  playerPileGroup.alignmentInParent = Pos.BottomRight
  playerPile.setPadding(Insets(10))
  playerPile.setAlignment(Pos.BottomRight)
  playerPile.setMaxWidth(150)
  playerPile.setMaxHeight(100)


  deckPileGroup.alignmentInParent = Pos.CenterRight
  deckPile.setPadding(Insets(10))
  deckPile.setAlignment(Pos.BottomRight)
  deckPile.setMaxWidth(170)
  deckPile.setMaxHeight(100)
  deckPile.setBackground(Background.fill(joo.CasinoGreen))
  deckPileGroup.setTranslateY(10)

  enemyPileGroup.alignmentInParent = Pos.TopRight
  enemyPile.setPadding(Insets(10))
  enemyPile.setAlignment(Pos.BottomRight)
  enemyPile.setMaxWidth(170)
  enemyPile.setMaxHeight(100)

  bottomBox.background = Background.fill(joo.CasinoGreen)
  buttonBox.background = Background.fill(joo.CasinoGreen)
  middleBox.background = Background.fill(joo.CasinoGreen)
  topBox.background = Background.fill(joo.CasinoGreen)
  grid.background = Background.fill(joo.CasinoGreen)

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
            println(game.currentPlayer.pile.mkString(", "))
            println()
            game.newTurn()
            update()

          else
            println("Illegal move!")

        case _ =>
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
        case _ =>
          println("You haven't chosen a card!")



  buttonBox.children += playMoveButton
  buttonBox.children += addToTableButton
  update()