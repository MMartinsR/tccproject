package projetotcc.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.inject.Inject;

import projetotcc.dao.DAO;
import projetotcc.dao.UsuarioDAO;
import projetotcc.model.Usuario;

public class UsuarioService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private DAO<Usuario> usuarioDao;
	
	@Inject
	private UsuarioDAO usuarioDAO;
	

	public void salvar(Usuario usuario) {

		// TODO - Validações
		
		if (usuario != null) {
			
			usuarioDao.salvar(usuario);	
		}

	}
	
	public void atualizar(Usuario usuario) {
		
		// TODO - Validações
		
		if (usuario != null) {

			usuarioDao.atualizar(usuario);	
		}
	}

	public void remover(Usuario usuario) {

		// TODO - Validações

		usuarioDao.remover(Usuario.class, usuario.getId());
	}

	public List<Usuario> listarTodos() {
		
		return usuarioDao.buscarTodos(Usuario.class);
	}
	
	// Verifica se o usuário existe ou se pode logar
	public Usuario usuarioPodeLogar(String email, String senha) {
		try {			
			email = email.toLowerCase().trim();
			System.out.println("Verificando login do usuário" + email);
			List<Usuario> retorno = usuarioDAO.findByNamedQuery(email, converteStringParaMd5(senha));
			
			if (retorno.size() == 1) {
				Usuario usuarioEncontrado = (Usuario) retorno.get(0);
				return usuarioEncontrado;
			}
			
			return null;
		} catch (Exception e) {
			e.printStackTrace();
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
		
		Usuario usuario = usuarioDAO.findByEmail(email);
		
		/*
		 * String[] carct = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a",
		 * "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
		 * "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E",
		 * "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
		 * "U", "V", "W", "X", "Y", "Z" };
		 * 
		 * String senha = "";
		 * 
		 * for (int x = 0; x < 10; x++) { int j = (int) (Math.random() * carct.length);
		 * senha += carct[j];
		 * 
		 * }
		 */
	     
	     usuario.setSenha(converteStringParaMd5(senha));
	     
	     usuarioDao.atualizar(usuario);

	     return senha;
	}

}
