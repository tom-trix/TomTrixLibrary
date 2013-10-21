package ru.tomtrix

import scala.slick.jdbc.GetResult
import akka.actor.Actor
import ru.tomtrix.ttl._


case class SetFilter(s: Option[String])
case object GetData
case object GetHeaders


class Model extends Actor with Observable {
  case class SingerSong(singerID: Long, singer: String, songID: Long, song: String, mix: String)
  implicit val getResult = GetResult(r => SingerSong(r.<<, r.<<, r.<<, r.<<, r.<<))

  val dataTable = new DataTable(
    SQLiteConnection("Tom-Trix-Music.sqlite"),
    "SELECT Singer.singerID, Singer.name, Songs.songID, Songs.name, Songs.mix FROM Singer JOIN Songs USING(singerID)",
    Map("singer" -> "Исполнитель", "song" -> "Название"),
    Set("singerID", "songID")
  )

  var filter: Option[String] = None

  override def receive = super.receive orElse {
    case SetFilter(s) =>
      filter = s
      notifyAll(FilterChanged)
    case GetData =>
      sender ! Data(dataTable getData filter)
    case GetHeaders =>
      sender ! Headers(dataTable getHeaders true)
  }
}
