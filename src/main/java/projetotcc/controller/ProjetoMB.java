package projetotcc.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import projetotcc.enums.PesoEnum;
import projetotcc.enums.StatusEnum;
import projetotcc.exception.CadastrarException;
import projetotcc.exception.DatabaseException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Projeto;
import projetotcc.model.Tag;
import projetotcc.model.Tarefa;
import projetotcc.model.Usuario;
import projetotcc.service.ProjetoService;
import projetotcc.service.TagService;
import projetotcc.service.TarefaService;
import projetotcc.service.UsuarioService;
import projetotcc.utility.Message;
import projetotcc.utility.RegexUtil;

@Named
@ViewScoped
public class ProjetoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Projeto projeto = new Projeto();
	
	@Inject 
	private Tarefa tarefa = new Tarefa(); 
	
	@Inject
	private Tag tag = new Tag();
	
	@Inject
	private ProjetoService projetoService;
	
	@Inject
	private TarefaService tarefaService;
	
	@Inject
	private TagService tagService;
	
	@Inject
	private UsuarioService usuarioService;
	
	private List<Tarefa> tarefas = new ArrayList<>();
	private List<Tag> tags = new ArrayList<>();
	private List<PesoEnum> listaPesos;
	private List<StatusEnum> listaStatus;
	private List<Usuario> participantes = new ArrayList<>();
	
	private Long projetoId;	
	private String mensagemBotaoExcluir = "Excluir";
	private boolean tarefasValidadas;
	private boolean participante;
	private boolean criador;
	private Usuario usuario = new Usuario();
	private String emailParticipante;
	
	
	public void init() {		
		System.out.println("Carregando Projeto");
		this.usuario = (Usuario) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("usuarioLogado");
		
		carregaProjetoSelecionado();
		validaTarefas();
		carregaTags();
		carregaTarefas();
		preenchePesos();
		preencheStatus();

	}
	
	private void carregaProjetoSelecionado() {
		// Valida se id do projeto foi passado pela url e se foi, busca por esse projeto para popular a página do projeto.
		// Busca usuarios do projeto através de um join fetch
		if (projetoId != null) {
			projeto = projetoService.buscarProjetoComParticipantesPorId(projetoId);
		}
	}
	
	// Valida se o usuário passado é o criador desse projeto
	public boolean validarTipoParticipanteCriador(Usuario participante) {
		return participante.getNomeExibicao().equals(projeto.getCriador());
	}
	
	// Valida se o usuario é criador e logo tem permissao de adicionar participante
	public boolean validarPermissaoAdicionarParticipante(Usuario participante) {
		return participante.getNomeExibicao().equals(projeto.getCriador()) 
				&& participante.getNomeExibicao().equals(this.usuario.getNomeExibicao());
	}
	
	public void adicionarParticipante() {
		
		try {
			if (RegexUtil.emailInvalido(emailParticipante)) {
				Message.erro("E-mail inválido. Verifique se o e-mail informado está correto e tente novamente.");
				return;
			}
			
			Usuario usuarioCadastrado = usuarioService.buscarPorEmail(emailParticipante);
			
			if (usuarioCadastrado == null) {				
				return;
			}
			
			if (projeto.getUsuarios().stream().anyMatch(u -> 
				u.getNomeExibicao().equals(usuarioCadastrado.getNomeExibicao()))) {
				Message.erro("O usuário informado já participa neste projeto.");
				return;
			}
			
			projeto.getUsuarios().add(usuarioCadastrado);
			
			projetoService.atualizar(projeto);		
			
			Message.info("O usuário '" + usuarioCadastrado.getNomeExibicao() 
				+ "' foi adicionado como participante no projeto '" + projeto.getNome() + "'.");
			
			PrimeFaces.current().executeScript("PF('gerenciaAdicionarParticipantesDialog').hide()");
			PrimeFaces.current().executeScript("PF('gerenciaExibirParticipantesDialog').show()");
			PrimeFaces.current().ajax().update("f-projeto:messages", "f-projetoParticipante-dialog:dt-participantes");
		} catch (SemResultadoException e) {
			Message.erro("Este usuário não está cadastrado em nosso sistema. Verifique o email informado.");
		}
	}

	// Valida se o usuario é participante no projeto, logo apenas pode retirar seu próprio usuário.
	public boolean validarPermissaoSairProjeto(Usuario participante) {			
		return !projeto.getCriador().equals(this.usuario.getNomeExibicao()) 
				&& participante.getNomeExibicao().equals(this.usuario.getNomeExibicao());
	}
	
	// Valida se usuario é criador, logo pode remover qualquer personagem menos ele mesmo.
	public boolean validarPermissaoRemoverUsuarios(Usuario participante) {
		return projeto.getCriador().equals(this.usuario.getNomeExibicao()) 
				&& !participante.getNomeExibicao().equals(this.usuario.getNomeExibicao());
	}
	
	// Permite configurar qual tooltip será exibido.
	public boolean filtrarTooltipParticipantes(Usuario participante) {
		return this.usuario.getNomeExibicao().equals(projeto.getCriador());
	}
	
	public void removerParticipante(Usuario participante) {
		
		try {
			
			if (projeto.getCriador().equals(this.usuario.getNomeExibicao())) {
				
				projeto.getUsuarios().remove(participante);
				
				projetoService.atualizar(projeto);
				
				Message.info("Participante removido com sucesso!");

				PrimeFaces.current().ajax().update("f-projeto:messages", "f-projetoParticipante-dialog:dt-participantes");
			} else {
				if (!participante.getNomeExibicao().equals(this.usuario.getNomeExibicao())) {
					Message.erro("Apenas o criador do projeto tem permissão para remover qualquer participante.");
					return;
				}
				projeto.getUsuarios().remove(participante);
				
				projetoService.atualizar(projeto);
				
				Message.info("Deixou de participar no projeto '" + projeto.getNome() + "'!");
				
				redirecionarDashboard();				
			}
			
		} catch (CadastrarException e) {
			Message.erro(e.getMessage());
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void carregaTarefas() {
		tarefas = tarefaService.listarTodos();		
	}
	
	private void carregaTags() {
		tags = tagService.listarTodos();
	}
	
	private void validaTarefas() {
		
		carregaTarefas();
		
		 // para cada tarefa se data de entrega for anterior a data atual, marcar status tarefa como atrasada
		for (Tarefa tarefa : this.tarefas) {
			if (tarefa.getDataEntrega().before(new Date())) {
				tarefa.setStatus(StatusEnum.ATRASADA);
				tarefaService.atualizar(tarefa);
			}
		}
		setTarefasValidadas(true);	
	}

	public void criarNova() {
		tarefa = new Tarefa();
		
	}
	
	public void criarNovaTag() {
		tag = new Tag();
	}
	
	public void adicionarTarefa() {
		
		try {
			
			if(tarefa.getId() == null) {
				tarefa.setStatus(StatusEnum.ABERTA);
				
				tarefa.setProjeto(projeto);
				
				tarefaService.salvar(tarefa);
				
				Message.info("Tarefa '" + tarefa.getNome() + "' criada com sucesso!");
			} else {
								
				tarefaService.atualizar(tarefa);
				
				// setar novas tags caso hajam
				
				// atualizar a tarefa
				
				// atualizar o projeto.
				
				Message.info("Tarefa '" + tarefa.getNome() + "' atualizada com sucesso!");
			}			
			
			
		} catch (CadastrarException e) {
			Message.erro("Ocorreu um erro ao salvar a tarefa - " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		carregaTarefas();
		
		PrimeFaces.current().executeScript("PF('gerenciaCriarTarefaDialog').hide()");
		PrimeFaces.current().ajax().update("f-projeto:messages", "f-projeto:ds-tarefas-projeto-aberta");
		
	}
	
	public void removerTarefa() {
		
	}
	
	public void adicionarTag() {
		
		try {
			
			tagService.salvar(tag);
			
			Message.info("Nova tag adicionada com sucesso!");
		} catch (CadastrarException e) {
			Message.erro("Ocorreu um erro ao salvar a tag - " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		// atualizar a lista de tags do banco
		carregaTags();
		
		PrimeFaces.current().executeScript("PF('gerenciaCriarTagDialog').hide()");
		PrimeFaces.current().ajax().update("f-projeto:messages", "f-projetoTarefa-dialog:tags");
	}
	
	public void redirecionarDashboard() {
		
		try {
			String url = "/restricted/dashboard.xhtml";
			
			String contextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + url);
			
		} catch (Exception e) {
			Message.erro("Ocorreu um problema ao redirecionar");
			e.printStackTrace();
		}
	}
	
	public void preenchePesos() {
		listaPesos = Arrays.asList(PesoEnum.values());
	}
	
	public void preencheStatus() {
		listaStatus = Arrays.asList(StatusEnum.values());
	}
	
	
	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public List<Tarefa> getTarefas() {
		return tarefas;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public List<PesoEnum> getListaPesos() {
		return listaPesos;
	}

	public List<StatusEnum> getListaStatus() {
		return listaStatus;
	}

	public List<Usuario> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(List<Usuario> participantes) {
		this.participantes = participantes;
	}

	public Long getProjetoId() {
		return projetoId;
	}

	public void setProjetoId(Long projetoId) {
		this.projetoId = projetoId;
	}

	public String getMensagemBotaoExcluir() {
		return mensagemBotaoExcluir;
	}

	public void setMensagemBotaoExcluir(String mensagemBotaoExcluir) {
		this.mensagemBotaoExcluir = mensagemBotaoExcluir;
	}

	public boolean isTarefasValidadas() {
		return tarefasValidadas;
	}

	public void setTarefasValidadas(boolean tarefasValidadas) {
		this.tarefasValidadas = tarefasValidadas;
	}

	public boolean isParticipante() {
		return participante;
	}

	public void setParticipante(boolean participante) {
		this.participante = participante;
	}

	public boolean isCriador() {
		return criador;
	}

	public void setCriador(boolean criador) {
		this.criador = criador;
	}

	public String getEmailParticipante() {
		return emailParticipante;
	}

	public void setEmailParticipante(String emailParticipante) {
		this.emailParticipante = emailParticipante;
	}

}
