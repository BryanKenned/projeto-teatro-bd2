package br.com.projetoteatro.test;

import br.com.projetoteatro.enums.StatusContrato;
import br.com.projetoteatro.enums.TipoSetor;
import br.com.projetoteatro.enums.Turno;
import br.com.projetoteatro.model.*;
import br.com.projetoteatro.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IngressoESessaoRepositoryTest {

    @Test
    @DisplayName("Deve persistir Sessão, emitir Ingressos e calcular faturamento do mês")
    public void deveGerenciarSessaoEIngresso() {
        SessaoRepository sessaoRepo = new SessaoRepository();
        IngressoRepository ingressoRepo = new IngressoRepository();
        ClienteRepository clienteRepo = new ClienteRepository();
        ContratoRepository contratoRepo = new ContratoRepository();

        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);

        Usuario cliente = new Usuario("Espectador " + sufixo, "espectador_" + sufixo + "@email.com", "11999999999", "444" + sufixo, "1234");
        clienteRepo.adicionarCliente(cliente);
        assertNotNull(cliente.getId());

        Contrato contrato = new Contrato();
        contrato.setNomePeca("O Grande Show " + sufixo);
        contrato.setValorIngresso(80.0);
        contrato.setStatusContrato(StatusContrato.ATIVO);
        contratoRepo.adicionarContrato(contrato);
        assertNotNull(contrato.getId());

        Sessao sessao = new Sessao("O Grande Show " + sufixo, LocalTime.of(20, 0));
        sessao.setData(LocalDate.now());
        sessao.setTurno(Turno.NOITE);
        sessao.setHorarioFim(LocalTime.of(22, 30));
        sessaoRepo.adicionarSessao(sessao);
        assertNotNull(sessao.getId());

        Ingresso ingresso = new Ingresso(
                cliente,
                sessao,
                null,
                TipoSetor.PLATEIA,
                true,
                80.0,
                contrato
        );
        ingressoRepo.adicionarIngresso(ingresso);
        assertNotNull(ingresso.getId(), "ID do ingresso deve ser gerado pelo banco");

        Ingresso buscado = ingressoRepo.buscarIngresso(ingresso.getId());
        assertNotNull(buscado);
        assertEquals(80.0, buscado.getValor());
        assertEquals("O Grande Show " + sufixo, buscado.getContrato().getNomePeca());

        List<Ingresso> ingressosContrato = ingressoRepo.buscarPorContrato(contrato.getId());
        assertFalse(ingressosContrato.isEmpty());

        double faturamento = ingressoRepo.calcularFaturamentoMesAtual();
        assertTrue(faturamento >= 80.0, "Faturamento do mês deve contabilizar o ingresso vendido");

        long vendidosHoje = ingressoRepo.contarIngressosVendidosHoje();
        assertTrue(vendidosHoje >= 1, "Deve contar ao menos 1 ingresso vendido hoje");

        ingressoRepo.removerIngresso(buscado);
        sessaoRepo.removerSessao(sessao);
        contratoRepo.removerContrato(contrato);
        clienteRepo.removerCliente(cliente);
    }
}
