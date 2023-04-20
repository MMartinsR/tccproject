package projetotcc.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import projetotcc.exception.CadastrarException;
import projetotcc.model.Projeto;
import projetotcc.model.Usuario;
import projetotcc.service.ProjetoService;
import projetotcc.service.UsuarioService;
import projetotcc.utility.Message;
import projetotcc.utility.RegexUtil;

@Named
@ViewScoped
public class DashboardMB implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private Projeto projetoSelecionadoProprio = new Projeto();
	
	@Inject
	private Projeto projetoSelecionadoParticipado = new Projeto();
	
	@Inject
	private ProjetoService projetoService;
	
	@Inject
	private UsuarioService usuarioService;
	
	private List<Projeto> projetosParticipados = new ArrayList<>();
	private List<Projeto> projetosProprios = new ArrayList<>();
	
	private String dataCriada;
	private Usuario usuario = new Usuario();
	private boolean participando = false;
	private String mensagemBotaoExcluir = "Excluir";
	private boolean existeProjetoSelecionado;
	
	

	public void init() {
		System.out.println("Carregando Dashboard");
		this.usuario = (Usuario) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("usuarioLogado");
		
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
		
		Long usuarioId = this.usuario.getId();
		Usuario usuarioEx = usuarioService.buscarPorId(usuarioId);
		List<Usuario> usuariosEx = new ArrayList<Usuario>();
		usuariosEx.add(usuarioEx);
		
		
		
		try {
			validarNomeProjeto(projetoSelecionadoProprio.getNome());
			
			if (projetoSelecionadoProprio.getId() == null) {
				
				projetoSelecionadoProprio.setUsuarios(usuariosEx);
				
				projetoService.salvar(projetoSelecionadoProprio);
				Message.info("Projeto '" + projetoSelecionadoProprio.getNome() + "' salvo com sucesso!");
				
			} else {
				
				projetoService.atualizar(projetoSelecionadoProprio);
				Message.info("Projeto '" + projetoSelecionadoProprio.getNome() + "' atualizado com sucesso!");
			}
			
			carregaProjetosProprios();
			filtraProjetosParticipados();
			
			PrimeFaces.current().executeScript("PF('gerenciaProjetoDashboardDialog').hide()");
			PrimeFaces.current().ajax().update("f-dashboard:messages", 
					"f-dashboard:dt-meusProjetos-dashboard");

		} catch (CadastrarException e) {
			Message.erro("Não foi possível cadastrar o projeto - " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private void validarNomeProjeto(String nomeProjeto) {
		if (RegexUtil.nomeProjetoInvalido(nomeProjeto)) {
			
			throw new CadastrarException("Nome inválido.");
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
			
		} catch (CadastrarException e) {
			Message.erro(e.getMessage());
		} catch (Exception e) {
			Message.erro(e.getMessage());
		}
		
	}
	
	private void carregaProjetosProprios() {
		this.projetosProprios = projetoService.buscarPorCriador(usuario.getNomeExibicao());
		
		if (this.projetosProprios == null) {
			Message.erro("Ocorreu um erro ao carregar os projetos");
		}


	}
	
	public void redirecionarParaProjeto(Projeto projeto) {
		
		try {
			
			String url = "/restricted/projeto.xhtml";
			url += "?projetoId=" + projeto.getId();
			
			String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + url);
			
		} catch (Exception e) {
			Message.erro("Ocorreu um problema ao redirecionar");
			e.printStackTrace();
		}
		
		
	}
	
	public void filtraProjetosParticipados() {
		
	}
	
	public void participarProjeto() {
		participando = true;
	}
	
	public void participar() {
		
	}
	
	public void linhaSelecionada(SelectEvent<Projeto> event) {
		setMensagemBotaoExcluir("1 projeto selecionado");
		Message.info("Projeto '" + event.getObject().getNome() + "' foi selecionado!");
		setExisteProjetoSelecionado(true);
	}
	
	public void linhaDeselecionada(UnselectEvent<Projeto> event) {
		setMensagemBotaoExcluir("Excluir");
		Message.info("Projeto '" + event.getObject().getNome() + "' não está mais selecionado!");
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
