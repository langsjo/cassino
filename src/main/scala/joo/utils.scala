package joo
import scala.collection.mutable.{Set, Buffer}


def getNewCardId(): Int =
  val returnVal = CardID
  CardID += 1
  returnVal

//Recursive function that returns all possible combinations to sum up to a specific value
def possibleSingleCombinations(playedCardValue: Int, tableCards: Buffer[Card]): Buffer[Buffer[Card]] =
  //Set used to remove duplicate combinations
  var possibilities = Buffer[Buffer[Card]]()

  if tableCards.isEmpty then //if no cards in pool, return the empty set
    return possibilities

  //filter out cards that are above the needed value
  var cards = tableCards.filter( x => x.tableValue <= playedCardValue )

  for card <- cards do
    //if card on the table has the same value as needed, add just that to the set and loop again
    if card.tableValue == playedCardValue then
      possibilities += Buffer(card)

    else
      val cardsClone = cards.clone()
      //remove the card currently being inspected so it's not passed in recursion
      cardsClone.remove(cardsClone.indexWhere( x => x.id == card.id ))

      val neededValue = playedCardValue - card.tableValue //needed value to combine to the required value
      val neededCardPossibilities = possibleSingleCombinations(neededValue, cardsClone) //recursive call to find the combination needed to get neededValue

      //if possible combinations were found, add them to possibilities. if the recursive call returned an empty set, no combinations were found and thus don't
      //add anything to possibilities
      if neededCardPossibilities.nonEmpty then
        possibilities ++= neededCardPossibilities.map( x => (x :+ card) )

  //the sort is done so set removes the duplicates that just had the cards in a different order
  //have to do this weird syntax with adding it to empty set because otherwise duplicates werent being removed..
  possibilities

def isDuplicate(s1: Buffer[Card], s2: Buffer[Card]): Boolean = //check if s1 and s2 have all the same cards
    s1.size == s2.size && s1.forall( card => s2.contains(card) )

//function to combine combinations of cards that add up to a specific value into all possible combinations of them
//that have no overlapping cards.
def combineCombinations(madeCombos: Buffer[Buffer[Card]], originalCombos: Buffer[Buffer[Card]]): Buffer[Buffer[Card]] =
  def hasOverlap(s1: Buffer[Card], s2: Buffer[Card]): Boolean = //check if s1 and s2 share any card
    s1.exists(card1 => s2.exists(card2 => card1.trueEquals(card2)))

  var newCombos = Buffer[Buffer[Card]]()
  def alreadyExists(s1: Buffer[Card]): Boolean = //check if newCombos already has s1
    newCombos.exists( x => isDuplicate(x, s1) )

  for madeCombo <- madeCombos do //loop through already made combos in previous recursions
    for originalCombo <- originalCombos do //loop through the possible single combinations
      if !(hasOverlap(madeCombo, originalCombo)) && !alreadyExists(madeCombo ++ originalCombo) then //if they don't share cards, its a valid combination and is added to the result
        newCombos += (madeCombo ++ originalCombo)

  if newCombos.nonEmpty then //if new combos were made, check if the next recursion can also make more.
    newCombos ++ combineCombinations(newCombos, originalCombos)

  else //this is only ran on the last recursion, so originalCombos added only once. toSet to remove dupes
    newCombos ++ originalCombos.toSet



final case class WrongCardException(private val message: String = "Card written wrong.", private val cause: Throwable = None.orNull) extends Exception(message, cause)



//returns card matching the repr
def cardReprToCard(repr: String): Card =
  if repr.length != 2 then //all repr are length of 2
    throw WrongCardException()

  else
    val value = repr(0) match //gets the value of card, either a number or T, J, Q, or K (T is 10)
      case num: Char if num.isDigit && num.asDigit >= 1 && num.asDigit <= 9 =>
        num.asDigit

      case char: Char =>
        if char == 'T' then 10
        else if char == 'J' then 11
        else if char == 'Q' then 12
        else if char == 'K' then 13
        else throw WrongCardException()

      case _ => throw WrongCardException()

    //get suit of card (S, C, H, or D)
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

    Card(suit, value, getNewCardId())
