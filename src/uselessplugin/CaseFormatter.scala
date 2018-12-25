package uselessplugin

import uselessplugin.testrail.{TestCase, TestRailIntegration}


object CaseFormatter {
  def formatCase(testCase: TestCase): String = {
    s"""<b>${testCase.name}</b>
       |Priority is ${testCase.priorityName}. <a href="$getBaseUrl/index.php?/cases/view/${testCase.id}">See in TestRail</a>
               """.stripMargin
  }

  def formatLoading(caseId: String): String = {
    s"""<a href="$getBaseUrl/index.php?/cases/view/$caseId">Loading...</a>"""
  }

  private def getBaseUrl: String = TestRailIntegration.credsOpt.map(_.baseUrl).getOrElse("")

}
