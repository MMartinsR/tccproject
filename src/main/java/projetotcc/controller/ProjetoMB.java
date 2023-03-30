package projetotcc.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import projetotcc.enums.PesoEnum;
import projetotcc.enums.StatusEnum;
import projetotcc.model.Projeto;
import projetotcc.model.Tag;
import projetotcc.model.Tarefa;
import projetotcc.service.ProjetoService;
import projetotcc.service.TagService;
import projetotcc.service.TarefaService;
import projetotcc.utility.Message;

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
	
	private List<Tarefa> tarefas = new ArrayList<>();
	private List<Tag> tags = new ArrayList<>();
	private List<PesoEnum> listaPesos;
	private List<StatusEnum> listaStatus;
	
	private Long projetoId;	
	private String mensagemBotaoExcluir = "Excluir";
	private boolean tarefasValidadas;
	
	
	public void init() {		
		
		carregaProjetoSelecionado();
		validaTarefas();
		carregaTarefas();
		carregaTags();
		preenchePesos();
		preencheStatus();
		

	}
	
	private void carregaProjetoSelecionado() {
		// Valida se id do projeto foi passado pela url e se foi, busca por esse projeto para popular a página do projeto.
		if (projetoId != null) {
			projeto = projetoService.buscarPorId(projetoId);
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
				
				Message.info("Nova tarefa adicionada com sucesso!");
			} else {
				
				Tarefa tarefaEx = tarefaService.buscarPorId(tarefa.getId());
				
				if (tarefaEx != null) {
					
					
				}
				
				// setar novas tags caso hajam
				
				// atualizar a tarefa
				
				// atualizar o projeto.
				
				Message.info("Tarefa " + tarefa.getNome() + " atualizada com sucesso!");
			}			
			
			
		} catch (Exception e) {
			Message.erro("Ocorreu um erro ao salvar a tarefa: " + e.getMessage());
		}
		
		carregaTarefas();
		
		PrimeFaces.current().executeScript("PF('gerenciaCriarTarefaDialog').hide()");
		PrimeFaces.current().ajax().update("f-projeto:messages", "f-projeto:dt-tarefas-projeto");
		
	}
	
	public void removerTarefa() {
		
	}
	
	public void adicionarTag() {
		
		try {
			
			tagService.salvar(tag);
			
			Message.info("Nova tag adicionada com sucesso!");
		} catch (Exception e) {
			Message.erro("Ocorreu um erro ao salvar a tag: " + e.getMessage());
		}		
		
		// atualizar a lista de tags do banco
		carregaTags();
		
		PrimeFaces.current().executeScript("PF('gerenciaCriarTagDialog').hide()");
		PrimeFaces.current().ajax().update("f-projeto:messages", "f-projetoTarefa-dialog:tags");
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



}
