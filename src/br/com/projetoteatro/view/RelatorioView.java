package br.com.projetoteatro.view;

import javax.swing.*;
import java.awt.*;

public class RelatorioView extends JPanel {
    private JLabel lblTitulo;
    private JComboBox<String> cbxPecasLista, cbxPecasFinanceiro;
    private JTextField txtDataInicio, txtDataFim;
    private JButton btnGerarLista, btnConsolidarPeca, btnGerarFaturamento;

    public RelatorioView() {
        // Layout vertical principal e configurações de espaçamento
        this.setLayout(new BorderLayout(20, 20));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inicializarComponentes();
        montarLayout();
    }

    private void inicializarComponentes() {
        // Título Principal da Tela
        lblTitulo = new JLabel("RELATÓRIOS E EXPORTAÇÕES", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        // Dados provisórios para os ComboBoxes
        String[] pecasExemplo = {"Auto da Compadecida", "Romeu e Julieta", "O Fantasma da Ópera"};

        // Seção 1: Lista de Presença
        cbxPecasLista = new JComboBox<>(pecasExemplo);
        btnGerarLista = new JButton("GERAR LISTA (PDF)");
        btnGerarLista.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Seção 2: Fechamento Financeiro
        cbxPecasFinanceiro = new JComboBox<>(pecasExemplo);
        cbxPecasFinanceiro.setSelectedItem("Romeu e Julieta"); // Conforme o desenho
        btnConsolidarPeca = new JButton("CONSOLIDAR PEÇA");
        btnConsolidarPeca.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Seção 3: Faturamento Total
        txtDataInicio = new JTextField("01/06/2026", 10);
        txtDataFim = new JTextField("30/06/2026", 10);
        btnGerarFaturamento = new JButton("GERAR RELATÓRIO DO TEATRO (PDF)");
        btnGerarFaturamento.setFont(new Font("Arial", Font.BOLD, 13));
        btnGerarFaturamento.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ações de clique provisórias para você testar os botões
        btnGerarLista.addActionListener(e -> {
            String peca = cbxPecasLista.getSelectedItem().toString();
            JOptionPane.showMessageDialog(this, "Criando arquivo PDF com a lista de espectadores para: " + peca);
        });

        btnConsolidarPeca.addActionListener(e -> {
            String peca = cbxPecasFinanceiro.getSelectedItem().toString();
            JOptionPane.showMessageDialog(this, "Calculando balanço financeiro da peça: " + peca);
        });

        btnGerarFaturamento.addActionListener(e -> {
            String inicio = txtDataInicio.getText();
            String fim = txtDataFim.getText();
            JOptionPane.showMessageDialog(this, "Compilando faturamento total de " + inicio + " até " + fim);
        });
    }

    private void montarLayout() {
        this.add(lblTitulo, BorderLayout.NORTH);

        // Painel central que vai empilhar as 3 seções do seu rascunho
        JPanel painelCentral = new JPanel(new GridBagLayout());
        painelCentral.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 10, 0); // Espaço entre os blocos

        // --- BLOCO 1: LISTA DE PRESENÇA ---
        JPanel painelSecao1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelSecao1.setBackground(Color.WHITE);
        painelSecao1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "1. LISTA DE PRESENÇA", 0, 0, new Font("Arial", Font.BOLD, 14))
        );
        painelSecao1.add(new JLabel("Selecione a Peça:"));
        painelSecao1.add(cbxPecasLista);
        painelSecao1.add(btnGerarLista);

        gbc.gridy = 0;
        painelCentral.add(painelSecao1, gbc);

        // --- BLOCO 2: FECHAMENTO FINANCEIRO ---
        JPanel painelSecao2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        painelSecao2.setBackground(Color.WHITE);
        painelSecao2.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "2. FECHAMENTO FINANCEIRO POR CONTRATO", 0, 0, new Font("Arial", Font.BOLD, 14))
        );
        painelSecao2.add(new JLabel("Selecione a Peça:"));
        painelSecao2.add(cbxPecasFinanceiro);
        painelSecao2.add(btnConsolidarPeca);

        gbc.gridy = 1;
        painelCentral.add(painelSecao2, gbc);

        // --- BLOCO 3: FATURAMENTO TOTAL ---
        JPanel painelSecao3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        painelSecao3.setBackground(Color.WHITE);
        painelSecao3.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "3. FATURAMENTO TOTAL DO TEATRO", 0, 0, new Font("Arial", Font.BOLD, 14))
        );
        painelSecao3.add(new JLabel("Data Inicial:"));
        painelSecao3.add(txtDataInicio);
        painelSecao3.add(new JLabel("Data Final:"));
        painelSecao3.add(txtDataFim);
        painelSecao3.add(btnGerarFaturamento);

        gbc.gridy = 2;
        painelCentral.add(painelSecao3, gbc);

        this.add(painelCentral, BorderLayout.CENTER);
    }
}
