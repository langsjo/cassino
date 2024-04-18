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
      returnString.dropRight(1) + "|" //dropRight to remove last semicolon

  //saves game to file in save file format
  //data is separated into different lines. first line is game data, the ones after are player data.
  //data pieces are separated by semicolons and
  def save(game: Game, location: File): Unit =
    var saveString = ""

    //add general data of game state to save string
    saveString += s"${game.turnCount};" +
                  s"${game.dealerCount};" +
                  s"${game.roundNumber};" +
                  s"${game.deckCount};" +
                  s"${game.lastCardTaker match
                    case Some(player) => player.name
                    case _ => "None"}|"

    saveString += getCardReprString(game.table.toVector) //add tables cards to save
    saveString += getCardReprString(game.deck.getCards.toVector).dropRight(1) //add deck to save and remove last |
    saveString += "\n" //start new line
    for player <- game.players do //go through all players and add their data to save
      var playerString = ""
      playerString += s"${player.name};" +
                      s"${player match
                        case bot: AIPlayer => bot.difficulty //None means human, number is bot
                        case human => "None"};" + //says its an error but its not...
                      s"${player.points};" +
                      s"${player.sweeps}|"
      playerString += getCardReprString(player.hand.toVector) //player hand
      playerString += getCardReprString(player.pile.toVector) //player pile

      saveString += playerString.dropRight(1) + "\n" //start new line for new player


    val fileWriter = new FileWriter(location)
    fileWriter.write(saveString) //write file in specified file
    fileWriter.close()