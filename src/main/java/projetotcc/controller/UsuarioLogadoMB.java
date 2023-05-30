package projetotcc.controller;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.PrimeFaces;

import projetotcc.exception.AutenticacaoException;
import projetotcc.exception.CadastrarException;
import projetotcc.exception.EmailException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Usuario;
import projetotcc.service.UsuarioService;
import projetotcc.utility.EmailUtil;
import projetotcc.utility.Message;
import projetotcc.utility.RegexUtil;

@Named
@SessionScoped
public class UsuarioLogadoMB implements Serializable{

	private static final long serialVersionUID = 1L;	

	@Inject 
	private Usuario usuario;
	
	@Inject 
	private Usuario usuarioCadastro;
	
	@Inject
	private UsuarioService usuarioService;
	
	private List<Usuario> usuarios = new ArrayList<>();
	
	private String senhaConfirmacao;
	
	// Envio de email
	final String deEmail = "mrtcc2023@gmail.com"; // gmail valido 
	final String appSenha = "vqodekhdgrfootys"; // senha app do gmail
	
	
	public void init() {
		usuario = new Usuario();
		usuarioCadastro = new Usuario();
		System.out.println("Carregando Login");
	}
	
	/**
	 * 
	 * @return usuario logado.
	 */
	public Usuario retornaUsuario() {
		return (Usuario) SessionContext.getInstance().getUsuarioLogado();
	}
	
	/**
	 * 1. Método que permite fazer o login no sistema.
	 * 2. Inicialmente faz-se uma validação de usuario, caso exista algum usuario na sessao, valida se o usuário que está tentando
	 * 	  se conectar é o mesmo que já possuia sessão aberta, se sim, permite a esse usuario o acesso. Se não for o mesmo usuário, faz 
	 * 	  o logout do sistema e inicia o processo de login normalmente.
	 * 3. Seguindo o processo de login, acessamos o service o usuario e passamos as credenciais que foram recebidas da view. Caso
	 * 	  um usuário com aquelas credenciais não sejam encontradas, retornamos uma mensagem de erro. Caso seja encontrado, buscamos no 
	 * 	  banco o usuario e adicionamos este a sessão, além de redirecionar a área privada.
	 * @return
	 */
	public String fazerLogin() {
		
		try {
			
			Usuario usuarioEncontrado = retornaUsuario();
			
			if (usuarioEncontrado != null && (usuarioEncontrado.getEmail().equals(usuario.getEmail()) 
					&& usuarioEncontrado.getNomeExibicao().equals(usuario.getNomeExibicao()))) {
					
					String url = "/restricted/dashboard.xhtml?faces-redirect=true";
		            return url;
				}
			
			if (validaCampos(usuario)) {
				Message.erro("Email ou senha não seguem as regras de aceitação, "
						+ "por favor verifique e tente novamente!");
				return "";
			}			
			
			System.out.println("Tentando logar com usuário " + usuario.getEmail());
			Usuario usuarioPermitido = usuarioService.usuarioPodeLogar(usuario.getEmail(), usuario.getSenha());
			
			if(usuarioPermitido != null) {
				System.out.println("Usuario permitido encontrado " + usuarioPermitido.getEmail());
				
				Usuario usuarioPodeLogar = usuarioService.buscarPorId(usuarioPermitido.getId());
	            System.out.println("Login efetuado com sucesso");
	            SessionContext.getInstance().setAttribute("usuarioLogado", usuarioPodeLogar);
	           
	            String url = "/restricted/dashboard.xhtml?faces-redirect=true";
	            return url;
			}			
			
			Message.erro("Ocorreu um erro ao validar este usuário.");
			return "";
			
		} catch (AutenticacaoException e) {
			Message.erro(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();			
			return "";
		} catch (SemResultadoException e) {
			Message.erro(e.getMessage());
			return "";
		}
	}
	
	private boolean validaCampos(Usuario usuario) {		
		return RegexUtil.emailInvalido(usuario.getEmail()) 
				|| RegexUtil.senhaInvalida(usuario.getSenha()) 
				|| (senhaConfirmacao != null && RegexUtil.senhaInvalida(senhaConfirmacao)) ;
	}
	
	public String fazerLogout() {
		System.out.println("Fazendo logout do usuario "
				+ SessionContext.getInstance().getUsuarioLogado().getNomeExibicao());
		
		SessionContext.getInstance().encerrarSessao();
		Message.info("Logout realizado com sucesso!");
		return "/login.xhtml?faces-redirect=true";
	}
	
	public void limpar() {
		usuario = new Usuario();
		usuarioCadastro = new Usuario();
	}
	
	public void cadastrarUsuario() {
		
		try {
			
			this.usuarioCadastro.setEmail(this.usuarioCadastro.getEmail().toLowerCase().trim());			
			
			validarCamposCadastro();
			
			String senha = usuarioCadastro.getSenha();
			
			usuarioService.salvar(usuarioCadastro);
			Message.info("Seja bem-vindo! Cadastro realizado com sucesso.");
			
			enviarEmailCadastro(usuarioCadastro, senha);
			
			limpar();
			PrimeFaces.current().executeScript("PF('novoCadastroDialog').hide();");
			PrimeFaces.current().ajax().update("f-login");
			
		} catch (CadastrarException e) {
			Message.erro("Não foi possível cadastrar o usuário - " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}
	
	private void validarCamposCadastro() {
		
		if (RegexUtil.emailInvalido(usuarioCadastro.getEmail())) {
			usuarioCadastro.setEmail(null);
			
			throw new CadastrarException("E-mail não segue as regras estabelecidas.");
		}
		
		if (RegexUtil.senhaInvalida(usuarioCadastro.getSenha()) || RegexUtil.senhaInvalida(senhaConfirmacao)) {
			throw new CadastrarException("Senha ou confirmação de senha não seguem as regras estabelecidas.");
		}
		
		if (!usuarioCadastro.getSenha().equals(senhaConfirmacao)) {
			throw new CadastrarException("As senhas devem ser iguais");
		}
		
		if (usuarioCadastro.getNomeExibicao().length() < 3 || RegexUtil.nomeUsuarioInvalido(usuarioCadastro.getNomeExibicao())) {
			usuarioCadastro.setNomeExibicao(null);
			
			throw new CadastrarException("Nome de usuário não segue as regras estabelecidas.");				
		}
		
		
	}

	public void solicitarNovaSenha() {
		
		try {
			
			if (validaCampos(usuario)) {
				Message.erro("Email, senha ou confirmação de senha não seguem as regras de aceitação, "
						+ "por favor verifique e tente novamente!");
				return;
			}
			
			if (!usuario.getSenha().equals(senhaConfirmacao)) {
				Message.erro("As senha e sua confirmação devem ser iguais.");
				return;
			}
			
			String email = this.usuario.getEmail().toLowerCase().trim();
			String novaSenha = this.usuario.getSenha();
			
			if (usuarioService.gerarNovaSenha(email, novaSenha) == null) {
				Message.erro("Ocorreu um erro ao redefinir senha.");
				return;
			}			
			
			Message.info("Senha alterada com sucesso!" );
			
			enviarEmailRedefinicaoSenha(email, novaSenha);
			
			limpar();
			PrimeFaces.current().executeScript("PF('resetarSenhaDialog').hide();");
			carregar();
			
		} catch (AutenticacaoException e) {
			Message.erro(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
		} catch (CadastrarException e) {
			Message.erro(e.getMessage());
			FacesContext.getCurrentInstance().validationFailed();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void carregar() throws IOException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
	    ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
	}
	
	/**
	 * 1. Método que permite criar uma conexao ao servidor de correio eletronico.
	 * 2. Este método permite setar as propriedades necessárias para conectar ao servidor do gmail utilizando autenticacao SSL
	 * 
	 * @param email
	 * @param novaSenha
	 */
	private void enviarEmailRedefinicaoSenha(String email, String novaSenha) {		
		
		try {
			System.out.println("Inicio de envio de email SSL - Nova senha");
			
			Session sessao = configurarSessaoEnvioEmail();
			
			System.out.println("Sessao criada");
			
			EmailUtil.enviarEmail(sessao, email, "Redefinição de Credenciais", corpoEmailRedefinicaoSenha(novaSenha));
			
		} catch (IOException |MessagingException | EmailException e) {
			Message.erro("Ocorreu um erro ao enviar o email");
			e.printStackTrace();
		}
			
		
	}
	
	private void enviarEmailCadastro(Usuario usuario, String senha) {
		try {
			System.out.println("Inicio de envio de email SSL - Cadastro");
			
			Session sessao = configurarSessaoEnvioEmail();
			
			System.out.println("Sessao criada");
			
			EmailUtil.enviarEmail(sessao, usuario.getEmail(), "Cadastro", corpoEmailNovoCadastro(usuario, senha));
			
		} catch (IOException |MessagingException | EmailException e) {
			Message.erro("Ocorreu um erro ao enviar o email");
			e.printStackTrace();
		}
	}
	
	private Session configurarSessaoEnvioEmail() {
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
		
		return Session.getInstance(props, auth);
	}
	
	private MimeMultipart corpoEmailRedefinicaoSenha(String senha) throws MessagingException, IOException {
		
		MimeMultipart mimeCorpo = new MimeMultipart();
		
		ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();		
		String imgPath = context.getRealPath("/img/logoEmail.png");
		
		MimeBodyPart imgPart = new MimeBodyPart();
		imgPart.attachFile(new File(imgPath));
		imgPart.setContentID("<logoEmail>");
		
		String htmlCorpo = "<div>";
			   htmlCorpo += "<div style='display: flex; justify-content: center; margin: 50px 0px 0px 0px;'>";
			   htmlCorpo += "<h2 style='background-color: #f2fee8; padding: 30px 0px 10px 0px; width: 500px; margin: 0px; "
			   		+ "border-radius: 10px 10px 0px 0px; font-family: Arial, sans-serif; text-align: center;'>Olá!</h2></div>"; 
			   htmlCorpo += "<div style='display: flex; justify-content: center; margin-bottom: 0px;'>";
			   htmlCorpo += "<p style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; "
			   		+ "font-family: Arial, sans-serif; font-size: 20px; text-align: center;'>"
			   		+ "Foi feito um pedido de redefinição de senha para o email " + this.usuario.getEmail() + "! </p> <br/></div>";			   
			   htmlCorpo += "<div style='display: flex; justify-content: center; margin-bottom: 0px;'>";			   
			   htmlCorpo += "<p style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; font-family: Arial, "
			   		+ "sans-serif; font-size: 20px; text-align: center;'>Segue a nova credencial solicitada!!</p> <br/></div>";			   
			   htmlCorpo += "<div style='display: flex; justify-content: center;'>";			   
			   htmlCorpo += "<p style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; font-family: Arial, "
			   		+ "sans-serif; font-size: 20px; text-align: center;'>Nova senha: " + senha + "</p><br/><br/></div>";			   
			   htmlCorpo += "<div style='display: flex; justify-content: center;'>";			   
			   htmlCorpo += "<p style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; font-family: Arial, sans-serif; "
			   		+ "font-size: 20px; text-align: center;'>Atenciosamente, equipe Teamwork Tasks</p></div>";		 	   
			   htmlCorpo += "<div style='display: flex; justify-content: center;'>";
			   htmlCorpo += "<div style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; text-align: center;'>";
			   htmlCorpo += "<img style='width: 200px;' src='cid:logoEmail' />";
			   htmlCorpo += "</div>";
			   htmlCorpo += "</div>";
			   htmlCorpo += "</div>";
		
		MimeBodyPart htmlPart = new MimeBodyPart();
		
		htmlPart.setContent(htmlCorpo, "text/html");
		
		mimeCorpo.addBodyPart(htmlPart);
		mimeCorpo.addBodyPart(imgPart);
		
		return mimeCorpo;		
	}
	
	private MimeMultipart corpoEmailNovoCadastro(Usuario usuario, String senha) throws MessagingException, IOException {
		
		MimeMultipart mimeCorpo = new MimeMultipart();
		
		ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();		
		String imgPath = context.getRealPath("/img/logoEmail.png");
		
		MimeBodyPart imgPart = new MimeBodyPart();
		imgPart.attachFile(new File(imgPath));
		imgPart.setContentID("<logoEmail>");
		
		String htmlCorpo = "<div>";
			   htmlCorpo += "<div style='display: flex; justify-content: center; margin: 50px 0px 0px 0px;'>";
			   htmlCorpo += "<h2 style='background-color: #f2fee8; padding: 30px 0px 10px 0px; width: 500px; margin: 0px; "
			   		+ "border-radius: 10px 10px 0px 0px; font-family: Arial, sans-serif; text-align: center;'>Seja bem-vindo " 
					   + usuario.getNomeExibicao() + "!!"+ "</h2></div>"; 
			   htmlCorpo += "<div style='display: flex; justify-content: center; margin-bottom: 0px;'>";
			   htmlCorpo += "<p style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; "
			   		+ "font-family: Arial, sans-serif; font-size: 20px; text-align: center;'>"
			   		+ "Primeiramente, gostariamos de agradecer por confiar em nossos serviços! </p> <br/></div>";			   
			   htmlCorpo += "<div style='display: flex; justify-content: center; margin-bottom: 0px;'>";			   
			   htmlCorpo += "<p style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; font-family: Arial, "
			   		+ "sans-serif; font-size: 20px; text-align: center;'>Seus dados de cadastro são: </p> <br/></div>";
			   htmlCorpo += "<div style='display: flex; justify-content: center;'>";			   
			   htmlCorpo += "<p style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; font-family: Arial, "
			   		+ "sans-serif; font-size: 20px; text-align: center;'>* " +  usuario.getEmail() + "</p><br/><br/></div>";
			   htmlCorpo += "<div style='display: flex; justify-content: center;'>";			   
			   htmlCorpo += "<p style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; font-family: Arial, "
			   		+ "sans-serif; font-size: 20px; text-align: center;'>* " + senha + "</p><br/><br/></div>";			   
			   htmlCorpo += "<div style='display: flex; justify-content: center;'>";			   
			   htmlCorpo += "<p style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; font-family: Arial, sans-serif; "
			   		+ "font-size: 20px; text-align: center;'>Atenciosamente, equipe Teamwork Tasks</p></div>";		 	   
			   htmlCorpo += "<div style='display: flex; justify-content: center;'>";
			   htmlCorpo += "<div style='background-color: #f2fee8; padding: 20px 0px 10px 0px; width: 500px; margin: 0px; text-align: center;'>";
			   htmlCorpo += "<img style='width: 200px;' src='cid:logoEmail' />";
			   htmlCorpo += "</div>";
			   htmlCorpo += "</div>";
			   htmlCorpo += "</div>";
		
		MimeBodyPart htmlPart = new MimeBodyPart();
		
		htmlPart.setContent(htmlCorpo, "text/html");
		
		mimeCorpo.addBodyPart(htmlPart);
		mimeCorpo.addBodyPart(imgPart);
		
		return mimeCorpo;		
	}
	

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public String getSenhaConfirmacao() {
		return senhaConfirmacao;
	}

	public void setSenhaConfirmacao(String senhaConfirmacao) {
		this.senhaConfirmacao = senhaConfirmacao;
	}

	
	
	
	
	

}
