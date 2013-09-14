package ru.tomtrix

import java.io._
import java.net.URL
import scala.compat.Platform

object TTL  {
  def getHTML(url: String): String = {
    assert(url contains "://", """Specify the protocol (i.e. "http://")""")
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
