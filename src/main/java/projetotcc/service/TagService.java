package projetotcc.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.exception.CadastrarException;
import projetotcc.exception.DatabaseException;
import projetotcc.model.Tag;
import projetotcc.utility.Message;

public class TagService implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@Inject
	private DAO<Tag> tagDao;
	

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
			Message.erro(e.getMessage() + " as tags.");
			return null;
		}
		
	}

}
