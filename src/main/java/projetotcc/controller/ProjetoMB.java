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
	private Projeto projetoSelecionadoProprio = new Projeto();
	
	@Inject
	private Projeto projetoSelecionadoParticipado = new Projeto();
	
	@Inject
	private ProjetoService projetoService;
		
	private List<Projeto> projetos = new ArrayList<>();
	private List<Projeto> projetosParticipados = new ArrayList<>();
	private List<Projeto> projetosProprios = new ArrayList<>();
	
	private String dataCriada;
	
	private Usuario usuario = new Usuario();
	private boolean participando = false;
	private String mensagemBotaoExcluir = "Excluir";
	private boolean existeProjetoSelecionado;
	
	

	public void init() {
		System.out.println("entrou no init!");
		atualizaProjetos();
		gerarData();
		filtraProjetos();
	}
	
	
	public void abrirNovo() {
		this.projetoSelecionadoProprio = new Projeto();
		setParticipando(false);
	}
	
	private void gerarData() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date data = new Date();
		
		setDataCriada(sdf.format(data));
	}
	
	public void salvarProjeto() {
		
		try {
			
			if (projetoSelecionadoProprio.getId() == null) {			
				
				projetoSelecionadoProprio.setDataCriacao(new Date());
				projetoSelecionadoProprio.setCriador(usuario.getNomeExibicao());
				
				projetoService.salvar(projetoSelecionadoProprio);
				Message.info("Projeto salvo com sucesso!");
				
			} else {
				
				projetoService.salvar(projetoSelecionadoProprio);
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
			projetoService.remover(projetoSelecionadoProprio);
			
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
	
	public void filtraProjetos() {
		
		for (Projeto p : projetos) {
			if (!p.getCriador().equalsIgnoreCase(usuario.getNomeExibicao())) {
				projetosParticipados.add(p);
			} else {
				projetosProprios.add(p);
			}
		}

	}
	
	public void participarProjeto() {
		participando = true;
	}
	
	public void participar() {
		
	}
	
	public void linhaSelecionada(SelectEvent<Projeto> event) {
		setMensagemBotaoExcluir("1 projeto selecionado");
		Message.info("Projeto " + event.getObject().getNome() + " foi selecionado!");
		setExisteProjetoSelecionado(true);
	}
	
	public void linhaDeselecionada(UnselectEvent<Projeto> event) {
		setMensagemBotaoExcluir("Excluir");
		Message.info("Projeto " + event.getObject().getNome() + " foi deselecionado!");
		setExisteProjetoSelecionado(false);
	}	

	public Projeto getProjetoSelecionadoProprio() {
		return projetoSelecionadoProprio;
	}

	public void setProjetoSelecionadoProprio(Projeto projetoSelecionadoProprio) {
		this.projetoSelecionadoProprio = projetoSelecionadoProprio;
	}

	public Projeto getProjetoSelecionadoParticipado() {
		return projetoSelecionadoParticipado;
	}


	public void setProjetoSelecionadoParticipado(Projeto projetoSelecionadoParticipado) {
		this.projetoSelecionadoParticipado = projetoSelecionadoParticipado;
	}


	public List<Projeto> getProjetos() {
		return projetos;
	}

	public List<Projeto> getprojetosParticipados() {
		return projetosParticipados;
	}
	
	public List<Projeto> getProjetosProprios() {
		return projetosProprios;
	}

	public String getDataCriada() {
		return dataCriada;
	}

	public void setDataCriada(String dataCriada) {
		this.dataCriada = dataCriada;
	}

	public boolean isParticipando() {
		return participando;
	}

	public void setParticipando(boolean participando) {
		this.participando = participando;
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
