package projetotcc.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.dao.TagDAO;
import projetotcc.exception.CadastrarException;
import projetotcc.exception.DatabaseException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Tag;
import projetotcc.utility.Message;

public class TagService implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@Inject
	private DAO<Tag> tagDao;
	
	@Inject
	private TagDAO tagDAO;
	

	public void salvar(Tag tag) {

		if (tag == null) {
			System.out.println("A tag está nula");
			throw new CadastrarException("Ocorreu um erro ao salvar a tag");
		}

		try {
			
			tagDao.salvar(tag);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		}
	}

	public void atualizar(Tag tag) {

		if (tag == null) {
			System.out.println("A tag está nula");
			throw new CadastrarException("Ocorreu um erro ao atualizar a tag");
		}

		try {
			
			tagDao.atualizar(tag);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		}
	}

	public void remover(Tag tag) {

		if (tag == null) {
			System.out.println("A tag está nula");
			throw new CadastrarException("Ocorreu um erro ao remover a tag");
		}

		try {
			
			tagDao.remover(Tag.class, tag.getId());
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		}
	}

	public List<Tag> listarTodos() {
		
		try {
			
			return tagDao.buscarTodos(Tag.class);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage() + " as tags.");
			return null;
		}		
	}
	
	public Tag buscarPorId(Long id) {
		
		try {
			
			return tagDao.buscarPorId(Tag.class, id);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage() + " a tags.");
			return null;
		}
		
	}
	
	public Tag buscarPorNome(String nome, Long projeto_id) {
		
		try {
			
			return tagDAO.findByNome(nome, projeto_id);
			
		} catch (SemResultadoException e) {
			System.out.println("Nome de tag válido");
			return null;
		} catch (DatabaseException e) {
			throw new DatabaseException(e);
		}
	}
	
	public void removerTodasTags(Long projetoId) {
		
		try {
			
			List<Tag> tagsDeletar = tagDAO.findTagsByProjetoId(projetoId);
			
			if (tagsDeletar != null) {
				for (Tag tag : tagsDeletar) {
					remover(tag);
				}
			}
			
		} catch (SemResultadoException e) {
			System.out.println("Esse projeto não possui nenhuma tag e pode ser deletado com segurança.");
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
