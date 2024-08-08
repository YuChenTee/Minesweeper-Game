package minesweeper.view

import minesweeper.MainApp._
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class HelpDialogController() {

  // The dialog stage, initialized to null
  var dialogStage: Stage = _

  // Blur the background stage when the help dialog is shown
  blurBackgroundStage()

  // Resume the game by closing the dialog, unblurring the background, and unfreezing the timer
  def resume(): Unit = {
    dialogStage.close()         // Close the help dialog
    unblurBackgroundStage()    // Unblur the background stage
    game.timer.get.isFrozen = false // Unfreeze the timer to resume the game
  }
}