package minesweeper.model

import minesweeper.MainApp.game
import minesweeper.util.Database
import scalafx.beans.property.StringProperty
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

import scala.util.Try

// Class representing a leaderboard entry
class Leaderboard(val nameS: String, val levelS: Int, val flagsS: Int) {

  // Secondary constructor with default values
  def this() = this(null, 0, 0)

  // Properties for the leaderboard entry
  val rank: StringProperty = StringProperty("0")
  val name: StringProperty = StringProperty(nameS)
  val level: StringProperty = StringProperty(levelS.toString)
  val flags: StringProperty = StringProperty(flagsS.toString)

  // Save the leaderboard entry to the database
  def save(): Try[Int] = {
    println("Saving leaderboard entry...")
    Try(DB autoCommit { implicit session =>
      sql"""
        INSERT INTO leaderboard (name, level, flags)
        VALUES (${name.value}, ${level.value.toInt}, ${flags.value.toInt})
      """.update.apply()
    }).recover {
      // Handle any exceptions that occur during saving
      case e: Exception =>
        e.printStackTrace()
        0
    }
  }
}

// Companion object for Leaderboard
object Leaderboard extends Database {

  // Create a new leaderboard entry
  def apply(nameS: String, levelS: Int, flagsS: Int): Leaderboard = {
    new Leaderboard(nameS, levelS, flagsS) {}
  }

  // Get the minimum number of flags from the leaderboard
  def getMinFlagsFromLeaderboard(): Int = {
    val leaderboards = Leaderboard.getAllLeaderboard
    if (leaderboards.isEmpty) 0
    else leaderboards.last.flags.value.toInt
  }

  // Update the leaderboard with a new player's score
  def updateLeaderboard(playerName: String, newFlags: Int): Unit = {
    val leaderboards = Leaderboard.getAllLeaderboard
    val maxEntries = 10

    // Remove the lowest-ranked entry if the leaderboard is full
    if (leaderboards.size >= maxEntries) {
      deleteLeaderboardEntry()
    }

    // Create and save a new leaderboard entry
    val name = playerName
    val level = game.level.value
    val leaderboard = Leaderboard(name, level, newFlags)
    leaderboard.save()
  }

  // Delete the entry with the minimum number of flags
  def deleteLeaderboardEntry(): Unit = {
    DB autoCommit { implicit session =>
      // Find the ID of the entry with the minimum number of flags
      val idToDelete = sql"""
        SELECT id
        FROM leaderboard
        ORDER BY flags ASC
        FETCH FIRST 1 ROW ONLY
      """.map(rs => rs.int("id")).single.apply()

      // Delete the entry if a valid ID is found
      idToDelete.foreach { id =>
        sql"""
          DELETE FROM leaderboard
          WHERE id = ${id}
        """.update.apply()
      }
    }
  }

  // Initialize the leaderboard table in the database
  def initializeTable(): Unit = {
    DB autoCommit { implicit session =>
      sql"""
        CREATE TABLE leaderboard (
          id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          name VARCHAR(64),
          level INT,
          flags INT
        )
      """.execute.apply()
    }
  }

  // Get all leaderboard entries and rank them
  def getAllLeaderboard: List[Leaderboard] = {
    DB readOnly { implicit session =>
      val rawData = sql"""
        SELECT id, name, level, flags
        FROM leaderboard
        ORDER BY level DESC, flags DESC
      """.map(rs => (rs.int("id"), rs.string("name"), rs.int("level"), rs.int("flags"))).list.apply()

      // Manually assign ranks based on the order of entries
      rawData.zipWithIndex.map { case ((id, name, level, flags), index) =>
        val leaderboard = Leaderboard(name, level, flags)
        leaderboard.rank.value = (index + 1).toString
        leaderboard
      }
    }
  }
}