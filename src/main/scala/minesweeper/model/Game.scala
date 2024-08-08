package minesweeper.model

import scalafx.beans.property.IntegerProperty
import minesweeper.MainApp.{showGameOverDialog, showVictoryDialog}
import java.util.concurrent.{Executors, TimeUnit}
import scalafx.application.Platform
import scalafx.scene.control.Button


//Class representing the Minesweeper game.
class Game(var board: Board) {
  var state: String = "Playing"  // Current state of the game
  var level: IntegerProperty = IntegerProperty(1)  // Current level of the game
  var timer: Option[CountdownTimer] = None  // Option timer object
  var timeLeft: Int = 60  // Time left in seconds
  var minesLeft: IntegerProperty = IntegerProperty(board.mineCount)  // Number of mines left to be flagged
  var superPower1: SuperPower1 = new SuperPower1(this)  // SuperPower1 instance
  var superPower2: SuperPower2 = new SuperPower2(this)  // SuperPower2 instance
  var totalFlags: IntegerProperty = IntegerProperty(0)  // Total number of flags placed
  var playerName: String = ""  // Player's name

  // Reset game variables for a new game.
  def resetVariables(): Unit = {
    level.value = 1
    totalFlags.value = 0
    state = "Playing"
    timeLeft = 60
    minesLeft.value = board.mineCount
    superPower1 = new SuperPower1(this)
    superPower2 = new SuperPower2(this)
  }

  // Start a new game by initializing a new board and cells.
  def startNewGame(): Unit = {
    board = new Board(board.rows, board.cols)
    minesLeft = IntegerProperty(board.mineCount)
    superPower1 = new SuperPower1(this)
    superPower2 = new SuperPower2(this)
    board.initializeCells()
  }

  // Set the game timer.
  def setTimer(timer: CountdownTimer): Unit = {
    this.timer = Some(timer)
  }

  // Check for victory condition by ensuring all mines are flagged.
  def checkVictory(): Unit = {
    for (row <- 0 until board.rows; col <- 0 until board.cols) {
      board.grid(row)(col) match {
        case cell: MineCell =>
          if (!cell.isFlagged.value) return
        case _ =>
      }
    }
    state = "Victory"
    timer.get.stop()  // Stop the timer before displaying the victory dialog
    showVictoryDialog()
  }

   // Check for loss condition when a mine cell is revealed.
   // getButtonAt Function to get the button at specific coordinates.
  def checkLoss(cell: Cell, getButtonAt: (Int, Int) => Button): Unit = {
    if (cell.isInstanceOf[MineCell]) {
      state = "Loss"
      timer.get.stop()  // Stop the timer before displaying the game over dialog
      revealAllCellsGradually(5, getButtonAt) { () =>
        showGameOverDialog()
      }
    }
  }

  // Reveal all cells gradually with a delay.
  // getButtonAt Function to get the button at specific coordinates.
  // onComplete Callback function to be executed when all cells are revealed.
  def revealAllCellsGradually(delay: Int, getButtonAt: (Int, Int) => Button)(onComplete: () => Unit): Unit = {
    val scheduler = Executors.newScheduledThreadPool(1)
    val cells = for {
      row <- 0 until board.rows
      col <- 0 until board.cols
    } yield board.grid(row)(col)

    var index = 0

    val task = new Runnable {
      def run(): Unit = {
        if (index < cells.length) {
          val cell = cells(index)
          Platform.runLater(new Runnable {
            def run(): Unit = {
              if (cell.isInstanceOf[NormalCell]) {
                val button = getButtonAt(cell.coordinate.row, cell.coordinate.col)
                button.style = "-fx-background-color: lightgrey"
              }
              cell.reveal(board)
            }
          })
          index += 1
        } else {
          scheduler.shutdown()
          Platform.runLater(new Runnable {
            def run(): Unit = onComplete()
          })
        }
      }
    }
    scheduler.scheduleAtFixedRate(task, 0, delay, TimeUnit.MILLISECONDS)
  }
}