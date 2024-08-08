package minesweeper.view

import minesweeper.MainApp
import scalafx.Includes._
import minesweeper.MainApp._
import minesweeper.model.{Cell, CountdownTimer, MineCell, NormalCell}
import scalafxml.core.macros.sfxml
import scalafx.scene.control.{Button, Label}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.GridPane
import scalafx.animation.{FadeTransition, ParallelTransition}
import scalafx.scene.image.ImageView
import scalafx.util.Duration

import java.util.concurrent.Executors

@sfxml
class GameController(
                      private val LevelLabel: Label,
                      private val gridPane: GridPane,
                      private val timerLabel: Label,
                      private val messageLabel: Label,
                      private val MineLeftLabel: Label,
                      private val superpower1Image: ImageView,
                      private val superpower2Image: ImageView
                    ) {
  // Flag to check if it's the first click
  var isFirstClick = true
  // Initialize timer with the game's time left, update and timeout callbacks

  game.setTimer(new CountdownTimer(game.timeLeft, updateTimerLabel, () => MainApp.showGameOverDialog())) // instantiate a timer to the game class
  playGameMusic()

  // Scheduler for additional tasks
  val scheduler = Executors.newScheduledThreadPool(1)

  // Handle application close event by stopping the timer
  MainApp.stage.setOnCloseRequest(_ => game.timer.get.stop())

  // Pause the game and show pause dialog
  def pause(): Unit = {
    game.timer.get.isFrozen = true // Pause the timer
    showPauseDialog()
  }

  // Pause the game and show help dialog
  def help(): Unit = {
    game.timer.get.isFrozen = true // Pause the timer
    showHelpDialog()
  }

  // Convert button to corresponding cell
  def buttonToCell(button: Button): Cell = {
    val row = Option(GridPane.getRowIndex(button)).getOrElse(0).asInstanceOf[Int]
    val col = Option(GridPane.getColumnIndex(button)).getOrElse(0).asInstanceOf[Int]
    game.board.grid(row)(col)
  }

  // Get button at specified row and column
  def getButtonAt(row: Int, col: Int): Button = {
    gridPane.getChildren.filter(node => GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col)
      .head.asInstanceOf[javafx.scene.control.Button]
  }

  // Initialize the game grid and UI elements
  def initialize(): Unit = {
    // Create buttons in the grid
    for (row <- 0 until game.board.rows; col <- 0 until game.board.cols) {
      val button = new Button()
      bindCellButtons(button)
      button.setPrefWidth(50)
      button.setPrefHeight(20)
      gridPane.add(button, col, row)
    }

    // Set initial game state and UI labels
    game.state = "Playing"
    MineLeftLabel.text = "Mines left: " + game.minesLeft.value
    timerLabel.text = "Time left: " + game.timeLeft.toString + "s"
    LevelLabel.text = "Level: " + game.level.value
  }

  // Bind mouse events to cell buttons
  def bindCellButtons(button: Button): Unit = {
    button.onMousePressed = (event: MouseEvent) => {
      if (event.isPrimaryButtonDown) {
        revealCell(event)
      } else if (event.isSecondaryButtonDown) {
        flagCell(event)
      }
    }
  }

  // Bind mouse events to superpower buttons
  def bindSuperPowerButton(): Unit = {
    superpower1Image.onMouseClicked = (event: MouseEvent) => {
      if (game.superPower1.amount > 0) {
        activateSuperPower1(game.superPower1.activate(), event)
      }
      updateSuperPower1Image()
    }

    superpower2Image.onMouseClicked = (event: MouseEvent) => {
      if (game.superPower2.amount > 0) {
        game.superPower2.activate() // Activate superpower effect
        setMessage("Time Frozen for 10 seconds!", "blue", event.getSceneX, event.getSceneY)
      }
      updateSuperPower2Image()
    }
  }

  // Bind listeners to cell properties and update UI accordingly
  def bindListeners(cell: Cell, button: Button): Unit = {
    cell.isRevealed.onChange((_, _, isRevealed) => {
      if (isRevealed) {
        cell match {
          case mine: MineCell =>
            button.graphic = mine.image
            button.styleClass.add("revealed-mine-cell")
          case normalCell: NormalCell =>
            if (normalCell.adjacentMines != 0) {
              button.text = normalCell.adjacentMines.toString
            }
            button.styleClass.add("revealed-normal-cell")
        }
      }
    })

    cell.isFlagged.onChange((_, _, isFlagged) => {
      button.graphic = if (isFlagged) cell.flagImage else null
    })

    cell.isPermanentlyFlagged.onChange((_, _, isPermanentlyFlagged) => {
      if (isPermanentlyFlagged) {
        button.styleClass.add("permanently-flagged-cell")
        button.graphic = cell.flagImage
      }
    })

    game.minesLeft.onChange(updateMinesLeftLabel())
    game.level.onChange(updateLevelLabel())
  }

  // Update superpower 1 image based on amount
  def updateSuperPower1Image(): Unit = {
    if (game.superPower1.amount == 0) {
      superpower1Image.opacity = 0.3
      superpower1Image.setOnMouseClicked(null) // Disable click
    }
  }

  // Update superpower 2 image based on amount
  def updateSuperPower2Image(): Unit = {
    if (game.superPower2.amount == 0) {
      superpower2Image.opacity = 0.3
      superpower2Image.setOnMouseClicked(null) // Disable click
    }
  }

  // Activate superpower 1 and reveal cells
  def activateSuperPower1(cells: Seq[Cell], event: MouseEvent): Unit = {
    cells.foreach { cell =>
      val button = getButtonAt(cell.coordinate.row, cell.coordinate.col)
      cell.flag()
      game.superPower1.applyRevealEffect(button)
      game.minesLeft.value -= 1
      setMessage("Reveal Activated!!!", "purple", event.getSceneX, event.getSceneY)
    }
    game.checkVictory()
  }

  // Reveal a cell when clicked
  def revealCell(event: MouseEvent): Unit = {
    if (game.state.equals("Playing")) {
      val cell = buttonToCell(event.getSource.asInstanceOf[javafx.scene.control.Button])
      if (isFirstClick) {
        isFirstClick = false
        game.timer.get.start()
        game.board.initializeFirstClick(cell.coordinate)
        for (row <- 0 until game.board.rows; col <- 0 until game.board.cols) {
          val cell = game.board.grid(row)(col)
          val button = getButtonAt(row, col)
          bindListeners(cell, button)
        }
        bindSuperPowerButton()
      }
      if (!cell.isRevealed.value && !cell.isFlagged.value) {
        cell.reveal(game.board)
        game.checkLoss(cell, getButtonAt)
        if (game.state == "Loss") {
          setMessage("KABOOOM!!!", "red", event.getSceneX, event.getSceneY)
        }
      }
    }
  }

  // Flag a cell when right-clicked
  def flagCell(event: MouseEvent): Unit = {
    if (game.state.equals("Playing") && !isFirstClick) {
      val cell = buttonToCell(event.getSource.asInstanceOf[javafx.scene.control.Button])
      if (!cell.isRevealed.value) {
        if (cell.isInstanceOf[MineCell]) {
          if (!cell.isFlagged.value) {
            game.timer.get.addTime(5)
            cell.flag()
            game.minesLeft.value -= 1
            setMessage("Correct Flag! +5 secs.", "green", event.getSceneX, event.getSceneY)
            game.checkVictory()
          }
        } else if (cell.isInstanceOf[NormalCell]) {
          game.timer.get.addTime(-10)
          setMessage("Wrong Flag! -10 secs.", "red", event.getSceneX, event.getSceneY)
        }
      }
    }
  }

  // Update the timer label with the remaining time
  def updateTimerLabel(time: Int): Unit = {
    timerLabel.text = if (time > 0) s"Time left: ${time}s" else "Time's up!"
  }

  // Update the mines left label
  def updateMinesLeftLabel(): Unit = {
    MineLeftLabel.text = "Mines Left: " + game.minesLeft.value
  }

  // Update the level label
  def updateLevelLabel(): Unit = {
    LevelLabel.text = "Level: " + game.level.value
  }

  // Display a message to the player
  def setMessage(text: String, color: String, x: Double, y: Double): Unit = {
    messageLabel.text = text
    messageLabel.style = s"""
      -fx-text-fill: $color;
      -fx-font-size: 15px;
      -fx-font-weight: Bold;
      -fx-background-color: white;
      -fx-border-color: black;
      -fx-border-width: 2px;
      -fx-padding: 2px;
      -fx-background-radius: 3px;
      -fx-border-radius: 3px;
    """
    messageLabel.layoutX = x + 10
    messageLabel.layoutY = y + 10
    messageLabel.mouseTransparent = true // Make the label ignore mouse events

    val fadeIn = new FadeTransition {
      duration = Duration(200)
      node = messageLabel
      fromValue = 0.0
      toValue = 1.0
    }

    val fadeOut = new FadeTransition {
      duration = Duration(200)
      node = messageLabel
      fromValue = 1.0
      toValue = 0.0
      delay = Duration(1000) // Delay before starting the fade out
    }

    val parallelTransition = new ParallelTransition {
      children = Seq(fadeIn, fadeOut)
    }

    parallelTransition.play()
  }
}