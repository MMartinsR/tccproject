package projetotcc.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import projetotcc.exception.DatabaseException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Tarefa;

public class TarefaDAO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public Tarefa findByNomeTarefa(String nome, Long projeto_id) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			Query query = manager.createNamedQuery("Tarefa.findByNomeTarefa");
			query.setParameter("nome", nome);
			query.setParameter("projeto_id", projeto_id);
			
			Tarefa objeto = (Tarefa) query.getSingleResult();
			return objeto;
		} catch (NoResultException e) {
			throw new SemResultadoException(e);
		}  catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar");
		} finally {
			manager.close();
		}
	}
	
	public List<Tarefa> findTarefasByProjetoId(Long projetoId) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			
			Query query = manager.createNamedQuery("Tarefa.findTarefasByProjetoId");
			query.setParameter("projetoId", projetoId);
			
			@SuppressWarnings("unchecked")
			List<Tarefa> objetos = query.getResultList();
			
			return objetos;
		} catch (NoResultException e) {
			throw new SemResultadoException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar as tarefas deste projeto");
		} finally {
			manager.close();
		}
	}

}
