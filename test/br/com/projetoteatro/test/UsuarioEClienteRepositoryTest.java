package br.com.projetoteatro.test;

import br.com.projetoteatro.model.Usuario;
import br.com.projetoteatro.repository.ClienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioEClienteRepositoryTest {

    @Test
    @DisplayName("Deve persistir, consultar, atualizar e remover Cliente/Usuario no MySQL")
    public void deveGerenciarCliente() {
        ClienteRepository repo = new ClienteRepository();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String cpf = "888" + sufixo;
        String email = "cliente_" + sufixo + "@email.com";

        Usuario cliente = new Usuario("Cliente Teste", email, "888888888", cpf, "1234");

        repo.adicionarCliente(cliente);
        assertNotNull(cliente.getId(), "ID do cliente deve ser gerado pelo banco");

        Usuario buscadoCpf = repo.buscarCliente(cpf);
        assertNotNull(buscadoCpf);
        assertEquals("Cliente Teste", buscadoCpf.getNome());

        Usuario buscadoEmail = repo.buscarClienteEmail(email);
        assertNotNull(buscadoEmail);
        assertEquals(cpf, buscadoEmail.getCpf());

        buscadoEmail.setNome("Cliente Teste Atualizado");
        repo.atualizar(buscadoEmail);
        Usuario atualizado = repo.buscarCliente(cpf);
        assertEquals("Cliente Teste Atualizado", atualizado.getNome());

        List<Usuario> lista = repo.listarCliente();
        assertFalse(lista.isEmpty());

        repo.removerCliente(atualizado);
        assertNull(repo.buscarCliente(cpf), "Cliente deve ser removido com sucesso");
    }
}
