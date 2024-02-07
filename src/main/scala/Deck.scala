package joo

import scala.util.Random
import scala.collection.mutable.Buffer

object Deck:
  //Buffer that stores cards in the deck
  private var cards = Buffer[Card]()
  this.shuffle()

  //Used to initialize deck and reshuffle for new rounds
  def shuffle(): Unit =

    //Clear buffer in case any cards are still in it
    this.cards.clear()

    //Loop to add cards 1-13 of all 4 suits
    for
      suit <- Vector[Suit](Suit.Spade, Suit.Club, Suit.Heart, Suit.Diamond)
      faceValue <- 1 to 13
    do
      (suit, faceValue) match //Special cards get their special values
        case (Suit.Spade, 2) => this.cards += Card(suit, faceValue, 15) //2 of Spades
        case (Suit.Diamond, 10) => this.cards += Card(suit, faceValue, 16) //10 of Diamonds
        case (_, 1) => this.cards += Card(suit, faceValue, 14) //Aces
        case _ => this.cards += Card(suit, faceValue, faceValue) //Other cards

    this.cards = Random.shuffle(this.cards) //Shuffle the cards into a random order

  //Remove the first card in the buffer and return it
  def takeCard(): Card =
    val cardTaken = this.cards.head
    this.cards = this.cards.tail
    cardTaken

  //Represents the deck as a string
  override def toString: String =
    var repr = ""
    for card <- this.cards do
      repr += s"${card}, "

    repr.dropRight(2)

  //Returns the next card but does not remove it from the deck
  def next = this.cards.head


@main def test() =
  println(Deck)