package projetotcc.utility;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class Message {

	public static void info(String msg) {

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

	}

	public static void erro(String msg) {

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

	}

	public static void aviso(String msg) {

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);

	}
	
	public static void info(String msg, String detail) {
		
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, detail);
		FacesContext.getCurrentInstance().addMessage(null, message);
		FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
	}

}
