package br.com.projetoteatro.test;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.enums.StatusAssento;
import br.com.projetoteatro.enums.StatusContrato;
import br.com.projetoteatro.enums.StatusProposta;
import br.com.projetoteatro.enums.TipoSetor;
import br.com.projetoteatro.enums.Turno;
import br.com.projetoteatro.model.*;
import br.com.projetoteatro.repository.*;
import br.com.projetoteatro.service.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Bateria de Testes Automatizados de Integração Funcional de Ponta a Ponta.
 * <p>
 * Valida fluxos com persistência real no MySQL:
 * - Proposta -> Efetivação -> Contrato real persistido e ativo;
 * - Peça -> Sessão -> Persistência -> Consulta na Agenda;
 * - Assento disponível -> Venda com Assento real -> Ocupado na sessão;
 * - Assento ocupado -> Tentativa de venda duplicada -> Rejeição com exceção;
 * - Proposta existente -> Edição com recálculo -> UPDATE no banco -> Consulta
 * de alteração;
 * - Relatórios com dados reais gerando arquivos PDF em disco.
 * </p>
 */
public class IntegracaoFluxosCompletosTest {

    private ArtistaRepository artistaRepo;
    private PropostasRepository propostaRepo;
    private ContratoRepository contratoRepo;
    private RegrasPrecoRepository regrasRepo;
    private SessaoRepository sessaoRepo;
    private PecaRepository pecaRepo;
    private SetorRepository setorRepo;
    private AssentoRepository assentoRepo;
    private UsuarioRepository usuarioRepo;
    private IngressoRepository ingressoRepo;

    private RegrasService regrasService;
    private PropostaService propostaService;
    private ContratoService contratoService;
    private SessaoService sessaoService;
    private PecaService pecaService;
    private IngressoService ingressoService;

    private String sufixo;

    @BeforeEach
    public void setup() {
        sufixo = String.valueOf(System.currentTimeMillis() % 1000000);

        artistaRepo = new ArtistaRepository();
        propostaRepo = new PropostasRepository();
        contratoRepo = new ContratoRepository();
        regrasRepo = new RegrasPrecoRepository();
        sessaoRepo = new SessaoRepository();
        pecaRepo = new PecaRepository();
        setorRepo = new SetorRepository();
        assentoRepo = new AssentoRepository();
        usuarioRepo = new UsuarioRepository();
        ingressoRepo = new IngressoRepository();

        regrasService = new RegrasService(regrasRepo);
        propostaService = new PropostaService(regrasService, propostaRepo, artistaRepo, contratoRepo);
        contratoService = new ContratoService(contratoRepo);
        sessaoService = new SessaoService(sessaoRepo);
        pecaService = new PecaService(pecaRepo);
        ingressoService = new IngressoService(ingressoRepo);
    }

    @AfterEach
    public void tearDown() {
        // Recursos gerenciados via transações pontuais de cada DAO
    }

    @Test
    @DisplayName("REQ 1: Proposta -> Efetivação deve criar Contrato real persistido no MySQL e ativo na Bilheteria")
    public void deveEfetivarPropostaECriarContratoAtivoReal() throws Exception {
        // 1. Cadastrar Artista / Contratante
        Contratante artista = new Contratante("Cia " + sufixo, "cia_" + sufixo + "@teatro.com", "81999990000",
                "cpf_ef_" + sufixo, "123");
        artistaRepo.adicionarArtista(artista);

        // 2. Cadastrar Proposta com período exclusivo no futuro para evitar colisão
        int offsetDias = 200 + (int) (System.currentTimeMillis() % 300);
        PropostaAluguel proposta = new PropostaAluguel(
                artista,
                "Auto de Ariano " + sufixo,
                3000.0,
                LocalDate.now().plusDays(offsetDias),
                LocalDate.now().plusDays(offsetDias + 2),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0),
                60.0);
        propostaService.cadastrarProposta(proposta);
        assertNotNull(proposta.getId(), "Proposta deve ter ID gerado pelo MySQL");
        assertEquals(StatusProposta.EM_CONTRATACAO, proposta.getStatusProposta());

        // 3. Efetivar Contrato
        Contrato contratoCriado = propostaService.efetivarContrato(proposta);
        assertNotNull(contratoCriado, "Efetivação deve instanciar e retornar Contrato");
        assertNotNull(contratoCriado.getId(), "Contrato deve ser persistido com ID no MySQL");
        assertEquals(StatusContrato.ATIVO, contratoCriado.getStatusContrato(), "Contrato deve ficar com status ATIVO");

        // 4. Verificar se a proposta foi atualizada para CONTRATADO e ATIVO
        PropostaAluguel propAtualizada = propostaRepo.buscarProposta(proposta.getId());
        assertEquals(StatusProposta.CONTRATADO, propAtualizada.getStatusProposta());
        assertEquals(StatusContrato.ATIVO, propAtualizada.getStatusContrato());

        // 5. Garantir que ContratoService.listarContratosAtivos() encontra o contrato
        // recém-criado (disponível na Bilheteria)
        List<Contrato> contratosAtivos = contratoService.listarContratosAtivos();
        boolean encontrado = contratosAtivos.stream().anyMatch(c -> c.getId().equals(contratoCriado.getId()));
        assertTrue(encontrado, "Contrato recém-efetivado deve estar presente em listarContratosAtivos()");

        // 6. Evitar duplicidade: efetivar novamente deve retornar o contrato existente
        // sem duplicar
        Contrato segundaEfetivacao = propostaService.efetivarContrato(proposta);
        assertEquals(contratoCriado.getId(), segundaEfetivacao.getId(),
                "Não deve duplicar contrato para a mesma proposta");

        // Limpeza
        contratoRepo.removerContrato(contratoCriado);
        propostaRepo.removerProposta(propAtualizada);
        artistaRepo.excluirArtista(artista);
    }

    @Test
    @DisplayName("REQ 2 & 6: Peça -> Sessão -> Persistência -> Consulta dinâmica na Agenda")
    public void deveGerenciarSessaoDinamicamenteEExibirNaAgenda() throws Exception {
        Contratante artista = new Contratante("Diretor " + sufixo, "dir_" + sufixo + "@teatro.com", "81999990001",
                "cpf_se_" + sufixo, "123");
        artistaRepo.adicionarArtista(artista);

        Peca peca = new Peca();
        peca.setNome("A Flauta Mágica " + sufixo);
        peca.setArtistaResponsavel(artista);
        peca.setDataInicio(LocalDate.now());
        peca.setDataFim(LocalDate.now().plusDays(10));
        peca.setPrecoIngresso(70.0);
        peca.setStatus(StatusProposta.CONTRATADO);
        pecaRepo.adicionarPeca(peca);
        assertNotNull(peca.getId());

        Sessao sessao = new Sessao(peca.getNome(), LocalTime.of(19, 30));
        sessao.setData(LocalDate.now().plusDays(3));
        sessao.setHorarioFim(LocalTime.of(21, 30));
        sessao.setTurno(Turno.NOITE);
        sessao.setPeca(peca);

        sessaoService.cadastrarSessaoCompleta(sessao);
        assertNotNull(sessao.getId(), "Sessão deve ter ID gerado no MySQL");

        // Consulta dinâmica por peça
        List<Sessao> sessoesPorPeca = sessaoService.buscarPorPeca("Flauta Mágica " + sufixo);
        assertFalse(sessoesPorPeca.isEmpty(), "Sessão deve ser encontrada por busca dinâmica de peça");
        assertEquals(sessao.getId(), sessoesPorPeca.get(0).getId());

        // Consulta geral utilizada pela Agenda
        List<Sessao> todasSessoes = sessaoService.listarSessao();
        boolean naListaGeral = todasSessoes.stream().anyMatch(s -> s.getId().equals(sessao.getId()));
        assertTrue(naListaGeral, "Sessão cadastrada deve estar listável para a Agenda");

        // Limpeza
        sessaoRepo.removerSessao(sessao);
        pecaRepo.removerPeca(peca);
        artistaRepo.excluirArtista(artista);
    }

    @Test
    @DisplayName("REQ 3, 4 & 5: Assento disponível -> Venda com Assento real -> Assento ocupado na sessão")
    public void deveVenderIngressoComAssentoRealEAtualizarOcupacao() throws Exception {
        Setor setor = new Setor(TipoSetor.PLATEIA, 80.0, 100);
        setorRepo.adicionarSetor(setor);

        String codAssento = "A1_" + sufixo;
        Assento assento = new Assento(codAssento, StatusAssento.DISPONIVEL, setor);
        assentoRepo.adicionarAssento(assento);

        Usuario cliente = new Usuario("Espectador " + sufixo, "esp_" + sufixo + "@email.com", "81999990002",
                "cpf_cli_" + sufixo, "123");
        usuarioRepo.adicionar(cliente);

        Contrato contrato = new Contrato();
        contrato.setNomePeca("O Grande Show " + sufixo);
        contrato.setValorIngresso(80.0);
        contrato.setStatusContrato(StatusContrato.ATIVO);
        contratoRepo.adicionarContrato(contrato);

        Sessao sessao = new Sessao(contrato.getNomePeca(), LocalTime.of(20, 0));
        sessao.setData(LocalDate.now().plusDays(4));
        sessao.setHorarioFim(LocalTime.of(22, 0));
        sessao.setTurno(Turno.NOITE);
        sessaoRepo.adicionarSessao(sessao);

        // Antes da venda: assento NÃO está ocupado nesta sessão
        assertFalse(ingressoRepo.assentoOcupadoNaSessao(sessao.getId(), codAssento));

        // Realizar venda na Bilheteria
        Ingresso ingressoVendido = ingressoService.venderIngresso(
                cliente,
                sessao,
                assento,
                TipoSetor.PLATEIA,
                contrato);

        assertNotNull(ingressoVendido.getId(), "Ingresso deve ser persistido no MySQL");
        assertNotNull(ingressoVendido.getAssento(), "Ingresso NUNCA deve ter assento nulo na venda");
        assertEquals(codAssento, ingressoVendido.getAssento().getCodigo());

        // Após a venda: assento agora DEVE estar ocupado nesta sessão
        assertTrue(ingressoRepo.assentoOcupadoNaSessao(sessao.getId(), codAssento),
                "Assento deve constar como ocupado na sessão após a venda");

        List<String> ocupadosNaSessao = ingressoService.buscarAssentosOcupadosNaSessao(sessao.getId());
        assertTrue(ocupadosNaSessao.contains(codAssento),
                "Lista de assentos ocupados da sessão deve conter o assento vendido");

        // Limpeza
        ingressoRepo.removerIngresso(ingressoVendido);
        sessaoRepo.removerSessao(sessao);
        contratoRepo.removerContrato(contrato);
        usuarioRepo.remover(cliente);
        assentoRepo.remover(assento);
        setorRepo.removerSetor(setor);
    }

    @Test
    @DisplayName("REQ 3 & 5: Venda duplicada de assento na mesma sessão deve ser rejeitada com exceção")
    public void deveRejeitarVendaDuplicadaDeAssentoNaMesmaSessao() throws Exception {
        Setor setor = new Setor(TipoSetor.PLATEIA, 80.0, 100);
        setorRepo.adicionarSetor(setor);

        String codAssento = "B2_" + sufixo;
        Assento assento = new Assento(codAssento, StatusAssento.DISPONIVEL, setor);
        assentoRepo.adicionarAssento(assento);

        Usuario cliente1 = new Usuario("Cliente Um " + sufixo, "c1_" + sufixo + "@email.com", "81999990003",
                "cpf_c1_" + sufixo, "123");
        usuarioRepo.adicionar(cliente1);

        Usuario cliente2 = new Usuario("Cliente Dois " + sufixo, "c2_" + sufixo + "@email.com", "81999990004",
                "cpf_c2_" + sufixo, "123");
        usuarioRepo.adicionar(cliente2);

        Contrato contrato = new Contrato();
        contrato.setNomePeca("Ópera dos Mendigos " + sufixo);
        contrato.setValorIngresso(75.0);
        contrato.setStatusContrato(StatusContrato.ATIVO);
        contratoRepo.adicionarContrato(contrato);

        Sessao sessao = new Sessao(contrato.getNomePeca(), LocalTime.of(19, 0));
        sessao.setData(LocalDate.now().plusDays(5));
        sessao.setHorarioFim(LocalTime.of(21, 0));
        sessao.setTurno(Turno.NOITE);
        sessaoRepo.adicionarSessao(sessao);

        // Venda 1: com sucesso
        Ingresso ingresso1 = ingressoService.venderIngresso(cliente1, sessao, assento, TipoSetor.PLATEIA, contrato);
        assertNotNull(ingresso1.getId());

        // Venda 2: tentativa de vender o mesmo assento para a mesma sessão deve
        // disparar exceção
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            ingressoService.venderIngresso(cliente2, sessao, assento, TipoSetor.PLATEIA, contrato);
        }, "Sistema deve impedir venda duplicada do mesmo assento na mesma sessão");

        assertTrue(ex.getMessage().contains("já está ocupado"));

        // Limpeza
        ingressoRepo.removerIngresso(ingresso1);
        sessaoRepo.removerSessao(sessao);
        contratoRepo.removerContrato(contrato);
        usuarioRepo.remover(cliente2);
        usuarioRepo.remover(cliente1);
        assentoRepo.remover(assento);
        setorRepo.removerSetor(setor);
    }

    @Test
    @DisplayName("REQ 8: Proposta existente -> Edição com validação e recálculo -> UPDATE real no MySQL")
    public void devePermitirEdicaoDePropostaComUpdateReal() throws Exception {
        Contratante artista = new Contratante("Cia Edicao " + sufixo, "ed_" + sufixo + "@teatro.com", "81999990005",
                "cpf_ed_" + sufixo, "123");
        artistaRepo.adicionarArtista(artista);

        int offsetDias = 600 + (int) (System.currentTimeMillis() % 300);
        PropostaAluguel proposta = new PropostaAluguel(
                artista,
                "Nome Inicial " + sufixo,
                2000.0,
                LocalDate.now().plusDays(offsetDias),
                LocalDate.now().plusDays(offsetDias + 2),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                50.0);
        propostaService.cadastrarProposta(proposta);
        Long idProposta = proposta.getId();
        assertNotNull(idProposta);

        // Alterar campos
        proposta.setNomePeca("Nome Alterado " + sufixo);
        proposta.setValorIngresso(65.0);
        proposta.setHorarioInicio(LocalTime.of(15, 0));
        proposta.setHorarioFim(LocalTime.of(17, 30));

        // Executar atualização real
        propostaService.atualizarProposta(proposta);

        // Consultar diretamente no banco
        PropostaAluguel propostaRecuperada = propostaRepo.buscarProposta(idProposta);
        assertNotNull(propostaRecuperada);
        assertEquals("Nome Alterado " + sufixo, propostaRecuperada.getNomePeca(),
                "UPDATE deve persistir novo nome no MySQL");
        assertEquals(65.0, propostaRecuperada.getValorIngresso(),
                "UPDATE deve persistir novo valor de ingresso no MySQL");
        assertEquals(LocalTime.of(15, 0), propostaRecuperada.getHorarioInicio());
        assertEquals(LocalTime.of(17, 30), propostaRecuperada.getHorarioFim());
        assertTrue(propostaRecuperada.getValorAluguel() > 0, "Aluguel deve ser recalculado no banco");

        // Limpeza
        propostaRepo.removerProposta(propostaRecuperada);
        artistaRepo.excluirArtista(artista);
    }

    @Test
    @DisplayName("REQ 7: Relatórios com dados reais devem gerar arquivos PDF físicos em disco")
    public void deveGerarRelatoriosComDadosReaisEPDF() {
        Contrato contrato = new Contrato();
        contrato.setNomePeca("O Corsário " + sufixo);
        contrato.setContratante("Cia Ballet");
        contrato.setDataInicio(LocalDate.now());
        contrato.setDataFim(LocalDate.now().plusDays(3));
        contrato.setHorarioInicio(LocalTime.of(20, 0));
        contrato.setHorarioFim(LocalTime.of(22, 0));
        contrato.setValorAluguel(2500.0);
        contrato.setValorIngresso(90.0);
        contrato.setStatusContrato(StatusContrato.ATIVO);
        contratoRepo.adicionarContrato(contrato);

        // 1. Testar geração de PDF de Relatório de Espectadores
        String pdfEspectadores = PdfService.gerarRelatorioEspectadores(
                contrato.getNomePeca(),
                "Sessão Noturna",
                List.of());
        assertNotNull(pdfEspectadores);
        File fileEspectadores = new File(pdfEspectadores);
        assertTrue(fileEspectadores.exists(), "Arquivo PDF de espectadores deve existir fisicamente no disco");
        assertTrue(fileEspectadores.length() > 0, "Arquivo PDF não deve estar vazio");

        // 2. Testar geração de PDF de Consolidação Financeira
        String pdfConsolidacao = PdfService.gerarConsolidacaoFinanceira(
                contrato,
                50,
                4500.0);
        assertNotNull(pdfConsolidacao);
        File fileConsolidacao = new File(pdfConsolidacao);
        assertTrue(fileConsolidacao.exists(), "Arquivo PDF de consolidação deve existir fisicamente no disco");
        assertTrue(fileConsolidacao.length() > 0);

        // 3. Testar geração de PDF de Relatório Geral do Teatro
        String pdfGeral = PdfService.gerarRelatorioGeralTeatro(
                LocalDate.now().minusDays(10),
                LocalDate.now(),
                120,
                9600.0,
                80.0,
                List.of(contrato));
        assertNotNull(pdfGeral);
        File fileGeral = new File(pdfGeral);
        assertTrue(fileGeral.exists(), "Arquivo PDF de relatório geral deve existir fisicamente no disco");
        assertTrue(fileGeral.length() > 0);

        // Limpeza de arquivos gerados no teste
        fileEspectadores.delete();
        fileConsolidacao.delete();
        fileGeral.delete();
        contratoRepo.removerContrato(contrato);
    }
}
