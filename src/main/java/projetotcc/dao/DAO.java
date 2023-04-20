package projetotcc.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import projetotcc.exception.DatabaseException;
import projetotcc.model.Base;


public class DAO<T extends Base> implements Serializable {

	private static final long serialVersionUID = 1L;
	

	public T buscarPorId(Class<T> clazz, Long id) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {			
			return manager.find(clazz, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar");
		} finally {
			manager.close();
		}
		
	}
	
	
	public void salvar(T obj) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {			
			
			manager.getTransaction().begin();
			
			if (obj.getId() == null) {
				manager.persist(obj);
			} 
			
			manager.getTransaction().commit();
			
		} catch (Exception e) {
			manager.getTransaction().rollback();
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao salvar");
		} finally {
			manager.close();
		}
	}
	
	public void atualizar(T obj) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			
			manager.getTransaction().begin();
			
			if (obj.getId() != null) {
				manager.merge(obj);
			}
			
			manager.getTransaction().commit();
			
		} catch (Exception e) {
			manager.getTransaction().rollback();
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao atualizar");
		} finally {
			manager.close();
		}
	}
	
	
	public void remover(Class<T> clazz, Long id) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		T obj = manager.find(clazz, id);
		
		try {
			manager.getTransaction().begin();
			
			manager.remove(obj);
			
			manager.getTransaction().commit();
			
		} catch (Exception e) {
			manager.getTransaction().rollback();
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao remover");
		} finally {
			manager.close();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<T> buscarTodos(Class<T> clazz) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();		
		
		try {
			String sql = " select object(o) from " + clazz.getName() + " as o ";
			
			Query query = manager.createQuery(sql);
			
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar");
		} finally {
			manager.close();
		}
		
	}

}
