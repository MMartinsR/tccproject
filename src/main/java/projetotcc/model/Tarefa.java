package projetotcc.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import projetotcc.enums.PrioridadeEnum;
import projetotcc.enums.StatusEnum;

@Entity
@Table(name = "tb_tarefa")
@NamedQueries(value = {
		@NamedQuery(name = "Tarefa.findByNomeTarefa",
				query = "SELECT ta FROM Tarefa ta "
						+ "WHERE ta.nome = :nome AND ta.projeto.id = :projeto_id"),
		@NamedQuery(name = "Tarefa.findTarefasByProjetoId",
		query = "SELECT ta FROM Tarefa ta "
				+ "WHERE ta.projeto.id = :projetoId")
})
public class Tarefa implements Serializable, Base {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 500)
	private String nome;
	@Column(length = 3000)
	private String descricao;
	@Enumerated(EnumType.ORDINAL)
	private PrioridadeEnum prioridade;
	@Temporal(TemporalType.DATE)
	private Date dataEntrega;
	@Enumerated(EnumType.ORDINAL)
	private StatusEnum status;
	
	@ManyToOne
	@JoinColumn(name = "projeto_id")
	private Projeto projeto;
	
	@Fetch(FetchMode.JOIN)
	@ManyToMany
	@JoinTable(name = "tb_tarefa_tag",
	joinColumns = @JoinColumn(name = "tarefa_id"),
	inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags = new ArrayList<>();
	
	
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

	public PrioridadeEnum getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(PrioridadeEnum prioridade) {
		this.prioridade = prioridade;
	}

	public Date getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public void setStatus(StatusEnum status) {
		this.status = status;
	}
	
	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
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
		Tarefa other = (Tarefa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
