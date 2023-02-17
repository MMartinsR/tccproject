package projetotcc.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import projetotcc.model.Base;

public class DAO<T extends Base> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static EntityManager manager = ConnectionFactory.getEntityManager();
	
	
	public T buscarPorId(Class<T> clazz, Long id) {
		
		return manager.find(clazz, id);
	}
	
	
	public void salvar(T obj) {
		
		try {
			
			manager.getTransaction().begin();
			
			if (obj.getId() == null) {
				manager.persist(obj);
			} 
			
			manager.getTransaction().commit();
			
		} catch (Exception e) {
			manager.getTransaction().rollback();
		}
	}
	
	public void atualizar(T obj) {
		try {
			
			manager.getTransaction().begin();
			
			if (obj.getId() != null) {
				manager.merge(obj);
			}
			
			manager.getTransaction().commit();
			
		} catch (Exception e) {
			manager.getTransaction().rollback();
		}
	}
	
	
	public void remover(Class<T> clazz, Long id) {
		
		T obj = buscarPorId(clazz, id);
		
		try {
			manager.getTransaction().begin();
			
			manager.remove(obj);
			
			manager.getTransaction().commit();
			
		} catch (Exception e) {
			manager.getTransaction().rollback();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public List<T> buscarTodos(Class<T> clazz) {
		
		String sql = " select object(o) from " + clazz.getName() + " as o ";
		
		Query query = manager.createQuery(sql);
		
		return query.getResultList();
	}

}
