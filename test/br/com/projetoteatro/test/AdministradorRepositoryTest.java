package br.com.projetoteatro.test;

import br.com.projetoteatro.model.Administrador;
import br.com.projetoteatro.repository.AdministradorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AdministradorRepositoryTest {

    @Test
    @DisplayName("Deve persistir, buscar, atualizar e remover Administrador com JPA")
    public void deveGerenciarAdministrador() {
        AdministradorRepository repo = new AdministradorRepository();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String cpf = "999" + sufixo;
        String email = "adm_" + sufixo + "@teatro.com";

        Administrador adm = new Administrador("Adm Teste", email, "999999999", cpf, "senha123");

        repo.adicionarAdm(adm);
        assertNotNull(adm.getId(), "O ID do administrador deve ser gerado pelo JPA");

        Administrador buscadoCpf = repo.buscarAdm(cpf);
        assertNotNull(buscadoCpf, "Deve encontrar administrador pelo CPF");
        assertEquals("Adm Teste", buscadoCpf.getNome());

        Administrador buscadoEmail = repo.buscarAdmEmail(email);
        assertNotNull(buscadoEmail, "Deve encontrar administrador pelo e-mail");

        assertTrue(repo.verSeAdmEstaPreenchido(), "Deve retornar true para administradores preenchidos");

        adm.setSenha("novaSenha456");
        repo.salvarOuAtualizar(adm);
        Administrador admAtualizado = repo.buscarAdmEmail(email);
        assertEquals("novaSenha456", admAtualizado.getSenha(), "Senha deve ter sido atualizada no MySQL");

        List<Administrador> lista = repo.listarTodos();
        assertFalse(lista.isEmpty(), "Lista de administradores não deve estar vazia");

        repo.excluirAdm(cpf);
        Administrador admExcluido = repo.buscarAdm(cpf);
        assertNull(admExcluido, "Administrador deve ter sido removido do banco");
    }
}
