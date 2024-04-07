package joo

import java.io.{File, FileWriter}

object GameSaver:

  //returns string with card reprs with semicolons between them and a | at the end
  private def getCardReprString[C[Card] <: Seq[Card]](cards: C[Card]): String =
    if cards.isEmpty then
      "NO|"
    else
      var returnString = ""
      for card <- cards do
        returnString += s"${card.toRepr};"
      returnString.dropRight(1) + "|"

  //saves game to file in save file format
  def save(game: Game, location: File): Unit =
    var saveString = ""

    saveString += s"${game.turnCount};" +
                  s"${game.dealerCount};" +
                  s"${game.roundNumber};" +
                  s"${game.deckCount};" +
                  s"${game.lastCardTaker match
                    case Some(player) => player.name
                    case _ => "None"}|"

    saveString += getCardReprString(game.table.toVector)
    saveString += getCardReprString(game.deck.getCards.toVector).dropRight(1)
    saveString += "\n"
    for player <- game.players do
      var playerString = ""
      playerString += s"${player.name};" +
                      s"${player match
                        case bot: AIPlayer => bot.difficulty
                        case human => "None"};" +
                      s"${player.points};" +
                      s"${player.sweeps}|"
      playerString += getCardReprString(player.hand.toVector)
      playerString += getCardReprString(player.pile.toVector)

      saveString += playerString.dropRight(1) + "\n"


    val fileWriter = new FileWriter(location)
    fileWriter.write(saveString)
    fileWriter.close()