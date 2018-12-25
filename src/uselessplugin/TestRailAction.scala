package uselessplugin

import java.awt.Color

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent, CommonDataKeys}
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.{Balloon, JBPopupFactory}
import com.intellij.psi.PsiElement
import com.intellij.ui.awt.RelativePoint
import javax.swing.SwingWorker
import javax.swing.event.HyperlinkEvent
import uselessplugin.testrail.{TestCase, TestRailIntegration}

import scala.util.{Failure, Success, Try}


class TestRailAction extends AnAction {
  override def actionPerformed(e: AnActionEvent): Unit = {
    for (element <- getPsiElementFromContext(e);
         caseNumberOpt = "\\d+".r.findFirstIn(element.getText)) {

      val editor = e.getRequiredData(CommonDataKeys.EDITOR)

      if (TestRailIntegration.credsOpt.isEmpty)
        new CredsInputDialog(e.getProject, TestRailIntegration.saveCreds).show()
      else {
        caseNumberOpt match {
          case Some(caseNumber) =>
            val balloonText = CaseFormatter.formatLoading(caseNumber)
            val balloon = showBalloon(editor, balloonText)

            new SwingWorker[Try[TestCase], Void]() {
              override def doInBackground(): Try[TestCase] = Try {
                TestRailIntegration.getCase(caseNumber)
              }

              override def done(): Unit = {
                if (!balloon.isDisposed) {
                  balloon.setAnimationEnabled(false)
                  balloon.hide()
                  get() match {
                    case Success(testCase) => showBalloon(editor, CaseFormatter.formatCase(testCase))
                    case Failure(exception) => showBalloon(editor, exception.getLocalizedMessage)
                  }
                }
              }
            }.execute()

          case None => showBalloon(editor, "Couldn't parse case number")
        }
      }
    }
  }

  private def showBalloon(editor: Editor, balloonText: String): Balloon = {
    val balloon = JBPopupFactory.getInstance()
      .createHtmlTextBalloonBuilder(
        balloonText, null, Color.WHITE, (e: HyperlinkEvent) => {
          if (e.getEventType == HyperlinkEvent.EventType.ACTIVATED)
            BrowserUtil.browse(e.getURL)
        })
      .createBalloon()
    val point = editor.visualPositionToXY(editor.getCaretModel.getVisualPosition)
    point.y += editor.getLineHeight
    balloon.show(new RelativePoint(editor.getContentComponent, point), Balloon.Position.below)
    balloon
  }

  override def update(e: AnActionEvent): Unit = {
    e.getPresentation.setEnabledAndVisible(getPsiElementFromContext(e).nonEmpty)
  }

  private def getPsiElementFromContext(e: AnActionEvent): Option[PsiElement] = {
    import com.intellij.psi.PsiFile
    val psiFile: PsiFile = e.getRequiredData(CommonDataKeys.PSI_FILE)
    val editor = e.getRequiredData(CommonDataKeys.EDITOR)

    if (psiFile == null || editor == null) {
      None
    } else {
      val offset: Int = editor.getCaretModel.getOffset
      Option(psiFile.findElementAt(offset))
    }
  }
}

