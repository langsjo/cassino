package joo
import scala.collection.mutable.{Buffer, Set, Map}

class Game(deckCount: Int):
  val players = Buffer[Player]()
  val deck = Deck(deckCount)
  var dealerCount = (this.players.size) - 1
  var turnCount = this.dealerCount //this starts first round with first Player in players with the first turn, then cycles the first turn
  val table = Set[Card]()
  var roundNumber = 0
  var lastCardTaker: Option[Player] = None

  def addPlayer(player: Player): Unit =
    this.players += player

  def addPlayers(players: Seq[Player]): Unit =
    this.players ++= players

  def addToTable(card: Card): Unit =
    this.table += card

  def takeFromTable(card: Card): Unit =
    this.table -= card

  def takeFromTable(cards: Set[Card]): Unit =
    this.table --= cards

  def getTable: String = this.table.mkString(", ")
  
  def tableHas(card: Card): Boolean =
    this.table(card)

  def clearTable(): Unit =
    this.table.clear()

  def currentDealer: Player = this.players((this.dealerCount - 1) % this.players.size)

  def currentPlayer: Player = this.players(this.turnCount % this.players.size)

  def nextPlayer: Player = this.players((this.turnCount + 1) % this.players.size)

  //returns vector of all players, starting with the one whose turn it is now and ending with the one
  //whose turn it was last turn/whose turn is the farthest away in the order
  def turnOrder: Vector[Player] = (this.players.drop(this.turnCount % this.players.size) ++
    this.players.take(this.turnCount % this.players.size)).toVector

  //draws the player of the last turn a card as well as giving the turn to the next player.
  //calls setAllPossibleMoves for next player
  def newTurn(): Unit =
    this.currentPlayer.drawCard()
    this.turnCount += 1
    this.currentPlayer.setAllPossibleMoves()

  //calculates the points each player should get once the round has ended and returns it in a map, used by addPoints
  private def calculatePoints(): Map[Player, Int] =
    val addedPoints = Map() ++ this.players.map( x => (x, 0) ).toMap

    val mostCardsPlayer = this.players.maxBy( x => x.pile.size )
    addedPoints(mostCardsPlayer) += 1

    val mostSpadesPlayer = this.players.maxBy( x => x.pile.count( y => y.suit == Suit.Spade ) )
    addedPoints(mostSpadesPlayer) += 2

    for player <- this.players do
      val sweeps = player.sweeps
      val aceCount = player.pile.count( x => x.handValue == 14 )
      val lillaCount = player.pile.count( x => x.handValue == 15 )
      val tuuraCount = player.pile.count( x => x.handValue == 16 )

      addedPoints(player) += sweeps * 1 + aceCount * 1 + lillaCount * 1 + tuuraCount * 2

    addedPoints

  //adds points based on parameter map to the players
  private def addPoints(points: Map[Player, Int]): Unit =
    for (player, addedPoints) <- points do
      player.addPoints(addedPoints)

  //deals four cards to each player at the start of the round. each player gets one card at a time, with the dealer getting them last
  def dealStartingCards(): Unit =
    for i <- 1 to 4 do
      for player <- this.turnOrder do
        player.drawCard()

  def addStartingCardsToTable(): Unit =
    for i <- 1 to 4 do
      this.addToTable(this.deck.takeCard())

  //ends the round by:
  //clearing each players hands (shouldn't have any cards anyway, just in case)
  //gives the last card taker (if they exist) all the remaining cards on the table
  //and adds the earned points to the players
  def endRound(): Unit =
    this.players.foreach( x => x.clearHand() )
    this.lastCardTaker match
      case Some(player) =>
        player.addToPile(this.table)
        this.clearTable()
      case None =>
        this.clearTable()

    this.addPoints(this.calculatePoints())

  //starts a new round by:
  //incrementing the roundNumber and dealerCount
  //setting the turnCount variable so that the dealer gets their turn last
  //shuffling the deck, dealing everyone 4 cards, putting 4 cards on the table and calling setAllPossibleMoves on player whose turn it is to start
  def newRound(): Unit =
    this.roundNumber += 1
    this.dealerCount = (this.players.size + this.roundNumber)
    this.turnCount = this.dealerCount

    this.deck.shuffle()
    this.dealStartingCards()
    this.addStartingCardsToTable()
    this.currentPlayer.setAllPossibleMoves()
