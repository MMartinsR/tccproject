package projetotcc.controller;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import projetotcc.enums.CorEnum;
import projetotcc.enums.PrioridadeEnum;
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
	private List<PrioridadeEnum> listaPrioridades;
	private List<StatusEnum> listaStatus;
	private List<Usuario> participantes = new ArrayList<>();
	
	private List<Tarefa> abertas;
	private List<Tarefa> emDesenvolvimento;
	private List<Tarefa> emValidacao;
	private List<Tarefa> concluidas;
	private List<CorEnum> cores;	
	
	private Long projetoId;	
	private String mensagemBotaoExcluir = "Excluir";
	private boolean tarefasValidadas;
	private boolean participante;
	private boolean criador;
	private Usuario usuario = new Usuario();
	private String emailParticipante;
	private String corSelecionada;
	
	
	public void init() {		
		System.out.println("Carregando Projeto");
		this.usuario = (Usuario) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get("usuarioLogado");
		
		carregaProjetoSelecionado();
		carregaTarefas();
		carregaTags();
		preenchePrioridades();
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
			setEmailParticipante(null);
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
		
		abertas = tarefas.stream()
				.filter(ta -> ta.getStatus() == StatusEnum.ABERTA).collect(Collectors.toList());
		emDesenvolvimento = tarefas.stream()
				.filter(te -> te.getStatus() == StatusEnum.EM_DESENVOLVIMENTO).collect(Collectors.toList());
		emValidacao = tarefas.stream()
				.filter(tat -> tat.getStatus() == StatusEnum.EM_VALIDACAO).collect(Collectors.toList());
		concluidas = tarefas.stream()
				.filter(tc -> tc.getStatus() == StatusEnum.CONCLUIDA).collect(Collectors.toList());
		
		for (Tarefa ab : abertas) {
			System.out.println("Aberta: " + ab.getNome());
		}
		
		for (Tarefa ed : emDesenvolvimento) {
			System.out.println("Em Desenvolvimento: " + ed.getNome());
		}
		
		for (Tarefa at : emValidacao) {
			System.out.println("emValidacao: " + at.getNome());
		}
		
		for (Tarefa c : concluidas) {
			System.out.println("Concluída: " + c.getNome());
		}
	}
	
	private void carregaTags() {
		tags = tagService.listarTodos();
		cores = Arrays.asList(CorEnum.values());
	}
	
	public boolean validaTarefa(Tarefa tarefa) {	
		
		 // caso a data de entrega for anterior a data atual, marcar como atrasada
		if (tarefa.getDataEntrega().before(new Date())) {
			return true;
		}
		return false;
	}

	public void criarNova() {
		tarefa = new Tarefa();		
	}
	
	public void criarNovaTag() {
		tag = new Tag();
	}
	
	public void salvarTarefa() {
		
		try {
			
			if(tarefa.getId() == null) {
				tarefa.setStatus(StatusEnum.ABERTA);
				
				tarefa.setProjeto(projeto);
				
				tarefaService.salvar(tarefa);
				
				Message.info("Tarefa '" + tarefa.getNome() + "' criada com sucesso!");
			} else {
								
				tarefaService.atualizar(tarefa);
				
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
	
	public boolean dataEntregaPassou() {
		
		if (tarefa != null && tarefa.getId() != null) {
			if (tarefa.getDataEntrega().before(new Date())) {
				return true;	
			}
		}		
		return false;
	}
	
	public void removerTarefa() {
		
	}
	
	public void adicionarTag() {
		
		try {
			if (nomeVazio()) {
				Message.erro("O nome da tag deve estar preenchido.");
				return;
			}
		
			if (RegexUtil.nomeTagInvalida(tag.getNome())) {
				Message.erro("O nome escolhido não cumpre as regras estabelecidas.");
				return;
			}
			
			if(corSelecionada == null) {
				Message.erro("Selecione uma cor para a nova tag");
				return;
			}
			
			tag.setCor(corSelecionada);
			
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
	
	private boolean nomeVazio() {
		return (tag.getNome().trim().isEmpty() || tag.getNome() == null);
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
	
	public String getDataEntregaFormatada(Tarefa tarefa) {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    sdf.setTimeZone(TimeZone.getDefault());
	    sdf.setLenient(false);
	    return sdf.format(tarefa.getDataEntrega());
	}
	
	public void preenchePrioridades() {
		listaPrioridades = Arrays.asList(PrioridadeEnum.values());
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

	public List<PrioridadeEnum> getListaPrioridades() {
		return listaPrioridades;
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

	public List<Tarefa> getAbertas() {
		return abertas;
	}

	public void setAbertas(List<Tarefa> abertas) {
		this.abertas = abertas;
	}

	public List<Tarefa> getEmDesenvolvimento() {
		return emDesenvolvimento;
	}

	public void setEmDesenvolvimento(List<Tarefa> emDesenvolvimento) {
		this.emDesenvolvimento = emDesenvolvimento;
	}

	public List<Tarefa> getEmValidacao() {
		return emValidacao;
	}

	public void setEmValidacao(List<Tarefa> emValidacao) {
		this.emValidacao = emValidacao;
	}

	public List<Tarefa> getConcluidas() {
		return concluidas;
	}

	public void setConcluidas(List<Tarefa> concluidas) {
		this.concluidas = concluidas;
	}

	public List<CorEnum> getCores() {
		return cores;
	}

	public void setCores(List<CorEnum> cores) {
		this.cores = cores;
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
	
	public Usuario getUsuario() {
		return usuario;
	}

	public String getEmailParticipante() {
		return emailParticipante;
	}

	public void setEmailParticipante(String emailParticipante) {
		this.emailParticipante = emailParticipante;
	}

	public String getCorSelecionada() {
		return corSelecionada;
	}

	public void setCorSelecionada(String corSelecionada) {
		this.corSelecionada = corSelecionada;
	}

}
