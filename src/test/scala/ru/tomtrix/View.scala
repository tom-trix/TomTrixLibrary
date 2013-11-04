package ru.tomtrix

import akka.actor._
import org.eclipse.swt.SWT
import org.eclipse.swt.layout._
import org.eclipse.swt.events._
import org.eclipse.swt.widgets._
import ru.tomtrix.ttl.GUI._
import ru.tomtrix.ttl.global._
import ru.tomtrix.ttl.Register
import ru.tomtrix.ttl.gui.{ComboBoxItem, TextBoxItem, DialogItem, ExtendedDialog}


case object FilterChanged
case class Headers(headers: Seq[String])
case class Data(data: scala.List[Seq[Any]])
case class ShowItemDialog(artistOpt: Option[String], songOpt: Option[String])


class View(shell: Shell) extends Actor {
  val model = context.actorFor("akka://Trixer/user/Model")
  var controller = context.actorFor("akka://Trixer/user/Controller")

  var table: Table = _

  runInGUIThread {
    shell setLayout new GridLayout(2, true)

    // table
    table = new Table (shell, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION)
    table setLinesVisible true
    table setHeaderVisible true
    val gdTable = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1)
    table setLayoutData gdTable

    //search textbox
    val searchTextbox = new Text(shell, SWT.BORDER | SWT.SEARCH | SWT.ICON_SEARCH)
    searchTextbox setMessage "Поиск..."
    searchTextbox setLayoutData new GridData(SWT.FILL, SWT.NONE, true, false)
    searchTextbox.addModifyListener(new ModifyListener {
      def modifyText(e: ModifyEvent) {
        controller ! SetSearchedText(searchTextbox.getText)
      }
    })

    //new item button
    val newButton = new Button(shell, SWT.CENTER)
    newButton setText "New"
    val gdNewButton = new GridData(SWT.CENTER, SWT.NONE, false, false)
    gdNewButton.widthHint = 100
    newButton setLayoutData gdNewButton
    newButton addSelectionListener new SelectionAdapter {
      override def widgetSelected (e: SelectionEvent) {
        controller ! NewItem
      }
    }

    shell.open()
  }

  model ! Register(context.self)
  model ! GetHeaders
  model ! GetData

  def receive = {
    case Headers(headers) => runInGUIThread {
      for (h <- headers)
        new TableColumn(table, SWT.NONE) setText h
    }
    case Data(data) => runInGUIThread {
      table removeAll()
      data foreach {new TableItem(table, SWT.NONE) setText _.map{t => str(t)}.toArray}
      table.getColumns foreach {_.pack}
    }
    case FilterChanged =>
      model ! GetData
    case ShowItemDialog(artistOpt, songOpt) => runInGUIThread {
      new ExtendedDialog("Новая композиция", Seq(
        DialogItem("Исполнитель", TextBoxItem, Nil, "Введите имя..."),
        DialogItem("Композиция", TextBoxItem, Nil, "Введите название..."),
        DialogItem("Бла-бла", ComboBoxItem, Seq("fs", "bnhdui", "sff"), "ger", 0)
      )) (results =>
        println(results)
      )
    }
  }
}
