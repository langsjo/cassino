package joo
import scala.collection.mutable.{Set, Map}

class Player(val game: Game, val name: String):
  val hand = Set[Card]()
  val pile = Set[Card]()
  var sweeps = 0
  var points = 0
  var allPossibleMoves = Map[Card, Set[Set[Card]]]()


  def addPoints(addedPoints: Int): Unit =
    this.points += addedPoints

  def drawCard(): Unit =
    this.hand += game.deck.takeCard()

  def addToPile(card: Card): Unit =
    this.pile += card

  def addToPile(cards: Set[Card]): Unit =
    this.pile ++= cards

  def removeCardFromHand(card: Card): Unit =
    this.hand -= card

  def clearHand(): Unit =
    this.hand.foreach( x => this.removeCardFromHand(x) )
  
  def addCardToTable(card: Card): Unit =
    this.removeCardFromHand(card)
    this.game.addToTable(card)

  def playMove(playedCard: Card, chosenCards: Set[Card]): Boolean =
    if this.allPossibleMoves.contains(playedCard) && this.allPossibleMoves(playedCard).contains(chosenCards) then
      this.game.takeFromTable(chosenCards)
      this.addToPile(chosenCards + playedCard)
      if game.table.isEmpty then
        this.sweeps += 1
      true

    else
      false

  def setAllPossibleMoves(): Unit =
    this.allPossibleMoves = Map[Card, Set[Set[Card]]]()
    for card <- this.hand do
      val singleCombos = possibleSingleCombinations(card.handValue, this.game.table)
      val allCombos = combineCombinations(singleCombos, singleCombos)
      if allCombos.nonEmpty then
        this.allPossibleMoves += (card, allCombos)
