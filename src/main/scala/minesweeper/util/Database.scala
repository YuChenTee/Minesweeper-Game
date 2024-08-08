package minesweeper.util
import scalikejdbc._
import minesweeper.model.Leaderboard

// referred from Dr Chin's AddressApp
trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"

  val dbURL = "jdbc:derby:myDB;create=true;"; // "create=true" create database named myDB if not exist
  // initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)

  ConnectionPool.singleton(dbURL, "me", "mine") // create connection

  // ad-hoc session provider on the REPL
  implicit val session = AutoSession


}
object Database extends Database{
  def setupDB() = {
    if (!hasDBInitialize)
      Leaderboard.initializeTable()
  }
  def hasDBInitialize : Boolean = {
    DB getTable "leaderboard" match {
      case Some(x) => true
      case None => false
    }

  }
}
