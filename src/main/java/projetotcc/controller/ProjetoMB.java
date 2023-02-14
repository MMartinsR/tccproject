package projetotcc.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import projetotcc.model.Projeto;
import projetotcc.model.Usuario;
import projetotcc.service.ProjetoService;
import projetotcc.utility.Message;

@Named
@ViewScoped
public class ProjetoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Projeto projetoSelecionado = new Projeto();
	
	@Inject
	private ProjetoService projetoService;
		
	private List<Projeto> projetos = new ArrayList<>();
	
	private String dataCriada;
	
	private Usuario usuario = new Usuario();
	private boolean atualizando;
	private String mensagemBotaoExcluir = "Excluir";
	private boolean existeProjetoSelecionado;
	
	

	public void init() {
		System.out.println("entrou no init!");
		atualizaProjetos();
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
		
		try {
			
			if (projetoSelecionado.getId() == null) {			
				
				projetoSelecionado.setDataCriacao(new Date());
				projetoSelecionado.setCriador(usuario.getNomeExibicao());
				
				projetoService.salvar(projetoSelecionado);
				Message.info("Projeto salvo com sucesso!");
				
			} else {
				
				projetoService.salvar(projetoSelecionado);
				Message.info("Projeto atualizado com sucesso!");
			}
			atualizaProjetos();
			PrimeFaces.current().executeScript("PF('gerenciaNovoProjetoDialog').hide()");
			PrimeFaces.current().ajax().update("f-dashboard:messages", 
					"f-dashboard:dt-meusProjetos-dashboard");
			
			
			
		} catch (Exception e) {
			Message.erro(e.getMessage());
		}
		
	}
	
	public void removerProjeto() {
		
		try {
			projetoService.remover(projetoSelecionado);
			
			atualizaProjetos();
			
			Message.info("Projeto excluído com sucesso");
			setMensagemBotaoExcluir("Excluir");
			setExisteProjetoSelecionado(false);
			
			PrimeFaces.current().ajax().update("f-dashboard:messages", 
					"f-dashboard:dt-meusProjetos-dashboard");
			
		} catch (Exception e) {
			Message.erro(e.getMessage());
		}
		
	}
	
	private void atualizaProjetos() {
		this.projetos = projetoService.listarTodos();
	}
	
	public void linhaSelecionada(SelectEvent<Projeto> event) {
		setMensagemBotaoExcluir("1 projeto selecionado");
		Message.info("Projeto " + event.getObject().getId() + " foi selecionado!");
		setExisteProjetoSelecionado(true);
	}
	
	public void linhaDeselecionada(UnselectEvent<Projeto> event) {
		setMensagemBotaoExcluir("Excluir");
		Message.info("Projeto " + event.getObject().getId() + " foi deselecionado!");
		setExisteProjetoSelecionado(false);
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

	public boolean isAtualizando() {
		return atualizando;
	}

	public void setAtualizando(boolean atualizando) {
		this.atualizando = atualizando;
	}

	public String getMensagemBotaoExcluir() {
		return mensagemBotaoExcluir;
	}

	public void setMensagemBotaoExcluir(String mensagemBotaoExcluir) {
		this.mensagemBotaoExcluir = mensagemBotaoExcluir;
	}

	public boolean isExisteProjetoSelecionado() {
		return existeProjetoSelecionado;
	}

	public void setExisteProjetoSelecionado(boolean existeProjetoSelecionado) {
		this.existeProjetoSelecionado = existeProjetoSelecionado;
	}
	
	


	
	
	

}
