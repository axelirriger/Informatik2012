
package controllers;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import remote.system.RemoteNodeApplication;
import views.html.index;

/**
 * Application.
 * @author vladutb
 * @version 1.0
 * @since 15.02.2013
 * @see  Controller
 */
public class Application extends Controller {
    /**
    * 
    * @return Result
    */
    public static Result index() {
        RemoteNodeApplication.createRemoteActorSystem();
        return ok(index.render());
    }
    /**
     * @return Result
     * 
     */
    public static Result renderMessage() {
        if (Logger.isDebugEnabled()) {
            Logger.debug("> Application.renderMessage()");
        }
        String dateToString = RemoteNodeApplication.receiveDate != null ? ("("
            + RemoteNodeApplication.receiveDate.toString() + ")") : "";
        String stringToSend = RemoteNodeApplication.messageStr != null
            ? RemoteNodeApplication.messageStr
            : "";
        return ok(stringToSend + dateToString);
    }
}