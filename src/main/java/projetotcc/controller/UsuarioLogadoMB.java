package projetotcc.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

import org.primefaces.PrimeFaces;

import projetotcc.dao.UsuarioDAO;
import projetotcc.model.Usuario;
import projetotcc.service.UsuarioService;
import projetotcc.utility.EmailUtil;
import projetotcc.utility.Message;

@Named
@SessionScoped
public class UsuarioLogadoMB implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// é possivel realizar as ações usando apenas um objeto usuario
	@Inject
	private Usuario usuarioCadastrado = new Usuario();
	
	@Inject
	private Usuario usuarioCadastrar = new Usuario();
	
	@Inject
	private Usuario usuarioResetar = new Usuario();
	
	@Inject
	private UsuarioService usuarioService;
	
	private List<Usuario> usuarios = new ArrayList<>();
	
	// Envio de email
	final String deEmail = "mrtcc2023@gmail.com"; // gmail valido 
	final String appSenha = "vqodekhdgrfootys"; // senha app do gmail
	
	
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
					
			Usuario usuario = usuarioService.buscarPorId(user.getId()).get(0);
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
			
			String email = this.usuarioResetar.getEmail().toLowerCase().trim();
			String novaSenha = this.usuarioResetar.getSenha();
					
			usuarioService.gerarNovaSenha(email, novaSenha);
			
			Message.info("Nova senha alterada com sucesso!" );
			
			enviarEmailRedefinicaoSenha(email, novaSenha);
			
			PrimeFaces.current().executeScript("PF('resetarSenhaDialog').hide();");
			
		} catch (Exception e) {
			Message.erro(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
		}
	}
	
	private void enviarEmailRedefinicaoSenha(String email, String novaSenha) {		
		
		System.out.println("Inicio de envio de email SSL");
		
		Properties props = System.getProperties();
		props.put("mail.smtp.host", "smtp.gmail.com");  // SMTP host
		props.put("mail.socketFactory.port", "465");  // SSL Port
		props.put("mail.smtp.socketFactory.class", 
				"javax.net.ssl.SSLSocketFactory");  // SSL Factory Class
		props.put("mail.smtp.auth", "true");  // Permite autenticação SMTP
		props.put("mail.smtp.port", "465");  // SMTP Port
		
		Authenticator auth = new Authenticator() {
			
			//override do método getPasswordAuthentication
			protected PasswordAuthentication getPasswordAuthentication() {
				 return new PasswordAuthentication(deEmail, appSenha);
			}
		};
		
		Session sessao = Session.getInstance(props, auth);
		
		System.out.println("Sessao criada");
		
		EmailUtil.enviarEmail(sessao, email, "Redefinição de Credenciais", corpoEmail(novaSenha));	
		
	}
	
	private String corpoEmail(String senha) {
		
		String corpoEmail = "Olá! " + "\n\n";
		
		corpoEmail += "Segue abaixo a nova credencial solicitada!!" + "\n\n";
		
		corpoEmail += "Nova senha: " + senha;
		
		return corpoEmail;		
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

	public Usuario getUsuarioResetar() {
		return usuarioResetar;
	}

	public void setUsuarioResetar(Usuario usuarioResetar) {
		this.usuarioResetar = usuarioResetar;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	
	
	
	
	

}
