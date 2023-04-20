package projetotcc.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.exception.CadastrarException;
import projetotcc.exception.DatabaseException;
import projetotcc.model.Tarefa;
import projetotcc.utility.Message;

public class TarefaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private DAO<Tarefa> tarefaDao;
	

	public void salvar(Tarefa tarefa) {

		if (tarefa == null) {
			System.out.println("A tarefa está nula");
			throw new CadastrarException("Ocorreu um erro ao salvar a tarefa");
		}

		try {
			
			tarefaDao.salvar(tarefa);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		}		
	}

	public void atualizar(Tarefa tarefa) {
		
		if (tarefa == null) {
			System.out.println("A tarefa está nula");
			throw new CadastrarException("Ocorreu um erro ao atualizar a tarefa");
		}

		try {
			
			tarefaDao.atualizar(tarefa);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		}		
	}
	
	public void remover(Tarefa tarefa) {

		if (tarefa == null) {
			System.out.println("A tarefa está nula");
			throw new CadastrarException("Ocorreu um erro ao remover a tarefa");
		}

		try {
			
			tarefaDao.remover(Tarefa.class, tarefa.getId());
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		}	
	}
	
	public List<Tarefa> listarTodos() {
		
		try {
			
			return tarefaDao.buscarTodos(Tarefa.class);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage() + " as tarefas.");
			return null;
		}	
	}
	
	public Tarefa buscarPorId(Long id) {
		
		try {
			
			return tarefaDao.buscarPorId(Tarefa.class, id);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage() + " a tarefa.");
			return null;
		}
	}

}
