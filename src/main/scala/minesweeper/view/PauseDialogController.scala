package minesweeper.view

import minesweeper.MainApp._
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class PauseDialogController() {
  var dialogStage: Stage = _ // Stage for the dialog

  // Apply a blur effect to the background stage (not shown in this snippet)
  blurBackgroundStage()

  // Resume the game by closing the dialog and unblurring the background stage
  def resume(): Unit = {
    dialogStage.close() // Close the pause dialog
    unblurBackgroundStage() // Remove the blur effect
    game.timer.get.isFrozen = false // Resume the game timer
  }

  // Return to the main page by closing the dialog and unblurring the background stage
  def MainPage(): Unit = {
    showHome() // Navigate to the home page
    dialogStage.close() // Close the pause dialog
    unblurBackgroundStage() // Remove the blur effect
  }

  // Restart the game by resetting variables, showing the game, and closing the dialog
  def restart(): Unit = {
    game.resetVariables() // Reset game variables
    showGame() // Show the game view
    dialogStage.close() // Close the pause dialog
    unblurBackgroundStage() // Remove the blur effect
  }
}