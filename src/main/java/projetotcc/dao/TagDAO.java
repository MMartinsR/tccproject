package projetotcc.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import projetotcc.exception.DatabaseException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Tag;

public class TagDAO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public Tag findByNome(String nome, Long projeto_id) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			Query query = manager.createNamedQuery("Tag.findByNome");
			query.setParameter("nome", nome);
			query.setParameter("projeto_id", projeto_id);
			
			Tag objeto = (Tag) query.getSingleResult();
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
	
	public List<Tag> findTagsByProjetoId(Long projetoId) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			
			Query query = manager.createNamedQuery("Tag.findTagsByProjetoId");
			query.setParameter("projetoId", projetoId);
			
			@SuppressWarnings("unchecked")
			List<Tag> objetos = query.getResultList();
			
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

}
