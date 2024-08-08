package minesweeper.view

import minesweeper.MainApp
import minesweeper.MainApp._
import minesweeper.model.Leaderboard
import scalafx.collections.ObservableBuffer
import scalafx.scene.control.TextField
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class HighScoreDialogController(val nameTextField: TextField) {

  // The dialog stage, initialized to null
  var dialogStage: Stage = _

  // Save the player's name and update the leaderboard
  def save(): Unit = {
    // Get the player's name from the text field
    game.playerName = nameTextField.getText

    // Update the leaderboard with the player's name and total flags
    Leaderboard.updateLeaderboard(game.playerName, game.totalFlags.value)

    // Retrieve the updated leaderboard data and update the observable buffer
    val leaderboardData = ObservableBuffer(Leaderboard.getAllLeaderboard: _*)
    MainApp.leaderboardData = leaderboardData

    // Close the dialog
    dialogStage.close()
  }

  // Close the dialog without saving
  def cancel(): Unit = {
    dialogStage.close()
  }
}