package uselessplugin

import com.intellij.openapi.actionSystem.{AnAction, AnActionEvent}
import uselessplugin.testrail.TestRailIntegration

class TestRailProperties extends AnAction {
  override def actionPerformed(anActionEvent: AnActionEvent): Unit = {
    new CredsInputDialog(anActionEvent.getProject, TestRailIntegration.saveCreds, TestRailIntegration.credsOpt).show()
  }
}
