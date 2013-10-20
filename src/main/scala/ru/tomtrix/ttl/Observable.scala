package ru.tomtrix.ttl

import scala.collection.mutable.ListBuffer
import akka.actor.ActorRef

trait Observable {
  val observers = ListBuffer[ActorRef]()

  def register(observer: ActorRef) {
    observers += observer
  }

  def deregister(observer: ActorRef) {
    observers -= observer
  }

  def notifyAll[T](data: T) {
    observers foreach {_ ! data}
  }
}