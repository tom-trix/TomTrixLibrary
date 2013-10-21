package ru.tomtrix.ttl

import scala.collection.mutable.ListBuffer
import akka.actor.ActorRef

case class Register(observer: ActorRef)
case class Deregister(observer: ActorRef)
case class NotifyAll(data: Any)

trait Observable {
  val observers = ListBuffer[ActorRef]()

  def notifyAll[T](data: T) {
    observers foreach {_ ! data}
  }

  def receive: PartialFunction[Any, Unit] = {
    case Register(observer) =>
      observers += observer
    case Deregister(observer) =>
      observers -= observer
  }
}