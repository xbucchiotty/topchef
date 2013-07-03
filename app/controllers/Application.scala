package controllers

import play.api.mvc._
import play.api.libs.ws.WS
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import play.api.templates.Html
import collection.JavaConversions._

object Application extends Controller {

  def console(consoleName: String, top: Option[Int]) = Action {
    val page = Jsoup.connect("http://www.jeuxvideos.com/articles/listes/tests-%s-type-0-note-0-tri-0-0.htm".format(consoleName)).get()
    val games: Iterator[Element] = page.select("tr.tr1").iterator() ++ page.select("tr.tr2").iterator()

    val gamesList: List[Game] = games.map(extract).toList

    if (top.isDefined) {
      Ok(views.html.main(gamesList.sortWith((g1, g2) => g1.note > g2.note).take(top.get)))
    } else {
      Ok(views.html.main(gamesList))
    }
  }

  def index = Action {
    Ok(views.html.welcome())
  }

  def extract(el: Element): Game = {
    val gameLine = el.select("td")
    Game(
      title = gameLine.first().select("a").text(),
      date = gameLine.get(3).text(),
      note = gameLine.get(5).text().toInt
    )
  }

}

case class Game(title: String, date: String, note: Int)