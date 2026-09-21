package br.com.projetoteatro.test;

import br.com.projetoteatro.enums.*;
import br.com.projetoteatro.exceptions.ConflitoHorarioException;
import br.com.projetoteatro.exceptions.LoginInvalidoException;
import br.com.projetoteatro.model.*;
import br.com.projetoteatro.repository.*;
import br.com.projetoteatro.service.*;
import br.com.projetoteatro.service.validators.ServicoTeatro;
import br.com.projetoteatro.service.validators.ValidadorHorarios;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServicesRegrasNegocioTest {

    @Test
    @DisplayName("ValidadorHorarios: Deve validar horários dentro dos turnos e identificar conflitos de agendamento")
    public void deveValidarHorariosEConflitos() throws Exception {
        ValidadorHorarios validador = new ValidadorHorarios();

        assertDoesNotThrow(() -> validador.duracaoPecaPorPeriodo(LocalTime.of(19, 0), LocalTime.of(21, 0)));

        assertDoesNotThrow(() -> validador.duracaoPecaPorPeriodo(LocalTime.of(8, 0), LocalTime.of(12, 0)));
        assertDoesNotThrow(() -> validador.duracaoPecaPorPeriodo(LocalTime.of(13, 0), LocalTime.of(18, 0)));
        assertDoesNotThrow(() -> validador.duracaoPecaPorPeriodo(LocalTime.of(19, 0), LocalTime.of(23, 0)));

        assertThrows(ConflitoHorarioException.class, () ->
                validador.duracaoPecaPorPeriodo(LocalTime.of(11, 0), LocalTime.of(14, 0))
        );

        PropostaAluguel existente = new PropostaAluguel();
        existente.setDataInicio(LocalDate.now());
        existente.setDataFim(LocalDate.now().plusDays(2));
        existente.setHorarioInicio(LocalTime.of(19, 0));
        existente.setHorarioFim(LocalTime.of(21, 0));

        ArrayList<PropostaAluguel> lista = new ArrayList<>();
        lista.add(existente);

        assertThrows(ConflitoHorarioException.class, () ->
                validador.validarConflitoHorario(
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        LocalTime.of(20, 0),
                        LocalTime.of(22, 0),
                        lista
                )
        );
    }

    @Test
    @DisplayName("RegrasService: Deve calcular o valor de aluguel aplicando as regras de preço vigentes")
    public void deveCalcularValorAluguelPorRegras() throws Exception {
        ServicoTeatro teatro = new ServicoTeatro();
        RegrasService regrasService = teatro.getRegrasService();

        RegraAluguel regraNoite = new RegraAluguel(
                150.0,
                DiasDaSemana.SABADO,
                Turno.NOITE,
                Meses.JULHO,
                LocalTime.of(19, 0),
                LocalTime.of(23, 0)
        );
        regrasService.cadastrarRegra(regraNoite);
        assertNotNull(regraNoite.getId());

        PropostaAluguel proposta = new PropostaAluguel();
        proposta.setDataInicio(LocalDate.of(2026, 7, 11));
        proposta.setDataFim(LocalDate.of(2026, 7, 11));
        proposta.setHorarioInicio(LocalTime.of(19, 0));
        proposta.setHorarioFim(LocalTime.of(22, 0));

        Double valorTotal = regrasService.calcularAluguel(proposta);
        assertNotNull(valorTotal);
        assertTrue(valorTotal >= 450.0, "O cálculo do aluguel deve aplicar a taxa horária correspondente");

        PropostaAluguel propostaViradaMes = new PropostaAluguel();
        propostaViradaMes.setDataInicio(LocalDate.of(2026, 4, 30));
        propostaViradaMes.setDataFim(LocalDate.of(2026, 5, 2));
        propostaViradaMes.setHorarioInicio(LocalTime.of(19, 0));
        propostaViradaMes.setHorarioFim(LocalTime.of(22, 0));
        Double valorVirada = regrasService.calcularAluguel(propostaViradaMes);
        assertNotNull(valorVirada);
        assertTrue(valorVirada > 0, "O cálculo na virada do mês deve ser positivo e proporcional aos 3 dias");

        regrasService.excluirRegra(regraNoite.getId());
    }

    @Test
    @DisplayName("PropostaService e ContratoService: Deve cadastrar, buscar ativos, estender e encerrar contratos")
    public void deveGerenciarCicloDeVidaContrato() throws Exception {
        ServicoTeatro teatro = new ServicoTeatro();
        ContratoService contratoService = teatro.getContratoService();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);

        Contrato contrato = new Contrato();
        contrato.setNomePeca("Ciclo da Vida " + sufixo);
        contrato.setContratante("Produtora " + sufixo);
        contrato.setDataInicio(LocalDate.now());
        contrato.setDataFim(LocalDate.now().plusDays(10));
        contrato.setHorarioInicio(LocalTime.of(19, 0));
        contrato.setHorarioFim(LocalTime.of(21, 30));
        contrato.setValorIngresso(50.0);
        contrato.setStatusContrato(StatusContrato.ATIVO);

        contratoService.cadastrarContrato(contrato);
        assertNotNull(contrato.getId());

        List<Contrato> ativos = contratoService.listarContratosAtivos();
        assertTrue(ativos.stream().anyMatch(c -> c.getId().equals(contrato.getId())));

        LocalDate novaData = LocalDate.now().plusDays(20);
        contratoService.estenderContrato(contrato.getId(), novaData);
        Contrato estendido = contratoService.buscarContrato(contrato.getId());
        assertEquals(novaData, estendido.getDataFim());

        contratoService.encerrarContrato(contrato.getId());
        Contrato encerrado = contratoService.buscarContrato(contrato.getId());
        assertEquals(StatusContrato.INATIVO, encerrado.getStatusContrato());

        contratoService.getContratoRepository().removerContrato(encerrado);
    }

    @Test
    @DisplayName("LoginService: Deve autenticar usuário existente, barrar senha errada e redefinir senha com persistência real")
    public void deveAutenticarERedefinirSenha() throws Exception {
        ServicoTeatro teatro = new ServicoTeatro();
        LoginService loginService = teatro.getLoginService();
        ClienteRepository clienteRepo = new ClienteRepository();

        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String email = "espectador_" + sufixo + "@email.com";
        String cpf = "cpf_login_" + sufixo;

        Usuario usuario = new Usuario("Lucas " + sufixo, email, "81988889999", cpf, "senhaSecreta");
        clienteRepo.adicionarCliente(usuario);
        assertNotNull(usuario.getId());

        Pessoa autenticado = loginService.autenticar(email, "senhaSecreta");
        assertNotNull(autenticado);
        assertEquals(email, autenticado.getEmail());

        assertThrows(LoginInvalidoException.class, () ->
                loginService.autenticar(email, "senhaIncorreta")
        );

        assertThrows(LoginInvalidoException.class, () ->
                loginService.autenticar("inexistente@email.com", "123")
        );

        assertThrows(LoginInvalidoException.class, () -> loginService.autenticar("", ""));
        assertThrows(LoginInvalidoException.class, () -> loginService.autenticar(null, "senha"));

        clienteRepo.removerCliente(usuario);
    }

    @Test
    @DisplayName("AdministradorService: Deve validar campos obrigatórios e barrar CPF duplicado")
    public void deveValidarCamposEBloquearCpfDuplicadoNoAdministrador() throws Exception {
        ServicoTeatro teatro = new ServicoTeatro();
        AdministradorService admService = teatro.getAdministradorService();
        AdministradorRepository admRepo = new AdministradorRepository();

        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);
        String cpfValido = "11144477735";
        String email = "adm_val_" + sufixo + "@teatro.com";

        Administrador admSemNome = new Administrador("", email, "81999991111", cpfValido, "senha123");
        assertThrows(br.com.projetoteatro.exceptions.AdiministradorInvalidoException.class, () ->
                admService.cadastrarAdministrador(admSemNome)
        );

        Administrador admSemSenha = new Administrador("Adm Val " + sufixo, email, "81999991111", cpfValido, "");
        assertThrows(br.com.projetoteatro.exceptions.AdiministradorInvalidoException.class, () ->
                admService.cadastrarAdministrador(admSemSenha)
        );

        Administrador admValido = new Administrador("Adm Val " + sufixo, email, "81999991111", cpfValido, "senha123");
        admService.cadastrarAdministrador(admValido);
        assertNotNull(admValido.getId());

        Administrador admCpfDuplicado = new Administrador("Outro Adm", "outro_" + sufixo + "@teatro.com", "81999992222", cpfValido, "outraSenha");
        assertThrows(br.com.projetoteatro.exceptions.AdiministradorInvalidoException.class, () ->
                admService.cadastrarAdministrador(admCpfDuplicado)
        );

        admRepo.excluirAdm(cpfValido);
    }

    @Test
    @DisplayName("SessaoService: Deve intermediar operações de negócio de Sessão com persistência no MySQL")
    public void deveGerenciarSessaoPeloService() {
        ServicoTeatro teatro = new ServicoTeatro();
        SessaoService sessaoService = teatro.getSessaoService();
        String sufixo = String.valueOf(System.currentTimeMillis() % 100000);

        Sessao sessao = new Sessao("Espetáculo Musical " + sufixo, LocalTime.of(14, 0));
        sessao.setHorarioFim(LocalTime.of(16, 0));
        sessao.setData(LocalDate.now().plusDays(1));
        sessao.setTurno(Turno.TARDE);

        sessaoService.adicionarSessao(sessao);
        assertNotNull(sessao.getId(), "ID da sessão deve ser gerado pelo banco");

        Sessao buscada = sessaoService.buscarSessao(sessao.getId());
        assertNotNull(buscada);
        assertEquals("Espetáculo Musical " + sufixo, buscada.getNomePeca());

        List<Sessao> porPeca = sessaoService.buscarPorPeca("Espetáculo Musical " + sufixo);
        assertFalse(porPeca.isEmpty());

        sessaoService.removerSessao(buscada);
        assertNull(sessaoService.buscarSessao(sessao.getId()));
    }
}
