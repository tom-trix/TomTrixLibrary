package ru.tomtrix.ttl

import org.eclipse.swt.widgets._
import akka.actor.ActorSystem

object GUI {
  def createApp(title: String, width: Int, height: Int)(f: (Shell, ActorSystem) => Unit) {
    // Akka Actor System
    val system = ActorSystem(title)
    // display & shell
    val display = Display.getDefault
    val shell = new Shell(display)

    // customize the shell
    shell setText title
    shell setSize (width, height)
    shell setMinimumSize shell.getSize
    shell setLocation(display.getBounds.width /2-width/2, display.getBounds.height /2-height/2)

    f(shell, system)

    // main loop
    while (!shell.isDisposed)
      if (!display.readAndDispatch()) display.sleep

    //close everything
    display.dispose()
    system.shutdown()
  }

  def runInGUIThread(f: => Unit) {
    Display.getDefault.asyncExec(new Runnable {
      def run() = f
    })
  }
}
