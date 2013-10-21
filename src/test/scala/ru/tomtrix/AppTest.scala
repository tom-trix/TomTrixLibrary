package ru.tomtrix

import akka.actor._
import ru.tomtrix.ttl.GUI._

object AppTest extends App {
  createApp("Trixer", 500, 400) { (shell, system) =>
    system.actorOf(Props[Model], name = "Model")
    system.actorOf(Props(new View(shell)), name = "View")
    system.actorOf(Props[Controller], name = "Controller")
  }
}