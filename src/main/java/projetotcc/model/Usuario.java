package projetotcc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "tb_usuario")
@NamedQueries(value = {
		@NamedQuery(name = "Usuario.findByEmailSenha",
				query = "SELECT c FROM Usuario c "
				+ "WHERE c.email = :email AND c.senha = :senha"),
		@NamedQuery(name = "Usuario.findById",
				query = "SELECT c FROM Usuario c "
				+ "WHERE c.id = :id"),
		@NamedQuery(name = "Usuario.findByEmail",
				query = "SELECT c FROM Usuario c "
				+ "WHERE c.email = :email"),
		@NamedQuery(name = "Usuario.findByNomeExibicao",
				query = "SELECT c FROM Usuario c "
				+ "WHERE c.nomeExibicao = :nomeExibicao"),
		@NamedQuery(name = "Projeto.findByUsuario",
				query = "SELECT u FROM Usuario u "
				+ "INNER JOIN FETCH u.projetos "
				+ "WHERE u.id = :id")
})

public class Usuario implements Serializable, Base {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private String email;
	@Column(unique = true, length = 30)
	private String nomeExibicao;
	private String senha;
	
	@ManyToMany(mappedBy = "usuarios")
	private List<Projeto> projetos = new ArrayList<Projeto>();
	

	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getNomeExibicao() {
		return nomeExibicao;
	}
	
	public void setNomeExibicao(String nomeExibicao) {
		this.nomeExibicao = nomeExibicao;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public List<Projeto> getProjetos() {
		return projetos;
	}

	public void setProjetos(List<Projeto> projetos) {
		this.projetos = projetos;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


}
