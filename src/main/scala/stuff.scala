package joo
import scala.collection.mutable.Buffer

//Recursive function that returns all possible combinations to sum up to a specific value
def possibleCombinations(playedCardValue: Int, tableCards: Buffer[Card]): Set[Buffer[Card]] =
  //Set used to remove duplicate combinations
  var possibilities = Set[Buffer[Card]]()

  if tableCards.isEmpty then //if no cards in pool, return the empty set
    return possibilities

  //filter out cards that are above the needed value
  var cards = tableCards.filter( x => x.tableValue <= playedCardValue )

  for card <- cards do
    //if card on the table has the same value as needed, add just that to the set and loop again
    if card.tableValue == playedCardValue then
      makePossibility(Buffer(card))

    else
      val cardsClone = cards.clone()
      //remove the card currently being inspected so it's not passed in recursion
      cardsClone -= card

      val neededValue = playedCardValue - card.tableValue //needed value to combine to the required value
      val neededCardPossibilities = possibleCombinations(neededValue, cardsClone) //recursive call to find the combination needed to get neededValue

      //if possible combinations were found, add them to possibilities. if the recursive call returned an empty set, no combinations were found and thus don'tä
      //add anything to possibilities
      if neededCardPossibilities.nonEmpty then
        for cardCombination <- neededCardPossibilities do
          makePossibility( (card +: cardCombination).sortWith(sortCompare) )

  //sorting function used to sort card combination buffers. must always produce the same ordering regardless of intiial ordering
  //of the buffer in order for set to properly remove copies
  def sortCompare(card1: Card, card2: Card): Boolean =
    if card1.tableValue == card2.tableValue then
      val suits = Vector(Suit.Spade, Suit.Club, Suit.Heart, Suit.Diamond)
      suits.indexOf(card1.suit) < suits.indexOf(card2.suit)
    else
      card1.tableValue > card2.tableValue

  //function to add card combination to possibilities
  def makePossibility(newCombination: Buffer[Card]): Unit =
    possibilities = possibilities + newCombination

  possibilities


@main def tester(): Unit =
  val hand = Buffer[Card]()
  val table = Buffer[Card]()

  for i <- 1 to 4 do
    hand += Deck.takeCard()

  for i <- 1 to 7 do
    table += Deck.takeCard()


  println(hand)
  println(table)
  for card <- hand do
    println(possibleCombinations(card.handValue, table))