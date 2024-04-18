package joo
import scala.collection.mutable
import scala.collection.mutable.{Set, Map, Buffer}
import scala.util.Random
import scala.math.round

class AIPlayer(override val game: Game, override val name: String, val difficulty: Int) extends Player(game, name):
  require(Array(1, 2, 3) contains this.difficulty, "Difficulty must be either 1, 2 or 3. (Easy, Medium, Hard)")


  //plays a move using Player class playMove method or addCardToTable method
  //returns the move played so that the GUI can display it
  def AIPlayMove(): (Card, Buffer[Card]) =
    val (playedCard, chosenCards) = this.selectMove
    // if no chosenCards, that means the bot is putting a card on the table.
    if chosenCards.isEmpty then
      this.addCardToTable(playedCard)

    else
      super.playMove(playedCard, chosenCards)

    (playedCard, chosenCards)

  // selects the move the bot should play based on its difficulty. splits all moves into 4 groups based on the moves' scoring
  // then selects group based on the bot's difficulty, then randomly selects a move from that group.
  // if there are less than 4 possible moves, select move based on match case below
  // if there are no possible moves, select the worst card in the bot's hand and put it on the table.
  private def selectMove: (Card, Buffer[Card]) =
    val scoredMoves = this.scoreMoves.sortBy( (x, y) => y ).map( (x, y) => x ).toVector
    //println(scoredMoves)
    if scoredMoves.size >= 4 then
      val groupedMoves = this.groupMoves(scoredMoves, 4)
      //println(groupedMoves)
      val movePool = groupedMoves(this.difficulty)
      val chosenMove = movePool(Random.nextInt(movePool.size))
      chosenMove

    else
      scoredMoves.size match
        case 3 => scoredMoves(this.difficulty - 1)
        case 2 if this.difficulty != 3 => scoredMoves(this.difficulty - 1)
        case 2 => scoredMoves(1)
        case 1 => scoredMoves.head
        case 0 => (this.worstCardInHand, Buffer[Card]())

  // group moves into 'count' number of relatively even groups.
  private def groupMoves(list: Vector[(Card, Buffer[Card])], count: Int): Vector[Vector[(Card, Buffer[Card])]] =
    val result = mutable.Buffer[Vector[(Card, Buffer[Card])]]()
    val slices = this.getSlices(list.size, 4)
    for (start, end) <- slices.zip(slices.tail) do
      result += list.slice(start, end)

    result.toVector

  // returns a vector with slices that can be used to slice a list into sliceCount number of (relatively) even slices
  // ie. n=29 sliceCount=4 returns Vector(0, 7, 14, 22, 29). made to be used with slice method
  private def getSlices(n: Int, sliceCount: Int): Vector[Int] =
    var x = n
    var slices = Buffer[Int](0)

    for i <- 0 until sliceCount do
      // divide remaining number of indices by how many more slices are needed, round to nearest integer then add to it value of
      // the last slice
      slices += slices.last + round(x / (sliceCount - i).toDouble).toInt
      x = n - slices.last

    slices.toVector


  //scores all of the bot's possible moves and returns them in a Buffer as a tuple with the move and its score
  private def scoreMoves: Buffer[((Card, Buffer[Card]), Int)] =
    var scoredMoves = Buffer[((Card, Buffer[Card]), Int)]()

    for (playedCard, chosenCards) <- this.allPossibleMoves do
      for cards <- chosenCards do
        val score = this.scoreMove(cards :+ playedCard)
        scoredMoves += (((playedCard, cards), score))

    scoredMoves

  // scores a whole move by scores of the cards contained in it and whether it will give a sweep
  private def scoreMove(cards: Buffer[Card]): Int =
    var score = 0
    for card <- cards do
      score += this.scoreCard(card)

    if this.game.table.size <= cards.size - 1 then // sweep
      score += PointScore

    score

  //scores the value of a card based on scoring variables.
  private def scoreCard(card: Card): Int =
    var score = CardScore
    if card.suit == Suit.Spade then
      score += SpadeScore

    score += PointScore * card.pointCount

    score

  // finds the worst card in the bots hand, used for selecting a card to put on the table.
  private def worstCardInHand: Card =
    //score cards, then take min by their score and then by value in hand. Choose the card with lowest score and hand value.
    this.hand.map( x => (x, this.scoreCard(x)) ).minBy( (x, y) => (y, x.handValue ) )._1
