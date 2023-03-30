package projetotcc.enums;

public enum StatusEnum {
	
	ABERTA(0, "Aberta"),
	CONCLUIDA(1, "Concluída"),
	ATRASADA(2, "Atrasada"),
	EM_DESENVOLVIMENTO(3, "Em desenvolvimento");
	
	private StatusEnum(Integer id, String status) {
		this.id = id;
		this.status = status;
	}
	
	private Integer id;
	private String status;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public static StatusEnum getStatusEnumById(Integer id) {
		for (StatusEnum statusEnum: StatusEnum.values()) {
			if (id.equals(statusEnum.getId())) {
				return statusEnum;
			}
		}
		throw new IllegalArgumentException("Não existe este id: " + id);
	}

}
