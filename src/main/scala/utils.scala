package joo
import scala.collection.mutable.{Buffer, Set}

//Recursive function that returns all possible combinations to sum up to a specific value
def possibleSingleCombinations(playedCardValue: Int, tableCards: Set[Card]): Set[Set[Card]] =
  //Set used to remove duplicate combinations
  var possibilities = Set[Set[Card]]()

  if tableCards.isEmpty then //if no cards in pool, return the empty set
    return possibilities

  //filter out cards that are above the needed value
  var cards = tableCards.filter( x => x.tableValue <= playedCardValue )

  for card <- cards do
    //if card on the table has the same value as needed, add just that to the set and loop again
    if card.tableValue == playedCardValue then
      possibilities = possibilities + Set(card)

    else
      val cardsClone = cards.clone()
      //remove the card currently being inspected so it's not passed in recursion
      cardsClone -= card

      val neededValue = playedCardValue - card.tableValue //needed value to combine to the required value
      val neededCardPossibilities = possibleSingleCombinations(neededValue, cardsClone) //recursive call to find the combination needed to get neededValue

      //if possible combinations were found, add them to possibilities. if the recursive call returned an empty set, no combinations were found and thus don't
      //add anything to possibilities
      if neededCardPossibilities.nonEmpty then
        possibilities = possibilities ++ neededCardPossibilities.map( x => (x + card) )

  possibilities

//function to combine combinations of cards that add up to a specific value into all possible combinations of them
//that have no overlapping cards.
def combineCombinations(madeCombos: Set[Set[Card]], originalCombos: Set[Set[Card]]): Set[Set[Card]] =
  def hasOverlap(s1: Set[Card], s2: Set[Card]): Boolean = s1.intersect(s2).nonEmpty //checks if two sets of cards have any overlap, true if so
  val newCombos = Set[Set[Card]]()

  for madeCombo <- madeCombos do //loop through already made combos in previous recursions
    for originalCombo <- originalCombos if originalCombo != madeCombo do //loop through the possible single combinations, and check that they are not the same
      if !(hasOverlap(madeCombo, originalCombo)) then //if they don't share cards, its a valid combination and is added to the result
        newCombos += madeCombo ++ originalCombo

  if newCombos.nonEmpty then //if new combos were made, check if the next recursion can also make more.
    newCombos ++= combineCombinations(newCombos, originalCombos)

  newCombos ++ originalCombos //add the original single combinations, Set removes duplicates.

final case class WrongCardException(private val message: String = "Card written wrong.", private val cause: Throwable = None.orNull) extends Exception(message, cause)


def cardReprToCard(repr: String): Card =
  if repr.length != 2 then
    throw WrongCardException()

  else
    val value = repr(0) match
      case num: Char if num.toInt >= 1 && num.toInt <= 9 =>
        num.toInt

      case char: Char =>
        if char == 'T' then 10
        else if char == 'J' then 11
        else if char == 'Q' then 12
        else if char == 'K' then 13
        else throw WrongCardException()

      case _ => throw WrongCardException()

    val suit = repr(1) match
      case char: Char =>
        if char == 'S' then Suit.Spade
        else if char == 'C' then Suit.Club
        else if char == 'H' then Suit.Heart
        else if char == 'D' then Suit.Diamond
        else
          throw WrongCardException()

      case _ =>
        throw WrongCardException()

    Card(suit, value)


@main def tester(): Unit =
  val a = Deck(1)
  val hand = Set[Card]()
  val table = Set[Card]()
  for i <- 1 to 4 do
    hand += a.takeCard()
  //println(Deck)
  for i <- 1 to 7 do
    table += a.takeCard()
  println(hand)
  println(table)
  for card <- hand.take(1) do
    println(possibleSingleCombinations(card.handValue, table))
  println(combineCombinations(possibleSingleCombinations(hand.head.handValue, table), possibleSingleCombinations(hand.head.handValue, table)))
