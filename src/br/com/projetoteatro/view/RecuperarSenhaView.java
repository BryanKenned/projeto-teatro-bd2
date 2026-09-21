package br.com.projetoteatro.view;

import br.com.projetoteatro.service.AdministradorService;
import br.com.projetoteatro.service.CodigoRecuperacaoSenhaService;

import javax.swing.*;

public class RecuperarSenhaView extends JFrame {
    private JTextField campoEmail = new JTextField();
    private JTextField campoCodigo = new JTextField();
    private JPasswordField campoNovaSenha = new JPasswordField();

    private CodigoRecuperacaoSenhaService codigoRecuperar;
    private AdministradorService admService;

    public RecuperarSenhaView(CodigoRecuperacaoSenhaService codigoRecuperar,AdministradorService admService) {
        this.codigoRecuperar = codigoRecuperar;
        this.admService = admService;

        setTitle("Recuperar Senha");
        setSize(400,400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblEmail = new JLabel("E-mail:");
        lblEmail.setBounds(50, 50, 100, 30);
        add(lblEmail);
        campoEmail.setBounds(150, 50, 200, 30);
        add(campoEmail);

        JButton btnEnviarCodigo = new JButton("Enviar Código");
        btnEnviarCodigo.setBounds(150, 90, 200, 30);
        add(btnEnviarCodigo);

        JLabel lblCodigo = new JLabel("Código:");
        lblCodigo.setBounds(50, 150, 100, 30);
        add(lblCodigo);
        campoCodigo.setBounds(150, 150, 200, 30);
        add(campoCodigo);

        JLabel lblNovaSenha = new JLabel("Nova Senha:");
        lblNovaSenha.setBounds(50, 190, 100, 30);
        add(lblNovaSenha);
        campoNovaSenha.setBounds(150, 190, 200, 30);
        add(campoNovaSenha);

        JButton btnConfirmar = new JButton("Confirmar Alteração");
        btnConfirmar.setBounds(150, 240, 200, 30);
        add(btnConfirmar);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.setBounds(150, 300, 100, 30);
        add(btnVoltar);

        btnVoltar.addActionListener(e -> {
            this.dispose();
        });

        btnEnviarCodigo.addActionListener(e -> {
            codigoRecuperar.solicitarCodigo(campoEmail.getText());
            JOptionPane.showMessageDialog(this, "Código enviado!");
        });

        btnConfirmar.addActionListener(e -> {
            if (codigoRecuperar.validarCodigo(campoCodigo.getText())) {
                String novaSenha = new String(campoNovaSenha.getPassword());
                admService.atualizarSenha(campoEmail.getText(), novaSenha);

                JOptionPane.showMessageDialog(this, "Senha alterada com sucesso!");
                this.dispose();
                JOptionPane.showMessageDialog(this, "Código confirmado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Código inválido.");
            }
        });

    }
}
