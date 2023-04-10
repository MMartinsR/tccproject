package projetotcc.service;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.model.Tag;

public class TagService implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@Inject
	private DAO<Tag> tagDao;
	

	public void salvar(Tag tag) {

		// TODO - Validações

		if (tag != null) {
			tagDao.salvar(tag);
		}

	}

	public void atualizar(Tag tag) {

		// TODO - Validações

		if (tag != null) {
			tagDao.atualizar(tag);
		}

	}

	public void remover(Tag tag) {

		// TODO - Validações

		tagDao.remover(Tag.class, tag.getId());
	}

	public List<Tag> listarTodos() {
		return tagDao.buscarTodos(Tag.class);
	}
	
	public Tag buscarPorId(Long id) {
		return tagDao.buscarPorId(Tag.class, id);
	}

}
