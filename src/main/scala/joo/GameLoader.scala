package joo

import scalafx.application.JFXApp3
import java.io.File
import scala.collection.mutable.{Buffer, Set}

final case class CorruptedSaveException(private val message: String = "File is corrupted", private val cause: Throwable = None.orNull) extends Exception(message, cause)

object GameLoader:

  //returns a vector of cards from an array of card reprs
  def getCardData(cards: Array[String]): Vector[Card] =
      val saveTo = Buffer[Card]()
      if cards.head != "NO" then// NO = no cards saved
        for cardRepr <- cards do
          saveTo += cardReprToCard(cardRepr)
      saveTo.toVector

  //returns string as int if it is a non-negative int
  def returnAsIntOrThrowError(input: String): Int =
    if input.forall( x => x.isDigit ) then //checks that all characters in the string are numbers
      if input.toInt >= 0 then
        input.toInt
      else
        throw CorruptedSaveException("Save had negative number where it should be non-negative only.")
    else
      throw CorruptedSaveException("Save had non-number where a number should be.")

  def load(file: File): Option[Game] = //loads game state from save file and returns option of the game, None if load failed
    val source = scala.io.Source.fromFile(file)
    try //try so we can use finally block to always close source

      val lines = source.getLines().toVector //get lines of save file to vector

      val gameData = lines.head.split('|').map( x => x.split(';') ) //first line is game data, split it into appropriate chunks
      val generalData = gameData(0) //data about state of game
      val tableData = gameData(1) //data about cards on table
      val deckData = gameData(2) //data about cards in the deck

      val turnCount = returnAsIntOrThrowError(generalData(0))
      val dealerCount = returnAsIntOrThrowError(generalData(1))
      val roundNumber = returnAsIntOrThrowError(generalData(2))
      val deckCount = returnAsIntOrThrowError(generalData(3))
      val lastCardTaker = generalData(4)

      val tableCards = getCardData(tableData)
      val deckCards = getCardData(deckData)

      val game = Game(deckCount) //initialize game before players, since players need game as parameter

      val players = Buffer[Player]() //store players in buffer to be added to game later
      for line <- lines.tail do //first line is game data, all others are player data
        val data = line.split('|').map( x => x.split(';') ) //split one players data into appropriate chunks
        val playerData = data(0) //data about player
        val handData = data(1) //data about cards in hand
        val pileData = data(2) //data about cards in pile

        val name = playerData(0) //name
        val difficulty = playerData(1) //difficulty of bot or if the player is human
        val points = playerData(2).toInt //points
        val sweeps = playerData(3).toInt //sweeps

        val handCards = getCardData(handData)
        val pileCards = getCardData(pileData)

        val player = difficulty match //check if the difficulty in save is None or (1, 2, 3). If neither of these, throw error.
          case human if human == "None" =>
            Player(game, name)
          case bot if Array("1", "2", "3").contains(bot) =>
            AIPlayer(game, name, difficulty.toInt)
          case _ => throw CorruptedSaveException("Difficulty of bot is not correct.")

        player.points = points
        player.sweeps = sweeps
        player.hand ++= handCards
        player.pile ++= pileCards

        players += player

      //adding loaded data to game
      game.addPlayers(players.toSeq)
      game.table ++= tableCards
      game.deck.setCards(deckCards)
      game.turnCount = turnCount
      game.dealerCount = dealerCount
      game.roundNumber = roundNumber
      game.currentPlayer.setAllPossibleMoves() //set possible moves for current player so checking of moves works
      game.lastCardTaker = lastCardTaker match //check if last card taker is None or a player who is actually in the game
        case nobody if nobody == "None" => //This means nobody has taken a card yet
          None
        case player =>
          val foundPlayer = players.find( x => x.name == player ) //find player with name in the file
          foundPlayer match
            case Some(guy) => Some(guy) //if that player exists, return it in an Option
            // if that player doesnt exist, corrupsed save.
            case None => throw CorruptedSaveException("Player marked as last card taker not found in game.")

      Some(game)

    catch
      case e => throw e //throw the caught error again

    finally
      source.close() //close file