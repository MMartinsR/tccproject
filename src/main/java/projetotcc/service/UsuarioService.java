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
import projetotcc.model.Usuario;
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
				throw new CadastrarException("E-mail inválido.");
			}
			
			if (!RegexUtil.senhaValida(usuario.getSenha())) {
				throw new CadastrarException("A senha deve conter de 8 a 20 caracteres, "
						+ "sendo ao menos um maiusculo, um minusculo, um número e um caractere especial.");
			}
			
			if (usuario.getNomeExibicao().length() < 3 || !RegexUtil.nomeUsuarioValido(usuario.getNomeExibicao())) {
				throw new CadastrarException("Nome de usuário deve possuir entre 3 e 30 caracteres, "
							+ "sendo a primeira letra maiuscula. Pode ainda inserir - ou _ ou . além de números.");				
			}
			
			Usuario usuarioExiste = usuarioDAO.findByEmail(usuario.getEmail());
			Usuario nomeUsuarioDisponivel = usuarioDAO.findByNomeExibicao(usuario.getNomeExibicao());
			
			if (usuarioExiste != null) {
				throw new CadastrarException("Este e-mail já se encontra cadastrado.");
			}
			
			if (nomeUsuarioDisponivel != null) {
				throw new CadastrarException("Este nome de usuário não está disponível.");
			}
			
			usuario.setSenha(converteStringParaMd5(usuario.getSenha()));
			
			usuarioDao.salvar(usuario);	
		}

	}
	

	public void remover(Usuario usuario) {

		// TODO - Validações

		usuarioDao.remover(Usuario.class, usuario.getId());
	}

	public List<Usuario> listarTodos() {
		
		return usuarioDao.buscarTodos(Usuario.class);
	}
	
	public List<Usuario> buscarPorId(Long id){
		return usuarioDAO.findByNamedQuery(id);
	}

	
	// Verifica se o usuário existe ou se pode logar
	public Usuario usuarioPodeLogar(String email, String senha) {

		if (!RegexUtil.emailValido(email)) {
			throw new AutenticacaoException("E-mail inválido.");
		}
		
		if (!RegexUtil.senhaValida(senha)) {
			throw new AutenticacaoException("A senha deve conter de 8 a 20 caracteres, "
					+ "sendo ao menos um maiusculo, um minusculo, um número e um caractere especial.");
		}
		
		System.out.println("Verificando login do usuário " + email);
		List<Usuario> retorno = usuarioDAO.findByNamedQuery(email, converteStringParaMd5(senha));
		
		if (retorno != null && retorno.size() == 1) {
			Usuario usuarioEncontrado = (Usuario) retorno.get(0);
			return usuarioEncontrado;
		}
		
		throw new AutenticacaoException("Email ou senha incorretas, verifique suas credenciais e tente novamente!");

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
		
		if (!RegexUtil.emailValido(email)) {
			throw new CadastrarException("E-mail inválido.");
		}
		
		if (!RegexUtil.senhaValida(senha)) {
			throw new CadastrarException("A senha deve conter de 8 a 20 caracteres, "
					+ "sendo ao menos um maiusculo, um minusculo, um número e um caractere especial.");
		}		
		
		Usuario usuario = usuarioDAO.findByEmail(email);
		
		if (usuario == null) {
			throw new AutenticacaoException("O email fornecido não se encontra em nosso cadastro. Cadastra-se para ter acesso "
					+ "aos nossos serviços.");
		}
		
		if (usuario.getSenha().equals(converteStringParaMd5(senha))) {
			throw new AutenticacaoException("As senhas não podem ser iguais.");
		}
	     
	     usuario.setSenha(converteStringParaMd5(senha));
	     
	     usuarioDao.atualizar(usuario);	

	     return senha;
	}

}
