package projetotcc.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;

import projetotcc.model.Projeto;
import projetotcc.service.ProjetoService;
import projetotcc.utility.Message;

@Named
@ViewScoped
public class ProjetoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Projeto projeto = new Projeto();
	
	@Inject
	private ProjetoService projetoService;
	
	private Long projetoId;
	private DashboardModel modelo;
	
	private String nomeLista = "lista1";
	
	private List<DashboardColumn> listaColunas = new ArrayList<DashboardColumn>();

	
	public void init() {
		
		if (projetoId != null) {
			projeto = projetoService.buscarPorId(projetoId);
		}
		
		modelo = new DefaultDashboardModel();
		
//		DashboardColumn coluna1 = new DefaultDashboardColumn();
//		DashboardColumn coluna2 = new DefaultDashboardColumn();
//		DashboardColumn coluna3 = new DefaultDashboardColumn();
//		DashboardColumn coluna4 = new DefaultDashboardColumn();
//		
//		coluna1.addWidget("lista1");
//		coluna1.addWidget("lista1");
//		coluna1.addWidget("lista2");
//		
//		coluna2.addWidget("lista1");
//		coluna2.addWidget("weather");
//		
//		coluna3.addWidget("politics");
//		coluna3.addWidget("lista1");
//		
//		
//		modelo.addColumn(coluna1);
//		modelo.addColumn(coluna2);
//		modelo.addColumn(coluna3);
//		modelo.addColumn(coluna4);
	}
	
	// Criar um método que irá gerar os dashboard columns
	public void novaLista() {		
		
		modelo.addColumn(new DefaultDashboardColumn());					
			
		setListaColunas(modelo.getColumns());
	}
	
	public void reordenacao(DashboardReorderEvent event) {
		Message.info("Reordenado: " + event.getWidgetId(), 
				"Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() 
				+ ", Sender index: " + event.getSenderColumnIndex());
	}
	

	public Projeto getProjeto() {
		return projeto;
	}

	public void setProjeto(Projeto projeto) {
		this.projeto = projeto;
	}

	public Long getProjetoId() {
		return projetoId;
	}

	public void setProjetoId(Long projetoId) {
		this.projetoId = projetoId;
	}

	public DashboardModel getModelo() {
		return modelo;
	}

	public List<DashboardColumn> getListaColunas() {
		return listaColunas;
	}

	public void setListaColunas(List<DashboardColumn> listaColunas) {
		this.listaColunas = listaColunas;
	}

}
