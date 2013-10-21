package ru.tomtrix

import akka.actor._


case class SetSearchedText(s: String)


/**
 * Controller
 */
class Controller extends Actor {
  val model = context.actorFor("akka://Trixer/user/Model")
  var view = context.actorFor("akka://Trixer/user/View")

  def receive = {
    case SetSearchedText(s) => s match {
      case "" => model ! SetFilter(None)
      case x  => model ! SetFilter(Some(x.trim))
    }
  }
}
