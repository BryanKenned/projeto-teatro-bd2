package br.com.projetoteatro.view;

import br.com.projetoteatro.enums.TipoSetor;
import br.com.projetoteatro.model.Assento;
import br.com.projetoteatro.model.Sessao;
import br.com.projetoteatro.repository.AssentoRepository;
import br.com.projetoteatro.service.IngressoService;
import br.com.projetoteatro.service.SessaoService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;


public class MapaAssentosView extends JPanel {

    private final SessaoService sessaoService;
    private final AssentoRepository assentoRepo;
    private final IngressoService ingressoService;

    private JComboBox<Sessao> cbxSessao;
    private JComboBox<TipoSetor> cbxSetor;
    private JPanel painelGridAssentos;
    private JLabel lblResumoOcupacao;

    private final List<Assento> assentosSelecionados = new ArrayList<>();
    private final Set<String> codigosOcupados = new HashSet<>();
    private Consumer<List<Assento>> callbackSelecao;
    private JButton btnConfirmarSelecao;

    public MapaAssentosView(
            SessaoService sessaoService,
            AssentoRepository assentoRepo,
            IngressoService ingressoService) {

        this.sessaoService = sessaoService;
        this.assentoRepo = assentoRepo;
        this.ingressoService = ingressoService;

        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inicializarComponentes();
        montarLayout();
        atualizarDados();
    }

    public void setCallbackSelecao(Consumer<List<Assento>> callback) {
        this.callbackSelecao = callback;
        if (btnConfirmarSelecao != null) {
            btnConfirmarSelecao.setVisible(callback != null);
        }
    }

    public void selecionarSessaoESetor(Sessao sessao, TipoSetor setor) {
        if (sessao != null && cbxSessao != null) {
            boolean encontrado = false;
            for (int i = 0; i < cbxSessao.getItemCount(); i++) {
                Sessao s = cbxSessao.getItemAt(i);
                if (s != null && s.getId() != null && s.getId().equals(sessao.getId())) {
                    cbxSessao.setSelectedIndex(i);
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                cbxSessao.addItem(sessao);
                cbxSessao.setSelectedItem(sessao);
            }
        }
        if (setor != null && cbxSetor != null) {
            cbxSetor.setSelectedItem(setor);
        }
        renderizarMapa();
    }

    private void inicializarComponentes() {
        cbxSessao = new JComboBox<>();
        cbxSessao.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Sessao s) {
                    setText(String.format("ID %d - %s (%s %s)",
                            s.getId(), s.getNomePeca(),
                            s.getData() != null ? s.getData().toString() : "Data n/d",
                            s.getHorarioInicio() != null ? s.getHorarioInicio().toString() : ""));
                } else if (value == null) {
                    setText("Nenhuma sessão selecionada");
                }
                return this;
            }
        });

        cbxSetor = new JComboBox<>(TipoSetor.values());

        cbxSessao.addActionListener(e -> renderizarMapa());
        cbxSetor.addActionListener(e -> renderizarMapa());

        painelGridAssentos = new JPanel();
        painelGridAssentos.setBackground(Color.WHITE);

        lblResumoOcupacao = new JLabel("Carregando mapa de assentos...");
        lblResumoOcupacao.setFont(new Font("Arial", Font.BOLD, 13));
        lblResumoOcupacao.setForeground(new Color(50, 50, 50));

        btnConfirmarSelecao = new JButton("Confirmar Assento(s) Selecionado(s)");
        btnConfirmarSelecao.setBackground(new Color(52, 152, 219));
        btnConfirmarSelecao.setForeground(Color.WHITE);
        btnConfirmarSelecao.setFont(new Font("Arial", Font.BOLD, 13));
        btnConfirmarSelecao.setFocusPainted(false);
        btnConfirmarSelecao.setVisible(false);

        btnConfirmarSelecao.addActionListener(e -> {
            if (callbackSelecao != null) {
                if (assentosSelecionados.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Por favor, selecione ao menos um assento disponível (verde) no mapa!",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                callbackSelecao.accept(new ArrayList<>(assentosSelecionados));
            }
        });
    }

    private void montarLayout() {
        JPanel painelTopo = new JPanel(new BorderLayout(10, 10));
        painelTopo.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("MAPA DE OCUPAÇÃO E DISPONIBILIDADE DE ASSENTOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        painelTopo.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        painelFiltros.setBackground(new Color(245, 247, 250));
        painelFiltros.setBorder(BorderFactory.createLineBorder(new Color(220, 225, 230)));

        painelFiltros.add(new JLabel("Sessão / Espetáculo:"));
        painelFiltros.add(cbxSessao);
        painelFiltros.add(new JLabel("Setor:"));
        painelFiltros.add(cbxSetor);

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> atualizarDados());
        painelFiltros.add(btnAtualizar);

        painelTopo.add(painelFiltros, BorderLayout.SOUTH);
        add(painelTopo, BorderLayout.NORTH);

        JPanel painelCentro = new JPanel(new BorderLayout(10, 15));
        painelCentro.setBackground(Color.WHITE);

        JPanel painelPalco = new JPanel();
        painelPalco.setPreferredSize(new Dimension(0, 38));
        painelPalco.setBackground(new Color(44, 62, 80));
        JLabel lblPalco = new JLabel("=====================  P A L C O  =====================");
        lblPalco.setForeground(Color.WHITE);
        lblPalco.setFont(new Font("Arial", Font.BOLD, 14));
        painelPalco.add(lblPalco);
        painelCentro.add(painelPalco, BorderLayout.NORTH);

        JScrollPane scrollAssentos = new JScrollPane(painelGridAssentos);
        scrollAssentos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelCentro.add(scrollAssentos, BorderLayout.CENTER);

        add(painelCentro, BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new BorderLayout(10, 5));
        painelRodape.setBackground(Color.WHITE);

        JPanel painelLegenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        painelLegenda.setBackground(Color.WHITE);
        painelLegenda.add(criarItemLegenda(new Color(46, 204, 113), "Disponível"));
        painelLegenda.add(criarItemLegenda(new Color(231, 76, 60), "Ocupado"));
        painelLegenda.add(criarItemLegenda(new Color(52, 152, 219), "Selecionado"));

        painelRodape.add(painelLegenda, BorderLayout.NORTH);

        JPanel painelAcoesSul = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        painelAcoesSul.setBackground(Color.WHITE);
        painelAcoesSul.add(lblResumoOcupacao);
        painelAcoesSul.add(btnConfirmarSelecao);

        painelRodape.add(painelAcoesSul, BorderLayout.SOUTH);
        add(painelRodape, BorderLayout.SOUTH);
    }

    private JPanel criarItemLegenda(Color cor, String texto) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(16, 16));
        box.setBackground(cor);
        box.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        item.add(box);
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 12));
        item.add(lbl);
        return item;
    }

    public void atualizarDados() {
        try {
            if (assentoRepo != null) {
                assentoRepo.inicializarAssentosPadraoSeNecessario();
            }

            cbxSessao.removeAllItems();
            List<Sessao> sessoes = sessaoService != null ? sessaoService.listarSessao() : List.of();
            for (Sessao s : sessoes) {
                cbxSessao.addItem(s);
            }

            renderizarMapa();
        } catch (Exception e) {
            System.err.println("Erro ao atualizar Mapa de Assentos: " + e.getMessage());
        }
    }

    public void renderizarMapa() {
        painelGridAssentos.removeAll();
        assentosSelecionados.clear();
        codigosOcupados.clear();

        Sessao sessaoSelecionada = (Sessao) cbxSessao.getSelectedItem();
        TipoSetor setorSelecionado = (TipoSetor) cbxSetor.getSelectedItem();

        if (sessaoSelecionada == null || setorSelecionado == null) {
            lblResumoOcupacao.setText("Selecione uma sessão e um setor para visualizar os assentos.");
            painelGridAssentos.revalidate();
            painelGridAssentos.repaint();
            return;
        }

        if (ingressoService != null && sessaoSelecionada.getId() != null) {
            codigosOcupados.addAll(ingressoService.buscarAssentosOcupadosNaSessao(sessaoSelecionada.getId()));
        }

        List<Assento> assentosSetor = assentoRepo != null ? assentoRepo.listarPorSetor(setorSelecionado) : List.of();

        if (assentosSetor.isEmpty()) {
            painelGridAssentos.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
            JLabel lblVazio = new JLabel("Nenhum assento cadastrado para o setor " + setorSelecionado);
            lblVazio.setFont(new Font("Arial", Font.ITALIC, 14));
            lblVazio.setForeground(Color.GRAY);
            painelGridAssentos.add(lblVazio);
            lblResumoOcupacao.setText("Total: 0 | Disponíveis: 0 | Ocupados: 0");
            painelGridAssentos.revalidate();
            painelGridAssentos.repaint();
            return;
        }

        int colunas = Math.min(8, Math.max(4, assentosSetor.size()));
        painelGridAssentos.setLayout(new GridLayout(0, colunas, 10, 10));

        int total = assentosSetor.size();
        int ocupadosCount = 0;

        for (Assento assento : assentosSetor) {
            boolean ocupado = codigosOcupados.contains(assento.getCodigo());
            if (ocupado) {
                ocupadosCount++;
            }

            JButton btnAssento = new JButton(assento.getCodigo());
            btnAssento.setPreferredSize(new Dimension(65, 45));
            btnAssento.setFont(new Font("Arial", Font.BOLD, 12));
            btnAssento.setFocusPainted(false);

            if (ocupado) {
                btnAssento.setBackground(new Color(231, 76, 60));
                btnAssento.setForeground(Color.WHITE);
                btnAssento.setToolTipText("Assento " + assento.getCodigo() + " - OCUPADO");
                btnAssento.setEnabled(false);
            } else {
                btnAssento.setBackground(new Color(46, 204, 113));
                btnAssento.setForeground(Color.WHITE);
                btnAssento.setToolTipText("Assento " + assento.getCodigo() + " - DISPONÍVEL (Clique para selecionar)");
                btnAssento.setCursor(new Cursor(Cursor.HAND_CURSOR));

                btnAssento.addActionListener(e -> {
                    if (assentosSelecionados.contains(assento)) {
                        assentosSelecionados.remove(assento);
                        btnAssento.setBackground(new Color(46, 204, 113));
                    } else {
                        assentosSelecionados.add(assento);
                        btnAssento.setBackground(new Color(52, 152, 219));
                    }
                    atualizarTextoResumo(total, codigosOcupados.size());
                });
            }

            painelGridAssentos.add(btnAssento);
        }

        atualizarTextoResumo(total, ocupadosCount);
        painelGridAssentos.revalidate();
        painelGridAssentos.repaint();
    }

    private void atualizarTextoResumo(int total, int ocupados) {
        int disponiveis = total - ocupados;
        int selecionados = assentosSelecionados.size();
        lblResumoOcupacao
                .setText(String.format("Total no Setor: %d | Disponíveis: %d | Ocupados: %d | Selecionados agora: %d",
                        total, disponiveis, ocupados, selecionados));
    }

    public List<Assento> getAssentosSelecionados() {
        return new ArrayList<>(assentosSelecionados);
    }
}
