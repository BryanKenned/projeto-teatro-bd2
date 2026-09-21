package br.com.projetoteatro.test;

import br.com.projetoteatro.model.Contratante;
import br.com.projetoteatro.repository.ArtistaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArtistaRepositoryTest {

    @Test
    @DisplayName("Deve persistir, buscar, atualizar e remover Contratante/Artista no MySQL")
    public void deveGerenciarArtista() throws Exception {
        ArtistaRepository repo = new ArtistaRepository();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String cpf = "777" + sufixo;
        String email = "artista_" + sufixo + "@arte.com";

        Contratante artista = new Contratante("Companhia de Teatro " + sufixo, email, "777777777", cpf, "senhaArte");

        repo.adicionarArtista(artista);
        assertNotNull(artista.getId(), "ID do artista deve ser gerado pelo JPA");

        Contratante buscadoCpf = repo.buscarContratante(cpf);
        assertNotNull(buscadoCpf);
        assertEquals(email, buscadoCpf.getEmail());

        Contratante buscadoEmail = repo.buscarContratanteEmail(email);
        assertNotNull(buscadoEmail);

        buscadoEmail.setNome("Companhia Atualizada");
        repo.atualizar(buscadoEmail);
        Contratante atualizado = repo.buscarContratante(cpf);
        assertEquals("Companhia Atualizada", atualizado.getNome());

        List<Contratante> lista = repo.listarArtista();
        assertFalse(lista.isEmpty());

        repo.excluirArtista(atualizado);
        assertNull(repo.buscarContratante(cpf), "Artista deve ser excluído com sucesso");
    }
}
