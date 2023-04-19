package projetotcc.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import projetotcc.exception.DatabaseException;
import projetotcc.model.Projeto;

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

}
