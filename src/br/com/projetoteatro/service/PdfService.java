package br.com.projetoteatro.service;

import br.com.projetoteatro.model.Contrato;
import br.com.projetoteatro.model.Ingresso;
import br.com.projetoteatro.model.PropostaAluguel;
import br.com.projetoteatro.model.Sessao;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfService {

        public static String gerarContrato(Contrato contrato) {
                String nomeArquivo = "contrato_" + contrato.getId() + ".pdf";

                try {

                        Document documento = new Document();

                        PdfWriter.getInstance(
                                        documento,
                                        new FileOutputStream(nomeArquivo));
                        documento.open();

                        Font titulo = FontFactory.getFont(
                                        FontFactory.HELVETICA_BOLD,
                                        18);

                        Font texto = FontFactory.getFont(
                                        FontFactory.HELVETICA,
                                        12);

                        Paragraph cabecalho = new Paragraph(
                                        "CONTRATO DE ALUGUEL DO TEATRO",
                                        titulo);

                        cabecalho.setAlignment(Element.ALIGN_CENTER);
                        documento.add(cabecalho);

                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(
                                        "ID da proposta: "
                                                        + contrato.getId(),
                                        texto));

                        documento.add(new Paragraph(
                                        "Contratante: "
                                                        + contrato.getProposta().getContratante().getNome(),
                                        texto));

                        documento.add(new Paragraph(
                                        "Peça: "
                                                        + contrato.getNomePeca(),
                                        texto));

                        documento.add(new Paragraph(
                                        "Período: "
                                                        + contrato.getDataInicio()
                                                        + " até "
                                                        + contrato.getDataFim(),
                                        texto));

                        documento.add(new Paragraph(
                                        "Horário: "
                                                        + contrato.getHorarioInicio()
                                                        + " às "
                                                        + contrato.getHorarioFim(),
                                        texto));

                        documento.add(new Paragraph(
                                        "Valor do aluguel: R$ "
                                                        + contrato.getValorAluguel(),
                                        texto));

                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(
                                        "As partes concordam com os termos estabelecidos neste contrato."));

                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(
                                        "_________________________________"));

                        documento.add(new Paragraph(
                                        "Assinatura do Contratante"));

                        documento.close();

                        return nomeArquivo;

                } catch (Exception e) {
                        throw new RuntimeException(
                                        "Erro ao gerar PDF.", e);
                }
        }

        public static String gerarProposta(PropostaAluguel proposta) {
                String nomeArquivo = "Proposta_" + proposta.getId() + ".pdf";

                try {

                        Document documento = new Document();

                        PdfWriter.getInstance(
                                        documento,
                                        new FileOutputStream(nomeArquivo));
                        documento.open();

                        Font titulo = FontFactory.getFont(
                                        FontFactory.HELVETICA_BOLD,
                                        18);

                        Font texto = FontFactory.getFont(
                                        FontFactory.HELVETICA,
                                        12);

                        Paragraph cabecalho = new Paragraph(
                                        "PROPOSTA DE ALUGUEL DO TEATRO",
                                        titulo);

                        cabecalho.setAlignment(Element.ALIGN_CENTER);
                        documento.add(cabecalho);

                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(
                                        "ID da proposta: "
                                                        + proposta.getId(),
                                        texto));

                        documento.add(new Paragraph(
                                        "Contratante: "
                                                        + proposta.getContratante().getNome(),
                                        texto));

                        documento.add(new Paragraph(
                                        "Peça: "
                                                        + proposta.getNomePeca(),
                                        texto));

                        documento.add(new Paragraph(
                                        "Período: "
                                                        + proposta.getDataInicio()
                                                        + " até "
                                                        + proposta.getDataFim(),
                                        texto));

                        documento.add(new Paragraph(
                                        "Horário: "
                                                        + proposta.getHorarioInicio()
                                                        + " às "
                                                        + proposta.getHorarioFim(),
                                        texto));

                        documento.add(new Paragraph(
                                        "Valor do aluguel: R$ "
                                                        + proposta.getValorAluguel(),
                                        texto));

                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(
                                        "As partes concordam com os termos estabelecidos neste contrato."));

                        documento.add(new Paragraph(" "));
                        Paragraph termos = new Paragraph(
                                        "- O locatário está ciente de que o primeiro aluguel será pago no ato da assinatura do contrato de locação, e os\n"
                                                        +
                                                        "seguintes serão pagos nos meses vencidos, conforme legislação vigente, acrescido do IPTU e condomínio (em caso de\n"
                                                        +
                                                        "conjunto comercial e apartamento).\n" +
                                                        "- O proprietário(a)/locador(a), esta ciente de que a comissão será paga no ato da assinatura do contrato de locação.\n"
                                                        +
                                                        "- Aceita a proposta, a elaboração do contrato e efetiva realização da locação estão condicionadas a apresentação de\n"
                                                        +
                                                        "documentos e informações do locatário(s) exigido conforme relação fornecida.\n"
                                                        +
                                                        "- Os honorários de locação pelos serviços prestados de intermediação, correspondente a 100% (cem por cento) do\n"
                                                        +
                                                        "valor proposto de locação mês, sem qualquer desconto, bonificação ou carência, que será pago pelo(s) proprietário(s)\n"
                                                        +
                                                        "a Berti, no ato da assinatura do contrato.\n" +
                                                        "- Após a aceitação a presente proposta, caso haja arrependimento ou desistência pelo locatário ou locador, ou mesmo\n"
                                                        +
                                                        "se concluir intermédio de outro corretor, empresa imobiliária ou diretamente com o proprietário, a Berti terá o direito\n"
                                                        +
                                                        "aos honorários correspondentes a 100% (cem por cento) do valor do aluguel mensal nominal, que serão pagos por\n"
                                                        +
                                                        "quem der causa.\n" +
                                                        "- Os honorários de intermediação não serão devidos salvo em caso de impossibilidade de conclusão da locação por\n"
                                                        +
                                                        "estar os documentos inaptos do proponente locatário ou do imóvel.\n");
                        termos.setAlignment(Element.ALIGN_JUSTIFIED);
                        documento.add(termos);
                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph(
                                        "_________________________________"));

                        documento.add(new Paragraph(
                                        "Assinatura do Contratante"));

                        documento.close();

                        return nomeArquivo;

                } catch (Exception e) {
                        throw new RuntimeException(
                                        "Erro ao gerar PDF.", e);
                }
        }

        public static String gerarIngresso(Ingresso ingresso) {
                String nomeArquivo = "ingresso_" + ingresso.getId() + ".pdf";

                try {

                        Document documento = new Document();

                        PdfWriter.getInstance(documento, new FileOutputStream(nomeArquivo));
                        documento.open();

                        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                        Font texto = FontFactory.getFont(FontFactory.HELVETICA, 12);
                        Paragraph cabecalho = new Paragraph("INGRESSO", titulo);

                        cabecalho.setAlignment(Element.ALIGN_CENTER);
                        documento.add(cabecalho);

                        documento.add(new Paragraph(" "));
                        documento.add(new Paragraph("ID do ingresso: " + ingresso.getId(), texto));
                        documento.add(new Paragraph("Peça: " + ingresso.getContrato().getNomePeca(), texto));
                        documento.add(new Paragraph(
                                        "Horário do espetáculo: " + ingresso.getContrato().getHorarioInicio()
                                                        + " ás " + ingresso.getContrato().getHorarioFim(),
                                        texto));
                        documento.close();

                        return nomeArquivo;

                } catch (Exception e) {
                        throw new RuntimeException(
                                        "Erro ao gerar PDF.", e);
                }
        }

        public static String gerarRelatorioEspectadores(String nomePeca, String infoSessao, List<Ingresso> ingressos) {
                String sanitized = (nomePeca != null ? nomePeca.replaceAll("[^a-zA-Z0-9.-]", "_") : "espetaculo");
                String nomeArquivo = "relatorio_espectadores_" + sanitized + "_" + System.currentTimeMillis() + ".pdf";

                try {
                        Document doc = new Document(PageSize.A4);
                        PdfWriter.getInstance(doc, new FileOutputStream(nomeArquivo));
                        doc.open();

                        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                        Font fontSub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                        Font fontTexto = FontFactory.getFont(FontFactory.HELVETICA, 10);
                        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);

                        Paragraph cabecalho = new Paragraph("RELATÓRIO DE ESPECTADORES / LISTA DE PRESENÇA",
                                        fontTitulo);
                        cabecalho.setAlignment(Element.ALIGN_CENTER);
                        doc.add(cabecalho);

                        doc.add(new Paragraph(" "));
                        doc.add(new Paragraph("Espetáculo: " + (nomePeca != null ? nomePeca : "Todos"), fontSub));
                        if (infoSessao != null && !infoSessao.isBlank()) {
                                doc.add(new Paragraph("Sessão / Período: " + infoSessao, fontSub));
                        }
                        doc.add(new Paragraph("Data de Emissão: "
                                        + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                        fontTexto));
                        doc.add(new Paragraph("Total de Ingressos / Espectadores: "
                                        + (ingressos != null ? ingressos.size() : 0), fontSub));
                        doc.add(new Paragraph(" "));

                        PdfPTable tabela = new PdfPTable(6);
                        tabela.setWidthPercentage(100);
                        tabela.setWidths(new float[] { 1.2f, 3.0f, 2.5f, 1.3f, 1.8f, 1.4f });

                        String[] headers = { "Ingresso", "Nome do Cliente", "E-mail", "Assento", "Setor",
                                        "Valor (R$)" };
                        for (String h : headers) {
                                PdfPCell cell = new PdfPCell(new Phrase(h, fontHeader));
                                cell.setBackgroundColor(new BaseColor(44, 62, 80));
                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                cell.setPadding(6);
                                tabela.addCell(cell);
                        }

                        if (ingressos != null && !ingressos.isEmpty()) {
                                for (Ingresso i : ingressos) {
                                        String cod = i.getCodigo() != null ? (i.getCodigo().length() > 8
                                                        ? i.getCodigo().substring(0, 8) + "..."
                                                        : i.getCodigo()) : String.valueOf(i.getId());
                                        String nomeCli = i.getCliente() != null ? i.getCliente().getNome() : "N/A";
                                        String emailCli = i.getCliente() != null ? i.getCliente().getEmail() : "N/A";
                                        String codAssento = i.getAssento() != null ? i.getAssento().getCodigo() : "S/A";
                                        String setor = i.getSetor() != null ? i.getSetor().name() : "N/A";
                                        String valor = String.format("%.2f", i.getValor());

                                        tabela.addCell(new PdfPCell(new Phrase(cod, fontTexto)));
                                        tabela.addCell(new PdfPCell(new Phrase(nomeCli, fontTexto)));
                                        tabela.addCell(new PdfPCell(new Phrase(emailCli, fontTexto)));
                                        tabela.addCell(new PdfPCell(new Phrase(codAssento, fontTexto)));
                                        tabela.addCell(new PdfPCell(new Phrase(setor, fontTexto)));
                                        tabela.addCell(new PdfPCell(new Phrase(valor, fontTexto)));
                                }
                        }

                        doc.add(tabela);
                        doc.close();

                        return new File(nomeArquivo).getAbsolutePath();
                } catch (Exception e) {
                        throw new RuntimeException("Erro ao gerar Relatório de Espectadores: " + e.getMessage(), e);
                }
        }

        public static String gerarConsolidacaoFinanceira(Contrato contrato, long ingressosVendidos,
                        double faturamentoBilheteria) {
                String sanitized = (contrato != null && contrato.getNomePeca() != null
                                ? contrato.getNomePeca().replaceAll("[^a-zA-Z0-9.-]", "_")
                                : "consolidacao");
                String nomeArquivo = "consolidacao_financeira_" + sanitized + "_" + System.currentTimeMillis() + ".pdf";

                try {
                        Document doc = new Document(PageSize.A4);
                        PdfWriter.getInstance(doc, new FileOutputStream(nomeArquivo));
                        doc.open();

                        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                        Font fontSub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                        Font fontTexto = FontFactory.getFont(FontFactory.HELVETICA, 11);

                        Paragraph cabecalho = new Paragraph("CONSOLIDAÇÃO FINANCEIRA POR CONTRATO", fontTitulo);
                        cabecalho.setAlignment(Element.ALIGN_CENTER);
                        doc.add(cabecalho);
                        doc.add(new Paragraph(" "));

                        if (contrato != null) {
                                doc.add(new Paragraph("Contrato ID: " + contrato.getId(), fontSub));
                                doc.add(new Paragraph("Espetáculo: " + contrato.getNomePeca(), fontSub));
                                doc.add(new Paragraph("Contratante: " + contrato.getContratante(), fontTexto));
                                doc.add(new Paragraph(
                                                "Período: " + contrato.getDataInicio() + " a " + contrato.getDataFim(),
                                                fontTexto));
                                doc.add(new Paragraph("Horários: " + contrato.getHorarioInicio() + " às "
                                                + contrato.getHorarioFim(), fontTexto));
                                doc.add(new Paragraph(
                                                "Valor Base do Ingresso: R$ "
                                                                + String.format("%.2f", contrato.getValorIngresso()),
                                                fontTexto));
                                doc.add(new Paragraph(
                                                "Valor do Aluguel Cobrado pelo Teatro: R$ "
                                                                + String.format("%.2f", contrato.getValorAluguel()),
                                                fontSub));
                        }

                        doc.add(new Paragraph(" "));
                        doc.add(new Paragraph("BALANÇO DA BILHETERIA:", fontSub));
                        doc.add(new Paragraph("Total de Ingressos Vendidos: " + ingressosVendidos, fontTexto));
                        doc.add(new Paragraph("Receita Bruta da Bilheteria: R$ "
                                        + String.format("%.2f", faturamentoBilheteria), fontSub));

                        double aluguel = contrato != null ? contrato.getValorAluguel() : 0.0;
                        double saldoLiquidoContratante = faturamentoBilheteria - aluguel;
                        doc.add(new Paragraph("Resultado Líquido do Contratante: R$ "
                                        + String.format("%.2f", saldoLiquidoContratante), fontSub));
                        doc.add(new Paragraph(" "));
                        doc.add(new Paragraph("Data de Apuração: "
                                        + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                        fontTexto));

                        doc.close();
                        return new File(nomeArquivo).getAbsolutePath();
                } catch (Exception e) {
                        throw new RuntimeException("Erro ao gerar Consolidação Financeira: " + e.getMessage(), e);
                }
        }

        public static String gerarRelatorioGeralTeatro(LocalDate inicio, LocalDate fim, long totalIngressos,
                        double faturamentoTotal, double precoMedio, List<Contrato> contratos) {
                String nomeArquivo = "relatorio_geral_teatro_" + System.currentTimeMillis() + ".pdf";

                try {
                        Document doc = new Document(PageSize.A4);
                        PdfWriter.getInstance(doc, new FileOutputStream(nomeArquivo));
                        doc.open();

                        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                        Font fontSub = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                        Font fontTexto = FontFactory.getFont(FontFactory.HELVETICA, 11);

                        Paragraph cabecalho = new Paragraph("RELATÓRIO GERAL DE GESTÃO DO TEATRO", fontTitulo);
                        cabecalho.setAlignment(Element.ALIGN_CENTER);
                        doc.add(cabecalho);
                        doc.add(new Paragraph(" "));

                        if (inicio != null && fim != null) {
                                doc.add(new Paragraph(
                                                "Período Analisado: "
                                                                + inicio.format(DateTimeFormatter
                                                                                .ofPattern("dd/MM/yyyy"))
                                                                + " até "
                                                                + fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                                fontSub));
                        } else {
                                doc.add(new Paragraph("Período: Histórico Consolidado", fontSub));
                        }

                        doc.add(new Paragraph(" "));
                        doc.add(new Paragraph("INDICADORES CONSOLIDADOS (CONSULTAS JPQL AGREGADORAS):", fontSub));
                        doc.add(new Paragraph("• Total de Ingressos Comercializados (COUNT): " + totalIngressos,
                                        fontTexto));
                        doc.add(new Paragraph("• Faturamento Total Acumulado (SUM): R$ "
                                        + String.format("%.2f", faturamentoTotal), fontTexto));
                        doc.add(new Paragraph("• Preço Médio do Ingresso - Ticket Médio (AVG): R$ "
                                        + String.format("%.2f", precoMedio), fontTexto));
                        doc.add(new Paragraph("• Total de Contratos Ativos / Gerenciados: "
                                        + (contratos != null ? contratos.size() : 0), fontTexto));

                        doc.add(new Paragraph(" "));
                        if (contratos != null && !contratos.isEmpty()) {
                                doc.add(new Paragraph("CONTRATOS REGISTRADOS:", fontSub));
                                for (Contrato c : contratos) {
                                        doc.add(new Paragraph(String.format(
                                                        " - ID %d: %s | Contratante: %s | Status: %s | Aluguel: R$ %.2f",
                                                        c.getId(), c.getNomePeca(), c.getContratante(),
                                                        c.getStatusContrato(), c.getValorAluguel()), fontTexto));
                                }
                        }

                        doc.add(new Paragraph(" "));
                        doc.add(new Paragraph("Gerado em: "
                                        + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                        fontTexto));

                        doc.close();
                        return new File(nomeArquivo).getAbsolutePath();
                } catch (Exception e) {
                        throw new RuntimeException("Erro ao gerar Relatório Geral do Teatro: " + e.getMessage(), e);
                }
        }
}