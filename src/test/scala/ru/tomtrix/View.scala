package ru.tomtrix

import akka.actor._
import org.eclipse.swt.SWT
import org.eclipse.swt.layout._
import org.eclipse.swt.widgets._
import ru.tomtrix.ttl.GUI._
import ru.tomtrix.ttl.global._

class View(model: ActorRef, shell: Shell) extends Actor {

  var table: Table = _

  runInGUIThread {
    shell setLayout new GridLayout
    table = new Table (shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION)
    table setLinesVisible true
    table setHeaderVisible true

    val gridData = new GridData(SWT.FILL, SWT.FILL, true, true)
    gridData.heightHint = 400
    table setLayoutData gridData

    shell.pack()
  }

  //model ! SetFilter(Some("DJ"))
  model ! GetHeaders
  model ! GetData

  def receive = {
    case Headers(headers) => runInGUIThread {
      for (h <- headers)
        new TableColumn(table, SWT.NONE) setText h
    }
    case Data(data) => runInGUIThread {
      data foreach {new TableItem(table, SWT.NONE) setText _.map{t => str(t)}.toArray}
      table.getColumns foreach {_.pack}
    }
  }
}
