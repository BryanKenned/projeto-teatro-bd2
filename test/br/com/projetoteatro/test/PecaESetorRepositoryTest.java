package br.com.projetoteatro.test;

import br.com.projetoteatro.enums.StatusAssento;
import br.com.projetoteatro.enums.StatusProposta;
import br.com.projetoteatro.enums.TipoSetor;
import br.com.projetoteatro.model.Assento;
import br.com.projetoteatro.model.Contratante;
import br.com.projetoteatro.model.Peca;
import br.com.projetoteatro.model.Setor;
import br.com.projetoteatro.repository.ArtistaRepository;
import br.com.projetoteatro.repository.AssentoRepository;
import br.com.projetoteatro.repository.PecaRepository;
import br.com.projetoteatro.repository.SetorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PecaESetorRepositoryTest {

    @Test
    @DisplayName("PecaRepository: Deve persistir, buscar por nome, atualizar e remover Peça no MySQL")
    public void deveGerenciarPeca() throws Exception {
        PecaRepository pecaRepo = new PecaRepository();
        ArtistaRepository artistaRepo = new ArtistaRepository();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);

        Contratante artista = new Contratante("Diretor " + sufixo, "diretor_" + sufixo + "@email.com", "81988880000", "cpf_dir_" + sufixo, "123");
        artistaRepo.adicionarArtista(artista);
        assertNotNull(artista.getId());

        Peca peca = new Peca();
        peca.setNome("Hamlet Contemporâneo " + sufixo);
        peca.setArtistaResponsavel(artista);
        peca.setDataInicio(LocalDate.now());
        peca.setDataFim(LocalDate.now().plusDays(15));
        peca.setPrecoIngresso(90.0);
        peca.setStatus(StatusProposta.EM_CONTRATACAO);
        peca.setValorAluguel(4000.0);

        pecaRepo.adicionarPeca(peca);
        assertNotNull(peca.getId());

        Peca buscada = pecaRepo.buscarPeca(peca.getId());
        assertNotNull(buscada);
        assertEquals("Hamlet Contemporâneo " + sufixo, buscada.getNome());

        List<Peca> porNome = pecaRepo.buscarPorNome("Hamlet Contemporâneo " + sufixo);
        assertFalse(porNome.isEmpty());

        buscada.setPrecoIngresso(95.0);
        pecaRepo.atualizar(buscada);
        Peca atualizada = pecaRepo.buscarPeca(peca.getId());
        assertEquals(95.0, atualizada.getPrecoIngresso());

        pecaRepo.removerPeca(atualizada);
        assertNull(pecaRepo.buscarPeca(peca.getId()));

        artistaRepo.excluirArtista(artista);
    }

    @Test
    @DisplayName("SetorRepository e AssentoRepository: Deve persistir Setor, associar Assentos e consultar")
    public void deveGerenciarSetorEAssento() {
        SetorRepository setorRepo = new SetorRepository();
        AssentoRepository assentoRepo = new AssentoRepository();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);

        Setor setor = new Setor(TipoSetor.CAMAROTE, 120.0, 50);
        setorRepo.adicionarSetor(setor);
        assertNotNull(setor.getId());

        String codAssento = "C1_" + sufixo;
        Assento assento = new Assento(codAssento, StatusAssento.DISPONIVEL, setor);
        assentoRepo.adicionarAssento(assento);

        Assento assentoBuscado = assentoRepo.buscarPorCodigo(codAssento);
        assertNotNull(assentoBuscado);
        assertEquals(StatusAssento.DISPONIVEL, assentoBuscado.getStatus());

        assentoBuscado.setStatus(StatusAssento.OCUPADO);
        assentoRepo.atualizar(assentoBuscado);
        Assento assentoAtualizado = assentoRepo.buscarPorCodigo(codAssento);
        assertEquals(StatusAssento.OCUPADO, assentoAtualizado.getStatus());

        List<Assento> assentos = assentoRepo.listarAssentos();
        assertFalse(assentos.isEmpty());

        List<Setor> setores = setorRepo.listarSetores();
        assertFalse(setores.isEmpty());

        assentoRepo.remover(assentoAtualizado);
        assertNull(assentoRepo.buscarPorCodigo(codAssento));

        setorRepo.removerSetor(setor);
    }
}
