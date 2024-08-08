package minesweeper.view

import minesweeper.MainApp._
import scalafx.scene.control.Label
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class VictoryDialogController(private val RemainingTimeLabel: Label) {
  var dialogStage: Stage = _ // Stage for the dialog

  // Initialize the remaining time label with the current remaining time from the game timer
  RemainingTimeLabel.text = "Remaining Time: " + game.timer.get.remainingTime + " s"

  playVictoryMusic()

  // Move to the next level or show game over dialog if the time left is non-positive
  def nextLevel(): Unit = {
    game.level.value += 1 // Increase the game level
    game.timeLeft = game.timer.get.remainingTime + 10 // Add 10 seconds to the time left

    // Check if time left is positive; show the appropriate dialog
    if (game.timeLeft <= 0) {
      showGameOverDialog() // Show game over dialog if time left is zero or negative
    } else {
      showGame() // Show the game view for the next level
    }

    dialogStage.close() // Close the victory dialog
  }
}