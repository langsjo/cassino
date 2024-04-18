package UI

import scalafx.application.JFXApp3


object Main extends JFXApp3:
  def start() =

    stage = new JFXApp3.PrimaryStage:
      title = "UniqueProjectName"
      width = 850
      height = 700
    stage.setMinWidth(stage.getWidth)
    stage.setMinHeight(stage.getHeight)
    stage.setMaxHeight(stage.getHeight)
    stage.setMaxWidth(stage.getWidth)

    stage.scene = Menu.scene


  end start



end Main

