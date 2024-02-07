package joo

//Simple card class to represent cards of the deck
//tableValue represents the value of the card once placed on the table
//handValue represents the value of the card when in a player's hand
case class Card(val suit: Suit, val tableValue: Int, val handValue: Int):
  override def toString: String = 
     val name = this.tableValue match
        case 1 => "Ace"
        case 11 => "Jack"
        case 12 => "Queen"
        case 13 => "King"
        case _ => s"${this.tableValue}"
        
     s"${name} of ${this.suit}s"


//Suit objects for checking suit of cards
enum Suit():
  case Spade extends Suit()
  case Club extends Suit()
  case Heart extends Suit()
  case Diamond extends Suit()