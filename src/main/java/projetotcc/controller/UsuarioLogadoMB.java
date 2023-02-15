package projetotcc.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;

import projetotcc.dao.UsuarioDAO;
import projetotcc.model.Usuario;
import projetotcc.service.UsuarioService;
import projetotcc.utility.Message;

@Named
@SessionScoped
public class UsuarioLogadoMB implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Usuario usuarioCadastrado = new Usuario();
	
	@Inject
	private Usuario usuarioCadastrar = new Usuario();
	
	@Inject
	private UsuarioService usuarioService;
	
	@Inject
	private UsuarioDAO usuarioDAO;
	
	private List<Usuario> usuarios = new ArrayList<>();
	
	
	public void init() {
		System.out.println("entrou no init login");
	}
	
	/**
	 * 
	 * @return usuario logado.
	 */
	public Usuario retornaUsuario() {
		return (Usuario) SessionContext.getInstance().getUsuarioLogado();
	}
	
	public String fazerLogin() {
		
		try {
			
			if (retornaUsuario() != null) {
				
				Usuario usuario = retornaUsuario();
				
				if (usuario.getEmail().equals(usuarioCadastrado.getEmail()) 
						&& usuario.getNomeExibicao().equals(usuarioCadastrado.getNomeExibicao())) {
					return "/restricted/dashboard.xhtml?faces-redirect=true";
				}
				
				fazerLogout();				
			}
			
			System.out.println("Tentando logar com usuário " + usuarioCadastrado.getEmail());
			Usuario user = usuarioService.usuarioPodeLogar(usuarioCadastrado.getEmail(), usuarioCadastrado.getSenha());
			
			if (user == null) {
				Message.erro("Login ou senha errada, tente novamente!");
				FacesContext.getCurrentInstance().validationFailed();
				return "";
			}
					
			Usuario usuario = (Usuario) getUsuarioDAO().findByNamedQuery(user.getId()).get(0);
	           System.out.println("Login efetuado com sucesso");
	           SessionContext.getInstance().setAttribute("usuarioLogado", usuario);
	           return "/restricted/dashboard.xhtml?faces-redirect=true";
			
		} catch (Exception e) {
			Message.erro(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
			e.printStackTrace();
			return "";
		}
	}
	
	public String fazerLogout() {
		System.out.println("Fazendo logout com usuario "
				+ SessionContext.getInstance().getUsuarioLogado().getNomeExibicao());
		
		SessionContext.getInstance().encerrarSessao();
		Message.info("Logout realizado com sucesso!");
		return "/login.xhtml?faces-redirect=true";
	}
	
	public void novoUsuario() {
		this.usuarioCadastrar = new Usuario();
	}
	
	public void cadastrarUsuario() {
		
		try {
			
			this.usuarioCadastrar.setEmail(this.usuarioCadastrar.getEmail().toLowerCase().trim());			
			this.usuarioCadastrar.setSenha(usuarioService.converteStringParaMd5(this.usuarioCadastrar.getSenha()));
			
			usuarioService.salvar(usuarioCadastrar);
			Message.info("Novo usuário cadastrado!");
			
			PrimeFaces.current().executeScript("PF('novoCadastroDialog').hide();");
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	public void solicitarNovaSenha() {
		try {			
			
			Message.info("Nova senha " + usuarioService.gerarNovaSenha(this.usuarioCadastrado.getEmail()));
			
		} catch (Exception e) {
			Message.erro(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
		}
	}
	

	public Usuario getUsuarioCadastrado() {
		return usuarioCadastrado;
	}

	public void setUsuarioCadastrado(Usuario usuarioCadastrado) {
		this.usuarioCadastrado = usuarioCadastrado;
	}

	public Usuario getUsuarioCadastrar() {
		return usuarioCadastrar;
	}

	public void setUsuarioCadastrar(Usuario usuarioCadastrar) {
		this.usuarioCadastrar = usuarioCadastrar;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public UsuarioDAO getUsuarioDAO() {
		return usuarioDAO;
	}
	
	
	
	
	
	

}
