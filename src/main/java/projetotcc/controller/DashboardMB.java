package projetotcc.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

import projetotcc.exception.CadastrarException;
import projetotcc.exception.DatabaseException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Projeto;
import projetotcc.model.Usuario;
import projetotcc.service.ProjetoService;
import projetotcc.service.TagService;
import projetotcc.service.TarefaService;
import projetotcc.service.UsuarioService;
import projetotcc.utility.GeradorCodigo;
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
	
	@Inject
	private TarefaService tarefaService;
	
	@Inject
	private TagService tagService;
	
	private List<Projeto> projetosParticipados = new ArrayList<>();
	private List<Projeto> projetosProprios = new ArrayList<>();
	
	private String dataCriada;
	private Usuario usuario = new Usuario();
	private String mensagemBotaoExcluir = "Excluir";
	private boolean existeProjetoSelecionado;
	private String codigoParticipacao;
	
	

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
	}
	
	public void salvarProjeto() {
		
		Long usuarioId = this.usuario.getId();
		Usuario usuarioEx = usuarioService.buscarPorId(usuarioId);
		List<Usuario> usuariosEx = new ArrayList<Usuario>();
		usuariosEx.add(usuarioEx);
		
		try {
			validarNomeProjeto(projetoSelecionadoProprio.getNome());

			if (projetoSelecionadoProprio.getId() == null) {
				
				Projeto nomeProjetoExiste = projetoService.buscarProjetoPorNome(projetoSelecionadoProprio.getNome().trim());
				
				if(nomeProjetoExiste != null) {
					Message.erro("Já existe um projeto com o nome '" + projetoSelecionadoProprio.getNome() + "'."
							+ " Insira outro nome para prosseguir.");
					return;
				}
				
				String codigo = gerarCodigo();
				
				projetoSelecionadoProprio.setUsuarios(usuariosEx);
				projetoSelecionadoProprio.setCodigo(codigo);
				
				projetoService.salvar(projetoSelecionadoProprio);
				Message.info("Projeto '" + projetoSelecionadoProprio.getNome() + "' criado com sucesso!");
				
			} else {
				
				projetoService.atualizar(projetoSelecionadoProprio);
				Message.info("Projeto '" + projetoSelecionadoProprio.getNome() + "' atualizado com sucesso!");
			}
			
			carregaProjetosProprios();
			
			PrimeFaces.current().executeScript("PF('gerenciaProjetoDashboardDialog').hide()");
			PrimeFaces.current().ajax().update("f-dashboard:dt-meusProjetos-dashboard");

		}  catch (DatabaseException e) {
			Message.erro("Ocorreu um erro ao salvar este projeto");
		} catch (CadastrarException e) {
			Message.erro("Não foi possível cadastrar o projeto - " + e.getMessage());
		} 
		
	}

	private void validarNomeProjeto(String nomeProjeto) {
		if (RegexUtil.nomeProjetoInvalido(nomeProjeto)) {
			
			throw new CadastrarException("Nome inválido.");
		}
	}
	
	private String gerarCodigo() {
		
		String codigo;
		
		Projeto projetoExisteCodigo = null;
				
		do {
			codigo = GeradorCodigo.geraCodigoProjeto();			

			projetoExisteCodigo = projetoService.buscarPorCodigo(codigo);
		
		} while (projetoExisteCodigo != null);		
		
		return codigo;
		
		
	}
	
	public void removerProjeto() {
		
		try {
			
			tarefaService.removerTodasTarefas(projetoSelecionadoProprio.getId());
			tagService.removerTodasTags(projetoSelecionadoProprio.getId());
			
			
			projetoService.remover(projetoSelecionadoProprio);

			Message.info("Projeto excluído com sucesso");
			setMensagemBotaoExcluir("Excluir");
			setExisteProjetoSelecionado(false);
			
			carregaProjetosProprios();
			
			PrimeFaces.current().ajax().update("f-dashboard:messages", 
					"f-dashboard:dt-meusProjetos-dashboard");
			
		} catch (CadastrarException e) {
			Message.erro(e.getMessage());
		} catch (DatabaseException e) {
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
		
		try {
			List<Projeto> projetos = usuarioService.buscarProjetosPorUsuarioId(this.usuario.getId());
			
			if(projetos == null) {
				Message.erro("Ocorreu um erro ao carregar os projetos");
				return;
			} 
			
			this.projetosParticipados = projetos.stream()
					.filter(p -> !p.getCriador().equals(this.usuario.getNomeExibicao()) )
					.collect(Collectors.toList());
			
		} catch (SemResultadoException e) {
				return;
		}
				
	}
	
	public void participar() {
		
		try {
			Projeto projetoExiste = projetoService.buscarPorCodigo(codigoParticipacao);
			
			Projeto projetoComParticipantes = validaParticipacaoUsuario(projetoExiste);
			
			if (projetoComParticipantes == null) {
				return;
			}
			
			projetoComParticipantes.getUsuarios().add(this.usuario);
			
			projetoService.atualizar(projetoComParticipantes);
			
			Message.info(this.usuario.getNomeExibicao() 
					+ " está agora participando do projeto " + projetoComParticipantes.getNome() + "!");			

			filtraProjetosParticipados();
			
			PrimeFaces.current().executeScript("PF('gerenciaProjetoParticiparDashboard').hide()");
			PrimeFaces.current().ajax().update("f-dashboard:messages", 
					"f-dashboard:dt-projetosParticipo-dashboard");			
			
		} catch (CadastrarException e) {
			Message.erro("Não foi possível completar a participação.");
		}  catch (DatabaseException e) {
			Message.erro("Ocorreu um erro ao participar neste projeto.");
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}


	private Projeto validaParticipacaoUsuario(Projeto projeto) {
		
		if (projeto == null) {
			Message.erro("Este é um código inválido");
			return null;
		}
		
		Projeto projetoParticipantes = projetoService.buscarProjetoComParticipantesPorId(projeto.getId());
		
		boolean usuarioParticipanteExiste = projetoParticipantes.getUsuarios()
			.stream()
			.anyMatch(u -> u.getNomeExibicao()
					.equals(this.usuario.getNomeExibicao()));
		
		if (usuarioParticipanteExiste) {
			Message.erro("Este projeto consta em sua lista");
			return null;
		}
		
		return projetoParticipantes;
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


	public String getCodigoParticipacao() {
		return codigoParticipacao;
	}


	public void setCodigoParticipacao(String codigoParticipacao) {
		this.codigoParticipacao = codigoParticipacao;
	}
	

}
