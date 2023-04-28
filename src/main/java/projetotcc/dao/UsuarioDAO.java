package projetotcc.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import projetotcc.exception.DatabaseException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Projeto;
import projetotcc.model.Usuario;

public class UsuarioDAO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public Usuario findByNamedQuery(String email, String senha) {
		
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {		
			
			Query query = manager.createNamedQuery("Usuario.findByEmailSenha");
			query.setParameter("email", email);
			query.setParameter("senha", senha);

			@SuppressWarnings("unchecked")
			List<Usuario> listObjetos = query.getResultList();
			return listObjetos.get(0);
			
		} catch (IndexOutOfBoundsException e) {
			 return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar este usuário");
		} finally {
			manager.close();
		}
		
	}
	
	public Usuario findByNamedQuery(Long id) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			
			Query query = manager.createNamedQuery("Usuario.findById");
			query.setParameter("id", id);

			@SuppressWarnings("unchecked")
			List<Usuario> listObjetos = query.getResultList();
			return listObjetos.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar");
		} finally {
			manager.close();
		}
		
	}
	
	public Usuario findByEmail(String email) throws IndexOutOfBoundsException {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			Query query = manager.createNamedQuery("Usuario.findByEmail");
			query.setParameter("email", email);

			Usuario objeto = (Usuario) query.getSingleResult();
			return objeto;
		} catch (NoResultException e) {
			throw new SemResultadoException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar este email");
		} finally {
			manager.close();
		}
		
		
	}
	
	public Usuario findByNomeExibicao(String nomeExibicao) {
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			Query query = manager.createNamedQuery("Usuario.findByNomeExibicao");
			query.setParameter("nomeExibicao", nomeExibicao);
			
			@SuppressWarnings("unchecked")
			List<Usuario> listObjetos = query.getResultList();
			return listObjetos.get(0);
		} catch (IndexOutOfBoundsException e) {
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar este nome de exibição");
		} finally {
			manager.close();
		}		
		
	}
	
	public List<Projeto> findByUsuarioProjetos(Long id) {
		
		EntityManager manager = ConnectionFactory.getEntityManager();
		
		try {
			Query query = manager.createNamedQuery("Projeto.findByUsuario");
			query.setParameter("id", id);
			
			List<Projeto> objetos = ((Usuario) query.getSingleResult()).getProjetos();
			
			return objetos;			
		} catch (NoResultException e) {
			throw new SemResultadoException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException("Ocorreu um erro ao buscar os projetos deste usuário");
		} finally {
			manager.close();
		}		
	}
	
}
