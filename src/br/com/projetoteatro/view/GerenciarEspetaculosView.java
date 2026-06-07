package br.com.projetoteatro.view;

import br.com.projetoteatro.model.Contratante;
import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.repository.ContratoRepository;
import br.com.projetoteatro.service.PropostaService;
import br.com.projetoteatro.exceptions.ConflitoHorarioException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class GerenciarEspetaculosView extends JPanel {

    private CardLayout internoCardLayout;
    private JPanel painelConteudoInterno;

    private JTable tabelaEspetaculos;
    private DefaultTableModel modeloTabela;

    public GerenciarEspetaculosView() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        this.internoCardLayout = new CardLayout();
        this.painelConteudoInterno = new JPanel(this.internoCardLayout);
        this.painelConteudoInterno.setBackground(Color.WHITE);

        JPanel painelSubmenu = criarPainelSubmenu();
        JPanel painelFormulario = criarPainelFormulario();
        JPanel painelListagem = criarPainelListagem();

        this.painelConteudoInterno.add(painelSubmenu, "submenu");
        this.painelConteudoInterno.add(painelFormulario, "formulario");
        this.painelConteudoInterno.add(painelListagem, "listagem");

        this.add(this.painelConteudoInterno, BorderLayout.CENTER);
    }

    // TELA 1: Submenu do Meio da Tela com os 2 Botões Grandes
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

        btnCadastrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnListar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnCadastrar.addActionListener(e -> {
            internoCardLayout.show(painelConteudoInterno, "formulario");
            painelConteudoInterno.revalidate();
            painelConteudoInterno.repaint();
        });

        btnListar.addActionListener(e -> {
            atualizarTabela("", "Todos");
            internoCardLayout.show(painelConteudoInterno, "listagem");
            painelConteudoInterno.revalidate();
            painelConteudoInterno.repaint();
        });

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(btnCadastrar, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        painel.add(btnListar, gbc);

        return painel;
    }

    // TELA NEW: O layout da Tabela com Filtros
    private JPanel criarPainelListagem() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBackground(Color.WHITE);
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- BARRA SUPERIOR: Filtros ---
        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painelFiltros.setBackground(Color.WHITE);

        JLabel lblBuscar = new JLabel("Filtros: Digite o nome da peça...");
        JTextField txtBuscaNome = new JTextField(18);

        JLabel lblStatus = new JLabel("Status:");
        String[] opcoesStatus = {"Todos", "EM_CONTRATACAO", "CONTRATADO", "ENCERRADO"};
        JComboBox<String> cbxStatus = new JComboBox<>(opcoesStatus);

        JButton btnFiltrar = new JButton("FILTRAR");

        btnFiltrar.addActionListener(e -> {
            atualizarTabela(txtBuscaNome.getText(), cbxStatus.getSelectedItem().toString());
        });

        painelFiltros.add(lblBuscar);
        painelFiltros.add(txtBuscaNome);
        painelFiltros.add(lblStatus);
        painelFiltros.add(cbxStatus);
        painelFiltros.add(btnFiltrar);

        painelPrincipal.add(painelFiltros, BorderLayout.NORTH);

        // --- MEIO: A Tabela JTable ---
        String[] colunas = {"ID", "Nome da Peça", "Artista Locatário", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0);
        tabelaEspetaculos = new JTable(modeloTabela);
        JScrollPane scrollTabela = new JScrollPane(tabelaEspetaculos);

        painelPrincipal.add(scrollTabela, BorderLayout.CENTER);

        // --- RODAPÉ: Botões de Navegação ---
        JPanel painelBotoesSul = new JPanel(new BorderLayout());
        painelBotoesSul.setBackground(Color.WHITE);

        JButton btnVoltarMenu = new JButton("Voltar ao Menu");
        JButton btnEditar = new JButton("DETALHAR / EDITAR");

        btnVoltarMenu.addActionListener(e -> {
            internoCardLayout.show(painelConteudoInterno, "submenu");
        });

        painelBotoesSul.add(btnVoltarMenu, BorderLayout.WEST);
        painelBotoesSul.add(btnEditar, BorderLayout.EAST);

        painelPrincipal.add(painelBotoesSul, BorderLayout.SOUTH);

        return painelPrincipal;
    }

    // TELA 2: Seu Formulário com Coleta Avançada do Contratante e Integração com a Service
    private JPanel criarPainelFormulario() {
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Nova Proposta de Aluguel / Espetáculo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        painelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtNomePeca = new JTextField(20);

        // Coleta dos dados do Contratante exigidos pelo construtor novo (Sem Senha)
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

        // Organização sequencial das linhas na interface
        adicionarLinhaFormulario(painelCampos, "Nome da Peça / Evento:", txtNomePeca, gbc, 0);
        adicionarLinhaFormulario(painelCampos, "Nome do Contratante:", txtContratante, gbc, 1);
        adicionarLinhaFormulario(painelCampos, "E-mail do Contratante:", txtEmail, gbc, 2);
        adicionarLinhaFormulario(painelCampos, "Telefone do Contratante:", txtTelefone, gbc, 3);
        adicionarLinhaFormulario(painelCampos, "CPF do Contratante:", txtCpf, gbc, 4);
        adicionarLinhaFormulario(painelCampos, "Valor do Aluguel cobrado (R$):", txtValorAluguel, gbc, 5);
        adicionarLinhaFormulario(painelCampos, "Preço base do Ingresso (R$):", txtValorIngresso, gbc, 6);
        adicionarLinhaFormulario(painelCampos, "Data de Início (AAAA-MM-DD):", txtDataInicio, gbc, 7);
        adicionarLinhaFormulario(painelCampos, "Data de Término (AAAA-MM-DD):", txtDataFim, gbc, 8);
        adicionarLinhaFormulario(painelCampos, "Horário de Abertura (HH:MM):", txtHoraInicio, gbc, 9);
        adicionarLinhaFormulario(painelCampos, "Horário de Fechamento (HH:MM):", txtHoraFim, gbc, 10);

        painelPrincipal.add(painelCampos, BorderLayout.CENTER);

        JPanel painelBotoesAcao = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        painelBotoesAcao.setBackground(Color.WHITE);

        JButton btnSalvar = new JButton("Salvar Proposta");
        JButton btnVoltar = new JButton("Voltar ao Menu");

        btnVoltar.addActionListener(e -> {
            internoCardLayout.show(painelConteudoInterno, "submenu");
            painelConteudoInterno.revalidate();
            painelConteudoInterno.repaint();
        });

        btnSalvar.addActionListener(e -> {
            ContratoRepository contratoRepository = new ContratoRepository();
            PropostaService propostaService = new PropostaService();

            try {
                // 1. Resgata as strings da interface gráfica
                String nomePeca        = txtNomePeca.getText();
                String nomeContratante = txtContratante.getText();
                String email           = txtEmail.getText();
                String telefone        = txtTelefone.getText();
                String cpf             = txtCpf.getText();

                String textoAluguel    = txtValorAluguel.getText();
                String textoIngresso   = txtValorIngresso.getText();
                String textoDataInic   = txtDataInicio.getText();
                String textoDataFim    = txtDataFim.getText();
                String textoHoraInic   = txtHoraInicio.getText();
                String textoHoraFim    = txtHoraFim.getText();

                // 2. Converte as variáveis para os tipos corretos do Java
                double valorAluguel = Double.parseDouble(textoAluguel);
                double valorIngresso = Double.parseDouble(textoIngresso);

                LocalDate dataInicio = LocalDate.parse(textoDataInic);
                LocalDate dataFim    = LocalDate.parse(textoDataFim);

                LocalTime horaInicio = LocalTime.parse(textoHoraInic);
                LocalTime horaFim    = LocalTime.parse(textoHoraFim);

                // 3. Validação Básica de Turno (UI level)
                if (!validarTurnoObrigatorio(horaInicio, horaFim)) {
                    JOptionPane.showMessageDialog(this,
                            "Erro: A peça ultrapassou os limites de um turno!\n\n" +
                                    "Os turnos permitidos são:\n" +
                                    "• Manhã: 08:00 às 12:00\n" +
                                    "• Tarde: 13:00 às 18:00\n" +
                                    "• Noite: 19:00 às 23:00\n\n" +
                                    "A peça precisa começar e terminar dentro do mesmo turno.",
                            "Violação de Regra de Turno", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 4. Cria os objetos usando os construtores limpos
                Contratante contratante = new Contratante(nomeContratante, email, telefone, cpf);
                PropostaAluguel proposta = new PropostaAluguel(contratante, nomePeca, valorAluguel, dataInicio, dataFim, horaInicio, horaFim, valorIngresso);

                // 5. Alimenta a lista interna da Service com o histórico real do XML
                propostaService.getListaPropostas().addAll(contratoRepository.carregarContratos());

                // 6. Roda as validações complexas da Service (incluindo o ConflitoHorarioException)
                propostaService.cadastrarProposta(proposta);

                // 7. Se a Service aceitou sem jogar erro, grava definitivamente no disco
                contratoRepository.salvarContrato(proposta);

                JOptionPane.showMessageDialog(this, "Sucesso! Proposta cadastrada e salva: " + txtNomePeca.getText());

                // Limpa os campos do formulário para o próximo preenchimento
                txtNomePeca.setText(""); txtContratante.setText(""); txtEmail.setText("");
                txtTelefone.setText(""); txtCpf.setText(""); txtValorAluguel.setText("");
                txtValorIngresso.setText(""); txtDataInicio.setText(""); txtDataFim.setText("");
                txtHoraInicio.setText(""); txtHoraFim.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha os campos de valores e números corretamente!", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (ConflitoHorarioException ex) {
                // Pega a mensagem disparada lá pelo seu ValidadorHorarios e joga na tela de erro
                JOptionPane.showMessageDialog(this, "Conflito de Agenda: " + ex.getMessage(), "Erro de Horário", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao processar dados: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        painelBotoesAcao.add(btnSalvar);
        painelBotoesAcao.add(btnVoltar);
        painelPrincipal.add(painelBotoesAcao, BorderLayout.SOUTH);

        return painelPrincipal;
    }

    private void adicionarLinhaFormulario(JPanel painel, String textoLabel, JComponent campoTexto, GridBagConstraints gbc, int linha) {
        gbc.gridy = linha;
        gbc.gridx = 0;
        gbc.weightx = 0.2;
        JLabel label = new JLabel(textoLabel);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        painel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.8;
        painel.add(campoTexto, gbc);
    }

    public void atualizarTabela(String buscaNome, String statusSelecionado) {
        ContratoRepository repo = new ContratoRepository();

        java.util.List<PropostaAluguel> lista = repo.listarComFiltros(buscaNome, statusSelecionado);

        modeloTabela.setRowCount(0);

        for (int i = 0; i < lista.size(); i++) {
            PropostaAluguel p = lista.get(i);

            modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getNomePeca(),
                    p.getContratante().getNome(),
                    p.getStatusProposta()
            });
        }
    }

    private boolean validarTurnoObrigatorio(LocalTime inicio, LocalTime fim) {
        LocalTime manhaInicio = LocalTime.of(8, 0);
        LocalTime manhaFim    = LocalTime.of(12, 0);

        LocalTime tardeInicio = LocalTime.of(13, 0);
        LocalTime tardeFim    = LocalTime.of(18, 0);

        LocalTime noiteInicio = LocalTime.of(19, 0);
        LocalTime noiteFim    = LocalTime.of(23, 0);

        if ((inicio.isAfter(manhaInicio) || inicio.equals(manhaInicio)) && (fim.isBefore(manhaFim) || fim.equals(manhaFim))) {
            return true;
        }
        if ((inicio.isAfter(tardeInicio) || inicio.equals(tardeInicio)) && (fim.isBefore(tardeFim) || fim.equals(tardeFim))) {
            return true;
        }
        if ((inicio.isAfter(noiteInicio) || inicio.equals(noiteInicio)) && (fim.isBefore(noiteFim) || fim.equals(noiteFim))) {
            return true;
        }

        return false;
    }
}
