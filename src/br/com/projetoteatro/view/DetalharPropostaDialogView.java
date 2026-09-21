package br.com.projetoteatro.view;

import br.com.projetoteatro.enums.StatusProposta;
import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.service.PropostaService;
import br.com.projetoteatro.service.validators.ValidadorHorarios;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;


public class DetalharPropostaDialogView extends JDialog {

    private final PropostaAluguel proposta;
    private final PropostaService propostaService;

    private JTextField txtNomePeca;
    private JTextField txtContratante;
    private JTextField txtDataInicio;
    private JTextField txtDataFim;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFim;
    private JTextField txtValorIngresso;
    private JLabel lblValorAluguel;
    private JLabel lblStatusProposta;
    private JLabel lblStatusContrato;

    public DetalharPropostaDialogView(Frame parent, PropostaAluguel p, PropostaService propostaService) {
        super(parent, "Editar / Detalhar Proposta (ID: " + p.getId() + ")", true);
        this.proposta = p;
        this.propostaService = propostaService;

        setSize(520, 560);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        inicializarComponentes();
        montarLayout();
    }

    private void inicializarComponentes() {
        txtNomePeca = new JTextField(proposta.getNomePeca(), 20);

        String nomeContratante = proposta.getContratante() != null ? proposta.getContratante().getNome() : "N/A";
        txtContratante = new JTextField(nomeContratante, 20);
        txtContratante.setEditable(false);

        txtDataInicio = new JTextField(proposta.getDataInicio() != null ? proposta.getDataInicio().toString() : "", 10);
        txtDataFim = new JTextField(proposta.getDataFim() != null ? proposta.getDataFim().toString() : "", 10);
        txtHoraInicio = new JTextField(
                proposta.getHorarioInicio() != null ? proposta.getHorarioInicio().toString() : "", 8);
        txtHoraFim = new JTextField(proposta.getHorarioFim() != null ? proposta.getHorarioFim().toString() : "", 8);
        txtValorIngresso = new JTextField(String.valueOf(proposta.getValorIngresso()), 10);

        lblValorAluguel = new JLabel(String.format("R$ %.2f", proposta.getValorAluguel()));
        lblValorAluguel.setFont(new Font("Arial", Font.BOLD, 13));

        lblStatusProposta = new JLabel(String.valueOf(proposta.getStatusProposta()));
        lblStatusProposta.setFont(new Font("Arial", Font.BOLD, 12));
        if (proposta.getStatusProposta() == StatusProposta.CONTRATADO) {
            lblStatusProposta.setForeground(new Color(46, 204, 113));
        } else if (proposta.getStatusProposta() == StatusProposta.ENCERRADO) {
            lblStatusProposta.setForeground(new Color(231, 76, 60));
        }

        lblStatusContrato = new JLabel(String.valueOf(proposta.getStatusContrato()));
        lblStatusContrato.setFont(new Font("Arial", Font.BOLD, 12));

        if (proposta.getStatusProposta() == StatusProposta.ENCERRADO) {
            txtNomePeca.setEditable(false);
            txtDataInicio.setEditable(false);
            txtDataFim.setEditable(false);
            txtHoraInicio.setEditable(false);
            txtHoraFim.setEditable(false);
            txtValorIngresso.setEditable(false);
        }
    }

    private void montarLayout() {
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(245, 247, 250));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Formulário de Edição da Proposta #" + proposta.getId());
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        painelTopo.add(lblTitulo, BorderLayout.WEST);

        add(painelTopo, BorderLayout.NORTH);

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBackground(Color.WHITE);
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        adicionarLinha(painelCampos, gbc, row++, "Contratante:", txtContratante);
        adicionarLinha(painelCampos, gbc, row++, "Nome da Peça:", txtNomePeca);
        adicionarLinha(painelCampos, gbc, row++, "Data Início (AAAA-MM-DD):", txtDataInicio);
        adicionarLinha(painelCampos, gbc, row++, "Data Fim (AAAA-MM-DD):", txtDataFim);
        adicionarLinha(painelCampos, gbc, row++, "Horário Início (HH:MM):", txtHoraInicio);
        adicionarLinha(painelCampos, gbc, row++, "Horário Fim (HH:MM):", txtHoraFim);
        adicionarLinha(painelCampos, gbc, row++, "Preço do Ingresso (R$):", txtValorIngresso);
        adicionarLinha(painelCampos, gbc, row++, "Aluguel Calculado:", lblValorAluguel);
        adicionarLinha(painelCampos, gbc, row++, "Status da Proposta:", lblStatusProposta);
        adicionarLinha(painelCampos, gbc, row++, "Status do Contrato:", lblStatusContrato);

        add(painelCampos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        painelBotoes.setBackground(new Color(245, 247, 250));

        JButton btnSalvar = new JButton("Salvar Alterações");
        btnSalvar.setBackground(new Color(52, 152, 219));
        btnSalvar.setForeground(Color.WHITE);

        JButton btnEfetivar = new JButton("Efetivar Contrato");
        btnEfetivar.setBackground(new Color(46, 204, 113));
        btnEfetivar.setForeground(Color.WHITE);

        JButton btnEncerrar = new JButton("Encerrar");
        btnEncerrar.setBackground(new Color(231, 76, 60));
        btnEncerrar.setForeground(Color.WHITE);

        JButton btnFechar = new JButton("Fechar");

        if (proposta.getStatusProposta() == StatusProposta.ENCERRADO) {
            btnSalvar.setEnabled(false);
            btnEfetivar.setEnabled(false);
            btnEncerrar.setEnabled(false);
        }

        btnSalvar.addActionListener(e -> salvarAlteracoes());
        btnEfetivar.addActionListener(e -> efetivarContrato());
        btnEncerrar.addActionListener(e -> encerrarProposta());
        btnFechar.addActionListener(e -> dispose());

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnEfetivar);
        painelBotoes.add(btnEncerrar);
        painelBotoes.add(btnFechar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void adicionarLinha(JPanel painel, GridBagConstraints gbc, int linha, String label, JComponent campo) {
        gbc.gridy = linha;
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        painel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        painel.add(campo, gbc);
    }

    private void salvarAlteracoes() {
        try {
            String nome = txtNomePeca.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome da peça não pode ficar vazio.", "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate dataInicio = LocalDate.parse(txtDataInicio.getText().trim());
            LocalDate dataFim = LocalDate.parse(txtDataFim.getText().trim());
            LocalTime horaInicio = LocalTime.parse(txtHoraInicio.getText().trim());
            LocalTime horaFim = LocalTime.parse(txtHoraFim.getText().trim());
            double valorIngresso = Double.parseDouble(txtValorIngresso.getText().trim());

            if (!ValidadorHorarios.isHorarioDentroDeTurno(horaInicio, horaFim)) {
                JOptionPane.showMessageDialog(this,
                        "Erro: O horário deve respeitar os turnos permitidos:\n"
                                + "Manhã (08-12), Tarde (13-18) ou Noite (19-23).",
                        "Turno Inválido", JOptionPane.ERROR_MESSAGE);
                return;
            }

            proposta.setNomePeca(nome);
            proposta.setDataInicio(dataInicio);
            proposta.setDataFim(dataFim);
            proposta.setHorarioInicio(horaInicio);
            proposta.setHorarioFim(horaFim);
            proposta.setValorIngresso(valorIngresso);

            propostaService.atualizarProposta(proposta);

            System.out.println(
                    "[INFO] Proposta atualizada no banco: ID " + proposta.getId() + " - " + proposta.getNomePeca());

            lblValorAluguel.setText(String.format("R$ %.2f", proposta.getValorAluguel()));

            JOptionPane.showMessageDialog(this,
                    "Proposta atualizada com sucesso no banco de dados!\nNovo valor do aluguel: R$ "
                            + String.format("%.2f", proposta.getValorAluguel()),
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço do ingresso deve ser um número válido.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar alterações: " + ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void efetivarContrato() {
        try {
            propostaService.efetivarContrato(proposta);

            System.out.println("[INFO] Contrato formalizado a partir da proposta: ID " + proposta.getId());

            JOptionPane.showMessageDialog(this,
                    "Contrato formalizado e persistido com sucesso no MySQL!\n"
                            + "O espetáculo já está disponível para venda na Bilheteria.",
                    "Contrato Ativo", JOptionPane.INFORMATION_MESSAGE);

            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao efetivar contrato: " + ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void encerrarProposta() {
        try {
            propostaService.encerrarProposta(proposta.getId());

            System.out.println("[INFO] Proposta encerrada: ID " + proposta.getId());

            JOptionPane.showMessageDialog(this, "Proposta encerrada com sucesso!", "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao encerrar proposta: " + ex.getMessage(), "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
