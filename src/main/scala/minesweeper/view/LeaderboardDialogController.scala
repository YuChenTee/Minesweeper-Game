package minesweeper.view

import minesweeper.MainApp._
import minesweeper.model.Leaderboard
import scalafx.scene.control.{TableColumn, TableView}
import scalafx.stage.Stage
import scalafxml.core.macros.sfxml

@sfxml
class LeaderboardDialogController(
                                   private val leaderboardTable: TableView[Leaderboard],
                                   private val rankColumn: TableColumn[Leaderboard, String],
                                   private val nameColumn: TableColumn[Leaderboard, String],
                                   private val levelColumn: TableColumn[Leaderboard, String],
                                   private val flagsColumn: TableColumn[Leaderboard, String]
                                 ) {
  var dialogStage: Stage = _ // Stage for the dialog

  // Apply a blur effect to the background stage (not shown in this snippet)
  blurBackgroundStage()

  // Initialize TableView with the leaderboard data
  leaderboardTable.items = leaderboardData

  // Configure columns' cell value factories
  // This maps the properties of Leaderboard to the TableView columns
  rankColumn.cellValueFactory = { _.value.rank }
  nameColumn.cellValueFactory = { _.value.name }
  levelColumn.cellValueFactory = { _.value.level }
  flagsColumn.cellValueFactory = { _.value.flags }

  // Close the dialog and remove the blur effect from the background stage
  def back(): Unit = {
    dialogStage.close()
    unblurBackgroundStage() // Remove the blur effect
  }
}