package uselessplugin

import java.nio.file.{Files, Paths}
import java.util.Properties

import uselessplugin.testrail.TestRailCreds

import scala.util.Try
import scala.collection.JavaConverters._

object PropertiesHelper {
  final private val BASEURL_KEY = "baseurl"
  final private val USERNAME_KEY = "username"
  final private val PASSWORD_KEY = "password"
  final private val TESTRAIL_PROPERTIES = Paths.get("TestrailProperties")

  def getCreds: Option[TestRailCreds] = {
    Try {
      Files.createFile(TESTRAIL_PROPERTIES)
    }

    val properties = new Properties()
    properties.load(Files.newInputStream(TESTRAIL_PROPERTIES))

    val propsMap = properties.entrySet().asScala.toSeq.map(entry => (entry.getKey, entry.getValue)).toMap

    for (baseUrl <- propsMap.get(BASEURL_KEY).map(_.toString);
         username <- propsMap.get(USERNAME_KEY).map(_.toString);
         password <- propsMap.get(PASSWORD_KEY).map(_.toString)) {
      return Some(TestRailCreds(baseUrl, username, password))
    }
    None
  }

  def saveCreds(testRailCreds: TestRailCreds): Unit = {
    val properties = new Properties()
    properties.load(Files.newInputStream(TESTRAIL_PROPERTIES))
    properties.setProperty(BASEURL_KEY, testRailCreds.baseUrl)
    properties.setProperty(USERNAME_KEY, testRailCreds.username)
    properties.setProperty(PASSWORD_KEY, testRailCreds.password)
    properties.store(Files.newOutputStream(TESTRAIL_PROPERTIES), "")
  }
}
