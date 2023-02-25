package projetotcc.controller;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import projetotcc.model.Projeto;

@Named
@ViewScoped
public class ProjetoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Projeto projeto = new Projeto();

	public void init() {
		
		Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
		setProjeto((Projeto) flash.get("projetoSelecionadoProprio"));
	}
	

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

}
