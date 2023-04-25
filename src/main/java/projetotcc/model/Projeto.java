package projetotcc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tb_projeto")
@NamedQueries(value = {
		@NamedQuery(name = "Projeto.findByCriador",
				query = "SELECT p FROM Projeto p "
				+ "WHERE p.criador = :criador"),
		@NamedQuery(name = "Projeto.findByCodigo",
		query = "SELECT p FROM Projeto p "
		+ "WHERE p.codigo = :codigo"),
		@NamedQuery(name = "Projeto.findByProjeto",
		query = "SELECT p FROM Projeto p "
		+ "INNER JOIN FETCH p.usuarios "
		+ "WHERE p.id = :id")
		
		
})
public class Projeto implements Serializable, Base {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 30)
	private String nome;
	private String descricao;
	@Temporal(TemporalType.DATE)
	private Date dataCriacao;
	@Column(nullable = false)
	private String criador;
	@Column(length = 10, unique = true, nullable = false)
	private String codigo;
	
	@ManyToMany
	@JoinTable(name = "tb_projeto_usuario",
	joinColumns = @JoinColumn(name = "projeto_id"),
	inverseJoinColumns = @JoinColumn(name = "usuario_id"))
	private List<Usuario> usuarios = new ArrayList<>();
	
	@OneToMany(mappedBy = "projeto")
	private List<Tarefa> tarefas = new ArrayList<Tarefa>();
	
	
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Date getDataCriacao() {
		return dataCriacao;
	}
	
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
	public String getCriador() {
		return criador;
	}
	
	public void setCriador(String criador) {
		this.criador = criador;
	}
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public List<Tarefa> getTarefas() {
		return tarefas;
	}

	public void setTarefas(List<Tarefa> tarefas) {
		this.tarefas = tarefas;
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
		Projeto other = (Projeto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
