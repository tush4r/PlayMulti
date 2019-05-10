package controllers.apilayer



import play.api.mvc.{Action, Controller}


object Application extends Controller {
  
  def home = Action {
    Ok(views.html.index("Hello there!"))
  }

  def main = Action {
    Ok("Only serviceA will respond to this.")
  }

  def greet(name: String) = Action {
    Ok(s"Hello $name!")
  }
}
