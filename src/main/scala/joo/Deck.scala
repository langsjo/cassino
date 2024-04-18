package joo

import scala.util.Random
import scala.collection.mutable.Stack

class Deck(val numberOfDecks: Int):
  //Buffer that stores cards in the deck
  private var cards = Stack[Card]()
  this.shuffle()

  //Used to initialize deck and reshuffle for new rounds
  def shuffle(): Unit =

    //Clear buffer in case any cards are still in it
    this.cards.clear()

    //Loop to add cards 1-13 of all 4 suits
    for i <- 1 to this.numberOfDecks do
      for
        suit <- Vector[Suit](Suit.Spade, Suit.Club, Suit.Heart, Suit.Diamond)
        faceValue <- 1 to 13
      do
        this.cards.push(Card(suit, faceValue, getNewCardId()))

    this.cards = Random.shuffle(this.cards) //Shuffle the cards into a random order

  //Remove the first card in the buffer and return it
  def takeCard(): Option[Card] =
    if this.cards.nonEmpty then
      Some(this.cards.pop())

    else
      None
  
  def cardsLeft: Int = this.cards.size

  def setCards[C[Card] <: Seq[Card]](cards: C[Card]): Unit =
    this.cards.clear()
    this.cards ++= cards

  def getCards: Stack[Card] = this.cards
  
  //Represents the deck as a string
  override def toString: String =
    var repr = ""
    for card <- this.cards do
      repr += s"${card}, "

    repr.dropRight(2)

  //Returns the next card but does not remove it from the deck
  def next = this.cards.head