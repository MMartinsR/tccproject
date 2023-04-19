package projetotcc.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.dao.ProjetoDAO;
import projetotcc.exception.CadastrarException;
import projetotcc.exception.DatabaseException;
import projetotcc.model.Projeto;
import projetotcc.utility.Message;

public class ProjetoService implements Serializable {

	private static final long serialVersionUID = 1L;	
	
	@Inject
	private DAO<Projeto> projetoDao;
	
	@Inject
	private ProjetoDAO projetoDAO;
	
	public void salvar(Projeto projeto) throws CadastrarException {
		
		if (projeto == null) {
			System.out.println("O projeto está nulo");
			throw new CadastrarException("Ocorreu um erro ao salvar o projeto.");
		}
		
		try {
			
			projetoDao.salvar(projeto);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		}			
		
	}
	
	public void atualizar(Projeto projeto) throws CadastrarException {
		
		if (projeto == null) {
			System.out.println("O projeto está nulo");
			throw new CadastrarException("Ocorreu um erro ao atualizar o projeto.");
		}
		
		try {
			
			projetoDao.atualizar(projeto);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		}

	}
	
	
	public void remover(Projeto projeto) throws CadastrarException {
		
		if (projeto == null) {
			System.out.println("O projeto está nulo");
			throw new CadastrarException("Ocorreu um erro ao remover o projeto.");
		}
		
		try {
			
			projetoDao.remover(Projeto.class, projeto.getId());
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		}	
		
	}
	

	public List<Projeto> listarTodos() {
		
		try {
			
			return projetoDao.buscarTodos(Projeto.class);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage() + " os projetos.");
			return null;
		}	
		
	}
	
	
	public List<Projeto> buscarPorCriador(String criador) {
		
		try {
			return projetoDAO.findByNamedQuery(criador);
		} catch (DatabaseException e) {
			return null;
		}
		
		
	}
	
	public Projeto buscarPorId(Long id) {
		
		try {
			
			return projetoDao.buscarPorId(Projeto.class, id);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage() + " o projeto.");
			return null;
		}	
		
	}
	
	
	
	

}
