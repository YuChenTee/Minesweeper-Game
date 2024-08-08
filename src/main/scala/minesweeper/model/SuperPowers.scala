package minesweeper.model

import scalafx.animation.{FadeTransition, ParallelTransition, ScaleTransition}
import scala.util.Random
import scalafx.scene.control.Button
import scalafx.util.Duration

// Abstract class representing a superpower in the game
abstract class SuperPower(val game: Game) {
  // Amount of superpower usage remaining
  var amount = 1

  // Abstract method to activate the superpower
  def activate(): Seq[Cell]
}

// SuperPower1 class that randomly flags two mine cells
class SuperPower1(_game: Game) extends SuperPower(_game) {

  // Activate the superpower by randomly flagging two mine cells
  def activate(): Seq[Cell] = {
    if (amount > 0) {
      // Collect all unrevealed, unflagged mine cells
      val cells = (for {
        row <- 0 until game.board.rows
        col <- 0 until game.board.cols
        cell = game.board.grid(row)(col)
        if cell.isInstanceOf[MineCell] && !cell.isRevealed.value && !cell.isFlagged.value
      } yield cell).toList

      // Decrement the amount of superpower usage
      amount -= 1

      // Randomly flag 2 mine cells if any exist
      if (cells.nonEmpty) {
        val random = new Random
        return random.shuffle(cells).take(2)
      }
    }
    Seq.empty
  }

  // Apply reveal effect to a button
  def applyRevealEffect(button: Button): Unit = {
    // Fade transition to fade the button in
    val fadeTransition = new FadeTransition {
      duration = Duration(1000) // Duration of 1 second
      fromValue = 0.0
      toValue = 1.0
      node = button
    }

    // Scale transition to enlarge and shrink the button
    val scaleTransition = new ScaleTransition {
      duration = Duration(500) // Duration of 0.5 seconds
      fromX = 1.0
      fromY = 1.0
      toX = 1.2 // Enlarge the button to 120%
      toY = 1.2
      autoReverse = true // Shrink it back to original size
      cycleCount = 2 // Do the scaling twice (enlarge then shrink)
      node = button
    }

    // Combine both transitions into a parallel transition
    val parallelTransition = new ParallelTransition {
      children = Seq(fadeTransition, scaleTransition)
    }

    // Play the transitions
    parallelTransition.play()
  }
}

// SuperPower2 class that freezes the timer for 10 seconds
class SuperPower2(_game: Game) extends SuperPower(_game) {
  // Activate the superpower by freezing the timer
  def activate(): Seq[Cell] = {
    if (amount > 0) {
      // Freeze the timer for 10 seconds
      game.timer.foreach(_.freezeFor(10))
      // Decrement the amount of superpower usage
      amount -= 1
    }
    Seq.empty
  }
}