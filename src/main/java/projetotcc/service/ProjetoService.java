package projetotcc.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.dao.ProjetoDAO;
import projetotcc.exception.CadastrarException;
import projetotcc.exception.DatabaseException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Projeto;
import projetotcc.model.Tag;
import projetotcc.model.Tarefa;
import projetotcc.model.Usuario;
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
			throw new DatabaseException(e);
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
			throw new DatabaseException(e);
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
			throw new DatabaseException(e);
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
	
	public Projeto buscarProjetoPorNome(String nome) {
		try {
			
			return projetoDAO.findByNomeProjeto(nome);
			
		} catch (SemResultadoException e) {
			System.out.println("Nome de projeto válido");
			return null;
		} catch (DatabaseException e) {
			throw new DatabaseException(e);
		}
	}
	
	public List<Usuario> buscarParticipantesPorProjetoId(Long id) {
		
		try {
			return projetoDAO.findByProjeto(id);
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
			return null;
		}
		
	}
	
	public Projeto buscarProjetoComParticipantesPorId(Long id) {
		
		try {
			return projetoDAO.findByProjetoParticipantes(id);
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
			return null;
		}
		
	}
	
	public Projeto buscarPorCodigo(String codigo) {
		
		try {
			return projetoDAO.findByCodigo(codigo);
		} catch (DatabaseException e) {
			throw new DatabaseException(e);
		} catch (SemResultadoException e) {
			System.out.println("Código gerado é válido");
			return null;
		}
	}
	
	public List<Tag> buscarTagsPorProjetoId(Long id) {
		try {
			return projetoDAO.findTagsByProjeto(id);
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
			return null;
		} catch (SemResultadoException e) {
			return null;
		}
	}
	
	public List<Tarefa> buscarTarefasPorProjetoId(Long id) {
		try {
			return projetoDAO.findTarefasByProjeto(id);
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
			return null;
		} catch (SemResultadoException e) {
			return null;
		}
	}
}
