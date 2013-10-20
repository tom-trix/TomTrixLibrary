package ru.tomtrix

import akka.actor._
import ru.tomtrix.ttl.GUI._

object AppTest extends App {
  createApp("Form1", 500, 400) { (shell, system) =>
    val model = system.actorOf(Props[Model])
    val view  = system.actorOf(Props(new View(model, shell)))
  }
}