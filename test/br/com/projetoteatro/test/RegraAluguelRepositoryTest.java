package br.com.projetoteatro.test;

import br.com.projetoteatro.enums.DiasDaSemana;
import br.com.projetoteatro.enums.Meses;
import br.com.projetoteatro.enums.Turno;
import br.com.projetoteatro.model.RegraAluguel;
import br.com.projetoteatro.repository.RegrasPrecoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegraAluguelRepositoryTest {

    @Test
    @DisplayName("Deve persistir, listar, atualizar e remover RegraAluguel mapeada no JPA")
    public void deveGerenciarRegraAluguel() {
        RegrasPrecoRepository repo = new RegrasPrecoRepository();

        RegraAluguel regra = new RegraAluguel(
                150.0,
                DiasDaSemana.SABADO,
                Turno.NOITE,
                Meses.DEZEMBRO,
                LocalTime.of(19, 0),
                LocalTime.of(23, 0)
        );

        repo.adicionarRegra(regra);
        assertNotNull(regra.getId(), "O ID da RegraAluguel deve ser gerado pelo JPA");

        RegraAluguel buscada = repo.buscarRegra(regra.getId());
        assertNotNull(buscada);
        assertEquals(150.0, buscada.getValorHora());
        assertEquals(DiasDaSemana.SABADO, buscada.getDiaDaSemana());
        assertEquals(Turno.NOITE, buscada.getTurno());

        buscada.setValorHora(200.0);
        repo.atualizar(buscada);
        RegraAluguel atualizada = repo.buscarRegra(regra.getId());
        assertEquals(200.0, atualizada.getValorHora());

        List<RegraAluguel> regras = repo.listarRegras();
        assertFalse(regras.isEmpty());

        repo.removerRegra(atualizada);
        assertNull(repo.buscarRegra(regra.getId()), "RegraAluguel deve ter sido excluída");
    }
}
