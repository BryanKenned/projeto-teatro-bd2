package br.com.projetoteatro.view;

import javax.swing.*;
import java.awt.*;

public class BilheteriaView extends JPanel {

    // Componentes da Tela
    private JLabel lblTitulo;
    private JTextField txtCpf, txtNome, txtEmail, txtQuantidade;
    private JButton btnBuscar, btnConfirmarVenda;
    private JComboBox<String> cbxEspetaculo, cbxSessao, cbxSetor;

    public BilheteriaView() {
        // Layout principal vertical (BorderLayout) e fundo branco
        this.setLayout(new BorderLayout(15, 15));
        this.setBackground(Color.WHITE);
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inicializarComponentes();
        montarLayout();
    }

    private void inicializarComponentes() {
        // Título Superior
        lblTitulo = new JLabel("BILHETERIA E VENDA DE INGRESSOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));

        // Campos de Texto
        txtCpf = new JTextField(15);
        txtNome = new JTextField(25);
        txtEmail = new JTextField(25);
        txtQuantidade = new JTextField(5);
        txtQuantidade.setText("1"); // Padrão começar com 1 ingresso

        // Botões
        btnBuscar = new JButton("BUSCAR");
        btnConfirmarVenda = new JButton("CONFIRMAR VENDA 🎟️");
        btnConfirmarVenda.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirmarVenda.setBackground(new Color(100, 220, 100)); // Um verde bonito para a venda
        btnConfirmarVenda.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Caixas de Seleção (Comboboxes)
        String[] espetaculos = {"O Fantasma da Ópera", "Romeu e Julieta", "Stand Up do Thiago"};
        cbxEspetaculo = new JComboBox<>(espetaculos);

        String[] sessoes = {"08/06/2026 - 19:00 (Noite)", "09/06/2026 - 15:00 (Tarde)"};
        cbxSessao = new JComboBox<>(sessoes);

        String[] setores = {"Platéia VIP", "Platéia Comum", "Frisas", "Camarote"};
        cbxSetor = new JComboBox<>(setores);

        // Configuração de Ação Provisória para o Botão Buscar
        btnBuscar.addActionListener(e -> {
            String cpfDigitado = txtCpf.getText().trim();
            if (cpfDigitado.equals("123.456.789-00") || cpfDigitado.equals("12345678900")) {
                // Simulação de cliente encontrado no banco/XML
                txtNome.setText("João da Silva");
                txtEmail.setText("joao@email.com");
                txtNome.setEnabled(false);  // Desabilita o nome conforme o seu desenho
                txtEmail.setEnabled(false); // Desabilita o e-mail também por segurança
                JOptionPane.showMessageDialog(this, "Cliente encontrado com sucesso!");
            } else {
                // Cliente novo: limpa e deixa digitar
                txtNome.setText("");
                txtEmail.setText("");
                txtNome.setEnabled(true);
                txtEmail.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Cliente não cadastrado. Digite os dados para criar o cadastro.");
            }
        });

        // Configuração de Ação Provisória para o Botão Confirmar
        btnConfirmarVenda.addActionListener(e -> {
            try {
                String nome = txtNome.getText();
                String peca = cbxEspetaculo.getSelectedItem().toString();
                int qtd = Integer.parseInt(txtQuantidade.getText());

                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, busque ou insira os dados do cliente primeiro!", "Erro", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this, "Venda Realizada!\n" + qtd + " ingresso(s) para \"" + peca + "\" salvos para " + nome);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Digite uma quantidade válida de ingressos!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void montarLayout() {
        this.add(lblTitulo, BorderLayout.NORTH);

        // Painel Central com GridBagLayout para alinhar tudo bonitinho
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- LINHA 0: Campo de Busca por CPF ---
        gbc.gridy = 0;
        gbc.gridx = 0; gbc.weightx = 0.2;
        painelFormulario.add(new JLabel("CPF do Espectador:"), gbc);

        gbc.gridx = 1; gbc.weightx = 0.6;
        painelFormulario.add(txtCpf, gbc);

        gbc.gridx = 2; gbc.weightx = 0.2;
        painelFormulario.add(btnBuscar, gbc);

        // --- LINHA 1: Divisória Seção Dados do Cliente ---
        gbc.gridy = 1; gbc.gridx = 0; gbc.gridwidth = 3;
        JLabel lblSecaoCliente = new JLabel("Dados do Cliente:");
        lblSecaoCliente.setFont(new Font("Arial", Font.BOLD, 14));
        lblSecaoCliente.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        painelFormulario.add(lblSecaoCliente, gbc);

        // --- LINHA 2: Nome do Cliente ---
        gbc.gridy = 2; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.2;
        painelFormulario.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.8;
        painelFormulario.add(txtNome, gbc);

        // --- LINHA 3: E-mail do Cliente ---
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.2;
        painelFormulario.add(new JLabel("E-mail:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.8;
        painelFormulario.add(txtEmail, gbc);

        // --- LINHA 4: Divisória Configuração do Ingresso ---
        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 3;
        JLabel lblSecaoIngresso = new JLabel("Configuração do Ingresso:");
        lblSecaoIngresso.setFont(new Font("Arial", Font.BOLD, 14));
        lblSecaoIngresso.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        painelFormulario.add(lblSecaoIngresso, gbc);

        // --- LINHA 5: Seleção do Espetáculo ---
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.2;
        painelFormulario.add(new JLabel("Espetáculo:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.8;
        painelFormulario.add(cbxEspetaculo, gbc);

        // --- LINHA 6: Seleção da Sessão ---
        gbc.gridy = 6; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.2;
        painelFormulario.add(new JLabel("Sessão / Turno:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.8;
        painelFormulario.add(cbxSessao, gbc);

        // --- LINHA 7: Seleção do Setor ---
        gbc.gridy = 7; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.2;
        painelFormulario.add(new JLabel("Setor do Teatro:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.8;
        painelFormulario.add(cbxSetor, gbc);

        // --- LINHA 8: Quantidade ---
        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 1; gbc.weightx = 0.2;
        painelFormulario.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.weightx = 0.8;
        painelFormulario.add(txtQuantidade, gbc);

        this.add(painelFormulario, BorderLayout.CENTER);

        // --- BOTÃO DE CONFIRMAÇÃO NO RODAPÉ ---
        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelRodape.setBackground(Color.WHITE);
        painelRodape.add(btnConfirmarVenda);
        this.add(painelRodape, BorderLayout.SOUTH);
    }
}
