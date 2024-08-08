package minesweeper.view

import minesweeper.MainApp._
import scalafx.scene.control.Label
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class GameOverDialogController(private val LevelLabel: Label) {

  // The dialog stage, initialized to null
  var dialogStage: Stage = _

  // Initialize the LevelLabel text with the current game level
  LevelLabel.text = "Level: "+game.level.get

  playGameOverMusic()

  // Show the high score dialog, navigate to home, and close the game over dialog
  def MainPage(): Unit = {
    showHighScoreDialog() // Show the high score dialog
    showHome()           // Navigate to the home page
    dialogStage.close()  // Close the current dialog
  }

  // Show the high score dialog, reset game variables, and restart the game
  def restart(): Unit = {
    showHighScoreDialog() // Show the high score dialog
    game.resetVariables() // Reset game variables
    showGame()           // Restart the game
    dialogStage.close()  // Close the current dialog
  }
}