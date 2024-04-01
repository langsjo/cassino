package UI

import joo.{AIPlayer, Card, Game, Player, Suit}
import scalafx.application.{JFXApp3, Platform}
import scalafx.geometry.{Insets, Orientation, Pos}
import scalafx.scene.{Group, Node, Scene}
import scalafx.scene.canvas.Canvas
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, ButtonType, Label, RadioButton, ScrollPane, Separator, TextField, ToggleButton, ToggleGroup, Tooltip}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.input.ScrollEvent
import scalafx.scene.layout.GridPane.getColumnIndex
import scalafx.scene.layout.{Background, Border, BorderImage, BorderStroke, BorderStrokeStyle, BorderWidths, ColumnConstraints, CornerRadii, GridPane, HBox, Pane, Priority, Region, RowConstraints, StackPane, VBox}
import scalafx.scene.paint.Color
import scalafx.scene.shape.Rectangle
import scalafx.scene.paint.Color.*
import scalafx.scene.text.{Font, FontWeight}

import scala.collection.mutable.Set
import java.io.FileInputStream

class SetupScene(val stage: JFXApp3.PrimaryStage, val grid: GridPane = GridPane()) extends Scene(parent = grid):
  grid.background = Background.fill(joo.CasinoGreen)
  val playerToggleGroup = new ToggleGroup() //toggleGroup for player list buttons

  //returns button, used for editing player info
  def getPlayerButton(name: String, difficulty: Option[Int] = None): ToggleButton =

    val wholeName = name + {difficulty match
      case Some(1) => " (Easy AI)"
      case Some(2) => " (Medium AI)"
      case Some(3) => " (Hard AI)"
      case _ => ""} //not ai, but human player

    new ToggleButton(wholeName):
      onAction = event => update()
      font = Font.font("Times New Roman", FontWeight.Bold, 25)
      textFill = White
      toggleGroup = playerToggleGroup
      padding = Insets(1, 1000, 1, 3)
      userData = difficulty
      selected = false //nothing selected by default
      style = "-fx-background-radius: 0px;" +
              "-fx-faint-focus-radius: 0px;" +
              "-fx-faint-focus-color: transparent;"

      this.getStylesheets.add(getClass.getResource("setupscene.css").toExternalForm)

  //returns the name of the player without the AI suffix
  def getName(btn: ToggleButton): String =
    btn.getText.replace(" (Easy AI)", "").replace(" (Medium AI)", "").replace(" (Hard AI)", "")

  //returns the first button in an option that is selected from list of buttons
  def getSelectedButton(buttons: scala.collection.mutable.Buffer[ToggleButton]): Option[ToggleButton] =
    buttons.find( x => x.isSelected )

  val row0 = new RowConstraints:
    percentHeight = 5
  val row1 = new RowConstraints:
    percentHeight = 85
  val row2 = new RowConstraints:
    percentHeight = 10

  grid.rowConstraints = Array(row0, row1)
  val column0 = new ColumnConstraints:
    percentWidth = 50
  val column1 = new ColumnConstraints:
    percentWidth = 50
  grid.columnConstraints = Array(column0, column1)

  val scrollPane = new ScrollPane():
    style = "-fx-background: transparent;" +
            "-fx-border-color: transparent;" +
            "-fx-faint-focus-color: transparent;" +
            "-fx-focus-color: transparent;" +
            "-fx-unfocus-color: transparent;" +
            "-fx-background-color: transparent;" +
            "-fx-faint-focus-radius: 0px;"
    this.setHbarPolicy(ScrollPane.ScrollBarPolicy.Never) //dont show horizontal scroll bar, can still scroll with wheel

  grid.add(scrollPane, 0, 1, 1, 3)
  val playerButtons = VBox()
  val playerList = scala.collection.mutable.Buffer[ToggleButton]() //used for storing the player buttons
  scrollPane.setContent(playerButtons)
  playerButtons.children = playerList

  val updatePlayerBox = new VBox():
    alignment = Pos.CenterLeft
    padding = Insets(10)
    spacing = 10

  grid.add(updatePlayerBox, 1, 0, 1, 2)

  val humanButton = new ButtonType("Human")
  val botButton = new ButtonType("Bot")
  //alert that asks if the player to be added should be bot or human
  def getHumanOrBotAlert = new Alert(AlertType.Confirmation): //has to be function, otherwise runtimeexception
      initOwner(stage)
      title = "Player type selection"
      headerText = "Do you want to add a human player or a bot player?"
      buttonTypes = Array(humanButton, botButton, ButtonType.Cancel)

  //checks if a name is already in use
  def nameIsTaken(name: String): Boolean =
      playerList.map( x => getName(x) ).contains(name)

  //returns difficulty as Int in an option from buttons userData
  def getDifficultyFromButton(btn: ToggleButton): Option[Int] =
    btn.userData match
      case Some(num: Int) => Some(num)
      case _ => None

  //updates the screen
  def update(): Unit =
    playerButtons.children = playerList

    //check which player is selected and show the edit menu for it
    getSelectedButton(playerList) match
      case Some(player) =>
        val playerDifficulty: Option[Int] = getDifficultyFromButton(player)

        //buffer for RadioButtons that are used to choose bot difficulty (for later on)
        val diffButtons = scala.collection.mutable.Buffer[ToggleButton]()

        val nameLabel = new Label("Name:"):
          font = Font.font("Times New Roman", FontWeight.Bold, 35)
          textFill = White
        val nameField = new TextField():
          text = getName(player)
          font = Font.font("Times New Roman", FontWeight.Bold, 30)
        nameField.textFormatter = getTextFormatter
        playerDifficulty match
          case Some(num) => //not None = player is AI
            val difficultyLabel = new Label("AI Difficulty:"):
              font = Font.font("Times New Roman", FontWeight.Bold, 35)
              textFill = White

            val diffToggleGroup = new ToggleGroup()
            val easyButton = new RadioButton("Easy"):
              toggleGroup = diffToggleGroup
              font = Font.font("Times New Roman", FontWeight.Bold, 30)
              textFill = White
              selected = num == 1
              userData = Some(1)
            val mediumButton = new RadioButton("Medium"):
              toggleGroup = diffToggleGroup
              font = Font.font("Times New Roman", FontWeight.Bold, 30)
              textFill = White
              selected = num == 2
              userData = Some(2)
            val hardButton = new RadioButton("Hard"):
              toggleGroup = diffToggleGroup
              font = Font.font("Times New Roman", FontWeight.Bold, 30)
              textFill = White
              selected = num == 3
              userData = Some(3)
            diffButtons ++= Array(easyButton, mediumButton, hardButton)
            updatePlayerBox.children = Array(nameLabel, nameField, difficultyLabel, easyButton, mediumButton, hardButton)

          case _ => //if selected player not bot, dont show difficulty selection
            updatePlayerBox.children = Array(nameLabel, nameField)

        //button to cancel changes to player
        val cancelButton = new Button("Cancel"):
          onAction = event =>
            player.setSelected(false)
            update()
          font = Font.font("Times New Roman", FontWeight.Bold, 30)

        //button to apply changes to player
        val applyButton = new Button("Apply"):
          onAction = event =>
            val name = nameField.getText
            if name != getName(player) && nameIsTaken(name) then //if name taken, alert user
              val alert = new Alert(AlertType.Information):
                title = "Illegal name"
                headerText = "Name has already been taken."
              alert.showAndWait()

            else if name.isEmpty then //if name empty, alert user
              val alert = new Alert(AlertType.Information):
                title = "Illegal name"
                headerText = "Name cannot be empty."
              alert.showAndWait()

            else
              //get chosen difficulty from radio buttons
              val selectedDifficulty = getSelectedButton(diffButtons) match
                case Some(btn) =>
                  getDifficultyFromButton(btn)
                case _ => None //means not AI

              val newButton = getPlayerButton(name, selectedDifficulty) //new button to be shown on the player list
              newButton.setSelected(true) //selected so the edit menu doesnt disappear
              val index = playerList.indexOf(player)
              playerList(index) = newButton //insert edited button on the same index as the last one

              update()
          font = Font.font("Times New Roman", FontWeight.Bold, 30)

        val buttonBox = new HBox():
          alignment = Pos.BottomCenter
          children = Array(applyButton, cancelButton)
          spacing = 10
          padding = Insets(3)
        updatePlayerBox.children += buttonBox
      case _ =>
        updatePlayerBox.children.clear()



  val addOrRemoveBox = new HBox():
    padding = Insets(3)
    spacing = 3
    alignment = Pos.CenterLeft
  grid.add(addOrRemoveBox, 0, 0)

  //returns lowest unused number in format "Player [num]"
  def getPlayerNumber: Int =
    var foundNumber = false
    var i = 0

    while !foundNumber do
      i += 1
      if !nameIsTaken(s"Player $i") then
        foundNumber = true
    i

  //button to add player
  val addButton = new Button("Add player"):
    font = Font.font("Times New Roman", FontWeight.Bold, 15)
    onAction = event =>
      val result = getHumanOrBotAlert.showAndWait()
      //check if user chose to add human or bot
      result match
        case Some(btn) if btn == humanButton => //chose human
          playerList += getPlayerButton(s"Player ${getPlayerNumber}")
        case Some(btn) if btn == botButton => //chose bot
          playerList += getPlayerButton(s"Player ${getPlayerNumber}", Some(1)) //bot is easy on default
        case _ =>

        update()

  //button to remove selected player
  val removeButton = new Button("Remove selected player"):
    font = Font.font("Times New Roman", FontWeight.Bold, 15)
    onAction = event =>
      val selectedPlayer = getSelectedButton(playerList)
      selectedPlayer match
        case Some(player) => playerList -= player
        case _ => //if no player selected, do nothing

      update()
  addOrRemoveBox.children = Array(addButton, removeButton)

  //button to start game with selected settings
  val startButton = new Button("Start"):
    font = Font.font("Times New Roman", FontWeight.Bold, 25)

    onAction = event =>
      if playerList.size < 2 then //game needs at least two players
        val alert = new Alert(AlertType.Information):
          title = "Too few players"
          headerText = "Game needs at least two players to start."
        alert.showAndWait()

      //check that at least one human player is added
      else if !playerList.map( x => getDifficultyFromButton(x) ).contains(None) then
        val alert = new Alert(AlertType.Information):
          title = "No human players"
          headerText = "Game needs at least one human player to start."
        alert.showAndWait()

      //good to go
      else
        val players = scala.collection.mutable.Buffer[Player]()
        val game = Game((playerList.size + 4) / 4) //scale amount of decks by amount of players. add one deck every 4 players
        //get data from buttons and turn it into Players
        for button <- playerList do
          val name = button.getText
          val difficulty = getDifficultyFromButton(button)
          val player = difficulty match
            case Some(num) => AIPlayer(game, name, num)
            case _ => Player(game, name)

          players += player

        //initialize game and switch scene to it
        game.addPlayers(players.toVector)
        game.newRound()
        val newScene = GameScene(game, stage)
        stage.setScene(newScene)
        //check if first player is AI and have it play move if so
        //if its not here then the scene switch goes wrong
        newScene.ifAIThenPlayMove()



  //button to return back to menu
  val menuButton = new Button("Back to menu"):
    font = Font.font("Times New Roman", FontWeight.Bold, 25)

    onAction = event =>
      stage.width = stage.getWidth - 1
      stage.scene = Menu.scene

  val playOrMenuBox = new HBox():
    alignment = Pos.Center
    padding = Insets(5)
    spacing = 10
    children = Array(startButton, menuButton)

  grid.add(playOrMenuBox, 1, 3)

  //need to resize window to update scene...
  stage.width = stage.getWidth + 1 //hack

