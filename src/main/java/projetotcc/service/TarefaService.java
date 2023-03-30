package projetotcc.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.model.Tarefa;

public class TarefaService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private DAO<Tarefa> tarefaDao;
	

	public void salvar(Tarefa tarefa) {

		// TODO - Validações

		if (tarefa != null) {
			tarefaDao.salvar(tarefa);
		}
	}

	public void atualizar(Tarefa tarefa) {

		// TODO - Validações

		if (tarefa != null) {
			tarefaDao.atualizar(tarefa);
		}
	}
	
	public void remover(Tarefa tarefa) {

		// TODO - Validações

		if (tarefa != null) {
			tarefaDao.remover(Tarefa.class, tarefa.getId());
		}
	}
	
	public List<Tarefa> listarTodos() {
		return tarefaDao.buscarTodos(Tarefa.class);
	}
	
	public Tarefa buscarPorId(Long id) {
		return tarefaDao.buscarPorId(Tarefa.class, id);
	}

}
