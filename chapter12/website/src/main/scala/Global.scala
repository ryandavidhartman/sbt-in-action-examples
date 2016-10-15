import java.io.File

import controllers.Assets
import play.api.libs.json.Json
import play.api.mvc.{Controller, Action, RequestHeader}
import play.api.GlobalSettings
import play.core.StaticApplication
import play.navigator._
import play.navigator.{PlayNavigator, PlayResourcesController}
import scala.collection.mutable
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import org.pragmaticdemo.database._
import controllers.Scalate

object MyRoutes extends PlayNavigator {
  val index = GET on root to redirect("demo")
  val demo = GET on "skus" to { () => Application.kittens }
  val selected = POST on "selected" to { () => Application.selected }
  val purchase = POST on "purchase" to { () => Application.purchase }
}

object Application extends Controller {
  val skuSelectForm = Form[SelectSku](
    mapping(
      "select1" -> nonEmptyText,
      "select2" -> nonEmptyText,
      "select3" -> nonEmptyText
    )(SelectSku.apply)(SelectKitten.unapply)
  )

  def demo = Action {
    Ok(Scalate("app/views/demo.scaml").render('title -> "Sku List",
                'demo -> Sku.all(), 'attributes -> Attribute.all()))
  }

  def purchase = TODO

  def selected = Action { request =>
    val body: Option[Map[String, Seq[String]]] = request.body.asFormUrlEncoded

    body.map { map =>
      showSelectedSkus(map.get("select1").get.head, map.get("select2").get.head, map.get("select3").get.head)
    }.getOrElse{
      BadRequest("Expecting form url encoded body")
    }
  }

  def showSelectedSkus(id1: String, id2: String, id3: String) = {
    import org.pragmaticdemo.Logic._
    val buyerPreferences = org.pragmaticdemo.BuyerPreferences(Set(id1, id2, id3))

    val demoWithLikelihood = Sku.all().map{ k =>
      (k, matchLikelihood(org.pragmaticdemo.Sku(k.id, KittenAttribute.allForKitten(k).map("" + _.attributeId).toSet), buyerPreferences))
    }.sortWith((d1, d2) => d1._2 > d2._2).filter(_._2 > 0.5)

    Ok(Scalate("app/views/selected.scaml").render('title -> "Selected demo", 'skus -> kittensWithLikelihood))
  }

}

object Global extends GlobalSettings {
  // TODO - this was only necessary when we were instantiating play with the global object,
  //        because onejar REQUIRES the main class to exist in the main jar, yay?
  def main(args: Array[String]): Unit = {
    play.core.server.NettyServer.main(args)
    //new play.core.server.NettyServer(new StaticApplication(new File(".")), 9000)
  }

  import concurrent.Future
  override def onRouteRequest(request: RequestHeader) = 
    MyRoutes.onRouteRequest(request)
  override def onHandlerNotFound(request: RequestHeader) = 
    Future.successful(MyRoutes.onHandlerNotFound(request))
}
