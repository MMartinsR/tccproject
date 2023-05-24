package projetotcc.enums;

public enum CorEnum {
	
	VERDE_CLARO("#BAF3DB", "Verde claro"),	
	LARANJA_CLARO("#FFE2BD", "Laranja claro"),	
	ROXO_CLARO("#DFD8FD", "Roxo claro"),	
	AZUL_CLARO("#CCE0FF", "Azul claro"),
	VERDE_LIMAO_CLARO("#D3F1A7", "Verde limão claro"),
	CINZA_CLARO("#DCDFE4", "Cinza claro"),	
	VERDE_ESCURO("#4BCE97", "Verde escuro"),
	LARANJA_ESCURO("#FAA53D", "Laranja escuro"),
	ROXO_ESCURO("#9F8FEF", "Roxo escuro"),
	AZUL_ESCURO("#579DFF", "Azul escuro"),	
	VERDE_LIMAO_ESCURO("#94C748", "Verde limao escuro"),	
	CINZA_ESCURO("#8590A2", "Cinza escuro");
	
	private CorEnum(String hexaCor, String nomeCor) {
		this.hexaCor = hexaCor;
		this.nomeCor = nomeCor;
	}
	
	private String hexaCor;
	private String nomeCor;
	
	public String getHexaCor() {
		return hexaCor;
	}

	public void setHexaCor(String hexaCor) {
		this.hexaCor = hexaCor;
	}

	public String getNomeCor() {
		return nomeCor;
	}

	public void setNomeCor(String nomeCor) {
		this.nomeCor = nomeCor;
	}
	
}
