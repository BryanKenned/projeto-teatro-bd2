package br.com.projetoteatro.view;

import br.com.projetoteatro.repository.ClienteRepository;
import br.com.projetoteatro.repository.ContratoRepository;
import br.com.projetoteatro.repository.IngressoRepository;
import br.com.projetoteatro.repository.SessaoRepository;
import br.com.projetoteatro.repository.UsuarioRepository;
import br.com.projetoteatro.service.ClienteService;
import br.com.projetoteatro.service.ContratoService;
import br.com.projetoteatro.service.IngressoService;
import br.com.projetoteatro.service.PropostaService;
import br.com.projetoteatro.service.RegrasService;
import br.com.projetoteatro.service.SessaoService;
import br.com.projetoteatro.service.UsuarioService;

import javax.swing.*;
import java.awt.*;

public class DashBoardView extends JFrame {

        private GerenciarEspetaculosView gerenciarEspetaculosView;
        private BilheteriaView bilheteriaView;
        private ListagemPropostasView listagemPropostasView;
        private AgendaTeatroView agendaTeatroView;
        private RelatorioView relatorioView;
        private MapaAssentosView mapaAssentosView;
        private ClienteView clienteView;

        private RegrasService regrasService;
        private PropostaService propostaService;

        private ContratoService contratoService;
        private IngressoService ingressoService;
        private UsuarioService usuarioService;
        private ClienteService clienteService;
        private SessaoService sessaoService;
        private br.com.projetoteatro.repository.AssentoRepository assentoRepository;

        public DashBoardView(
                        RegrasService regrasService,
                        PropostaService propostaService) {
                this(regrasService, propostaService, new ClienteService(new ClienteRepository()));
        }

        public DashBoardView(
                        RegrasService regrasService,
                        PropostaService propostaService,
                        ClienteService clienteService) {

                this.regrasService = regrasService;
                this.propostaService = propostaService;
                this.clienteService = clienteService != null ? clienteService
                                : new ClienteService(new ClienteRepository());

                ContratoRepository contratoRepository = new ContratoRepository();
                IngressoRepository ingressoRepository = new IngressoRepository();
                UsuarioRepository usuarioRepository = new UsuarioRepository();
                SessaoRepository sessaoRepository = new SessaoRepository();
                assentoRepository = new br.com.projetoteatro.repository.AssentoRepository();

                contratoService = new ContratoService(contratoRepository);
                ingressoService = new IngressoService(ingressoRepository);
                usuarioService = new UsuarioService(usuarioRepository);
                sessaoService = new SessaoService(sessaoRepository);

                setTitle("Gerenciamento Teatro");
                setExtendedState(JFrame.MAXIMIZED_BOTH);
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
                setLayout(new BorderLayout());

                JPanel barraSuperior = new JPanel();

                barraSuperior.setPreferredSize(
                                new Dimension(0, 60));

                barraSuperior.setBackground(
                                new Color(215, 230, 245));

                barraSuperior.setLayout(
                                new BorderLayout());

                barraSuperior.setBorder(
                                BorderFactory.createEmptyBorder(
                                                0, 20, 0, 20));

                JLabel lblBoasVindas = new JLabel(
                                "Bem-vindo, Operador de Bilheteria");

                lblBoasVindas.setFont(
                                new Font(
                                                "Arial",
                                                Font.BOLD,
                                                16));

                lblBoasVindas.setForeground(
                                new Color(50, 50, 50));

                barraSuperior.add(
                                lblBoasVindas,
                                BorderLayout.WEST);

                JButton btnSair = new JButton("SAIR");

                btnSair.setBackground(
                                new Color(240, 100, 100));

                btnSair.setForeground(
                                Color.WHITE);

                btnSair.setFocusPainted(false);

                btnSair.setCursor(
                                new Cursor(
                                                Cursor.HAND_CURSOR));

                btnSair.addActionListener(e -> {
                        dispose();
                });

                barraSuperior.add(
                                btnSair,
                                BorderLayout.EAST);

                add(
                                barraSuperior,
                                BorderLayout.NORTH);

                JPanel barraLateral = new JPanel();

                barraLateral.setPreferredSize(
                                new Dimension(
                                                250,
                                                getHeight()));

                barraLateral.setBackground(
                                new Color(95, 170, 245));

                barraLateral.setLayout(
                                new BoxLayout(
                                                barraLateral,
                                                BoxLayout.Y_AXIS));

                barraLateral.setBorder(
                                BorderFactory.createEmptyBorder(
                                                20,
                                                15,
                                                20,
                                                15));

                Dimension tamanhoBotao = new Dimension(
                                220,
                                70);

                JButton btnDashBoard = new JButton(
                                "DashBoard de Vendas");

                btnDashBoard.setMaximumSize(
                                tamanhoBotao);

                btnDashBoard.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JButton btnGerenEspetaculos = new JButton(
                                "Gerenciar Espetáculos");

                btnGerenEspetaculos.setMaximumSize(
                                tamanhoBotao);

                btnGerenEspetaculos.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JButton btnClientes = new JButton(
                                "Gerenciar Clientes");

                btnClientes.setMaximumSize(
                                tamanhoBotao);

                btnClientes.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JButton btnAgenda = new JButton(
                                "Agenda e Sessões");

                btnAgenda.setMaximumSize(
                                tamanhoBotao);

                btnAgenda.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JButton btnBilheteria = new JButton(
                                "Bilheteria e Venda");

                btnBilheteria.setBorder(
                                BorderFactory.createLineBorder(
                                                Color.BLACK,
                                                1));

                btnBilheteria.setBackground(
                                new Color(224, 229, 239));

                btnBilheteria.setMaximumSize(
                                tamanhoBotao);

                btnBilheteria.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JButton btnMapaAssentos = new JButton(
                                "Mapa de Assentos");

                btnMapaAssentos.setMaximumSize(
                                tamanhoBotao);

                btnMapaAssentos.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                JButton btnRelatorio = new JButton(
                                "Relatórios");

                btnRelatorio.setMaximumSize(
                                tamanhoBotao);

                btnRelatorio.setAlignmentX(
                                Component.CENTER_ALIGNMENT);

                barraLateral.add(btnDashBoard);

                barraLateral.add(
                                Box.createRigidArea(
                                                new Dimension(0, 15)));

                barraLateral.add(
                                btnGerenEspetaculos);

                barraLateral.add(
                                Box.createRigidArea(
                                                new Dimension(0, 15)));

                barraLateral.add(btnAgenda);

                barraLateral.add(
                                Box.createRigidArea(
                                                new Dimension(0, 15)));

                barraLateral.add(btnClientes);

                barraLateral.add(
                                Box.createRigidArea(
                                                new Dimension(0, 15)));

                barraLateral.add(
                                btnBilheteria);

                barraLateral.add(
                                Box.createRigidArea(
                                                new Dimension(0, 15)));

                barraLateral.add(
                                btnMapaAssentos);

                barraLateral.add(
                                Box.createRigidArea(
                                                new Dimension(0, 15)));

                barraLateral.add(btnRelatorio);

                add(
                                barraLateral,
                                BorderLayout.WEST);

                CardLayout cardLayout = new CardLayout();

                JPanel contentPanel = new JPanel(cardLayout);

                this.gerenciarEspetaculosView = new GerenciarEspetaculosView(
                                regrasService,
                                propostaService);

                this.bilheteriaView = new BilheteriaView(
                                contratoService,
                                ingressoService,
                                usuarioService,
                                clienteService,
                                sessaoService,
                                assentoRepository);

                this.clienteView = new ClienteView(clienteService, () -> {
                        cardLayout.show(contentPanel, "home");
                        contentPanel.revalidate();
                        contentPanel.repaint();
                });

                this.listagemPropostasView = new ListagemPropostasView(
                                propostaService,
                                contratoService,
                                ingressoService);

                this.agendaTeatroView = new AgendaTeatroView(
                                sessaoService,
                                contratoService);

                this.relatorioView = new RelatorioView(
                                contratoService,
                                ingressoService,
                                sessaoService);

                this.mapaAssentosView = new MapaAssentosView(
                                sessaoService,
                                assentoRepository,
                                ingressoService);

                contentPanel.add(
                                listagemPropostasView,
                                "home");

                contentPanel.add(
                                agendaTeatroView,
                                "agenda");

                contentPanel.add(
                                clienteView,
                                "clientes");

                contentPanel.add(
                                relatorioView,
                                "relatorio");

                contentPanel.add(
                                bilheteriaView,
                                "bilheteria");

                contentPanel.add(
                                gerenciarEspetaculosView,
                                "espetaculos");

                contentPanel.add(
                                mapaAssentosView,
                                "mapaAssentos");

                add(
                                contentPanel,
                                BorderLayout.CENTER);

                btnGerenEspetaculos.addActionListener(e -> {

                        cardLayout.show(
                                        contentPanel,
                                        "espetaculos");

                        contentPanel.revalidate();
                        contentPanel.repaint();
                });

                btnRelatorio.addActionListener(e -> {

                        relatorioView.atualizarDados();

                        cardLayout.show(
                                        contentPanel,
                                        "relatorio");

                        contentPanel.revalidate();
                        contentPanel.repaint();
                });

                btnDashBoard.addActionListener(e -> {

                        listagemPropostasView
                                        .atualizarDadosDashboard();

                        cardLayout.show(
                                        contentPanel,
                                        "home");

                        contentPanel.revalidate();
                        contentPanel.repaint();
                });

                btnClientes.addActionListener(e -> {

                        clienteView.atualizarDados();

                        cardLayout.show(
                                        contentPanel,
                                        "clientes");

                        contentPanel.revalidate();
                        contentPanel.repaint();
                });

                btnBilheteria.addActionListener(e -> {

                        bilheteriaView.atualizarDados();

                        cardLayout.show(
                                        contentPanel,
                                        "bilheteria");

                        contentPanel.revalidate();
                        contentPanel.repaint();
                });

                btnAgenda.addActionListener(e -> {

                        agendaTeatroView.atualizarDados();

                        cardLayout.show(
                                        contentPanel,
                                        "agenda");

                        contentPanel.revalidate();
                        contentPanel.repaint();
                });

                btnMapaAssentos.addActionListener(e -> {

                        mapaAssentosView.atualizarDados();

                        cardLayout.show(
                                        contentPanel,
                                        "mapaAssentos");

                        contentPanel.revalidate();
                        contentPanel.repaint();
                });
        }

        public ClienteView getClienteView() {
                return clienteView;
        }
}
