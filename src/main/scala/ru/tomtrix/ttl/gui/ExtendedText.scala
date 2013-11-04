package ru.tomtrix.ttl.gui

import scala.collection.mutable.ArrayBuffer
import org.eclipse.swt.events._
import org.eclipse.swt.widgets._
import org.eclipse.swt.{SWT, widgets}
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.graphics.{Image, GC}
import ru.tomtrix.ttl.Keyboard


/**
 * fse
 */
class ExtendedText(tbox: Text) {
  private val shell = new Shell(Display getDefault, SWT.RESIZE | SWT.ON_TOP)
  private val list = new widgets.List(shell, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL)
  private var data = ArrayBuffer[String]()

  shell setLayout new FillLayout
  shell setSize(200, 100)
  shell addShellListener new ShellAdapter {
    override def shellActivated(e: ShellEvent) {
      drawCaretStamp()
    }

    override def shellDeactivated(e: ShellEvent) {
      hide()
    }
  }

  list addMouseListener new MouseAdapter {
    override def mouseUp(e: MouseEvent) {
      accept()
    }
  }
  list addKeyListener new KeyAdapter {
    override def keyPressed(e: KeyEvent) {
      val pos = tbox getCaretPosition()
      e.keyCode match {
        case Keyboard.BACKSPACE => {
          tbox setSelection(pos - 1, pos)
          tbox cut()
          update()
        }
        case Keyboard.DELETE => {
          tbox setSelection(pos, pos + 1)
          tbox cut()
        }
        case Keyboard.ENTER => accept()
        case Keyboard.UP =>
        case Keyboard.DOWN =>
        case SWT.SPACE => accept()
        case SWT.ESC => hide()
        case SWT.ARROW_LEFT => {
          tbox setSelection pos - 1
          update()
          e.doit = false
        }
        case SWT.ARROW_RIGHT => {
          tbox setSelection pos + 1
          update()
          e.doit = false
        }
        case _ => if (e.character.toString != "") {
          tbox insert e.character.toString
          update()
          e.doit = false
        }
      }
    }
  }

  tbox addModifyListener new ModifyListener {
    def modifyText(e: ModifyEvent) {
      if (!shell.isVisible && search() > 0) {
        shell setLocation(tbox.toDisplay(0, 0).x, tbox.toDisplay(0, 0).y + tbox.getSize.y + 5)
        shell open()
      }
    }
  }


  def setContent(content: Seq[String]) {
    data clear()
    data ++= content
    list setItems data.toArray
  }


  private def hide() {
    shell setVisible false
    tbox setBackgroundImage null
    tbox setFocus()
  }

  private def update() {
    if (search() == 0) hide()
    else drawCaretStamp()
  }

  private def accept() {
    if (list.getSelectionIndex >= 0)
      tbox setText list.getSelection.apply(0)
    tbox setSelection tbox.getText.size
    hide()
  }

  private def search() = {
    val s = tbox getText(0, tbox.getCaretPosition-1)
    list setItems data.filter {_ contains s}.toArray
    list getItemCount()
  }

  private def drawCaretStamp() {
    val img = new Image(Display getDefault, tbox.getBounds)
    val gc = new GC(img)
    gc fillRectangle img.getBounds
    gc drawLine(tbox.getCaretLocation.x, 0, tbox.getCaretLocation.x, tbox.getSize.y)
    tbox setBackgroundImage null
    tbox setBackgroundImage img
  }
}

/**
 * grgsd
 */
object ExtendedText {
  /**
   * nfseofnsefrgsg
   * @param source gsrg
   * @return gsr
   */
  implicit def toExtendedTTFText(source: Text) = new ExtendedText(source)
}