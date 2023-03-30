package projetotcc.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.dao.ProjetoDAO;
import projetotcc.model.Projeto;

public class ProjetoService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	@Inject
	private DAO<Projeto> projetoDao;
	
	@Inject
	private ProjetoDAO projetoDAO;
	
	public void salvar(Projeto projeto) {
		
		
		// TODO - Validações
		
		if (projeto != null) {
			projetoDao.salvar(projeto);
		}		
		
	}
	
	public void atualizar(Projeto projeto) {
		
		
		// TODO - Validações
		
		if (projeto != null) {
			projetoDao.atualizar(projeto);
		}		
		
	}
	
	
	public void remover(Projeto projeto) {
		
		// TODO - Validações
		
		
		projetoDao.remover(Projeto.class, projeto.getId());
	}
	

	public List<Projeto> listarTodos() {
		return projetoDao.buscarTodos(Projeto.class);
	}
	
	
	public List<Projeto> buscarPorCriador(String criador) {
		return projetoDAO.findByNamedQuery(criador);
	}
	
	public Projeto buscarPorId(Long id) {
		return projetoDao.buscarPorId(Projeto.class, id);
	}
	
	
	
	

}
