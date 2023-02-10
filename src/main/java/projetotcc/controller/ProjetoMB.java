package projetotcc.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import projetotcc.model.Projeto;
import projetotcc.service.ProjetoService;

@Named
@ViewScoped
public class ProjetoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Projeto projeto = new Projeto();
	
	@Inject
	private ProjetoService projetoService;
		
	private List<Projeto> projetos = new ArrayList<>();	
	private Projeto projetoSelecionado;
	
	private String dataCriada;
	

	public void init() {
		System.out.println("entrou no init!");
		this.projetos = projetoService.listarTodos();
		gerarData();
	}
	
	
	public void abrirNovo() {
		this.projetoSelecionado = new Projeto();
	}
	
	private void gerarData() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date data = new Date();
		
		setDataCriada(sdf.format(data));
	}
	
	public void salvarProjeto() {
		
	}
	
	
	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public Projeto getProjetoSelecionado() {
		return projetoSelecionado;
	}

	public void setProjetoSelecionado(Projeto projetoSelecionado) {
		this.projetoSelecionado = projetoSelecionado;
	}

	public List<Projeto> getProjetos() {
		return projetos;
	}

	public String getDataCriada() {
		return dataCriada;
	}

	public void setDataCriada(String dataCriada) {
		this.dataCriada = dataCriada;
	}
	
	
	

}
