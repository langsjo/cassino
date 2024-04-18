package joo
import scala.collection.mutable.{Set, Map, Buffer}

class Player(val game: Game, val name: String):
  val hand = Buffer[Card]()
  val pile = Buffer[Card]()
  var sweeps = 0
  var points = 0
  var allPossibleMoves = Map[Card, Buffer[Buffer[Card]]]()

  def addPoints(addedPoints: Int): Unit =
    this.points += addedPoints

  //draws card from the deck to hand of player IF there are cards in the deck left
  def drawCard(): Unit =
    val card = game.deck.takeCard()
    card match
      case Some(c) =>
        this.hand += c
      case None =>

  def addToPile(card: Card): Unit =
    this.pile += card

  def addToPile(cards: Buffer[Card]): Unit =
    this.pile ++= cards

  def removeCardFromHand(card: Card): Unit =
    this.hand.remove(this.hand.indexWhere( x => x.id == card.id ))

  def clearHand(): Unit =
    this.hand.clear()

  def clearPile(): Unit =
    this.pile.clear()

  def clearSweeps(): Unit =
    this.sweeps = 0

  def resetState(): Unit =
    this.clearHand()
    this.clearPile()
    this.clearSweeps()

  def has(card: Card): Boolean =
    this.hand.contains(card)

  //add card to table if that card is in this players hand
  def addCardToTable(card: Card): Boolean =
    if this.has(card) then
      this.removeCardFromHand(card)
      this.game.addToTable(card)
      true
      
    else
      false


  def moveIsLegal(playedCard: Card, chosenCards: Buffer[Card]): Boolean =
    this.allPossibleMoves.contains(playedCard) && this.allPossibleMoves(playedCard).exists( x => isDuplicate(x, chosenCards) )

  //checks if move is legal by checking if it exists in all possible moves and then plays it if it is. returns false if illegal
  def playMove(playedCard: Card, chosenCards: Buffer[Card]): Boolean =
    //the second part of this boolean statement will not evaluate if the first part is false, therefore no errors caused
    if this.moveIsLegal(playedCard, chosenCards) then
      this.game.takeFromTable(chosenCards.toVector) //take chosen cards from table
      this.addToPile(chosenCards :+ playedCard) //add chosen cards and the card from hand to pile
      this.removeCardFromHand(playedCard) //remove card from hand
      this.game.setLastCardTaker(this) //set this player as last taker of cards
      if game.table.isEmpty then //if this move cleared the table, its a sweep
        this.sweeps += 1
      true
    else //returns true if move was legal, false if illegal
      false

  //sets the allPossibleMoves variable to have all possible moves that can be made by this player. Should be called every time it's this players turn
  def setAllPossibleMoves(): Unit =
    this.allPossibleMoves = Map[Card, Buffer[Buffer[Card]]]()
    for card <- this.hand do
      val singleCombos = possibleSingleCombinations(card.handValue, this.game.table)
      try
        println(singleCombos.map( x => x.head.id ) )
      catch
        case e =>
      val allCombos = combineCombinations(singleCombos, singleCombos)
      if allCombos.nonEmpty then
        this.allPossibleMoves += ((card, allCombos))

    println(this.hand)
    println(this.allPossibleMoves)
    println()

  override def toString: String = this.name

  def getHand: String = this.hand.mkString(", ")