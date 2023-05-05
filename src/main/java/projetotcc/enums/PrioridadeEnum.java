package projetotcc.enums;

public enum PrioridadeEnum {
	
	BAIXA(0, "Baixa"),
	MEDIA(1, "Média"),
	ALTA(2, "Alta");
	
	private PrioridadeEnum(Integer id, String nomePrioridade) {
		this.id = id;
		this.nomePrioridade = nomePrioridade;
	}
	
	private Integer id;
	private String nomePrioridade;
	
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getnomePrioridade() {
		return nomePrioridade;
	}
	
	public void setnomePrioridade(String nomePrioridade) {
		this.nomePrioridade = nomePrioridade;
	}
	
	public static PrioridadeEnum getPrioridadeEnumById(Integer id) {
		for (PrioridadeEnum PrioridadeEnum: PrioridadeEnum.values()) {
			if (id.equals(PrioridadeEnum.getId())) {
				return PrioridadeEnum;
			}
		}
		throw new IllegalArgumentException("Não existe este id: " + id);
	}

}

