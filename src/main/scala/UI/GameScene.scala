package UI

import joo.{AIPlayer, Card, Game, GameSaver, Player}
import scalafx.application.JFXApp3
import scalafx.geometry.{Insets, Pos}
import scalafx.scene.{Node, Scene}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, ButtonType, Label, ToggleButton, ToggleGroup}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{Background, ColumnConstraints, GridPane, HBox, Priority, RowConstraints, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.paint.Color.*
import scalafx.scene.text.{Font, FontWeight}
import scalafx.stage.FileChooser.ExtensionFilter
import scalafx.stage.FileChooser

import scala.collection.mutable.Set
import java.io.{File, FileInputStream}
import scala.collection.mutable

class GameScene(val game: Game, val stage: JFXApp3.PrimaryStage, val grid: GridPane = GridPane()) extends Scene(parent = grid):

  //returns a button with the image of a card on it that can be toggled on/off
  //button is used for the cards on the table, that the player can select to take in a move
  def getTableButton(card: Card): ToggleButton =
    new ToggleButton():
        //toggleGroup = tGroup
        val view = ImageView(Image(FileInputStream(s"./assets/${card.toRepr}.png"))) //take card image from assets by its repr
        view.setFitHeight(joo.CardHeight + 20)
        view.setPreserveRatio(true)
        graphic = view
        padding = Insets(2)
        selected = false
        userData = card //card in userData so it can be retrieved later on when its needed
        style = "-fx-background-radius: 10px;" +
                "-fx-faint-focus-radius: 0px;" +
                s"-fx-faint-focus-color: transparent;"

        this.getStylesheets.add(getClass.getResource("/gamescene.css").toExternalForm)

  //returns a togglebutton with image of card on it that can be toggled on/off
  //used for the cards in the players hand. has a togglegroup so only one can be selected at a time
  def getHandButton(card: Card, tGroup: ToggleGroup): ToggleButton =
    new ToggleButton():
      toggleGroup = tGroup
      val view = ImageView(Image(FileInputStream(s"./assets/${card.toRepr}.png"))) //take card image from assets by its repr
      view.setFitHeight(joo.CardHeight + 20)
      view.setPreserveRatio(true)
      graphic = view
      padding = Insets(2)
      selected = false //nothing selected by default
      userData = card //card in userData so it can be retrieved later on when its needed
      style = "-fx-background-radius: 10px;" +
              "-fx-faint-focus-radius: 0px;" +
              "-fx-faint-focus-color: transparent;"

      this.getStylesheets.add(getClass.getResource("/gamescene.css").toExternalForm)

  //returns image of the back of a card, used for the cards in enemy players hand
  def getBackCard: ImageView =
    val view = ImageView(Image(FileInputStream(s"./assets/back.png")))
    view.setFitHeight(joo.CardHeight + 20)
    view.setPreserveRatio(true)
    view

  // returns image of a specific card, used whenever image of card is needed but a button is not
  def getCardImage(card: Card, height: Int): ImageView =
    val view = ImageView(Image(FileInputStream(s"./assets/${card.toRepr}.png")))
    view.setFitHeight(height)
    view.setPreserveRatio(true)

    view

  //returns arrow image for the alert for when a bot makes a move
  def getArrowImage(height: Int): ImageView =
    val view = ImageView(Image(FileInputStream(s"./assets/arrow.png")))
    view.setFitHeight(height)
    view.setPreserveRatio(true)

    view

  //returns image of a deck to show how many cards are in each players pile and in the deck
  def getDeckImage: ImageView =
    val view = ImageView(Image(FileInputStream(s"./assets/deck.png")))
    view.setFitHeight(joo.CardHeight + 20)
    view.setPreserveRatio(true)
    view

  //returns label with text that is used for showing how many cards is left in deck/pile
  def getCountLabel(fontSize: Int, count: Int): Label =
    val label = Label(s"×${count}")
    label.font = Font.font("Times New Roman", FontWeight.Bold, fontSize)
    label.textFill = White

    label

  //return label with specified text and fontsize
  def getTextLabel(text: String, fontSize: Int): Label =
    new Label(text):
      font = Font.font("Times New Roman", FontWeight.Bold, fontSize)
      //alignment = Pos.BottomLeft
      textFill = White

  //returns an alert for an AIPlayer playing a move
  def getPlayMoveAlert(player: Player, playedCard: Card, chosenCards: mutable.Buffer[Card]): Alert =
    new Alert(AlertType.Information):

      headerText = ""
      val cardHeight = 100
      val cardHBox = HBox()

      if chosenCards.nonEmpty then //is chosenCards not empty, AI made a move (didnt put card on table)
        title = s"${player.name} made a move!"

        //content is playedcard, arrow, chosencards
        val content = Array(getCardImage(playedCard, cardHeight), getArrowImage(cardHeight)) ++
          chosenCards.map( x => getCardImage(x, cardHeight) ).toArray
        cardHBox.children = content
      else //if chosenCards is empty, AI is putting a card on the table
        title = s"${player.name} put a card on the table!"
        val tableLabel = new Label("Table"):
          font = Font.font("Times New Roman", FontWeight.Bold, 40)
          translateY = 25 //move label down a bit so it's centered

        //content is card that goes on table, arrow, label with text "Table"
        val content = Array[Node](getCardImage(playedCard, cardHeight), getArrowImage(cardHeight), tableLabel)
        cardHBox.children = content

      graphic = cardHBox

      //makes the alert be as small as possible (not 1 pixel wide, it is the width of the contents + the OK button)
      dialogPane.getValue.setMaxWidth(1)

  //returns an alert that shows the new points after a round has ended
  def getPointsAlert: Alert =
    new Alert(AlertType.Information):
      initOwner(stage)
      headerText = ""
      title = "Round has ended! Here are the new points."

      val points = VBox()
      val texts = game.players.map( x => s"${x.name}: ${x.points}" )
      val content = texts.map{ x =>
        new Label(x):
          font = Font.font("Times New Roman", FontWeight.Bold, 18)
      }

      points.children = content
      graphic = points

  val saveButton = ButtonType("Save")
  val returnMenuButton = ButtonType("Return to menu")
  //function to make user choose file to save to
  def chooseFile(): Option[File] =
    val fileChooser = FileChooser()
    fileChooser.getExtensionFilters.add(new ExtensionFilter("Text Files", "*.txt"))
    fileChooser.initialDirectory = new File("./saves/")
    fileChooser.initialFileName =  "CassinoSave.txt"
    val chosenFile = fileChooser.showSaveDialog(stage)
    Option(chosenFile)

  //function that sets the stage scene to menu
  def returnToMenu(): Unit =
    stage.setScene(Menu.scene)
    stage.width = stage.getWidth + 1

  //function that returns alert when esc pressed
  def getEscAlert: Alert =
    new Alert(AlertType.Confirmation):
      initOwner(stage)
      title = "Paused"
      headerText = "Do you wish to save or return to menu?"
      buttonTypes = Array(saveButton, returnMenuButton, ButtonType.Cancel)

  //check if esc pressed, if so bring menu and initiate save if that is chosen
  grid.onKeyPressed = event =>
    if event.getCode.toString == "ESCAPE" then
      val result = getEscAlert.showAndWait() //returns the pressed button
      val chosenFile: Option[File] = result match
        case Some(btn) if btn == saveButton =>
          chooseFile()
        case Some(btn) if btn == returnMenuButton =>
          returnToMenu()
          None
        case _ =>
          None

      chosenFile match //if save chosen and a file is returned, save the game there
        case Some(file) =>
          GameSaver.save(game, file)
        case _ =>

  //returns Double that is at least minX, at most maxX, otherwise x. used for scrolling the table cards
  def clampX(x: Double, minX: Double, maxX: Double): Double = math.max(math.min(maxX, x), minX)

  val bottomBox = HBox()
  val middleBox = HBox()
  val buttonBox = HBox()
  val topBox = HBox()
  val playerPile = HBox()
  //val playerPileGroup = Group(playerPile) //has to be in a group to be aligned
  val enemyPile = HBox()
  //val enemyPileGroup = Group(enemyPile)//has to be in a group to be aligned
  val playerNameBox = new HBox():
    alignment = Pos.BottomLeft
  val enemyNameBox = new HBox():
    alignment = Pos.TopLeft
  val deckPile = HBox()
  //val deckPileGroup = Group(deckPile)//has to be in a group to be aligned

  val handCards = scala.collection.mutable.Buffer[ToggleButton]() //used to store cards in players hand
  val tableCards = scala.collection.mutable.Buffer[ToggleButton]() //used to store cards on the table
  val cardGroup = ToggleGroup() //togglegroup for cards in hand, so only one can be selected at a time

 //updates the screen to be appropriate to the information of the current human player
  def updateBottomPlayer(player: Player): Unit =
    handCards.clear()
    for card <- player.hand do
      val button = getHandButton(card, cardGroup)
      handCards += button
    bottomBox.children = handCards
    //updates the count of how many cards are in the next human players pile
    playerPile.children = Array(getDeckImage, getCountLabel(40, player.pile.size))
    playerNameBox.children = Array(getTextLabel(player.name, 20))

  //updates the screen to be appropriate for the next player
  def updateTopPlayer(player: Player): Unit =
    topBox.children.clear()
    for i <- player.hand.indices do
      topBox.children += getBackCard
    //update count of enemy players pile size
    enemyPile.children = Array(getDeckImage, getCountLabel(40, player.pile.size))
    enemyNameBox.children = Array(getTextLabel(player.name, 20))

  //updates screen with info about cards on table and cards in deck
  def updateTable(): Unit =
    tableCards.clear()
    for card <- game.table do
      val button = getTableButton(card)
      tableCards += button
    middleBox.children = tableCards
    middleBox.setTranslateX(0) //reset scroll
    deckPile.children = Array(getDeckImage, getCountLabel(40, this.game.deck.cardsLeft))

  //updates the screen as necessary
  def update(): Unit =
    this.game.currentPlayer match
      case ai: AIPlayer => //if current player is AI, don't show their cards on the bottom of screen
        updateTopPlayer(ai) //show them at the top
        this.game.nextHumanPlayer match //check next human player
          case Some(player: Player) =>
            updateBottomPlayer(player) //if a next human is returned, update the bottom of screen to reflect their status
          case _ => //a next human player should ALWAYS exist, if somehow it doesnt, just dont update bottom (everything works)

      case noAI => //if current player not ai, update bottom and top normally
        updateBottomPlayer(game.currentPlayer)
        updateTopPlayer(game.nextPlayer)
    //always update table
    updateTable()

  //if current player is AI, make it play move
  def ifAIThenPlayMove(): Unit =
    this.game.currentPlayer match
      case ai: AIPlayer => //if current player is bot, make it play a move right away
        val (playedCard, chosenCards) = ai.AIPlayMove()
        this.getPlayMoveAlert(ai, playedCard, chosenCards).showAndWait() //show what move the bot played in an alert
        this.startNewTurn() //call this method again to start a new turn, this will basically loop until player is not bot

      case _ => //if normal player, don't do anything else

  //returns alert notifying user of the winners
  def getWinnerAlert: Alert =
    new Alert(AlertType.Information):
      initOwner(stage)
      title = "The game has ended!"
      val winners = game.getLeadingPlayers
      headerText =
        if winners.size == 1 then //if only one winner
          s"${winners.head} has won the game with ${winners.head.points} points!"
        else //if more than one winner
          winners.dropRight(1).mkString(", ") + s" and ${winners.last} have won the game with ${winners.head.points} points!"

      buttonTypes = Array(returnMenuButton)

  //starts a new turn and updates the screen accordingly, used in the buttons that player uses to make moves.
  def startNewTurn(): Unit =
    this.game.newTurn() //start new turn in the game
    if this.game.roundEnded then //if round ended, new points get calculated and shown
      this.getPointsAlert.showAndWait()
      this.game.setRoundEndedFalse() //set flag to false
      if this.game.isGameOver then //if game ended (someone got >= 16 points) show alert with winner that returns player to menu
        getWinnerAlert.showAndWait()
        returnToMenu()

    if !this.game.isGameOver then
      this.update() //update the screen, wont update hand cards and enemy cards if player is AI
      this.ifAIThenPlayMove()

  //on scroll event so that player can scroll the cards on the table. in case there are too many cards to show on screen
  grid.setOnScroll(event =>
    middleBox.setTranslateX(clampX(middleBox.getTranslateX + event.getDeltaY, tableCards.size * -50, tableCards.size * 50)) )

  grid.add(bottomBox, 1, 3)
  grid.add(buttonBox, 1, 2)
  grid.add(middleBox, 0, 1, 3, 1)
  grid.add(topBox, 1, 0)
  grid.add(playerPile, 2, 3)
  grid.add(deckPile, 2, 1)
  grid.add(enemyPile, 2, 0)
  grid.add(playerNameBox, 0, 3)
  grid.add(enemyNameBox, 0, 0)


  val row0 = new RowConstraints:
    percentHeight = 33
  val row1 = new RowConstraints:
    percentHeight = 29
  val row2 = new RowConstraints:
    percentHeight = 5
  val row3 = new RowConstraints:
    percentHeight = 33
  val column0 = new ColumnConstraints:
    percentWidth = 22
  val column1 = new ColumnConstraints:
    percentWidth = 56
    hgrow = Priority.Never
  val column2 = new ColumnConstraints:
    percentWidth = 22
  grid.rowConstraints = Array(row0, row1, row2, row3)
  grid.columnConstraints = Array(column0, column1, column2)
  
  //bunch of setting alignments and padding etc.
  bottomBox.setAlignment(Pos.BottomCenter)
  buttonBox.setAlignment(Pos.TopCenter)
  middleBox.setAlignment(Pos.BottomCenter)
  topBox.setAlignment(Pos.TopCenter)

//  playerPileGroup.alignmentInParent = Pos.BottomRight
  playerPile.setPadding(Insets(0, 3, 5, 0))
  playerPile.setAlignment(Pos.BottomLeft)
  //playerPile.setMaxWidth(200)
  //playerPile.setMaxHeight(100)

 // deckPileGroup.alignmentInParent = Pos.CenterRight
  deckPile.setPadding(Insets(0, 3, 6, 5))
  deckPile.setAlignment(Pos.BottomLeft)
  //deckPile.setMaxWidth(200)
  //deckPile.setMaxHeight(100)
  deckPile.setBackground(Background.fill(joo.CasinoGreen))
  //deckPileGroup.setTranslateY(10)

  //enemyPileGroup.alignmentInParent = Pos.TopRight
  enemyPile.setPadding(Insets(5, 3, 0, 0))
  enemyPile.setAlignment(Pos.TopLeft)
  //enemyPile.setMaxWidth(200)
  //enemyPile.setMaxHeight(100)

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

  //returns the first selected button from a buffer of buttons
  def getSelectedButton(buttons: scala.collection.mutable.Buffer[ToggleButton]): Option[ToggleButton] =
    buttons.find( x => x.isSelected )

  def getIllegalMoveAlert(text: String): Alert =
    new Alert(AlertType.Warning):
      title = "Illegal move!"
      headerText = text

//button used to play a move
  val playMoveButton = new Button("Play move"):
    onAction = event =>

      //get the card that player has chosen in their hand
      val playedCard = getSelectedButton(handCards).flatMap( x => Some(x.userData) )
      //get the cards that player chose from the table
      val chosenCards: mutable.Buffer[Card] = tableCards.filter(button => button.isSelected).map(btn => btn.userData ).map{
        case card: Card => card
      }
      
      //check if player chose a card from their hand
      playedCard match
        case Some(card: Card) =>
          if game.currentPlayer.playMove(card, chosenCards) then //playMove returns true if its a legal move and vice versa
            startNewTurn() //start new turn after making a move
          else //if illegal move, show alert
            getIllegalMoveAlert("The cards on the table don't add up to the chosen card!").showAndWait()
        case _ => //if no card in hand selected, show alert
          getIllegalMoveAlert("You haven't chosen a card from your hand!").showAndWait()
  //if not, don't do anything

//button used to add a card from the players hand to the table
  val addToTableButton = new Button("Put on table"):
    onAction = event =>
      //find card chosen in players hand
      val chosenCard = getSelectedButton(handCards).flatMap( x => Some(x.userData) )
      //check if player chose a card
      chosenCard match
        case Some(card: Card) =>
          if game.currentPlayer.addCardToTable(card) then //add card to table, returns true if player has the card
            startNewTurn()
          else // should (technically) never be reached
            throw IllegalStateException("Player doesn't have chosen card")
        case _ =>

  buttonBox.children += playMoveButton
  buttonBox.children += addToTableButton
  stage.width = stage.getWidth - 1
  update()
  //this.ifAIThenPlayMove() //if first player is AI, this needs to be ran
