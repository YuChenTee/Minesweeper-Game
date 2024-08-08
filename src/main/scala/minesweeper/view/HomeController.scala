package minesweeper.view

import minesweeper.MainApp
import minesweeper.MainApp.{game, playHomeMusic, showLeaderboardDialog}
import scalafxml.core.macros.sfxml
import scalafx.animation.{ScaleTransition, Transition}
import scalafx.util.Duration
import scalafx.scene.image.ImageView

@sfxml
class HomeController(private val logoImage: ImageView) {

  playHomeMusic()

  // Initialize the scaling animation when the controller is set up
  def initialize(): Unit = {
    // Start the scaling animation
    scaleTransition.play()
  }

  // Define the scaling animation for the logo image
  val scaleTransition = new ScaleTransition {
    node = logoImage
    fromX = 1.0
    fromY = 1.0
    toX = 1.5
    toY = 1.5
    cycleCount = Transition.Indefinite // Repeat indefinitely
    autoReverse = true // Scale back to original size
    duration = Duration(1000) // Duration for one complete cycle (1 second)
  }

  // Start a new game by resetting variables and showing the game view
  def start(): Unit = {
    game.resetVariables()
    MainApp.showGame()
  }

  // Show the leaderboard dialog
  def leaderboard(): Unit = {
    showLeaderboardDialog()
  }

  // Exit the application
  def exit(): Unit = {
    System.exit(0)
  }
}