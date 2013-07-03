package controllers

import play.api.mvc._
import play.api.libs.ws.WS
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import play.api.templates.Html

object Application extends Controller {

  def console(consoleName: String) = Action {
    val page = Jsoup.connect("http://www.jeuxvideos.com/articles/listes/tests-%s-type-0-note-0-tri-0-0.htm".format(consoleName)).get()
    val games: Elements = page.select("tr.tr1")
    val lines = games.toArray.map(line => extract(line.asInstanceOf[Element])).map(_.toString).mkString("\n")

    val content = """
                    |<table class="table">
                    |<thead>
                    |<th>Name</th><th>Release date</th><th>Note</th>
                    |</thead>
                    |<tbody>
                    |%s
                    |</tbody>
                    |</table>
                  """.stripMargin.format(lines)

    Ok(views.html.index(Html(content)))
  }

  def index = Action {
    Ok(views.html.welcome())
  }

  def extract(el: Element): Game = {
    val gameLine = el.select("td")
    Game(gameLine.first().select("a").text(), gameLine.get(3).text(), gameLine.get(5).text().toInt)
  }


  def gameLine: (String) => Boolean = {
    line => line.contains("\"tr1\"") || line.contains("\"tr2\"")
  }
}

case class Game(title: String, date: String, note: Int) {
  override def toString: String = "<tr><td>%s</td><td>%s</td><td>%d</td></tr>".format(title, date, note)
}