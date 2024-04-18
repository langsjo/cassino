package joo

//Simple card class to represent cards of the deck
//tableValue represents the value of the card once placed on the table
//handValue represents the value of the card when in a player's hand
class Card(val suit: Suit, val tableValue: Int, val id: Int):
  
  val handValue =
    (this.suit, this.tableValue) match //Special cards get their special values
        case (Suit.Spade, 2) => 15 //2 of Spades
        case (Suit.Diamond, 10) => 16 //10 of Diamonds
        case (_, 1) => 14 //Aces
        case _ => this.tableValue //Other cards
  
  override def equals(other: Any): Boolean =
    other match
      case card: Card =>
        card.suit == this.suit && card.tableValue == this.tableValue
      case _ =>
        false

  //check if this card is the other card not just in suit and value but is literally the same object
  def trueEquals(other: Card): Boolean = this.id == other.id

  override def toString: String = 
     val name = this.tableValue match
        case 1 => "Ace"
        case 11 => "Jack"
        case 12 => "Queen"
        case 13 => "King"
        case _ => s"${this.tableValue}"
        
     s"${name} of ${this.suit}s"
  
  def pointCount: Int =
    this.handValue match
      case 14 => 1
      case 15 => 1
      case 16 => 2
      case _ => 0

  //returns the representing string of this card
  def toRepr: String =
    val value = this.tableValue match
      case num if num >= 1 && num <= 9 => num.toString.toCharArray.head //toChar of int returns char with ID of that int, not the int as char
      case char if char == 10 => 'T'
      case char if char == 11 => 'J'
      case char if char == 12 => 'Q'
      case char if char == 13 => 'K'

    val suit = this.suit match //is exhaustive
      case x if x == Suit.Heart => 'H'
      case x if x == Suit.Diamond => 'D'
      case x if x == Suit.Spade => 'S'
      case x if x == Suit.Club => 'C'

    s"$value$suit"


//Suit objects for checking suit of cards
enum Suit():
  case Spade extends Suit()
  case Club extends Suit()
  case Heart extends Suit()
  case Diamond extends Suit()