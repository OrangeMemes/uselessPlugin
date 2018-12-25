package uselessplugin.testrail

import org.json.simple.JSONObject
import uselessplugin.PropertiesHelper

object TestRailIntegration {
  val client = new APIClient("")
  var credsOpt: Option[TestRailCreds] = PropertiesHelper.getCreds
  for (creds <- credsOpt) {
    client.setBaseUrl(creds.baseUrl)
    client.setUser(creds.username)
    client.setPassword(creds.password)
  }

  def getCase(id: String): TestCase = {
    val result = client.sendGet(s"get_case/$id").asInstanceOf[JSONObject]
    TestCase(id, result.get("title").toString, result.get("priority_id").toString)
  }

  def saveCreds(testRailCreds: TestRailCreds): Unit = {
    PropertiesHelper.saveCreds(testRailCreds)
    setCreds(testRailCreds)
  }

  private def setCreds(testRailCreds: TestRailCreds): Unit = {
    client.setBaseUrl(testRailCreds.baseUrl)
    client.setUser(testRailCreds.username)
    client.setPassword(testRailCreds.password)
    credsOpt = Some(testRailCreds)
  }
}
