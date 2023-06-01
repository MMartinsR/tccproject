package projetotcc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import projetotcc.dao.DAO;
import projetotcc.dao.UsuarioDAO;
import projetotcc.exception.AutenticacaoException;
import projetotcc.exception.CadastrarException;
import projetotcc.exception.DatabaseException;
import projetotcc.exception.SemResultadoException;
import projetotcc.model.Projeto;
import projetotcc.model.Usuario;

class UsuarioServiceTest {
	
	private String senhaBase;
	private String emailBase;
	private List<Usuario> usuarios;
	
	@Mock
	private DAO<Usuario> usuarioDao;
	
	@Mock
	private UsuarioDAO usuarioDAO;

	@InjectMocks
	private UsuarioService usuarioService;
	
	private Usuario usuario1;
	private Usuario usuario2;
		
	
	@BeforeEach
	public void setUp() {
		
		MockitoAnnotations.initMocks(this);
		
		senhaBase = "Senhamari123@";
		emailBase = "mariana.martins.rosa.mmr@gmail.com";
		
		usuarios = new ArrayList<Usuario>();
		
		usuario1 = new Usuario();
		usuario1.setEmail("mariana.martins.rosa.mmr@gmail.com");
		usuario1.setNomeExibicao("Mariana Rosa");
		usuario1.setSenha("3263f4266599af106d89c4c74a06a9b8");
		usuario1.setId(1L);
		
		usuario2 = new Usuario();
		usuario2.setEmail("chidori_mari@hotmail.com");
		usuario2.setNomeExibicao("Chidori Martins");
		usuario2.setSenha("791f057c2473dc4409bcf686e495939e");
		usuario2.setId(2L);
		
		usuarios.add(usuario1);
		usuarios.add(usuario2);
			
	}

	@Test
	@DisplayName("Salvar_usuario_sucesso")
	void testSalvarSucesso() {
		
		Usuario usuario = new Usuario();
		usuario.setEmail("chidori_mari@hotmail.com");
		usuario.setNomeExibicao("Chidori Martins");
		usuario.setSenha("791f057c2473dc4409bcf686e495939e");
		
		usuarioService.salvar(usuario);
		
		Mockito.verify(usuarioDao).salvar(Mockito.anyObject());
	}
	
	@Test
	@DisplayName("Salvar_usuario_falha")
	void testSalvarFalha() {
		
		Usuario usuario = new Usuario();
		usuario.setEmail("chidori_mari@hotmail.com");
		usuario.setNomeExibicao("Chidori Martins");
		usuario.setSenha("791f057c2473dc4409bcf686e495939e");
		
		Mockito.doThrow(new CadastrarException("Ocorreu um erro ao cadastrar o usuário."))
		.when(usuarioDao).salvar(usuario);
		
		assertThrows(CadastrarException.class, () -> usuarioService.salvar(usuario));
	}

	@Test
	@DisplayName("Buscar_todos_usuarios_sucesso")
	void testListarTodosSucesso() {
		
		when(usuarioDao.buscarTodos(Usuario.class)).thenReturn(usuarios);
		
		assertEquals(2, usuarioService.listarTodos().size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Buscar_todos_usuarios_falha")
	void testListarTodosFalha() {
		
		when(usuarioDao.buscarTodos(Usuario.class)).thenThrow(DatabaseException.class);
		
		assertThrows(DatabaseException.class, () -> usuarioDao.buscarTodos(Usuario.class));
	}

	@Test
	@DisplayName("buscar_por_id_sucesso")
	void testBuscarPorIdSucesso() {
		
		when(usuarioDao.buscarPorId(Usuario.class, 1L)).thenReturn(usuario1);
		
		assertEquals(usuario1, usuarioService.buscarPorId(1L));
	}
	

	@Test
	@DisplayName("buscar_por_id_falha")
	void testBuscarPorIdFalha() {
		
		when(usuarioDao.buscarPorId(Usuario.class, 5L)).thenReturn(null);
		
		assertNull(usuarioService.buscarPorId(5L));
	}

	@Test
	@DisplayName("Buscando_por_email_sucesso")
	void testBuscarPorEmailSucesso() {
		
		when(usuarioDAO.findByEmail(emailBase)).thenReturn(usuario1);		
		
		assertEquals(usuario1, usuarioService.buscarPorEmail(emailBase));		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Buscando_por_email_falha")
	void testBuscarPorEmailFalha() {
		
		when(usuarioDAO.findByEmail(emailBase)).thenThrow(SemResultadoException.class);		
		
		assertThrows(SemResultadoException.class, () -> usuarioService.buscarPorEmail(emailBase));		
	}

	@Test
	@DisplayName("Buscando_projetos_por_id_de_usuario_sucesso")
	void testBuscarProjetosPorUsuarioIdSucesso() {
		
		List<Projeto> projetos = new ArrayList<Projeto>();
		
		Projeto projeto1 = new Projeto();
		projeto1.setCodigo("@WQ2MF0I2y");
		projeto1.setCriador("Mariana Rosa");
		projeto1.setDescricao("projeto mock");
		projeto1.setId(1L);
		projeto1.setNome("Mockando projeto");
		
		projetos.add(projeto1);
		
		when(usuarioDAO.findByUsuarioProjetos(Mockito.anyLong())).thenReturn(projetos);
		
		assertEquals(1, usuarioService.buscarProjetosPorUsuarioId(Mockito.anyLong()).size());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Buscando_projetos_por_id_de_usuario_falha")
	void testBuscarProjetosPorUsuarioIdFalha() {
		
		when(usuarioDAO.findByUsuarioProjetos(Mockito.anyLong())).thenThrow(SemResultadoException.class);
		
		assertThrows(SemResultadoException.class, () -> usuarioService.buscarProjetosPorUsuarioId(Mockito.anyLong()));
		
	}

	@Test
	@DisplayName("Esse_usuario_tem_dados_validos_pode_logar_sucesso")
	void testUsuarioPodeLogarSucesso() {		
		
		when(usuarioDAO.findByEmail(emailBase)).thenReturn(usuario1);
		when(usuarioDAO.findByNamedQuery(emailBase, "b509cbc018f40bf5dbff5deb1394935a")).thenReturn(usuario1);	
		
		Assertions.assertEquals(usuario1, usuarioService.usuarioPodeLogar(emailBase, senhaBase));
		
	}
	
	@Test
	@DisplayName("Esse_usuario_não_tem_dados_validos_para_logar_senha_errada_falha")
	void testUsuarioPodeLogarSenhaFalha() {

		String senha = "Sor1293";
		
		when(usuarioDAO.findByEmail(emailBase)).thenReturn(usuario1);
		when(usuarioDAO.findByNamedQuery(emailBase, "2222f4267799af106d89c4d74a04a9b5")).thenReturn(null);	
		
		
		Assertions.assertThrows(AutenticacaoException.class, () -> usuarioService.usuarioPodeLogar(emailBase, senha));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Esse_usuario_não_tem_dados_validos_para_logar_email_errado_falha")
	void testUsuarioPodeLogarEmailFalha() {
		
		String email = "exemplo@email.com";
		String senha = "Sor1293";
		
		when(usuarioDAO.findByEmail(email)).thenThrow(SemResultadoException.class);	
		
		
		Assertions.assertThrows(SemResultadoException.class, () -> usuarioService.usuarioPodeLogar(email, senha));
		
	}

	@Test
	@DisplayName("Senha_convertidaMD5_sucesso")
	void testConverteStringParaMd5Sucesso() {
		
		Assertions.assertNotSame(senhaBase, usuarioService.converteStringParaMd5(senhaBase));
	}
	

	@Test
	@DisplayName("Gerar_nova_senha_sucesso")
	void testGerarNovaSenhaSucesso() {
		String senha = "Senhamari123@";
		
		when(usuarioDAO.findByEmail(emailBase)).thenReturn(usuario1);				
		
		Assertions.assertEquals(senha, usuarioService.gerarNovaSenha(emailBase, senha));
		
		Mockito.verify(usuarioDao).atualizar(Mockito.anyObject());
	}
	
	@Test
	@DisplayName("Gerar_nova_senha_senhas_iguais_falha")
	void testGerarNovaSenhaFalha() {
		
		String email = "mariana.martins.rosa.mmr@gmail.com";
		String senha = "Senhamar123@";
		
		when(usuarioDAO.findByEmail(email)).thenReturn(usuario1);
		
		Assertions.assertThrows(AutenticacaoException.class, () -> usuarioService.gerarNovaSenha(email, senha));
	}

}
