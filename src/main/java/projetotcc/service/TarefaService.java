package projetotcc.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.dao.TarefaDAO;
import projetotcc.exception.CadastrarException;
import projetotcc.exception.DatabaseException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Tarefa;
import projetotcc.utility.Message;

public class TarefaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private DAO<Tarefa> tarefaDao;
	
	@Inject
	private TarefaDAO tarefaDAO;
	

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
	
	public Tarefa buscarTarefaPorNome(String nome, Long projeto_id) {
		try {
			
			return tarefaDAO.findByNomeTarefa(nome, projeto_id);
			
		} catch (SemResultadoException e) {
			System.out.println("Nome de tarefa válido");
			return null;
		} catch (DatabaseException e) {
			throw new DatabaseException(e);
		}
	}
	
	public void removerTodasTarefas(Long projetoId) {
		try {
			
			List<Tarefa> tarefasDeletar = tarefaDAO.findTarefasByProjetoId(projetoId);
			
			if (tarefasDeletar != null) {
				for (Tarefa tarefa : tarefasDeletar) {
					remover(tarefa);
				}
			}	
			
		} catch (SemResultadoException e) {
			System.out.println("Esse projeto não possui nenhuma tarefa e pode ser deletado com segurança.");
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
