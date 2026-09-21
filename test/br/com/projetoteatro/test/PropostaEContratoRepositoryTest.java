package br.com.projetoteatro.test;

import br.com.projetoteatro.enums.StatusContrato;
import br.com.projetoteatro.enums.StatusProposta;
import br.com.projetoteatro.model.Contratante;
import br.com.projetoteatro.model.Contrato;
import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.repository.ContratoRepository;
import br.com.projetoteatro.repository.PropostasRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PropostaEContratoRepositoryTest {

    @Test
    @DisplayName("Deve persistir Proposta com Contratante em cascata e gerar Contrato ativo")
    public void deveGerenciarPropostaEContrato() {
        PropostasRepository propostaRepo = new PropostasRepository();
        ContratoRepository contratoRepo = new ContratoRepository();

        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        Contratante contratante = new Contratante("Produtor " + sufixo, "produtor_" + sufixo + "@teatro.com", "11988887777", "555" + sufixo);

        PropostaAluguel proposta = new PropostaAluguel(
                contratante,
                "Espetáculo Teste " + sufixo,
                5000.0,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(12),
                LocalTime.of(19, 0),
                LocalTime.of(22, 0),
                50.0
        );

        propostaRepo.adicionarProposta(proposta);
        assertNotNull(proposta.getId(), "ID da proposta deve ser gerado pelo JPA");
        assertNotNull(proposta.getContratante().getId(), "ID do contratante deve ser gerado em cascata");

        PropostaAluguel buscada = propostaRepo.buscarProposta(proposta.getId());
        assertNotNull(buscada);
        assertEquals("Espetáculo Teste " + sufixo, buscada.getNomePeca());

        buscada.setStatusProposta(StatusProposta.CONTRATADO);
        propostaRepo.atualizar(buscada);
        assertEquals(StatusProposta.CONTRATADO, propostaRepo.buscarProposta(proposta.getId()).getStatusProposta());

        Contrato contrato = new Contrato(buscada);
        contrato.setNomePeca(buscada.getNomePeca());
        contrato.setContratante(contratante.getNome());
        contrato.setEmail(contratante.getEmail());
        contrato.setValorAluguel(buscada.getValorAluguel());
        contrato.setValorIngresso(buscada.getValorIngresso());
        contrato.setDataInicio(buscada.getDataInicio());
        contrato.setDataFim(buscada.getDataFim());
        contrato.setHorarioInicio(buscada.getHorarioInicio());
        contrato.setHorarioFim(buscada.getHorarioFim());
        contrato.setStatusContrato(StatusContrato.ATIVO);

        contratoRepo.adicionarContrato(contrato);
        assertNotNull(contrato.getId(), "ID do contrato deve ser gerado pelo JPA");

        Contrato contratoBuscado = contratoRepo.buscarContrato(contrato.getId());
        assertNotNull(contratoBuscado);
        assertEquals(StatusContrato.ATIVO, contratoBuscado.getStatusContrato());

        List<Contrato> ativos = contratoRepo.listarContratosAtivos();
        assertTrue(ativos.stream().anyMatch(c -> c.getId().equals(contrato.getId())), "Contrato deve estar na lista de ativos");

        List<Contrato> porNome = contratoRepo.buscarPorNomePeca("Espetáculo Teste " + sufixo);
        assertFalse(porNome.isEmpty(), "Deve encontrar contrato pelo nome da peça");

        contratoRepo.removerContrato(contratoBuscado);
        propostaRepo.removerProposta(buscada);
    }
}
