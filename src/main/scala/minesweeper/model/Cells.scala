package minesweeper.model

import scalafx.scene.image.{Image, ImageView}
import scalafx.beans.property.BooleanProperty
import minesweeper.MainApp._

// Abstract base class representing a cell on the Minesweeper board.
abstract class Cell(val coordinate: Coordinate) {
  // Properties to track the cell's state
  val isRevealed: BooleanProperty = BooleanProperty(false)
  val isFlagged: BooleanProperty = BooleanProperty(false)
  val isPermanentlyFlagged: BooleanProperty = BooleanProperty(false)

  // Image view for the flag icon
  val flagImage = new ImageView(new Image("images/flag.png")) {
    fitWidth = 20
    fitHeight = 20
    preserveRatio = true
  }

  // reveal the cell
  def reveal(board: Board): Unit = {
    if (!isRevealed.value && !isFlagged.value && !isPermanentlyFlagged.value) {
      isRevealed.value = true
    }
  }

  // flag the cell
  def flag(): Unit = {
    if (!isRevealed.value && !isPermanentlyFlagged.value) {
      isFlagged.value = !isFlagged.value
    }
  }
}


// Case class representing a coordinate on the board.
case class Coordinate(row: Int, col: Int)


// Class representing a normal cell on the Minesweeper board.
class NormalCell(coordinate: Coordinate) extends Cell(coordinate) {
  var adjacentMines: Int = 0

  // Reveal the cell and recursively reveal adjacent cells if there are no adjacent mines.
  override def reveal(board: Board): Unit = {
    super.reveal(board)
    if (adjacentMines == 0) {
      board.forEachAdjacentCellCoordinate(coordinate.row, coordinate.col) { (newRow, newCol) =>
        val neighbourCell = board.grid(newRow)(newCol)
        if (neighbourCell.isInstanceOf[NormalCell] && !neighbourCell.isRevealed.value) {
          neighbourCell.reveal(board)
        }
      }
    }
  }
}


// Class representing a mine cell on the Minesweeper board.
class MineCell(val _coordinate: Coordinate) extends Cell(_coordinate) {
  // Image view for the bomb icon
  val image = new ImageView(new Image("images/bomb.png")) {
    fitWidth = 20
    fitHeight = 20
    preserveRatio = true
  }

  // Flag the cell and update the game state.
  override def flag(): Unit = {
    super.flag()
    if (isFlagged.value) {
      isPermanentlyFlagged.value = true
      game.totalFlags.value += 1
    }
  }

  // Reveal the cell and play the bomb audio.
  override def reveal(board: Board): Unit = {
    super.reveal(board)
    playBombAudio()
  }
}