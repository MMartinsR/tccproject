package projetotcc.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import projetotcc.model.Usuario;

public class UsuarioDAO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static EntityManager manager = ConnectionFactory.getEntityManager();
	
	public List<Usuario> findByNamedQuery(String email, String senha) {
		
		//manager.getEntityManagerFactory().getCache().evict(Usuario.class);
		
		Query query = manager.createNamedQuery("Usuario.findByEmailSenha");
		query.setParameter("email", email);
		query.setParameter("senha", senha);

		@SuppressWarnings("unchecked")
		List<Usuario> listObjetos = query.getResultList();
		return listObjetos;
	}
	
	public List<Usuario> findByNamedQuery(Long id) {
		
		Query query = manager.createNamedQuery("Usuario.findById");
		query.setParameter("id", id);

		@SuppressWarnings("unchecked")
		List<Usuario> listObjetos = query.getResultList();
		return listObjetos;
	}
	
	public Usuario findByEmail(String email) {
		
		Query query = manager.createNamedQuery("Usuario.findByEmail");
		query.setParameter("email", email);

		@SuppressWarnings("unchecked")
		List<Usuario> listObjetos = query.getResultList();
		return listObjetos.get(0);
	}
	
}
