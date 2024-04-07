package joo

import scalafx.application.JFXApp3
import java.io.File
import scala.collection.mutable.{Buffer, Set}

final case class CorruptedSaveException(private val message: String = "File is corrupted", private val cause: Throwable = None.orNull) extends Exception(message, cause)

object GameLoader:

  def getCardData(cards: Array[String]): Vector[Card] =
      val saveTo = Buffer[Card]()
      if cards.head != "NO" then// NO = no cards saved
        for cardRepr <- cards do
          saveTo += cardReprToCard(cardRepr)
      saveTo.toVector

  def load(file: File): Option[Game] =
    try
      val source = scala.io.Source.fromFile(file)
      val lines = source.getLines().toVector

      val gameData = lines.head.split('|').map( x => x.split(';') )
      val generalData = gameData(0)
      val tableData = gameData(1)
      val deckData = gameData(2)

      val turnCount = generalData(0).toInt
      val dealerCount = generalData(1).toInt
      val roundNumber = generalData(2).toInt
      val deckCount = generalData(3).toInt
      val lastCardTaker = generalData(4)

      val tableCards = getCardData(tableData)
      val deckCards = getCardData(deckData)

      val game = Game(deckCount)

      val players = Buffer[Player]()
      for line <- lines.tail do //all but first (game data) line
        val data = line.split('|').map( x => x.split(';') )
        val playerData = data(0)
        val handData = data(1)
        val pileData = data(2)

        val name = playerData(0)
        val difficulty = playerData(1)
        val points = playerData(2).toInt
        val sweeps = playerData(3).toInt

        val handCards = getCardData(handData)
        val pileCards = getCardData(pileData)

        val player = difficulty match
          case human if human == "None" =>
            Player(game, name)
          case bot if Array("1", "2", "3").contains(bot) =>
            AIPlayer(game, name, difficulty.toInt)
          case _ => throw CorruptedSaveException()

        player.points = points
        player.sweeps = sweeps
        player.hand ++= handCards
        player.pile ++= pileCards

        players += player
      source.close()


      game.addPlayers(players.toSeq)
      game.table ++= tableCards
      game.deck.setCards(deckCards)
      game.turnCount = turnCount
      game.dealerCount = dealerCount
      game.roundNumber = roundNumber
      game.currentPlayer.setAllPossibleMoves()
      game.lastCardTaker = lastCardTaker match
        case nobody if nobody == "None" =>
          None
        case player =>
          players.find( x => x.name == player )

      Some(game)

    catch
      case e: CorruptedSaveException =>
        println("Save corrupted or something lol")
        None