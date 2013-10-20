package ru.tomtrix.ttl

import scala.slick.jdbc.GetResult
import ru.tomtrix.ttl.global._

class DataTable[Row <: AnyRef](connection: SQLConnection, query: String, titles: Map[String, String] = Map.empty, invisibleFields: Set[String] = Set.empty)(implicit getResult: GetResult[Row]) {
  private val data: List[Row] = executeQuery(connection, query) getOrElse Nil
  private val headers = data.headOption map caseclassToList getOrElse Nil map {_._1}

  def getHeaders(translate: Boolean = true): Seq[String] = {
    val filtered = headers filterNot invisibleFields.contains
    translate match {
      case true  => filtered map {header => titles getOrElse (header, header)}
      case false => filtered
    }
  }

  def getHeadersAll(translate: Boolean = true): Seq[String] =
    translate match {
      case true  => headers map {header => titles getOrElse (header, header)}
      case false => headers
  }

  def getData(search: Option[String] = None): List[Seq[Any]] = {
    val alldata = data map caseclassToList map {t => t filterNot {elem => invisibleFields contains elem._1} map {_._2}}
    search match {
      case Some(s) => alldata filter {_ exists {str(_) contains s}}
      case None    => alldata
    }
  }

  def getDataAll(search: Option[String] = None): List[Seq[Any]] = {
    val alldata = data map caseclassToList map {_ map {_._2}}
    search match {
      case Some(s) => alldata filter {_ exists {str(_) contains s}}
      case None    => alldata
    }
  }

  def getRow(n: Int): Row = data(n)
}
