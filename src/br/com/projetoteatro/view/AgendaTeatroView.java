package br.com.projetoteatro.view;

import br.com.projetoteatro.enums.Turno;
import br.com.projetoteatro.model.Contrato;
import br.com.projetoteatro.model.Sessao;
import br.com.projetoteatro.service.ContratoService;
import br.com.projetoteatro.service.SessaoService;
import br.com.projetoteatro.service.validators.ValidadorHorarios;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AgendaTeatroView extends JPanel {

    private final SessaoService sessaoService;
    private final ContratoService contratoService;

    private JComboBox<String> cbxMeses;
    private JComboBox<String> cbxPecas;
    private JTable tabelaAgenda;
    private DefaultTableModel modeloTabela;
    private JLabel lblMensagemVazio;

    public AgendaTeatroView(SessaoService sessaoService, ContratoService contratoService) {
        this.sessaoService = sessaoService;
        this.contratoService = contratoService;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inicializarComponentes();
        montarLayout();
        atualizarDados();
    }

    public AgendaTeatroView() {
        this(null, null);
    }

    private void inicializarComponentes() {
        String[] meses = { "Todos os Meses", "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
                "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro" };
        cbxMeses = new JComboBox<>(meses);

        cbxPecas = new JComboBox<>();
        cbxPecas.addItem("Todas");

        String[] colunas = { "ID", "Data Exibição", "Turno", "Horário", "Espetáculo / Peça" };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaAgenda = new JTable(modeloTabela);
        tabelaAgenda.setRowHeight(25);

        lblMensagemVazio = new JLabel("Nenhuma sessão cadastrada no banco de dados.", SwingConstants.CENTER);
        lblMensagemVazio.setFont(new Font("Arial", Font.ITALIC, 13));
        lblMensagemVazio.setForeground(Color.GRAY);
        lblMensagemVazio.setVisible(false);
    }

    private void montarLayout() {
        JPanel painelTopo = new JPanel(new BorderLayout(10, 10));
        painelTopo.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("AGENDA E CRONOGRAMA DE SESSÕES DO TEATRO", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        painelTopo.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        painelFiltros.setBackground(new Color(245, 247, 250));
        painelFiltros.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

        painelFiltros.add(new JLabel("Filtrar Mês:"));
        painelFiltros.add(cbxMeses);

        painelFiltros.add(new JLabel("Filtrar Peça:"));
        painelFiltros.add(cbxPecas);

        JButton btnFiltrar = new JButton("Filtrar");
        btnFiltrar.addActionListener(e -> filtrarSessoes());
        painelFiltros.add(btnFiltrar);

        JButton btnNovaSessao = new JButton("+ Agendar Nova Sessão");
        btnNovaSessao.setBackground(new Color(46, 204, 113));
        btnNovaSessao.setForeground(Color.WHITE);
        btnNovaSessao.setFont(new Font("Arial", Font.BOLD, 12));
        btnNovaSessao.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNovaSessao.addActionListener(e -> abrirDialogNovaSessao());
        painelFiltros.add(btnNovaSessao);

        painelTopo.add(painelFiltros, BorderLayout.SOUTH);
        add(painelTopo, BorderLayout.NORTH);

        JPanel painelCentro = new JPanel(new BorderLayout(5, 5));
        painelCentro.setBackground(Color.WHITE);

        JScrollPane scrollTabela = new JScrollPane(tabelaAgenda);
        painelCentro.add(scrollTabela, BorderLayout.CENTER);
        painelCentro.add(lblMensagemVazio, BorderLayout.SOUTH);

        add(painelCentro, BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelRodape.setBackground(Color.WHITE);
        JLabel lblNota = new JLabel(
                "* Nota: Horários definidos conforme turnos regulamentares (Manhã: 08-12h, Tarde: 13-18h, Noite: 19-23h).");
        lblNota.setFont(new Font("Arial", Font.ITALIC, 11));
        lblNota.setForeground(Color.DARK_GRAY);
        painelRodape.add(lblNota);
        add(painelRodape, BorderLayout.SOUTH);
    }

    public void atualizarDados() {
        carregarFiltroPecas();
        filtrarSessoes();
    }

    private void carregarFiltroPecas() {
        cbxPecas.removeAllItems();
        cbxPecas.addItem("Todas");

        Set<String> pecasUnicas = new HashSet<>();

        if (contratoService != null) {
            List<Contrato> contratos = contratoService.listarContratos();
            for (Contrato c : contratos) {
                if (c.getNomePeca() != null && !c.getNomePeca().isBlank()) {
                    pecasUnicas.add(c.getNomePeca());
                }
            }
        }

        if (sessaoService != null) {
            List<Sessao> sessoes = sessaoService.listarSessao();
            for (Sessao s : sessoes) {
                if (s.getNomePeca() != null && !s.getNomePeca().isBlank()) {
                    pecasUnicas.add(s.getNomePeca());
                }
            }
        }

        for (String nome : pecasUnicas) {
            cbxPecas.addItem(nome);
        }
    }

    private void filtrarSessoes() {
        modeloTabela.setRowCount(0);

        if (sessaoService == null) {
            lblMensagemVazio.setText("Serviço de sessão não conectado.");
            lblMensagemVazio.setVisible(true);
            return;
        }

        List<Sessao> sessoes = sessaoService.listarSessao();
        String pecaFiltro = cbxPecas.getSelectedItem() != null ? cbxPecas.getSelectedItem().toString() : "Todas";
        int mesFiltro = cbxMeses.getSelectedIndex();

        int count = 0;
        for (Sessao s : sessoes) {
            boolean matchPeca = pecaFiltro.equals("Todas")
                    || (s.getNomePeca() != null && s.getNomePeca().equalsIgnoreCase(pecaFiltro));
            boolean matchMes = (mesFiltro == 0) || (s.getData() != null && s.getData().getMonthValue() == mesFiltro);

            if (matchPeca && matchMes) {
                count++;
                String horario = (s.getHorarioInicio() != null ? s.getHorarioInicio().toString() : "")
                        + (s.getHorarioFim() != null ? " às " + s.getHorarioFim().toString() : "");

                String dataFormatada = s.getData() != null
                        ? s.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "N/D";

                modeloTabela.addRow(new Object[] {
                        s.getId(),
                        dataFormatada,
                        s.getTurno() != null ? s.getTurno().toString() : "N/D",
                        horario,
                        s.getNomePeca() != null ? s.getNomePeca() : "Sem nome"
                });
            }
        }

        if (count == 0) {
            lblMensagemVazio.setText(
                    "Nenhuma sessão encontrada para os filtros selecionados. Clique em '+ Agendar Nova Sessão' para adicionar.");
            lblMensagemVazio.setVisible(true);
        } else {
            lblMensagemVazio.setVisible(false);
        }
    }

    private void abrirDialogNovaSessao() {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parent, "Cadastrar Nova Sessão Teatral", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(parent);
        dialog.setLayout(new BorderLayout(15, 15));
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel painelCampos = new JPanel(new GridLayout(6, 2, 10, 10));
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        painelCampos.setBackground(Color.WHITE);

        JComboBox<String> cbxPecaDialog = new JComboBox<>();
        List<Contrato> contratos = contratoService != null ? contratoService.listarContratosAtivos() : List.of();
        for (Contrato c : contratos) {
            cbxPecaDialog.addItem(c.getNomePeca());
        }

        JTextField txtNomePecaManual = new JTextField();
        if (cbxPecaDialog.getItemCount() == 0) {
            cbxPecaDialog.setVisible(false);
        } else {
            txtNomePecaManual.setVisible(false);
        }

        JTextField txtData = new JTextField(LocalDate.now().plusDays(1).toString());
        JTextField txtHoraInicio = new JTextField("19:00");
        JTextField txtHoraFim = new JTextField("21:00");
        JComboBox<Turno> cbxTurno = new JComboBox<>(Turno.values());
        cbxTurno.setSelectedItem(Turno.NOITE);

        painelCampos.add(new JLabel("Espetáculo / Peça:"));
        if (cbxPecaDialog.getItemCount() > 0) {
            painelCampos.add(cbxPecaDialog);
        } else {
            painelCampos.add(txtNomePecaManual);
        }

        painelCampos.add(new JLabel("Data (AAAA-MM-DD):"));
        painelCampos.add(txtData);

        painelCampos.add(new JLabel("Horário Início (HH:MM):"));
        painelCampos.add(txtHoraInicio);

        painelCampos.add(new JLabel("Horário Fim (HH:MM):"));
        painelCampos.add(txtHoraFim);

        painelCampos.add(new JLabel("Turno:"));
        painelCampos.add(cbxTurno);

        dialog.add(painelCampos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelBotoes.setBackground(new Color(245, 247, 250));

        JButton btnSalvar = new JButton("Salvar Sessão");
        btnSalvar.setBackground(new Color(46, 204, 113));
        btnSalvar.setForeground(Color.WHITE);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> dialog.dispose());

        btnSalvar.addActionListener(e -> {
            try {
                String nomePeca = cbxPecaDialog.getItemCount() > 0
                        ? cbxPecaDialog.getSelectedItem().toString()
                        : txtNomePecaManual.getText().trim();

                if (nomePeca.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "O nome do espetáculo é obrigatório.", "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                LocalDate data = LocalDate.parse(txtData.getText().trim());
                LocalTime horaInicio = LocalTime.parse(txtHoraInicio.getText().trim());
                LocalTime horaFim = LocalTime.parse(txtHoraFim.getText().trim());
                Turno turno = (Turno) cbxTurno.getSelectedItem();

                if (!ValidadorHorarios.isHorarioDentroDeTurno(horaInicio, horaFim)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Erro: O horário deve respeitar os turnos permitidos:\n"
                                    + "Manhã (08-12), Tarde (13-18) ou Noite (19-23).",
                            "Violação de Turno", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Sessao novaSessao = new Sessao(nomePeca, horaInicio);
                novaSessao.setData(data);
                novaSessao.setHorarioFim(horaFim);
                novaSessao.setTurno(turno);

                sessaoService.cadastrarSessaoCompleta(novaSessao);

                System.out.println("[INFO] Sessão cadastrada: '" + novaSessao.getNomePeca() + "' em " + novaSessao.getData() + " às " + novaSessao.getHorarioInicio());

                JOptionPane.showMessageDialog(dialog,
                        "Sessão agendada e persistida com sucesso no MySQL!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                atualizarDados();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Erro ao agendar sessão: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        dialog.add(painelBotoes, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
