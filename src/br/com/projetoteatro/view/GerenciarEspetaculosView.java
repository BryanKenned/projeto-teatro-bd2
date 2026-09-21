package br.com.projetoteatro.view;

import br.com.projetoteatro.exceptions.ConflitoHorarioException;
import br.com.projetoteatro.model.Contratante;
import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.service.PropostaService;
import br.com.projetoteatro.service.RegrasService;
import br.com.projetoteatro.service.validators.ValidadorHorarios;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class GerenciarEspetaculosView extends JPanel {

        private CardLayout internoCardLayout;
        private JPanel painelConteudoInterno;

        private JTable tabelaEspetaculos;
        private DefaultTableModel modeloTabela;

        private RegrasService regrasService;
        private PropostaService propostaService;

        public GerenciarEspetaculosView(
                        RegrasService regrasService,
                        PropostaService propostaService) {

                this.regrasService = regrasService;
                this.propostaService = propostaService;

                this.setLayout(new BorderLayout());
                this.setBackground(Color.WHITE);

                this.internoCardLayout = new CardLayout();
                this.painelConteudoInterno = new JPanel(this.internoCardLayout);

                this.painelConteudoInterno.setBackground(Color.WHITE);

                JPanel painelSubmenu = criarPainelSubmenu();
                JPanel painelFormulario = criarPainelFormulario();
                JPanel painelListagem = criarPainelListagem();

                this.painelConteudoInterno.add(
                                painelSubmenu,
                                "submenu");

                this.painelConteudoInterno.add(
                                painelFormulario,
                                "formulario");

                this.painelConteudoInterno.add(
                                painelListagem,
                                "listagem");

                this.add(
                                this.painelConteudoInterno,
                                BorderLayout.CENTER);
        }

        private JPanel criarPainelSubmenu() {

                JPanel painel = new JPanel(new GridBagLayout());
                painel.setBackground(Color.WHITE);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(15, 15, 15, 15);

                JButton btnCadastrar = new JButton("Cadastrar Espetáculo (Aluguel)");

                JButton btnListar = new JButton("Visualizar Propostas");

                Dimension tamBotaoSub = new Dimension(260, 65);

                btnCadastrar.setPreferredSize(tamBotaoSub);
                btnListar.setPreferredSize(tamBotaoSub);

                btnCadastrar.setCursor(
                                new Cursor(Cursor.HAND_CURSOR));

                btnListar.setCursor(
                                new Cursor(Cursor.HAND_CURSOR));

                btnCadastrar.addActionListener(e -> {

                        internoCardLayout.show(
                                        painelConteudoInterno,
                                        "formulario");

                        painelConteudoInterno.revalidate();
                        painelConteudoInterno.repaint();
                });

                btnListar.addActionListener(e -> {

                        atualizarTabela("", "Todos");

                        internoCardLayout.show(
                                        painelConteudoInterno,
                                        "listagem");

                        painelConteudoInterno.revalidate();
                        painelConteudoInterno.repaint();
                });

                gbc.gridx = 0;
                gbc.gridy = 0;

                painel.add(btnCadastrar, gbc);

                gbc.gridx = 1;
                gbc.gridy = 0;

                painel.add(btnListar, gbc);

                return painel;
        }

        private JPanel criarPainelListagem() {

                JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));

                painelPrincipal.setBackground(Color.WHITE);

                painelPrincipal.setBorder(
                                BorderFactory.createEmptyBorder(
                                                15, 15, 15, 15));

                JPanel painelFiltros = new JPanel(
                                new FlowLayout(
                                                FlowLayout.LEFT,
                                                10,
                                                5));

                painelFiltros.setBackground(Color.WHITE);

                JLabel lblBuscar = new JLabel(
                                "Filtros: Digite o nome da peça...");

                JTextField txtBuscaNome = new JTextField(18);

                JLabel lblStatus = new JLabel("Status:");

                String[] opcoesStatus = {
                                "Todos",
                                "EM_CONTRATACAO",
                                "CONTRATADO",
                                "ENCERRADO"
                };

                JComboBox<String> cbxStatus = new JComboBox<>(opcoesStatus);

                JButton btnFiltrar = new JButton("FILTRAR");

                btnFiltrar.addActionListener(e -> {

                        atualizarTabela(
                                        txtBuscaNome.getText(),
                                        cbxStatus
                                                        .getSelectedItem()
                                                        .toString());
                });

                painelFiltros.add(lblBuscar);
                painelFiltros.add(txtBuscaNome);
                painelFiltros.add(lblStatus);
                painelFiltros.add(cbxStatus);
                painelFiltros.add(btnFiltrar);

                painelPrincipal.add(
                                painelFiltros,
                                BorderLayout.NORTH);

                String[] colunas = {
                                "ID",
                                "Nome da Peça",
                                "Artista Locatário",
                                "Status"
                };

                modeloTabela = new DefaultTableModel(
                                colunas,
                                0);

                tabelaEspetaculos = new JTable(modeloTabela);

                JScrollPane scrollTabela = new JScrollPane(tabelaEspetaculos);

                painelPrincipal.add(
                                scrollTabela,
                                BorderLayout.CENTER);

                JPanel painelBotoesSul = new JPanel(new BorderLayout());

                painelBotoesSul.setBackground(Color.WHITE);

                JButton btnVoltarMenu = new JButton("Voltar ao Menu");

                JButton btnEditar = new JButton("DETALHAR / EDITAR");

                btnVoltarMenu.addActionListener(e -> {

                        internoCardLayout.show(
                                        painelConteudoInterno,
                                        "submenu");
                });

                btnEditar.addActionListener(e -> {

                        int linha = tabelaEspetaculos.getSelectedRow();

                        if (linha == -1) {

                                JOptionPane.showMessageDialog(
                                                this,
                                                "Selecione uma linha na tabela primeiro!");

                                return;
                        }

                        long idSelecionado = ((Number) modeloTabela
                                        .getValueAt(linha, 0))
                                        .longValue();

                        try {

                                PropostaAluguel proposta = propostaService.buscarProposta(
                                                idSelecionado);

                                if (proposta == null) {
                                        JOptionPane.showMessageDialog(
                                                        this,
                                                        "Proposta não encontrada!");
                                        return;
                                }

                                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
                                DetalharPropostaDialogView dialog = new DetalharPropostaDialogView(parentFrame,
                                                proposta, propostaService);
                                dialog.setVisible(true);

                                atualizarTabela("", "Todos");

                        } catch (Exception ex) {

                                JOptionPane.showMessageDialog(
                                                this,
                                                "Erro ao buscar proposta: "
                                                                + ex.getMessage(),
                                                "Erro",
                                                JOptionPane.ERROR_MESSAGE);
                        }
                });

                painelBotoesSul.add(
                                btnVoltarMenu,
                                BorderLayout.WEST);

                painelBotoesSul.add(
                                btnEditar,
                                BorderLayout.EAST);

                painelPrincipal.add(
                                painelBotoesSul,
                                BorderLayout.SOUTH);

                return painelPrincipal;
        }

        private JPanel criarPainelFormulario() {

                JPanel painelPrincipal = new JPanel(new BorderLayout());

                painelPrincipal.setBackground(Color.WHITE);

                JLabel lblTitulo = new JLabel(
                                "Nova Proposta de Aluguel / Espetáculo",
                                SwingConstants.CENTER);

                lblTitulo.setFont(
                                new Font(
                                                "Arial",
                                                Font.BOLD,
                                                20));

                lblTitulo.setBorder(
                                BorderFactory.createEmptyBorder(
                                                20, 0, 20, 0));

                painelPrincipal.add(
                                lblTitulo,
                                BorderLayout.NORTH);

                JPanel painelCampos = new JPanel(new GridBagLayout());

                painelCampos.setBackground(Color.WHITE);

                GridBagConstraints gbc = new GridBagConstraints();

                gbc.insets = new Insets(8, 8, 8, 8);

                gbc.fill = GridBagConstraints.HORIZONTAL;

                JTextField txtNomePeca = new JTextField(20);

                JTextField txtContratante = new JTextField(20);

                JTextField txtEmail = new JTextField(20);

                JTextField txtTelefone = new JTextField(15);

                JTextField txtCpf = new JTextField(15);

                JTextField txtValorAluguel = new JTextField(10);

                JTextField txtValorIngresso = new JTextField(10);

                JTextField txtDataInicio = new JTextField(10);

                JTextField txtDataFim = new JTextField(10);

                JTextField txtHoraInicio = new JTextField(8);

                JTextField txtHoraFim = new JTextField(8);

                adicionarLinhaFormulario(
                                painelCampos,
                                "Nome da Peça / Evento:",
                                txtNomePeca,
                                gbc,
                                0);

                adicionarLinhaFormulario(
                                painelCampos,
                                "Nome do Contratante:",
                                txtContratante,
                                gbc,
                                1);

                adicionarLinhaFormulario(
                                painelCampos,
                                "E-mail do Contratante:",
                                txtEmail,
                                gbc,
                                2);

                adicionarLinhaFormulario(
                                painelCampos,
                                "Telefone do Contratante:",
                                txtTelefone,
                                gbc,
                                3);

                adicionarLinhaFormulario(
                                painelCampos,
                                "CPF do Contratante:",
                                txtCpf,
                                gbc,
                                4);

                adicionarLinhaFormulario(
                                painelCampos,
                                "Valor do Aluguel cobrado (R$):",
                                txtValorAluguel,
                                gbc,
                                5);

                adicionarLinhaFormulario(
                                painelCampos,
                                "Preço base do Ingresso (R$):",
                                txtValorIngresso,
                                gbc,
                                6);

                adicionarLinhaFormulario(
                                painelCampos,
                                "Data de Início (AAAA-MM-DD):",
                                txtDataInicio,
                                gbc,
                                7);

                adicionarLinhaFormulario(
                                painelCampos,
                                "Data de Término (AAAA-MM-DD):",
                                txtDataFim,
                                gbc,
                                8);

                adicionarLinhaFormulario(
                                painelCampos,
                                "Horário de Abertura (HH:MM):",
                                txtHoraInicio,
                                gbc,
                                9);

                adicionarLinhaFormulario(
                                painelCampos,
                                "Horário de Fechamento (HH:MM):",
                                txtHoraFim,
                                gbc,
                                10);

                painelPrincipal.add(
                                painelCampos,
                                BorderLayout.CENTER);

                JPanel painelBotoesAcao = new JPanel(
                                new FlowLayout(
                                                FlowLayout.CENTER,
                                                20,
                                                15));

                painelBotoesAcao.setBackground(Color.WHITE);

                JButton btnSalvar = new JButton("Salvar Proposta");

                JButton btnVoltar = new JButton("Voltar ao Menu");

                btnVoltar.addActionListener(e -> {

                        internoCardLayout.show(
                                        painelConteudoInterno,
                                        "submenu");

                        painelConteudoInterno.revalidate();
                        painelConteudoInterno.repaint();
                });

                btnSalvar.addActionListener(e -> {

                        try {

                                String nomePeca = txtNomePeca.getText();

                                String nomeContratante = txtContratante.getText();

                                String email = txtEmail.getText();

                                String telefone = txtTelefone.getText();

                                String cpf = txtCpf.getText();

                                String textoAluguel = txtValorAluguel.getText();

                                String textoIngresso = txtValorIngresso.getText();

                                String textoDataInic = txtDataInicio.getText();

                                String textoDataFim = txtDataFim.getText();

                                String textoHoraInic = txtHoraInicio.getText();

                                String textoHoraFim = txtHoraFim.getText();

                                double valorAluguel = Double.parseDouble(
                                                textoAluguel);

                                double valorIngresso = Double.parseDouble(
                                                textoIngresso);

                                LocalDate dataInicio = LocalDate.parse(
                                                textoDataInic);

                                LocalDate dataFim = LocalDate.parse(
                                                textoDataFim);

                                LocalTime horaInicio = LocalTime.parse(
                                                textoHoraInic);

                                LocalTime horaFim = LocalTime.parse(
                                                textoHoraFim);

                                if (!ValidadorHorarios
                                                .isHorarioDentroDeTurno(
                                                                horaInicio,
                                                                horaFim)) {

                                        JOptionPane.showMessageDialog(
                                                        this,
                                                        "Erro: O horário deve respeitar "
                                                                        + "os turnos permitidos:\n"
                                                                        + "Manhã (08-12), "
                                                                        + "Tarde (13-18) ou "
                                                                        + "Noite (19-23).",
                                                        "Violação de Regra de Turno",
                                                        JOptionPane.ERROR_MESSAGE);

                                        return;
                                }

                                Contratante contratante = new Contratante(
                                                nomeContratante,
                                                email,
                                                telefone,
                                                cpf);

                                PropostaAluguel proposta = new PropostaAluguel(
                                                contratante,
                                                nomePeca,
                                                valorAluguel,
                                                dataInicio,
                                                dataFim,
                                                horaInicio,
                                                horaFim,
                                                valorIngresso);

                                propostaService
                                                .cadastrarProposta(
                                                                proposta);

                                JOptionPane.showMessageDialog(
                                                this,
                                                "Sucesso! Proposta cadastrada e salva: "
                                                                + nomePeca);

                                txtNomePeca.setText("");
                                txtContratante.setText("");
                                txtEmail.setText("");
                                txtTelefone.setText("");
                                txtCpf.setText("");
                                txtValorAluguel.setText("");
                                txtValorIngresso.setText("");
                                txtDataInicio.setText("");
                                txtDataFim.setText("");
                                txtHoraInicio.setText("");
                                txtHoraFim.setText("");

                        } catch (NumberFormatException ex) {

                                JOptionPane.showMessageDialog(
                                                this,
                                                "Por favor, preencha os campos "
                                                                + "de valores e números corretamente!",
                                                "Erro",
                                                JOptionPane.ERROR_MESSAGE);

                        } catch (ConflitoHorarioException ex) {

                                JOptionPane.showMessageDialog(
                                                this,
                                                "Conflito de Agenda: "
                                                                + ex.getMessage(),
                                                "Erro de Horário",
                                                JOptionPane.ERROR_MESSAGE);

                        } catch (Exception ex) {

                                JOptionPane.showMessageDialog(
                                                this,
                                                "Erro ao processar dados: "
                                                                + ex.getMessage(),
                                                "Erro",
                                                JOptionPane.ERROR_MESSAGE);
                        }
                });

                painelBotoesAcao.add(btnSalvar);
                painelBotoesAcao.add(btnVoltar);

                painelPrincipal.add(
                                painelBotoesAcao,
                                BorderLayout.SOUTH);

                return painelPrincipal;
        }

        private void adicionarLinhaFormulario(
                        JPanel painel,
                        String textoLabel,
                        JComponent campoTexto,
                        GridBagConstraints gbc,
                        int linha) {

                gbc.gridy = linha;
                gbc.gridx = 0;
                gbc.weightx = 0.2;

                JLabel label = new JLabel(textoLabel);

                label.setFont(
                                new Font(
                                                "Arial",
                                                Font.PLAIN,
                                                13));

                painel.add(label, gbc);

                gbc.gridx = 1;
                gbc.weightx = 0.8;

                painel.add(
                                campoTexto,
                                gbc);
        }

        public void atualizarTabela(
                        String buscaNome,
                        String statusSelecionado) {

                modeloTabela.setRowCount(0);

                List<PropostaAluguel> lista = propostaService.getListaPropostas();

                for (PropostaAluguel p : lista) {

                        boolean matchNome = p.getNomePeca()
                                        .toLowerCase()
                                        .contains(
                                                        buscaNome.toLowerCase());

                        boolean matchStatus = statusSelecionado.equals("Todos")
                                        ||
                                        p.getStatusProposta()
                                                        .toString()
                                                        .equals(
                                                                        statusSelecionado);

                        if (matchNome && matchStatus) {

                                modeloTabela.addRow(
                                                new Object[] {
                                                                p.getId(),
                                                                p.getNomePeca(),
                                                                p.getContratante().getNome(),
                                                                p.getStatusProposta()
                                                });
                        }
                }
        }
}