package projetotcc.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import projetotcc.dao.ProjetoDAO;
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
	
	private List<Projeto> projetosParticipados = new ArrayList<>();
	private List<Projeto> projetosProprios = new ArrayList<>();
	
	private String dataCriada;
	
	private Usuario usuario = new Usuario();
	private boolean participando = false;
	private String mensagemBotaoExcluir = "Excluir";
	private boolean existeProjetoSelecionado;
	
	

	public void init() {
		System.out.println("entrou no init!");
		this.usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuarioLogado");
		carregaProjetosProprios();
		filtraProjetosParticipados();
		
	}
	
	
	public void abrirNovo() {
		this.projetoSelecionadoProprio = new Projeto();
		projetoSelecionadoProprio.setDataCriacao(new Date());
		projetoSelecionadoProprio.setCriador(usuario.getNomeExibicao());
		setParticipando(false);
	}
	
	public void salvarProjeto() {
		
		try {
			
			if (projetoSelecionadoProprio.getId() == null) {				

				projetoSelecionadoProprio.getUsuarios().add(this.usuario);
				
				projetoService.salvar(projetoSelecionadoProprio);
				Message.info("Projeto salvo com sucesso!");
				
			} else {
				
				projetoService.atualizar(projetoSelecionadoProprio);
				Message.info("Projeto atualizado com sucesso!");
			}
			
			carregaProjetosProprios();
			filtraProjetosParticipados();
			
			PrimeFaces.current().executeScript("PF('gerenciaProjetoDashboardDialog').hide()");
			PrimeFaces.current().ajax().update("f-dashboard:messages", 
					"f-dashboard:dt-meusProjetos-dashboard");

		} catch (Exception e) {
			Message.erro(e.getMessage());
		}
		
	}
	
	public void removerProjeto() {
		
		try {
			projetoService.remover(projetoSelecionadoProprio);

			Message.info("Projeto excluído com sucesso");
			setMensagemBotaoExcluir("Excluir");
			setExisteProjetoSelecionado(false);
			
			carregaProjetosProprios();
			filtraProjetosParticipados();
			
			PrimeFaces.current().ajax().update("f-dashboard:messages", 
					"f-dashboard:dt-meusProjetos-dashboard");
			
		} catch (Exception e) {
			Message.erro(e.getMessage());
		}
		
	}
	
	private void carregaProjetosProprios() {
		this.projetosProprios = projetoService.buscarPorCriador(usuario.getNomeExibicao());


	}
	
	public void filtraProjetosParticipados() {
		
		//List<Projeto> listaProjetos = projetoService.listarTodos();
		
		//this.projetosParticipados = listaProjetos.stream().filter(p -> p.getCriador() != this.usuario.getNomeExibicao()).collect(Collectors.toList());

//		
//		for (Projeto p : projetos) {
//			if (!p.getCriador().equalsIgnoreCase(usuario.getNomeExibicao())) {
//				projetosParticipados.add(p);
//			} else {
//				projetosProprios.add(p);
//			}
//		}

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
