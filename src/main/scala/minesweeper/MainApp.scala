package minesweeper

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import javafx.{scene => jfxs}
import minesweeper.model._
import minesweeper.util.Database
import minesweeper.view.{GameController, GameOverDialogController, HelpDialogController, HighScoreDialogController, HomeController, LeaderboardDialogController, PauseDialogController, VictoryDialogController}
import scalafx.collections.ObservableBuffer
import scalafx.scene.effect.GaussianBlur
import scalafx.stage.{Modality, Stage, StageStyle}
import scalafx.scene.media.{AudioClip, Media, MediaPlayer}

object MainApp extends JFXApp {

  // Initialize the database
  Database.setupDB()
  var leaderboardData = new ObservableBuffer[Leaderboard]()
  leaderboardData ++= Leaderboard.getAllLeaderboard

  // Declare MediaPlayer variable
  var mediaPlayer: MediaPlayer = null

  // Load the root layout from the FXML file
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()
  val roots = loader.getRoot[jfxs.layout.BorderPane]

  // Initialize the main stage
  stage = new PrimaryStage {
    title = "Minesweeper"
    initStyle(StageStyle.Undecorated) // Window without close button
    scene = new Scene {
      root = roots
    }
  }

  // Show the home screen
  def showHome() = {
    val resource = getClass.getResource("view/Home.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
    val controller = loader.getController[HomeController#Controller]
    controller.initialize()
  }

  // Show the game screen
  def showGame() = {
    val resource = getClass.getResource("view/Game.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
    game.startNewGame()
    val controller = loader.getController[GameController#Controller]
    controller.initialize()
  }

  // Show the Game Over dialog
  def showGameOverDialog(): Unit = {
    val resource = getClass.getResourceAsStream("view/GameOverDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[GameOverDialogController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal) // Let the window stay on top regardless of anything happening behind
      initOwner(stage) // Primary stage object
      initStyle(StageStyle.Undecorated) // Window without close button
      scene = new Scene {
        root = roots2
        stylesheets = List(getClass.getResource("view/style.css").toString)
      }
    }
    control.dialogStage = dialog
    dialog.showAndWait()
  }

  // Show the Victory dialog
  def showVictoryDialog(): Unit = {
    val resource = getClass.getResourceAsStream("view/VictoryDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[VictoryDialogController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.Undecorated)
      scene = new Scene {
        root = roots2
        stylesheets = List(getClass.getResource("view/style.css").toString)
      }
    }
    control.dialogStage = dialog
    dialog.showAndWait()
  }

  // Show the Pause dialog
  def showPauseDialog(): Unit = {
    val resource = getClass.getResourceAsStream("view/PauseDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[PauseDialogController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.Undecorated)
      scene = new Scene {
        root = roots2
        stylesheets = List(getClass.getResource("view/style.css").toString)
      }
    }
    control.dialogStage = dialog
    dialog.showAndWait()
  }

  // Show the Help dialog
  def showHelpDialog(): Unit = {
    val resource = getClass.getResourceAsStream("view/HelpDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[HelpDialogController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.Undecorated)
      scene = new Scene {
        root = roots2
        stylesheets = List(getClass.getResource("view/style.css").toString)
      }
    }
    control.dialogStage = dialog
    dialog.showAndWait()
  }

  // Show the Leaderboard dialog
  def showLeaderboardDialog(): Unit = {
    val resource = getClass.getResourceAsStream("view/LeaderboardDialog.fxml")
    val loader = new FXMLLoader(null, NoDependencyResolver)
    loader.load(resource)
    val roots2 = loader.getRoot[jfxs.Parent]
    val control = loader.getController[LeaderboardDialogController#Controller]
    val dialog = new Stage() {
      initModality(Modality.ApplicationModal)
      initOwner(stage)
      initStyle(StageStyle.Undecorated)
      scene = new Scene {
        root = roots2
        stylesheets = List(getClass.getResource("view/style.css").toString)
      }
    }
    control.dialogStage = dialog
    dialog.showAndWait()
  }

  // Show the High Score dialog if conditions are met
  def showHighScoreDialog(): Unit = {
    val leaderboardSize = Leaderboard.getAllLeaderboard.size
    // show dialog either if the the total flags is higher than the minimum flag in the leaderboard or the leaderboard rows are lesser than 10
    if (Leaderboard.getMinFlagsFromLeaderboard() < game.totalFlags.value || leaderboardSize < 10) {
      val resource = getClass.getResourceAsStream("view/HighScoreDialog.fxml")
      val loader = new FXMLLoader(null, NoDependencyResolver)
      loader.load(resource)
      val roots2 = loader.getRoot[jfxs.Parent]
      val control = loader.getController[HighScoreDialogController#Controller]
      val dialog = new Stage() {
        initModality(Modality.ApplicationModal)
        initOwner(stage)
        initStyle(StageStyle.Undecorated)
        scene = new Scene {
          root = roots2
          stylesheets = List(getClass.getResource("view/style.css").toString)
        }
      }
      control.dialogStage = dialog
      dialog.showAndWait()
    }
  }

  // Apply Gaussian blur effect to the main stage
  def blurBackgroundStage(): Unit = {
    stage.scene().root().effect = new GaussianBlur(20)
  }

  // Remove the Gaussian blur effect from the main stage
  def unblurBackgroundStage(): Unit = {
    stage.scene().root().effect = null
  }

  // Play the background music
  def playHomeMusic(): Unit = {
    val musicResource = getClass.getResource("/audio/home.mp3")
    val media = new Media(musicResource.toString)
    if (mediaPlayer != null) mediaPlayer.stop()
    mediaPlayer = new MediaPlayer(media)
    mediaPlayer.setCycleCount(MediaPlayer.Indefinite) // Play the music in a loop
    mediaPlayer.play()
  }

  // Play the background music
  def playGameMusic(): Unit = {
    val musicResource = getClass.getResource("/audio/game.mp3")
    val media = new Media(musicResource.toString)
    if (mediaPlayer != null) mediaPlayer.stop()
    mediaPlayer = new MediaPlayer(media)
    mediaPlayer.setCycleCount(MediaPlayer.Indefinite) // Play the music in a loop
    mediaPlayer.play()
  }

  // Play the victory music and stop the background music
  def playVictoryMusic(): Unit = {
    val musicResource = getClass.getResource("/audio/victory.mp3")
    val media = new Media(musicResource.toString)
    if (mediaPlayer != null) mediaPlayer.stop() // Stop the background music if playing
    mediaPlayer = new MediaPlayer(media)
    mediaPlayer.play()
  }

  // Play the game over music and stop the background music
  def playGameOverMusic(): Unit = {
    val musicResource = getClass.getResource("/audio/gameover.mp3")
    val media = new Media(musicResource.toString)
    if (mediaPlayer != null) mediaPlayer.stop() // Stop the background music if playing
    mediaPlayer = new MediaPlayer(media)
    mediaPlayer.play()
  }

  // Play the bomb sound effect
  def playBombAudio(): Unit = {
    val clickSoundResource = getClass.getResource("/audio/bomb.mp3")
    val clickSound = new AudioClip(clickSoundResource.toString)
    clickSound.play()
  }

  // Initialize a new game
  val game = new Game(new Board(15, 12))
  showHome()
  playHomeMusic()
}