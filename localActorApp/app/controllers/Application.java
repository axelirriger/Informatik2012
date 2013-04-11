
package controllers;

import local.system.LocalNodeApplication;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;
import forms.MessageForm;

/**
 * Application.
 * @author vladutb
 * @version 1.0
 * @since 15.02.2013
 * @see Controller
 */
public class Application extends Controller {
    /** <code>form</code> */
    private static Form<MessageForm> form = form(MessageForm.class);
    /**
    * 
    * @return Result
    */
    public static Result index() {
        return ok(index.render("", form));
    }
    /**
     * 
     * @return Result
     */
    public static Result sendMessage() {
        MessageForm receivedForm = form.bindFromRequest().get();
        String message = receivedForm.message;
        try {
            LocalNodeApplication.startLocalApplication(message);
        }
        catch (Exception exept) {
            if (Logger.isDebugEnabled()) {
                Logger.debug("Error > " + exept.getLocalizedMessage() + ";" + exept.getMessage());
            }
        }
        return ok(index.render("Sent message: '" + message + "'", form));
    }
}