package projetotcc.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.dao.UsuarioDAO;
import projetotcc.exception.AutenticacaoException;
import projetotcc.exception.CadastrarException;
import projetotcc.exception.DatabaseException;
import projetotcc.model.Usuario;
import projetotcc.utility.Message;
import projetotcc.utility.RegexUtil;

public class UsuarioService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private DAO<Usuario> usuarioDao;
	
	@Inject
	private UsuarioDAO usuarioDAO;
		

	public void salvar(Usuario usuario) {

		if (usuario != null) {
			
			if (!RegexUtil.emailValido(usuario.getEmail())) {
				usuario.setEmail(null);
				
				throw new CadastrarException("E-mail inválido.");
			}
			
			if (!RegexUtil.senhaValida(usuario.getSenha())) {
				throw new CadastrarException("A senha deve conter de 8 a 20 caracteres, "
						+ "sendo ao menos um maiusculo, um minusculo, um número e um caractere especial.");
			}
			
			if (usuario.getNomeExibicao().length() < 3 || !RegexUtil.nomeUsuarioValido(usuario.getNomeExibicao())) {
				usuario.setNomeExibicao(null);
				
				throw new CadastrarException("Nome de usuário deve possuir entre 3 e 30 caracteres, "
							+ "sendo a primeira letra maiuscula. Pode ainda inserir - ou _ ou . além de números.");				
			}
			
			Usuario usuarioExiste;
			Usuario nomeUsuarioDisponivel;
			
			try {
				usuarioExiste = usuarioDAO.findByEmail(usuario.getEmail());
				
				if (usuarioExiste != null) {
					usuario.setEmail(null);
					
					throw new CadastrarException("Este e-mail já se encontra cadastrado.");
				}
				
				nomeUsuarioDisponivel = usuarioDAO.findByNomeExibicao(usuario.getNomeExibicao());
				
				if (nomeUsuarioDisponivel != null) {
					usuario.setNomeExibicao(null);
					
					throw new CadastrarException("Este nome de usuário não está disponível.");
				}
				
			// Caso o usuário não exista, ele retornará um erro dizendo que o array está vazio, 
			// sendo assim podemos continuar.
			} catch (IndexOutOfBoundsException e) {
				
				Message.info("E-mail e usuário válidos, cadastrando...");
			} catch (DatabaseException e) {
				Message.erro(e.getMessage());
			}
			
			
			try {
				usuario.setSenha(converteStringParaMd5(usuario.getSenha()));
				
				if (usuario.getSenha() != null) {
					usuarioDao.salvar(usuario);
				} else {
					throw new CadastrarException("Ocorreu um erro ao cadastrar este usuário.");
				}
				
			} catch (DatabaseException e) {
				
				Message.erro(e.getMessage() + " o usuário.");
			}
							
		}

	}
	

	public void remover(Usuario usuario) {

		// TODO - Validações

		try {
			usuarioDao.remover(Usuario.class, usuario.getId());
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage() + " o usuário.");
		}
		
	}

	public List<Usuario> listarTodos() {
		
		try {			
			return usuarioDao.buscarTodos(Usuario.class);
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage() + " os usuários.");
			return null;
		}
		
	}
	
	public Usuario buscarPorId(Long id){
		
		try {
			return usuarioDao.buscarPorId(Usuario.class, id);
		} catch (DatabaseException e) {
			Message.erro(e.getMessage() + " o usuário.");
			return null;
		}
		
	}

	
	// Verifica se o usuário existe ou se pode logar
	public Usuario usuarioPodeLogar(String email, String senha) {

		try {
			
			if (!RegexUtil.emailValido(email)) {
				email = null;
				
				throw new AutenticacaoException("E-mail inválido.");
			}
			
			if (!RegexUtil.senhaValida(senha)) {
				senha = null;
				
				throw new AutenticacaoException("A senha deve conter de 8 a 20 caracteres, "
						+ "sendo ao menos um maiusculo, um minusculo, um número e um caractere especial.");
			}
			
			System.out.println("Verificando login do usuário " + email);
			
			String senhaConvertida = converteStringParaMd5(senha);
			
			Usuario retorno;
			if(senhaConvertida != null) {
				retorno = usuarioDAO.findByNamedQuery(email, senhaConvertida);
				
				if (retorno != null) {
					return retorno;
				}
			} else {
				throw new AutenticacaoException("Ocorreu um erro ao autenticar este usuário.");
			}
			
			throw new AutenticacaoException("Email ou senha incorretas, verifique suas credenciais e tente novamente!");
			
		} catch (DatabaseException e) {
			Message.erro(e.getMessage() + " ao validar este usuário.");
			return null;
		}		

	}
	
	public String converteStringParaMd5(String senha) {
		MessageDigest mDigest;
		
		try {
			
			// Instanciamos o HASH MD5
			mDigest = MessageDigest.getInstance("MD5");
			
			// Converte a String senha para um array de bytes em MD5
			byte[] valorMD5 = mDigest.digest(senha.getBytes("UTF-8"));
			
			// Convertemos os bytes para hexadecimal, para poder salvar no
			// banco para depois podermos comparar senhas.
			StringBuffer sb = new StringBuffer();
			
			for (byte b : valorMD5) {
				sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
			}
			
			return sb.toString();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String gerarNovaSenha(String email, String senha) {		
		
		try {
			
			if (!RegexUtil.emailValido(email)) {
				email = null;
				
				throw new CadastrarException("E-mail inválido.");
			}
			
			if (!RegexUtil.senhaValida(senha)) {
				senha = null;
				
				throw new CadastrarException("A senha deve conter de 8 a 20 caracteres, "
						+ "sendo ao menos um maiusculo, um minusculo, um número e um caractere especial.");
			}		
			
			Usuario usuario = usuarioDAO.findByEmail(email);
			
			if (usuario == null) {
				email = null;
				
				throw new AutenticacaoException("O email fornecido não se encontra em nosso cadastro. Cadastra-se para ter acesso "
						+ "aos nossos serviços.");
			}
			
			if (usuario.getSenha().equals(converteStringParaMd5(senha))) {
				senha = null;
				
				throw new AutenticacaoException("As senhas não podem ser iguais.");
			}
		     
		     usuario.setSenha(converteStringParaMd5(senha));
		     
		     if (usuario.getSenha() != null) {
		    	 usuarioDao.atualizar(usuario);
		    	 return senha;
		     }
		     
		     return null;		     
		} catch (DatabaseException e) {			
			return null;
		}
		
	}

}
