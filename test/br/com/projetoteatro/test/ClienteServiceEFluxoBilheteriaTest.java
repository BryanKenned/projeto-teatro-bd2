package br.com.projetoteatro.test;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.enums.Genero;
import br.com.projetoteatro.enums.StatusAssento;
import br.com.projetoteatro.enums.StatusContrato;
import br.com.projetoteatro.enums.StatusProposta;
import br.com.projetoteatro.enums.TipoSetor;
import br.com.projetoteatro.enums.Turno;
import br.com.projetoteatro.exceptions.CPFInvalidoException;
import br.com.projetoteatro.exceptions.EmailInvalidoException;
import br.com.projetoteatro.model.Assento;
import br.com.projetoteatro.model.Contratante;
import br.com.projetoteatro.model.Contrato;
import br.com.projetoteatro.model.Ingresso;
import br.com.projetoteatro.model.Peca;
import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.model.Sessao;
import br.com.projetoteatro.model.Setor;
import br.com.projetoteatro.model.Usuario;
import br.com.projetoteatro.repository.ArtistaRepository;
import br.com.projetoteatro.repository.AssentoRepository;
import br.com.projetoteatro.repository.ClienteRepository;
import br.com.projetoteatro.repository.ContratoRepository;
import br.com.projetoteatro.repository.IngressoRepository;
import br.com.projetoteatro.repository.PecaRepository;
import br.com.projetoteatro.repository.PropostasRepository;
import br.com.projetoteatro.repository.SessaoRepository;
import br.com.projetoteatro.repository.SetorRepository;
import br.com.projetoteatro.service.ClienteService;
import br.com.projetoteatro.service.IngressoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteServiceEFluxoBilheteriaTest {

    private ClienteService clienteService;
    private ClienteRepository clienteRepo;
    private IngressoService ingressoService;
    private IngressoRepository ingressoRepo;
    private AssentoRepository assentoRepo;
    private SessaoRepository sessaoRepo;
    private ContratoRepository contratoRepo;
    private PecaRepository pecaRepo;
    private ArtistaRepository artistaRepo;
    private PropostasRepository propostaRepo;
    private SetorRepository setorRepo;

    @BeforeEach
    public void setUp() {
        clienteRepo = new ClienteRepository();
        clienteService = new ClienteService(clienteRepo);

        ingressoRepo = new IngressoRepository();
        ingressoService = new IngressoService(ingressoRepo);

        assentoRepo = new AssentoRepository();
        sessaoRepo = new SessaoRepository();
        contratoRepo = new ContratoRepository();
        pecaRepo = new PecaRepository();
        artistaRepo = new ArtistaRepository();
        propostaRepo = new PropostasRepository();
        setorRepo = new SetorRepository();
    }

    private static final java.util.concurrent.atomic.AtomicLong COUNTER = new java.util.concurrent.atomic.AtomicLong(
            (System.currentTimeMillis() + System.nanoTime()) % 100000000L + 10000000L);

    private String gerarCpfValido() {
        long val = COUNTER.incrementAndGet();
        String base = String.format("%09d", Math.abs(val) % 1000000000L);
        if (base.matches("(\\d)\\1{8}")) {
            base = "1" + base.substring(1, 8) + "2";
        }
        int[] d = new int[11];
        for (int i = 0; i < 9; i++) {
            d[i] = base.charAt(i) - '0';
        }
        int soma1 = 0;
        for (int i = 0; i < 9; i++) {
            soma1 += d[i] * (10 - i);
        }
        int d1 = (soma1 % 11 < 2) ? 0 : 11 - (soma1 % 11);
        d[9] = d1;
        int soma2 = 0;
        for (int i = 0; i < 10; i++) {
            soma2 += d[i] * (11 - i);
        }
        int d2 = (soma2 % 11 < 2) ? 0 : 11 - (soma2 % 11);
        return base + d1 + d2;
    }

    @Test
    @DisplayName("1. Cadastro de cliente válido com persistência real no MySQL")
    public void deveCadastrarClienteValido() throws Exception {
        String sufixo = String.valueOf(System.nanoTime() % 1000000);
        String cpf = gerarCpfValido();
        String email = "cliente_" + sufixo + "@teatro.com";

        Usuario cliente = new Usuario("Maria Clara " + sufixo, cpf, email,
                Genero.FEMININO, LocalDate.of(1995, 5, 20), "81991234567", "senha123");

        clienteService.cadastrarCliente(cliente);

        assertNotNull(cliente.getId(), "ID do cliente deve ser gerado pelo banco de dados");
        Usuario buscado = clienteService.buscarPorCpf(cpf);
        assertNotNull(buscado, "Cliente deve ser recuperado do banco");
        assertEquals("Maria Clara " + sufixo, buscado.getNome());
    }

    @Test
    @DisplayName("2. Rejeição de CPF inválido no cadastro")
    public void deveRejeitarCpfInvalido() {
        String sufixo = String.valueOf(System.nanoTime() % 1000000);
        Usuario cliente = new Usuario("Cliente Invalido", "11111111111", "invalido_" + sufixo + "@teatro.com");

        assertThrows(CPFInvalidoException.class, () -> {
            clienteService.cadastrarCliente(cliente);
        }, "Deve lançar CPFInvalidoException para CPF incorreto");
    }

    @Test
    @DisplayName("3. Rejeição de CPF duplicado no cadastro")
    public void deveRejeitarCpfDuplicado() throws Exception {
        String sufixo1 = String.valueOf(System.nanoTime() % 1000000);
        String sufixo2 = String.valueOf((System.nanoTime() + 17) % 1000000);
        String cpf = gerarCpfValido();

        Usuario cliente1 = new Usuario("Cliente Um", cpf, "c1_" + sufixo1 + "@teatro.com");
        clienteService.cadastrarCliente(cliente1);

        Usuario cliente2 = new Usuario("Cliente Dois", cpf, "c2_" + sufixo2 + "@teatro.com");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.cadastrarCliente(cliente2);
        });

        assertTrue(ex.getMessage().contains("CPF já cadastrado"), "Mensagem deve indicar CPF duplicado");
    }

    @Test
    @DisplayName("4. Rejeição de e-mail duplicado no cadastro")
    public void deveRejeitarEmailDuplicado() throws Exception {
        String sufixo = String.valueOf(System.nanoTime() % 1000000);
        String emailComum = "duplicado_" + sufixo + "@teatro.com";

        String cpf1 = gerarCpfValido();
        Usuario cliente1 = new Usuario("Cliente Um", cpf1, emailComum);
        clienteService.cadastrarCliente(cliente1);

        String cpf2 = gerarCpfValido();
        Usuario cliente2 = new Usuario("Cliente Dois", cpf2, emailComum);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            clienteService.cadastrarCliente(cliente2);
        });

        assertTrue(ex.getMessage().contains("E-mail já cadastrado"), "Mensagem deve indicar e-mail duplicado");
    }

    @Test
    @DisplayName("5. Busca de cliente por CPF com e sem formatação")
    public void deveBuscarClientePorCpf() throws Exception {
        String sufixo = String.valueOf(System.nanoTime() % 1000000);
        String cpf = gerarCpfValido();
        String email = "busca_" + sufixo + "@teatro.com";

        Usuario cliente = new Usuario("Joao Silva " + sufixo, cpf, email);
        clienteService.cadastrarCliente(cliente);

        Usuario encontrado = clienteService.buscarPorCpf(cpf);
        assertNotNull(encontrado);
        assertEquals(cliente.getId(), encontrado.getId());

        String cpfFormatado = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-"
                + cpf.substring(9);
        Usuario encontradoFormatado = clienteService.buscarPorCpf(cpfFormatado);
        assertNotNull(encontradoFormatado, "Deve encontrar cliente mesmo pesquisando com CPF formatado");
    }

    @Test
    @DisplayName("6. Listagem de clientes cadastrados no MySQL")
    public void deveListarClientes() throws Exception {
        String sufixo = String.valueOf(System.nanoTime() % 1000000);
        String cpf = gerarCpfValido();

        Usuario cliente = new Usuario("Listavel " + sufixo, cpf, "listavel_" + sufixo + "@teatro.com");
        clienteService.cadastrarCliente(cliente);

        List<Usuario> lista = clienteService.listarClientes();
        assertNotNull(lista);
        assertFalse(lista.isEmpty(), "A lista de clientes não deve ser vazia");
        assertTrue(lista.stream().anyMatch(u -> u.getId().equals(cliente.getId())),
                "Cliente recém cadastrado deve constar na listagem");
    }

    @Test
    @DisplayName("7 e 8. Venda e persistência real de ingresso no MySQL para cliente cadastrado")
    public void deveVenderEPersistirIngressoParaCliente() throws Exception {
        String sufixo = String.valueOf(System.nanoTime() % 1000000);
        String cpf = gerarCpfValido();

        Usuario cliente = new Usuario("Comprador " + sufixo, cpf, "comprador_" + sufixo + "@teatro.com");
        clienteService.cadastrarCliente(cliente);

        Setor setor = new Setor(TipoSetor.PLATEIA, 100.0, 100);
        setorRepo.adicionarSetor(setor);

        String codAssento = "B1-" + sufixo;
        Assento assento = new Assento(codAssento, StatusAssento.DISPONIVEL, setor);
        assentoRepo.adicionarAssento(assento);

        Contratante produtor = new Contratante("Produtora " + sufixo, "prod_" + sufixo + "@teatro.com", "81999991111",
                "cpf_pr_" + sufixo, "123");
        artistaRepo.adicionarArtista(produtor);

        int offset = 900 + (int) (System.currentTimeMillis() % 400);
        PropostaAluguel proposta = new PropostaAluguel(produtor, "Espetáculo " + sufixo, 3000.0,
                LocalDate.now().plusDays(offset), LocalDate.now().plusDays(offset + 1),
                LocalTime.of(19, 0), LocalTime.of(21, 0), 60.0);
        proposta.setStatusProposta(StatusProposta.CONTRATADO);
        propostaRepo.adicionarProposta(proposta);

        Contrato contrato = new Contrato(proposta);
        contrato.setStatusContrato(StatusContrato.ATIVO);
        contratoRepo.adicionarContrato(contrato);

        Sessao sessao = new Sessao(proposta.getNomePeca(), LocalTime.of(19, 0));
        sessao.setData(LocalDate.now().plusDays(offset));
        sessao.setHorarioFim(LocalTime.of(21, 0));
        sessao.setTurno(Turno.NOITE);
        sessaoRepo.adicionarSessao(sessao);

        List<Ingresso> emitidos = ingressoService.venderIngressosComAssentos(
                cliente,
                sessao,
                List.of(assento),
                TipoSetor.PLATEIA,
                contrato);

        assertNotNull(emitidos);
        assertEquals(1, emitidos.size());
        Ingresso ingressoEmitido = emitidos.get(0);
        assertNotNull(ingressoEmitido.getId(), "Ingresso deve ser persistido no banco com ID gerado");

        Ingresso buscado = ingressoRepo.buscarIngresso(ingressoEmitido.getId());
        assertNotNull(buscado, "Ingresso deve ser recuperado do MySQL pelo ID");
        assertEquals(cliente.getId(), buscado.getCliente().getId(), "Comprador deve corresponder ao cliente");
        assertEquals(sessao.getId(), buscado.getSessao().getId(), "Sessão deve corresponder");
        assertEquals(assento.getCodigo(), buscado.getAssento().getCodigo(), "Assento deve corresponder");
        assertEquals(contrato.getId(), buscado.getContrato().getId(), "Contrato deve corresponder");
    }

    @Test
    @DisplayName("9. Assento ocupado não pode ser vendido novamente na mesma sessão")
    public void deveBloquearVendaDeAssentoOcupadoNaMesmaSessao() throws Exception {
        String sufixo = String.valueOf(System.nanoTime() % 1000000);
        String cpf1 = gerarCpfValido();
        String cpf2 = gerarCpfValido();

        Usuario cliente1 = new Usuario("Cliente 1 " + sufixo, cpf1, "c1_" + sufixo + "@teatro.com");
        clienteService.cadastrarCliente(cliente1);

        Usuario cliente2 = new Usuario("Cliente 2 " + sufixo, cpf2, "c2_" + sufixo + "@teatro.com");
        clienteService.cadastrarCliente(cliente2);

        Setor setor = new Setor(TipoSetor.PLATEIA, 100.0, 100);
        setorRepo.adicionarSetor(setor);

        String codAssento = "C1-" + sufixo;
        Assento assento = new Assento(codAssento, StatusAssento.DISPONIVEL, setor);
        assentoRepo.adicionarAssento(assento);

        Contratante produtor = new Contratante("Produtora " + sufixo, "prod2_" + sufixo + "@teatro.com", "81999992222",
                "cpf_p2_" + sufixo, "123");
        artistaRepo.adicionarArtista(produtor);

        int offset = 1400 + (int) (System.currentTimeMillis() % 400);
        PropostaAluguel proposta = new PropostaAluguel(produtor, "Espetáculo 2 " + sufixo, 3000.0,
                LocalDate.now().plusDays(offset), LocalDate.now().plusDays(offset + 1),
                LocalTime.of(19, 0), LocalTime.of(21, 0), 60.0);
        proposta.setStatusProposta(StatusProposta.CONTRATADO);
        propostaRepo.adicionarProposta(proposta);

        Contrato contrato = new Contrato(proposta);
        contrato.setStatusContrato(StatusContrato.ATIVO);
        contratoRepo.adicionarContrato(contrato);

        Sessao sessao = new Sessao(proposta.getNomePeca(), LocalTime.of(19, 0));
        sessao.setData(LocalDate.now().plusDays(offset));
        sessao.setHorarioFim(LocalTime.of(21, 0));
        sessao.setTurno(Turno.NOITE);
        sessaoRepo.adicionarSessao(sessao);

        ingressoService.venderIngressosComAssentos(cliente1, sessao, List.of(assento), TipoSetor.PLATEIA, contrato);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            ingressoService.venderIngressosComAssentos(cliente2, sessao, List.of(assento), TipoSetor.PLATEIA, contrato);
        });

        assertTrue(ex.getMessage().contains("já está ocupado"),
                "Mensagem deve acusar assento já ocupado para a sessão");
    }

    @Test
    @DisplayName("10. Mesmo assento pode ser vendido em sessão diferente")
    public void devePermitirVendaDoMesmoAssentoEmSessoesDiferentes() throws Exception {
        String sufixo = String.valueOf(System.nanoTime() % 1000000);
        String cpf1 = gerarCpfValido();
        String cpf2 = gerarCpfValido();

        Usuario cliente1 = new Usuario("Cliente A " + sufixo, cpf1, "ca_" + sufixo + "@teatro.com");
        clienteService.cadastrarCliente(cliente1);

        Usuario cliente2 = new Usuario("Cliente B " + sufixo, cpf2, "cb_" + sufixo + "@teatro.com");
        clienteService.cadastrarCliente(cliente2);

        Setor setor = new Setor(TipoSetor.PLATEIA, 100.0, 100);
        setorRepo.adicionarSetor(setor);

        String codAssento = "D1-" + sufixo;
        Assento assento = new Assento(codAssento, StatusAssento.DISPONIVEL, setor);
        assentoRepo.adicionarAssento(assento);

        Contratante produtor = new Contratante("Produtora " + sufixo, "prod3_" + sufixo + "@teatro.com", "81999993333",
                "cpf_p3_" + sufixo, "123");
        artistaRepo.adicionarArtista(produtor);

        int offset = 1800 + (int) (System.currentTimeMillis() % 400);
        PropostaAluguel proposta = new PropostaAluguel(produtor, "Espetáculo 3 " + sufixo, 3000.0,
                LocalDate.now().plusDays(offset), LocalDate.now().plusDays(offset + 2),
                LocalTime.of(19, 0), LocalTime.of(21, 0), 60.0);
        proposta.setStatusProposta(StatusProposta.CONTRATADO);
        propostaRepo.adicionarProposta(proposta);

        Contrato contrato = new Contrato(proposta);
        contrato.setStatusContrato(StatusContrato.ATIVO);
        contratoRepo.adicionarContrato(contrato);

        Sessao sessao1 = new Sessao(proposta.getNomePeca(), LocalTime.of(19, 0));
        sessao1.setData(LocalDate.now().plusDays(offset));
        sessao1.setHorarioFim(LocalTime.of(21, 0));
        sessao1.setTurno(Turno.NOITE);
        sessaoRepo.adicionarSessao(sessao1);

        Sessao sessao2 = new Sessao(proposta.getNomePeca(), LocalTime.of(19, 0));
        sessao2.setData(LocalDate.now().plusDays(offset + 1));
        sessao2.setHorarioFim(LocalTime.of(21, 0));
        sessao2.setTurno(Turno.NOITE);
        sessaoRepo.adicionarSessao(sessao2);

        List<Ingresso> venda1 = ingressoService.venderIngressosComAssentos(cliente1, sessao1, List.of(assento),
                TipoSetor.PLATEIA, contrato);
        assertNotNull(venda1);
        assertEquals(1, venda1.size());

        List<Ingresso> venda2 = ingressoService.venderIngressosComAssentos(cliente2, sessao2, List.of(assento),
                TipoSetor.PLATEIA, contrato);
        assertNotNull(venda2);
        assertEquals(1, venda2.size());

        assertNotEquals(venda1.get(0).getId(), venda2.get(0).getId());
        assertEquals(assento.getCodigo(), venda1.get(0).getAssento().getCodigo());
        assertEquals(assento.getCodigo(), venda2.get(0).getAssento().getCodigo());
    }
}
