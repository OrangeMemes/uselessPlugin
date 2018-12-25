package uselessplugin

import java.awt.{GridBagConstraints, GridBagLayout}

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing._
import uselessplugin.testrail.TestRailCreds

class CredsInputDialog(project: Project, callback: TestRailCreds => Unit, testRailCreds: Option[TestRailCreds] = None) extends DialogWrapper(project) {
  final val DEFAULT_FIELD_WIDTH = 30
  val baseUrlField = new JTextField(testRailCreds.map(_.baseUrl).getOrElse(""))
  val usernameField = new JTextField(testRailCreds.map(_.username).getOrElse(""))
  val passwordField = new JPasswordField(testRailCreds.map(_.password).getOrElse(""))

  init()
  setTitle("Set up your TestRail integration")

  override def doOKAction(): Unit = {
    val baseUrl = baseUrlField.getText
    val username = usernameField.getText
    val password = new String(passwordField.getPassword)
    if (Seq(baseUrl, username, password).forall(_.nonEmpty)) {
      callback(TestRailCreds(baseUrl, username, password))
      super.doOKAction()
    }
  }

  override def createCenterPanel(): JComponent = {
    val baseUrlLabel = new JLabel("Your TestRail setup URL: ")
    baseUrlLabel.setLabelFor(baseUrlField)

    val usernameLabel = new JLabel("Username: ")
    usernameLabel.setLabelFor(usernameField)

    val passwordLabel = new JLabel("Password: ")
    passwordLabel.setLabelFor(passwordField)


    val textControlsPane = new JPanel
    val gridbag = new GridBagLayout

    textControlsPane.setLayout(gridbag)

    val c = new GridBagConstraints
    c.anchor = GridBagConstraints.EAST

    Seq(
      (baseUrlField, baseUrlLabel),
      (usernameField, usernameLabel),
      (passwordField, passwordLabel)
    ).foreach { case (field, label) =>
      field.setColumns(DEFAULT_FIELD_WIDTH)
      c.gridwidth = GridBagConstraints.RELATIVE
      c.fill = GridBagConstraints.NONE
      c.weightx = 0.0
      textControlsPane.add(label, c)

      c.gridwidth = GridBagConstraints.REMAINDER
      c.fill = GridBagConstraints.HORIZONTAL
      c.weightx = 1.0
      textControlsPane.add(field, c)
    }
    textControlsPane
  }
}
