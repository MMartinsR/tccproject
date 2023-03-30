package projetotcc.enums;

public enum PesoEnum {
	
	BAIXA(0, "Baixa"),
	MEDIA(1, "Média"),
	ALTA(2, "Alta");
	
	private PesoEnum(Integer id, String nomePeso) {
		this.id = id;
		this.nomePeso = nomePeso;
	}
	
	private Integer id;
	private String nomePeso;
	
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getNomePeso() {
		return nomePeso;
	}
	
	public void setNomePeso(String nomePeso) {
		this.nomePeso = nomePeso;
	}
	
	public static PesoEnum getPesoEnumById(Integer id) {
		for (PesoEnum pesoEnum: PesoEnum.values()) {
			if (id.equals(pesoEnum.getId())) {
				return pesoEnum;
			}
		}
		throw new IllegalArgumentException("Não existe este id: " + id);
	}

}

