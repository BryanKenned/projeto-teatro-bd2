package br.com.projetoteatro.app;

import br.com.projetoteatro.config.JPAUtil;
import br.com.projetoteatro.dto.EstatisticaVendasDTO;
import br.com.projetoteatro.dto.IngressoResumoDTO;
import br.com.projetoteatro.enums.StatusAssento;
import br.com.projetoteatro.enums.StatusContrato;
import br.com.projetoteatro.enums.StatusProposta;
import br.com.projetoteatro.enums.TipoSetor;
import br.com.projetoteatro.enums.Turno;
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
import br.com.projetoteatro.repository.ContratoRepository;
import br.com.projetoteatro.repository.IngressoRepository;
import br.com.projetoteatro.repository.PecaRepository;
import br.com.projetoteatro.repository.PropostasRepository;
import br.com.projetoteatro.repository.SessaoRepository;
import br.com.projetoteatro.repository.SetorRepository;
import br.com.projetoteatro.repository.UsuarioRepository;
import br.com.projetoteatro.service.CodigoRecuperacaoSenhaService;
import br.com.projetoteatro.service.EnviarEmailService;
import br.com.projetoteatro.service.validators.ServicoTeatro;
import br.com.projetoteatro.view.LoginView;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class Main {

        public static void main(String[] args) {
                boolean modoTeste = (args != null && Arrays.asList(args).contains("--teste"));

                if (modoTeste) {
                        executarModoTeste();
                } else {
                        executarModoPadrao();
                }
        }

        private static void executarModoPadrao() {
                System.out.println("================================================================================");
                System.out.println("                   SISTEMA DE GESTÃO E BILHETERIA TEATRAL                       ");
                System.out.println(
                                "================================================================================\n");

                System.out.println("[INFO] Inicializando aplicação...");
                System.out.println("[INFO] Inicializando JPA (EntityManagerFactory)...");

                try {
                        JPAUtil.getEntityManagerFactory();

                        AssentoRepository assentoRepo = new AssentoRepository();
                        assentoRepo.inicializarAssentosPadraoSeNecessario();

                        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                                System.out.println("\n[INFO] Encerrando aplicação...");
                                JPAUtil.fechar();
                                System.out.println("[INFO] Recursos JPA encerrados com sucesso.");
                        }));

                        System.out.println("[INFO] Abrindo tela de login...");
                        SwingUtilities.invokeLater(() -> iniciarInterfaceGrafica());

                } catch (Exception e) {
                        System.err.println("[ERRO CRÍTICO] Falha ao inicializar recursos de persistência: "
                                        + e.getMessage());
                        e.printStackTrace();
                }
        }

        private static void executarModoTeste() {
                System.out.println("================================================================================");
                System.out.println("          SISTEMA DE GESTÃO E BILHETERIA TEATRAL - MODO TESTE (JPA/H2)          ");
                System.out.println(
                                "================================================================================\n");

                String sufixo = String.valueOf(System.currentTimeMillis() % 1000000);

                try {
                        System.out.println("[INFO] 1. Inicializando DAOs e verificando EntityManagerFactory...");
                        SetorRepository setorRepo = new SetorRepository();
                        AssentoRepository assentoRepo = new AssentoRepository();
                        ArtistaRepository artistaRepo = new ArtistaRepository();
                        PecaRepository pecaRepo = new PecaRepository();
                        SessaoRepository sessaoRepo = new SessaoRepository();
                        PropostasRepository propostaRepo = new PropostasRepository();
                        ContratoRepository contratoRepo = new ContratoRepository();
                        UsuarioRepository usuarioRepo = new UsuarioRepository();
                        IngressoRepository ingressoRepo = new IngressoRepository();
                        System.out.println("       -> DAOs instanciados com sucesso!\n");

                        System.out.println("[INFO] 2. Executando etapa de PERSISTÊNCIA REAL (Salvar)...");

                        Setor setor = new Setor(TipoSetor.PLATEIA, 100.0, 120);
                        setorRepo.adicionarSetor(setor);

                        String codAssento = "A1-" + sufixo;
                        Assento assento = new Assento(codAssento, StatusAssento.DISPONIVEL, setor);
                        setor.adicionarAssento(assento);
                        assentoRepo.adicionarAssento(assento);
                        System.out.println("       -> Setor [" + setor.getSetor() + "] e Assento [" + codAssento
                                        + "] salvos.");

                        Contratante artista = new Contratante(
                                        "Cia Teatral Suassuna " + sufixo,
                                        "suassuna_" + sufixo + "@teatro.com",
                                        "81999990001",
                                        "cpf_art_" + sufixo,
                                        "senhaArt123");
                        artistaRepo.adicionarArtista(artista);
                        System.out.println("       -> Artista/Contratante [" + artista.getNome()
                                        + "] persistido com ID: " + artista.getId());

                        Peca peca = new Peca();
                        peca.setNome("O Auto da Compadecida " + sufixo);
                        peca.setArtistaResponsavel(artista);
                        peca.setDataInicio(LocalDate.now());
                        peca.setDataFim(LocalDate.now().plusDays(10));
                        peca.setPrecoIngresso(80.0);
                        peca.setStatus(StatusProposta.CONTRATADO);
                        peca.setValorAluguel(4500.0);
                        pecaRepo.adicionarPeca(peca);
                        System.out.println("       -> Peça [" + peca.getNome() + "] salva com ID: " + peca.getId());

                        Sessao sessao = new Sessao(peca.getNome(), LocalTime.of(20, 0));
                        sessao.setData(LocalDate.now());
                        sessao.setHorarioFim(LocalTime.of(22, 0));
                        sessao.setTurno(Turno.NOITE);
                        sessao.setPeca(peca);
                        peca.adicionarSessao(sessao);
                        sessaoRepo.adicionarSessao(sessao);
                        System.out.println("       -> Sessão vinculada para as " + sessao.getHorarioInicio()
                                        + " salva com ID: " + sessao.getId());

                        PropostaAluguel proposta = new PropostaAluguel(
                                        artista,
                                        peca.getNome(),
                                        4500.0,
                                        LocalDate.now(),
                                        LocalDate.now().plusDays(10),
                                        LocalTime.of(20, 0),
                                        LocalTime.of(22, 0),
                                        80.0);
                        proposta.setStatusProposta(StatusProposta.CONTRATADO);
                        propostaRepo.adicionarProposta(proposta);

                        Contrato contrato = new Contrato(proposta);
                        contrato.setStatusContrato(StatusContrato.ATIVO);
                        contratoRepo.adicionarContrato(contrato);
                        System.out.println("       -> Contrato formalizado com ID: " + contrato.getId());

                        Usuario usuario = new Usuario(
                                        "Carlos Drummond " + sufixo,
                                        "carlos_" + sufixo + "@email.com",
                                        "81988880002",
                                        "cpf_usr_" + sufixo,
                                        "senhaUsr123");
                        usuarioRepo.adicionar(usuario);
                        System.out.println("       -> Usuário/Cliente [" + usuario.getNome() + "] persistido com ID: "
                                        + usuario.getId());

                        Ingresso ingresso = new Ingresso(
                                        usuario,
                                        sessao,
                                        assento,
                                        TipoSetor.PLATEIA,
                                        true,
                                        80.0,
                                        contrato);
                        ingressoRepo.adicionarIngresso(ingresso);
                        System.out.println("       -> Ingresso emitido com código UUID: " + ingresso.getCodigo()
                                        + " (ID: " + ingresso.getId() + ")\n");

                        System.out.println("[INFO] 3. Executando etapa de BUSCA REAL nos DAOs (Buscar)...");

                        Usuario usuarioEncontrado = usuarioRepo.buscarPorCpf("cpf_usr_" + sufixo);
                        System.out.println("       -> [Busca por CPF] Encontrado: " +
                                        (usuarioEncontrado != null
                                                        ? usuarioEncontrado.getNome() + " ("
                                                                        + usuarioEncontrado.getEmail() + ")"
                                                        : "NÃO ENCONTRADO"));

                        Peca pecaEncontrada = pecaRepo.buscarPeca(peca.getId());
                        System.out.println("       -> [Busca Peça por ID] Encontrada: " +
                                        (pecaEncontrada != null
                                                        ? pecaEncontrada.getNome() + " | Artista: "
                                                                        + pecaEncontrada.getArtistaResponsavel()
                                                                                        .getNome()
                                                        : "NÃO ENCONTRADA"));

                        Ingresso ingressoEncontrado = ingressoRepo.buscarIngresso(ingresso.getId());
                        System.out.println("       -> [Busca Ingresso por ID] Encontrado: R$ " +
                                        (ingressoEncontrado != null
                                                        ? ingressoEncontrado.getValor() + " | Setor: "
                                                                        + ingressoEncontrado.getSetor()
                                                        : "NÃO ENCONTRADO")
                                        + "\n");

                        System.out.println("[INFO] 4. Executando etapa de LISTAGEM REAL nos DAOs (Listar)...");

                        List<Peca> todasPecas = pecaRepo.listarPecas();
                        System.out.println("       -> Total de peças recuperadas no banco: " + todasPecas.size());
                        todasPecas.stream().limit(3).forEach(p -> System.out
                                        .println("          * " + p.getNome() + " (Status: " + p.getStatus() + ")"));

                        List<Ingresso> todosIngressos = ingressoRepo.listarIngresso();
                        System.out.println(
                                        "       -> Total de ingressos recuperados no banco: " + todosIngressos.size());
                        todosIngressos.stream().limit(3).forEach(i -> System.out.println(
                                        "          * Ingresso: " + i.getCodigo() + " | Valor: R$ " + i.getValor()));
                        System.out.println();

                        System.out.println(
                                        "[INFO] 5. Executando Consultas JPQL com Funções Agregadoras (COUNT, AVG, SUM)...");
                        Long totalIngressos = ingressoRepo.contarTotalIngressosVendidos();
                        Double faturamentoTotal = ingressoRepo.somarFaturamentoTotal();
                        Double precoMedio = ingressoRepo.calcularPrecoMedioIngresso();
                        Long totalPecas = pecaRepo.contarTotalPecas();

                        System.out.println(String.format("       -> [COUNT] Total de Ingressos Vendidos: %d",
                                        totalIngressos));
                        System.out.println(
                                        String.format("       -> [COUNT] Total de Peças Cadastradas: %d", totalPecas));
                        System.out.println(String.format("       -> [SUM]   Faturamento Total Arrecadado: R$ %.2f",
                                        faturamentoTotal));
                        System.out.println(String.format(
                                        "       -> [AVG]   Preço Médio do Ingresso (Ticket Médio): R$ %.2f\n",
                                        precoMedio));

                        System.out.println("[INFO] 6. Executando Consultas JPQL com JOIN Explícito entre Entidades...");

                        List<Ingresso> ingressosDoCliente = ingressoRepo
                                        .buscarIngressosPorCpfClienteComJoin("cpf_usr_" + sufixo);
                        System.out.println("       -> [JOIN: Ingresso -> Usuario] Ingressos do CPF [" + "cpf_usr_"
                                        + sufixo + "]: " + ingressosDoCliente.size());
                        for (Ingresso ing : ingressosDoCliente) {
                                System.out.println("          * " + ing.getCodigo() + " | Comprador: "
                                                + ing.getCliente().getNome() + " | Sessão: "
                                                + ing.getSessao().getNomePeca());
                        }

                        List<Peca> pecasPorArtista = pecaRepo.buscarPecasPorArtistaComJoin("Suassuna");
                        System.out.println("       -> [JOIN: Peca -> Contratante] Peças com artista 'Suassuna': "
                                        + pecasPorArtista.size());
                        for (Peca p : pecasPorArtista) {
                                System.out.println("          * " + p.getNome() + " | Artista Responsável: "
                                                + p.getArtistaResponsavel().getNome());
                        }
                        System.out.println();

                        System.out.println("[INFO] 7. Executando Consulta Otimizada via DTO Projection...");

                        List<IngressoResumoDTO> resumosDTO = ingressoRepo.listarResumoIngressosDTO();
                        System.out.println(
                                        "       -> DTOs retornados via 'SELECT new br.com.projetoteatro.dto.IngressoResumoDTO(...)':");
                        resumosDTO.stream().limit(3).forEach(dto -> {
                                System.out.println("          * " + dto);
                        });

                        EstatisticaVendasDTO estatisticaDTO = ingressoRepo.obterEstatisticaVendasDTO();
                        System.out.println(
                                        "       -> DTO consolidado via 'SELECT new br.com.projetoteatro.dto.EstatisticaVendasDTO(...)':");
                        System.out.println("          * " + estatisticaDTO);

                        System.out.println(
                                        "================================================================================");
                        System.out.println(
                                        "     TODAS AS OPERAÇÕES FORAM EXECUTADAS COM SUCESSO E SEM NENHUMA EXCEPTION!   ");
                        System.out.println(
                                        "================================================================================\n");

                } catch (Exception e) {
                        System.err.println("[ERRO CRÍTICO] Falha inesperada durante o pipeline de execução:");
                        e.printStackTrace();
                } finally {
                        JPAUtil.fechar();
                        System.out.println("[INFO] JPAUtil encerrado com sucesso.");
                        System.exit(0);
                }
        }

        private static void iniciarInterfaceGrafica() {
                try {
                        ServicoTeatro servicoTeatro = new ServicoTeatro();
                        EnviarEmailService emailService = new EnviarEmailService();
                        CodigoRecuperacaoSenhaService codigoService = new CodigoRecuperacaoSenhaService(emailService);

                        LoginView login = new LoginView(
                                        servicoTeatro.getLoginService(),
                                        servicoTeatro.getAdministradorService(),
                                        codigoService,
                                        servicoTeatro.getRegrasService(),
                                        servicoTeatro.getPropostaService());

                        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        login.setVisible(true);
                } catch (Exception e) {
                        System.err.println("[AVISO] Interface gráfica não pôde ser iniciada: " + e.getMessage());
                }
        }
}