package br.com.projetoteatro.view;

import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.repository.ContratoRepository;
import br.com.projetoteatro.repository.IngressoRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashBoardVendasView extends JPanel {
    // Componentes dos Cards
    private JLabel lblFaturamento;
    private JLabel lblIngressosHoje;
    private JLabel lblPecaDestaque;

    // Componentes da Tabela
    private JTable tabelaEspetaculos;
    private DefaultTableModel modeloTabela;

    // Repositories para buscar os dados dos XMLs
    private ContratoRepository contratoRepo;
    private IngressoRepository ingressoRepo;

    public DashBoardVendasView() {
        // Inicializa os repositórios (garanta que o XStream atualizado está neles)
        contratoRepo = new ContratoRepository();
        ingressoRepo = new IngressoRepository();

        // Configuração do Painel Principal do Dashboard
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 240, 240)); // Fundo cinza claro padrão

        // 1. Construir e Adicionar a Região dos Cards (Topo)
        add(criarPainelCards(), BorderLayout.NORTH);

        // 2. Construir e Adicionar a Região da Tabela (Centro)
        add(criarPainelTabela(), BorderLayout.CENTER);

        // 3. Carregar os dados vindos dos arquivos XML
        atualizarDadosDashboard();
    }

    private JPanel criarPainelCards() {
        JPanel painelCards = new JPanel(new GridLayout(1, 3, 15, 0));
        painelCards.setOpaque(false); // Mantém o fundo do painel pai

        // Card 1: Faturamento (Verde)
        JPanel cardFaturamento = criarCardEstilizado(" FATURAMENTO BRUTO (MÊS)", new Color(46, 204, 113));
        lblFaturamento = (JLabel) cardFaturamento.getComponent(1);

        // Card 2: Ingressos (Azul)
        JPanel cardIngressos = criarCardEstilizado("INGRESSOS VENDIDOS HOJE", new Color(52, 152, 219));
        lblIngressosHoje = (JLabel) cardIngressos.getComponent(1);

        // Card 3: Destaque (Laranja)
        JPanel cardDestaque = criarCardEstilizado("ESPETÁCULO EM DESTAQUE", new Color(230, 126, 34));
        lblPecaDestaque = (JLabel) cardDestaque.getComponent(1);

        painelCards.add(cardFaturamento);
        painelCards.add(cardIngressos);
        painelCards.add(cardDestaque);

        return painelCards;
    }

    private JPanel criarCardEstilizado(String titulo, Color corFundo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(corFundo);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel lblValor = new JLabel("Carregando...");
        lblValor.setForeground(Color.WHITE);
        lblValor.setFont(new Font("Arial", Font.BOLD, 22));

        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    private JPanel criarPainelTabela() {
        JPanel painelTabela = new JPanel(new BorderLayout(5, 5));
        painelTabela.setOpaque(false);

        JLabel lblTituloTabela = new JLabel("📊 Próximas Apresentações Agendadas");
        lblTituloTabela.setFont(new Font("Arial", Font.BOLD, 16));
        lblTituloTabela.setForeground(new Color(44, 62, 80));
        painelTabela.add(lblTituloTabela, BorderLayout.NORTH);

        // Configura as colunas da JTable
        String[] colunas = {"ID", "Espetáculo / Peça", "Data", "Status do Contrato"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Impede o usuário de editar o texto da tabela
            }
        };

        tabelaEspetaculos = new JTable(modeloTabela);
        tabelaEspetaculos.setRowHeight(25); // Linhas mais gordinhas e elegantes

        JScrollPane scrollPane = new JScrollPane(tabelaEspetaculos);
        painelTabela.add(scrollPane, BorderLayout.CENTER);

        return painelTabela;
    }

    public void atualizarDadosDashboard() {
        try {
            // Limpa a tabela antes de carregar
            modeloTabela.setRowCount(0);

            // Puxa os dados reais do seu XML usando o seu listarTodos()
            List<PropostaAluguel> contratos = contratoRepo.listarTodos();

            // 🛑 >>> INÍCIO DA MUDANÇA COM AS REGRAS MATEMÁTICAS <<<
            double faturamentoTotalMes = 0;
            int mesAtual = java.time.LocalDate.now().getMonthValue();
            int anoAtual = java.time.LocalDate.now().getYear();

            // Instancia a service que acabamos de destravar para calcular os ingressos
            br.com.projetoteatro.service.ContratoService contratoService = new br.com.projetoteatro.service.ContratoService();

            if (contratos != null && !contratos.isEmpty()) {
                for (PropostaAluguel c : contratos) {

                    // 1. CÁLCULO DO CARD DE FATURAMENTO
                    // Verifica se a peça acontece no mês e ano vigentes
                    if (c.getDataInicio() != null &&
                            c.getDataInicio().getMonthValue() == mesAtual &&
                            c.getDataInicio().getYear() == anoAtual) {

                        String status = c.getStatusProposta().toString();

                        if (status.equals("EM_CONTRATACAO")) {
                            faturamentoTotalMes += c.getValorAluguel(); // Soma o valor fixo da locação
                        } else if (status.equals("ENCERRADO")) {
                            faturamentoTotalMes += contratoService.calcularTotalIngressos(c.getId()); // Soma os ingressos reais vendidos
                        }
                    }

                    // 2. PREENCHIMENTO DA TABELA (Exclui os encerrados)
                    if (!"ENCERRADO".equals(c.getStatusProposta().toString())) {
                        modeloTabela.addRow(new Object[]{
                                c.getId(),
                                c.getNomePeca(),
                                c.getDataInicio() != null ? c.getDataInicio().toString() : "Sem Data",
                                c.getStatusProposta()
                        });
                    }
                }
            }

            // 3. ATUALIZAÇÃO VISUAL DOS CARDS COM OS VALORES REAIS
            String faturamentoFormatado = String.format("R$ %.2f", faturamentoTotalMes).replace(".", ",");
            lblFaturamento.setText(faturamentoFormatado);  // MUDA O CARD VERDE AQUI!

            // Puxa do repositório de ingressos a quantidade total vendida (Apenas para não ficar estático)
            int totalIngressos = ingressoRepo.listar() != null ? ingressoRepo.listar().size() : 0;
            lblIngressosHoje.setText(totalIngressos + " Unidades");

            // Define dinamicamente o nome da primeira peça ativa encontrada como destaque
            if (contratos != null && !contratos.isEmpty()) {
                lblPecaDestaque.setText(contratos.get(0).getNomePeca());
            } else {
                lblPecaDestaque.setText("Nenhum");
            }
            //  >>> FIM DA MUDANÇA DO DASHBOARD <<<

        } catch (Exception e) {
            System.err.println("Erro ao atualizar dados do Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
