package joo
import scala.collection.mutable.{Set, Map, Buffer}

class Player(val game: Game, val name: String):
  val hand = Buffer[Card]()
  val pile = Set[Card]()
  var sweeps = 0
  var points = 0
  var allPossibleMoves = Map[Card, Set[Set[Card]]]()

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

  def addToPile(cards: Set[Card]): Unit =
    this.pile ++= cards

  def removeCardFromHand(card: Card): Unit =
    this.hand -= card

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

  
  //checks if move is legal by checking if it exists in all possible moves and then plays it if it is. returns false if illegal
  def playMove(playedCard: Card, chosenCards: Set[Card]): Boolean =
    //the second part of this boolean statement will not evaluate if the first part is false, therefore no errors caused
    if this.allPossibleMoves.contains(playedCard) && this.allPossibleMoves(playedCard).contains(chosenCards) then
      this.game.takeFromTable(chosenCards) //take chosen cards from table
      this.addToPile(chosenCards + playedCard) //add chosen cards and the card from hand to pile
      this.removeCardFromHand(playedCard) //remove card from hand
      this.game.setLastCardTaker(this) //set this player as last taker of cards
      if game.table.isEmpty then //if this move cleared the table, its a sweep
        this.sweeps += 1
      true
    else //returns true if move was legal, false if illegal
      false

  //sets the allPossibleMoves variable to have all possible moves that can be made by this player. Should be called every time it's this players turn
  def setAllPossibleMoves(): Unit =
    this.allPossibleMoves = Map[Card, Set[Set[Card]]]()
    for card <- this.hand do
      val singleCombos = possibleSingleCombinations(card.handValue, Set[Card]() ++ this.game.table)
      val allCombos = combineCombinations(singleCombos, singleCombos)
      if allCombos.nonEmpty then
        this.allPossibleMoves += (card, allCombos)

  override def toString: String = this.name

  def getHand: String = this.hand.mkString(", ")