package minesweeper.model

import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}
import scalafx.application.Platform

// Class to manage a countdown timer with functionality to freeze, stop, and add time
class CountdownTimer(initialTime: Int, onUpdate: Int => Unit, onTimeout: () => Unit) {
  // Remaining time on the countdown
  var remainingTime = initialTime
  // Scheduler to handle time-based tasks
  val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
  // Flag to indicate if the timer is frozen
  var isFrozen = false

  // Start the countdown timer
  def start(): Unit = {
    // Task to run every second
    val task = new Runnable {
      def run(): Unit = {
        if (!isFrozen) {
          remainingTime -= 1
        }
        if (remainingTime <= 0) {
          // Update UI and handle timeout when time is up
          Platform.runLater(() => {
            onUpdate(remainingTime)
            stop() // Stop the timer
            onTimeout() // Call the timeout callback
          })
        } else {
          // Update UI with remaining time
          Platform.runLater(() => onUpdate(remainingTime))
        }
      }
    }
    // Schedule the task to run every second
    scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS)
  }

  // Freeze the timer for a specified duration
  def freezeFor(duration: Int): Unit = {
    isFrozen = true
    // Scheduler to handle unfreezing after the duration
    val freezeScheduler = Executors.newScheduledThreadPool(1)
    val unfreezeTask = new Runnable {
      def run(): Unit = {
        isFrozen = false
        freezeScheduler.shutdown()
      }
    }
    // Schedule the unfreeze task
    freezeScheduler.schedule(unfreezeTask, duration, TimeUnit.SECONDS)
  }

  // Stop the countdown timer
  def stop(): Unit = {
    scheduler.shutdown()
  }

  // Add a specified number of seconds to the remaining time
  def addTime(seconds: Int): Unit = {
    remainingTime += seconds
  }

}