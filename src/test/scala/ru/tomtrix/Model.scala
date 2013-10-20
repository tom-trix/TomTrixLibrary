package ru.tomtrix

import akka.actor.Actor
import ru.tomtrix.ttl.{SQLiteConnection, DataTable, Observable}
import scala.slick.jdbc.GetResult



abstract protected sealed class ModelMessage
case class SetFilter(s: Option[String]) extends ModelMessage
case object FilterChanged extends ModelMessage
case object GetData extends ModelMessage
case class Data(data: List[Seq[Any]]) extends ModelMessage
case object GetHeaders extends ModelMessage
case class Headers(headers: Seq[String]) extends ModelMessage



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

  def receive = {
    case SetFilter(s) =>
      filter = s
      notifyAll(FilterChanged)
    case GetData =>
      sender ! Data(dataTable getData filter)
    case GetHeaders =>
      sender ! Headers(dataTable getHeaders true)
  }
}
