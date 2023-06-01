package projetotcc.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import projetotcc.enums.PrioridadeEnum;
import projetotcc.enums.StatusEnum;
import projetotcc.exception.DatabaseException;
import projetotcc.model.Tarefa;

class DAOTest {
	
	private DAO<Tarefa> tarefaDao;
	private Tarefa tarefa;
	private Tarefa tarefaSalvar;
	private Tarefa tarefaAtualizar;
	private PrioridadeEnum prioridade;
	private StatusEnum status;
	
	@BeforeEach
	public void setUp() {
		tarefaDao = new DAO<Tarefa>();
		tarefa = null;
		prioridade = PrioridadeEnum.BAIXA;
		status = StatusEnum.ABERTA;
		tarefaSalvar = new Tarefa();
		tarefaSalvar.setNome("Tarefa de teste segundo");
		tarefaSalvar.setDataEntrega(new Date());
		tarefaSalvar.setDescricao("descricao de teste segundo");
		tarefaSalvar.setPrioridade(prioridade);
		tarefaSalvar.setStatus(status);
		tarefaAtualizar = new Tarefa();
		
	}

	@Test
	@DisplayName("Buscando_(Tarefa)_por_id_que_não_existe_falha")
	void testBuscarPorIdFalha() {
		
		tarefa = tarefaDao.buscarPorId(Tarefa.class, 1L);
		
		assertNull(tarefa);
		
	}
	
	@Test
	@DisplayName("Buscando_(Tarefa)_por_id_que_existe_sucesso")
	void testBuscarPorIdSucesso() {
		
		tarefa = tarefaDao.buscarPorId(Tarefa.class, 6L);
		
		assertNotNull(tarefa);
		
	}

	@Test
	@DisplayName("Salvando_(Tarefa)_nova_sucesso")
	void testSalvarSucesso() {
		
		tarefaDao.salvar(tarefaSalvar);
		
		tarefa = tarefaDao.buscarPorId(Tarefa.class, 20L);
		
		assertNotNull(tarefa);
		
	}
	
	@Test
	@DisplayName("Salvando_(Tarefa)_nova_falha")
	void testSalvarFalha() {
		
		assertThrows(DatabaseException.class, () -> tarefaDao.salvar(tarefaSalvar));
		
	}

	@Test
	@DisplayName("Atualizando_(Tarefa)_sucesso")
	void testAtualizarSucesso() {
		
		tarefaAtualizar = tarefaDao.buscarPorId(Tarefa.class, 17L);
		
		tarefaAtualizar.setNome("Testando trocar nome por teste");
		
		assertDoesNotThrow(() -> tarefaDao.atualizar(tarefaAtualizar));		
		
	}
	
	@Test
	@DisplayName("Atualizando_(Tarefa)_falha")
	void testAtualizarFalha() {
		
		tarefaAtualizar = tarefaDao.buscarPorId(Tarefa.class, 17L);
		
		tarefaAtualizar.setNome("Testando trocar nome por teste");
		
		tarefaDao.atualizar(tarefaAtualizar);
		
		assertThrows(DatabaseException.class, () -> tarefaDao.atualizar(tarefaAtualizar));		
		
	}
	

	@Test
	@DisplayName("Removendo_(Tarefa)_sucesso")
	void testRemoverSucesso() {
		
		tarefaDao.remover(Tarefa.class, 22L);
		
		tarefa = tarefaDao.buscarPorId(Tarefa.class, 22L);
		
		assertNull(tarefa);
	}

	@Test
	@DisplayName("Removendo_(Tarefa)_falha")
	void testRemoverFalha() {		
		
		assertThrows(DatabaseException.class, () -> tarefaDao.remover(Tarefa.class, 19L));
	}

	@Test
	@DisplayName("Buscando_todas_tarefas_sucesso")
	void testBuscarTodosSucesso() {
		
		assertDoesNotThrow(() -> tarefaDao.buscarTodos(Tarefa.class));
		
	}

}
