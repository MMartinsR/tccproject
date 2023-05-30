package projetotcc.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;

import projetotcc.exception.DatabaseException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Projeto;
import projetotcc.model.Tag;
import projetotcc.model.Tarefa;
import projetotcc.model.Usuario;

public class ProjetoDAO implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	public List<Projeto> findByNamedQuery(String criador) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			Query query = manager.createNamedQuery("Projeto.findByCriador");
			query.setParameter("criador", criador);
			
			@SuppressWarnings("unchecked")
			List<Projeto> listObjetos = query.getResultList();
			return listObjetos;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar");
		} finally {
			manager.close();
		}
		
	}
	
	public Projeto findByCodigo(String codigo) {
			
			EntityManager manager = ConnectionFactory.getEntityManager();
			
			try {
				Query query = manager.createNamedQuery("Projeto.findByCodigo");
				query.setParameter("codigo", codigo);
				
				Projeto objeto = (Projeto) query.getSingleResult();
				return objeto;
			} catch (NoResultException e) {
				throw new SemResultadoException(e);
			} catch (IllegalArgumentException | NonUniqueResultException | IllegalStateException | 
					QueryTimeoutException | TransactionRequiredException | PessimisticLockException | 
					LockTimeoutException e ) {
				e.printStackTrace();
				throw new DatabaseException(e);
			} finally {
				manager.close();
			}
			
		}
	
	public Projeto findByNomeProjeto(String nome, String nomeExibicao) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			Query query = manager.createNamedQuery("Projeto.findByNomeProjeto");
			query.setParameter("nome", nome);
			query.setParameter("criador", nomeExibicao);
			
			Projeto objeto = (Projeto) query.getSingleResult();
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
	
	public List<Usuario> findByProjeto(Long id) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			Query query = manager.createNamedQuery("Projeto.findByProjeto");
			query.setParameter("id", id);
			
			List<Usuario> objetos = ((Projeto) query.getSingleResult()).getUsuarios();
			
			return objetos;			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar os usuários deste projeto");
		} finally {
			manager.close();
		}		
		
	}
	
	public Projeto findByProjetoParticipantes(Long id) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			Query query = manager.createNamedQuery("Projeto.findByProjeto");
			query.setParameter("id", id);
			
			Projeto objetos = (Projeto) query.getSingleResult();
			
			return objetos;			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar os usuários deste projeto");
		} finally {
			manager.close();
		}		
	}
	
	public List<Tag> findTagsByProjeto(Long id) {
			
			EntityManager manager = ConnectionFactory.getEntityManager();
			
			try {
				Query query = manager.createNamedQuery("Projeto.findTagsByProjeto");
				query.setParameter("id", id);
				
				List<Tag> objetos = ((Projeto) query.getSingleResult()).getTags();
				
				return objetos;
			} catch (NoResultException e) {
				throw new SemResultadoException(e); 
			} catch (Exception e) {
				e.printStackTrace();
				throw new DatabaseException("Ocorreu um erro ao buscar as tags deste projeto");
			} finally {
				manager.close();
			}
		}
	
	public List<Tarefa> findTarefasByProjeto(Long id) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			Query query = manager.createNamedQuery("Projeto.findTarefasByProjeto");
			query.setParameter("id", id);
			
			List<Tarefa> objetos = ((Projeto) query.getSingleResult()).getTarefas();
			
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
