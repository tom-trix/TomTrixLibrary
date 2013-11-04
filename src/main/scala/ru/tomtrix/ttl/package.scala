package ru.tomtrix.ttl

import java.io._
import java.net.URL
import scala.compat.Platform
import scala.slick.session.Database
import scala.slick.jdbc.{StaticQuery => Q, GetResult}
import scala.collection.mutable.ListBuffer
import Database.threadLocalSession
import Q.interpolation

sealed trait SLICK_DRIVER
object POSTGRES_DRIVER extends SLICK_DRIVER
object MYSQL_DRIVER extends SLICK_DRIVER
object SQLITE_DRIVER extends SLICK_DRIVER

sealed trait SQLConnection {
  var driver: SLICK_DRIVER
  var host: String
  var port: Int
  var db: String
  var user: String
  var password: String
}

case class MySQLConnection(var db: String) extends SQLConnection {
  var driver: SLICK_DRIVER = MYSQL_DRIVER
  var host: String = "127.0.0.1"
  var port: Int = 3306
  var user: String = "root"
  var password: String = ""
}

case class PostgreSQLConnection(var db: String) extends SQLConnection {
  var driver: SLICK_DRIVER = MYSQL_DRIVER
  var host: String = "127.0.0.1"
  var port: Int = 5432
  var user: String = "postgres"
  var password: String = ""
}

case class SQLiteConnection(var db: String) extends SQLConnection {
  var driver: SLICK_DRIVER = SQLITE_DRIVER
  var host: String = ""
  var port: Int = 5432
  var user: String = ""
  var password: String = ""
}

/**
 * Created with IntelliJ IDEA.
 * User: Tom-Trix
 * Date: 20.10.13
 * Time: 0:30
 */
package object global {
  /**
   * Wraps the code so that all the exceptions/errors will be caught.<br>
   * @param f your code
   * @param finallyFunc code that must be run in a finally clause
   * @tparam T type parameter
   * @return Option[T]
   */
  def safe[T](f: => T, finallyFunc: => Unit = {}): Option[T] = {
    try {
      Some(f)
    }
    catch {
      case e: Throwable => println(s"Safe error: $e"); None
    }
    finally {
      finallyFunc
    }
  }

  def getHTML(url: String): Option[String] = {
    assert(url contains "://", """Specify the protocol (i.e. "http://")""")
    safe {
      val reader = new BufferedReader(new InputStreamReader(new URL(url).openStream))
      var html = new StringBuilder()
      var loop = true
      while (loop)
        reader.readLine() match {
          case null => loop = false
          case x => html ++= Platform.EOL ++= x
        }
      reader.close()
      html.toString()
    }
  }

  private def runSQLSession[T](connection: SQLConnection)(f: => T): Option[T] = {
    safe {
      val (jdbc, drv) = connection.driver match {
        case POSTGRES_DRIVER => "jdbc:postgresql" -> "org.postgresql.Driver"
        case MYSQL_DRIVER => "jdbc:mysql" -> "com.mysql.jdbc.Driver"
        case SQLITE_DRIVER => "jdbc:sqlite" -> "org.sqlite.JDBC"
      }
      Database.forURL(
        connection.host.isEmpty match {
          case true  => s"$jdbc:${connection.db}"
          case false => s"$jdbc://${connection.host}:${connection.port}/${connection.db}"
        },
        user = connection.user,
        password = connection.password,
        driver = drv
      ) withSession f
    }
  }

  def execute(connection: SQLConnection, sql: Seq[String]) {
    runSQLSession(connection) {
      sql foreach {q => sqlu"#$q".execute}
    }
  }

  def execute(connection: SQLConnection, sql: String) {
    execute(connection, Seq(sql))
  }

  def executeQuery[T](connection: SQLConnection, queries: Seq[String])(implicit getResult: GetResult[T]): Option[Seq[List[T]]] = {
    runSQLSession(connection) {
      queries map {q => sql"#$q".as[T].list()}
    }
  }

  def executeQuery[T](connection: SQLConnection, query: String)(implicit getResult: GetResult[T]): Option[List[T]]
    = executeQuery(connection, Seq(query)) map {_.head}

  /**
   * Serializes an object into a byte array
   * @param obj serializable object
   * @return byte array of a serialized object
   */
  def serialize(obj: Serializable): Option[Array[Byte]] = {
    safe {
      val baos = new ByteArrayOutputStream()
      val oos = new ObjectOutputStream(baos)
      oos writeObject obj
      val result = baos toByteArray()
      oos close()
      result
    }
  }

  /**
   * Deserializes an object from a byte array
   * @param buf byte array that keeps an object
   * @return deserialized object
   */
  def deserialize[T <% Serializable](buf: Array[Byte]): Option[T] = {
    safe {
      val ois = new ObjectInputStream(new ByteArrayInputStream(buf))
      val result = ois.readObject().asInstanceOf[T]
      ois close()
      result
    }
  }

  /**
   * Подробнее о методе см. <a href="http://stackoverflow.com/questions/1226555/case-class-to-map-in-scala">тута</a>
   * @param cc Case Class
   * @return
   */
  def caseclassToMap(cc: AnyRef): Map[String, Any] =
    (Map[String, Any]() /: cc.getClass.getDeclaredFields) {(a, f) =>
      f setAccessible true
      if (!f.getName.startsWith("$")) a + (f.getName -> f.get(cc))
      a
    }

  def caseclassToList(cc: AnyRef): List[(String, Any)] =
    (ListBuffer[(String, Any)]() /: cc.getClass.getDeclaredFields) {(a, f) =>
      f setAccessible true
      if (!f.getName.startsWith("$")) a += (f.getName -> f.get(cc))
      a
    }.toList

  def str(obj: Any) = obj match {
    case null => ""
    case x => x.toString
  }
}

object Keyboard {
  val BACKSPACE = 8
  val ENTER = 13
  val DELETE = 127
  val UP = 16777217
  val DOWN = 16777218
}