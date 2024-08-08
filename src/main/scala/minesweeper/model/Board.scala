package minesweeper.model

import scala.collection.mutable.ListBuffer
import scala.util.Random

// Class representing the Minesweeper game board
class Board(val rows: Int, val cols: Int) {
  // 2D array to represent the grid of cells
  var grid: Array[Array[Cell]] = Array.ofDim[Cell](rows, cols)
  // Percentage of cells that should be mines
  val mineCoverage = 0.1
  // Total number of mines on the board
  def mineCount: Int = (rows * cols * mineCoverage).toInt

  // Initialize the grid with normal cells
  def initializeCells(): Unit = {
    for (row <- 0 until rows; col <- 0 until cols) {
      grid(row)(col) = new NormalCell(Coordinate(row, col))
    }
  }

  // Initialize the board on the first click to avoid placing a mine on the first cell clicked
  def initializeFirstClick(coordinate: Coordinate): Unit = {
    val random = new Random
    var placedMines = 0

    // List to keep track of coordinates that should not contain mines
    val safeCells = new ListBuffer[Coordinate]()
    safeCells += coordinate

    // Add adjacent cells to the safe cells list
    forEachAdjacentCellCoordinate(coordinate.row, coordinate.col) { (newRow, newCol) =>
      safeCells += Coordinate(newRow, newCol)
    }

    // Place mines ensuring none of the safe cells are mines
    while (placedMines < mineCount) {
      val row = random.nextInt(rows)
      val col = random.nextInt(cols)
      val coord = Coordinate(row, col)

      if (!safeCells.contains(coord) && !grid(row)(col).isInstanceOf[MineCell]) {
        grid(row)(col) = new MineCell(coord)
        placedMines += 1
      }
    }

    // Calculate the number of adjacent mines for each cell
    calculateAdjacentMines()
  }

  // Higher order method to loop through each adjacent cell
  def forEachAdjacentCellCoordinate(row: Int, col: Int)(f: (Int, Int) => Unit): Unit = {
    for (i <- -1 to 1; j <- -1 to 1) {
      val newRow = row + i
      val newCol = col + j
      if (isInMap(newRow, newCol)) {
        f(newRow, newCol)
      }
    }
  }

  // Check if a given coordinate is within the bounds of the grid
  def isInMap(row: Int, col: Int): Boolean = {
    row >= 0 && row < rows && col >= 0 && col < cols
  }

  // Calculate the number of adjacent mines for each normal cell
  def calculateAdjacentMines(): Unit = {
    for (row <- 0 until rows; col <- 0 until cols) {
      grid(row)(col) match {
        case cell: NormalCell =>
          val adjacentMines = countAdjacentMines(row, col)
          cell.adjacentMines = adjacentMines
        case _ =>
      }
    }
  }

  // Count the number of mines adjacent to a given cell
  def countAdjacentMines(row: Int, col: Int): Int = {
    var count = 0
    forEachAdjacentCellCoordinate(row, col) { (newRow, newCol) =>
      if (grid(newRow)(newCol).isInstanceOf[MineCell]) {
        count += 1
      }
    }
    count
  }
}