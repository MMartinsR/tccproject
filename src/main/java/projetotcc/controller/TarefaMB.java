package projetotcc.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import projetotcc.model.Tarefa;
import projetotcc.service.TarefaService;

@Named
@ViewScoped
public class TarefaMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject 
	private Tarefa tarefa = new Tarefa();
	
	@Inject
	private TarefaService tarefaService;
	
	private List<Tarefa> tarefas = new ArrayList<Tarefa>();
	
	
	public void init() {
		tarefas = tarefaService.listarTodos();
	}

	public Tarefa getTarefa() {
		return tarefa;
	}

	public void setTarefa(Tarefa tarefa) {
		this.tarefa = tarefa;
	}

	public List<Tarefa> getTarefas() {
		return tarefas;
	}

}
