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
import projetotcc.model.Projeto;
import projetotcc.model.Usuario;
import projetotcc.utility.Message;

public class UsuarioService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private DAO<Usuario> usuarioDao;
	
	@Inject
	private UsuarioDAO usuarioDAO;
		

	public void salvar(Usuario usuario) throws CadastrarException {

		if (usuario == null) {
			System.out.println("O Usuário está nulo");
			throw new CadastrarException("Ocorreu um erro ao cadastrar o usuário.");
		}		
			
		try {
			
			validarUsuarioEmailExiste(usuario);
			validarUsuarioNomeExiste(usuario);
			
			usuario.setSenha(converteStringParaMd5(usuario.getSenha()));
			
			if (usuario.getSenha() != null) {
				usuarioDao.salvar(usuario);
			} else {
				throw new CadastrarException("Ocorreu um erro ao cadastrar este usuário.");
			}
		
		}  catch (DatabaseException e) {
			Message.erro(e.getMessage());
		}			

	}
	
	private void validarUsuarioEmailExiste(Usuario usuario) throws CadastrarException {
		
		Usuario usuarioExiste;

		usuarioExiste = usuarioDAO.findByEmail(usuario.getEmail());
		
		if (usuarioExiste != null) {
			usuario.setEmail(null);
			
			throw new CadastrarException("Este e-mail já se encontra cadastrado.");
		} else {
			Message.info("Este e-mail é valido.");
		}

	}
	
	private void validarUsuarioNomeExiste(Usuario usuario) throws CadastrarException {		

		Usuario nomeUsuarioDisponivel;
			
		nomeUsuarioDisponivel = usuarioDAO.findByNomeExibicao(usuario.getNomeExibicao());
		
		if (nomeUsuarioDisponivel != null) {
			usuario.setNomeExibicao(null);
			
			throw new CadastrarException("Este nome de usuário não está disponível.");
		} else {
			Message.info("Este nome de usuário é valido.");
		}

	}

	public void remover(Usuario usuario) {

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
	
	public List<Projeto> buscarProjetosPorUsuarioId(Long id){
		
		try {
			return usuarioDAO.findByUsuarioProjetos(id);
		} catch (DatabaseException e) {
			Message.erro(e.getMessage());
			return null;
		}
		
	}
	
	// Verifica se o usuário existe ou se pode logar
	public Usuario usuarioPodeLogar(String email, String senha) {

		try {
			
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
			Message.erro(e.getMessage());
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
	
	public String gerarNovaSenha(String email, String senha) throws CadastrarException {		
		
		try {
						
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
