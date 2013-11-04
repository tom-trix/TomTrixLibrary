package ru.tomtrix.ttl.gui

import org.eclipse.swt.widgets._
import ru.tomtrix.ttl.GUI._
import org.eclipse.swt.layout.{GridData, GridLayout}
import org.eclipse.swt.SWT
import ru.tomtrix.ttl.gui.ExtendedText.toExtendedTTFText
import org.eclipse.swt.events.{SelectionEvent, SelectionAdapter}

abstract sealed class DialogItemType

object TextBoxItem extends DialogItemType
object ComboBoxItem extends DialogItemType

case class DialogItem(label: String, `type`: DialogItemType, data: Seq[String] = Nil, hint: String = "", selectedIndex: Int = -1)

/**
 * Created with IntelliJ IDEA.
 */
class ExtendedDialog(title: String, items: Seq[DialogItem])(okFunc: Seq[String] => Unit) {
  val shell = new Shell(Display.getDefault, SWT.DIALOG_TRIM | SWT.ON_TOP)
  shell setLayout new GridLayout (2, true)
  shell setText title

  for (item <- items) {
    val label = new Label(shell, SWT.NONE)
    label setText item.label
    label setLayoutData new GridData(SWT.BEGINNING, SWT.CENTER, false, false)
    item.`type` match {
      case TextBoxItem =>
        val txtBox = new Text(shell, SWT.BORDER)
        txtBox setLayoutData new GridData(SWT.FILL, SWT.CENTER, false, false)
        txtBox setMessage item.hint
        if (!item.data.isEmpty)
          txtBox setContent item.data
      case ComboBoxItem =>
        val cmbBox = new Combo(shell, SWT.READ_ONLY)
        cmbBox setLayoutData new GridData(SWT.FILL, SWT.CENTER, false, false)
        cmbBox setItems item.data.toArray
        cmbBox.select(item.selectedIndex)
    }
  }
  val panel = new Composite(shell, SWT.NONE)
  panel setLayout new GridLayout(3, true)
  panel setLayoutData new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1)

  val btnOK = new Button(panel, SWT.FLAT)
  val btnOKData = new GridData(SWT.RIGHT, SWT.CENTER, true, true, 2, 1)
  btnOKData.widthHint = 90
  btnOKData.heightHint = 30
  btnOK setLayoutData btnOKData
  btnOK setText "OK"
  btnOK addSelectionListener new SelectionAdapter {
    override def widgetSelected(e: SelectionEvent) {
      val results = shell.getChildren map {
        case r: Text => r.getText
        case r: Combo => r.getText
        case _ => ""
      }
      okFunc(results.toList filter {!_.isEmpty})
      shell close()
    }
  }

  val btnCancel = new Button(panel, SWT.FLAT)
  val btnCancelData = new GridData(SWT.RIGHT, SWT.CENTER, true, true)
  btnCancelData.widthHint = 90
  btnCancelData.heightHint = 30
  btnCancel setLayoutData btnCancelData
  btnCancel setText "Cancel"
  btnCancel addSelectionListener new SelectionAdapter {
    override def widgetSelected(e: SelectionEvent) {
      shell.close()
    }
  }

  shell pack()
  putToCenter(shell)
  shell open()
}
