package UI

import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, TextFormatter}
import scalafx.scene.control.TextFormatter.Change

// Custom validation errors we can use in the TextFormatter
trait ValidationError
case object EmptyField extends ValidationError
case object TooLong extends ValidationError
case object IllegalSymbol extends ValidationError
// Simple input validation function
def validateText(str: String): Option[ValidationError] =
  str match
    case str if str.length > 18 => Some(TooLong)
    case str if str contains ";" => Some(IllegalSymbol)
    case _ => None

// Create the TextFormatter, and add a filter function to it
def getTextFormatter = new TextFormatter(
  filter = (change: Change) =>
    // Validation is performed whenever content is added, deleted or replaced
    if change.isContentChange then
      // Get the text with the change applied, and validate it
      val validationResult = validateText(change.controlNewText)

      // Handle validation result
      validationResult match
        case Some(TooLong) =>
          //infoLabel.text = "Text too long" //infoLabel not found error, using alerts instead
          val alert = new Alert(AlertType.Information):
            title = "Name too long"
            headerText = "Name cannot be longer than 18 characters."
          alert.showAndWait()
          null // Change isn't allowed through
        case Some(IllegalSymbol) =>
          val alert = new Alert(AlertType.Information):
            title = "Illegal symbol"
            headerText = "Name cannot have illegal symbols (;)"
          alert.showAndWait()
          null
        case None =>
          //infoLabel.text = "Write some text"
          change
        case _ =>
          null
    else change // Didn't need to do validation
)
