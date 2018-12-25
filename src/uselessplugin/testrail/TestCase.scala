package uselessplugin.testrail

case class TestCase (id: String, name: String, priorityCode: String) {
  def priorityName: String = priorityCode match {
    case "1" => "Low"
    case "2" => "Medium"
    case "3" => "High"
    case "4" => "Critical"
    case unsupported => s"Unsupported (5)"
  }
}
