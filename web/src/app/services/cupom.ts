import { Injectable } from '@angular/core';
import type { jsPDF } from 'jspdf';
import { Pedido } from '../../@types/pedido';

@Injectable({
  providedIn: 'root',
})
export class CupomService {
  // Formata o número de WhatsApp
  formatarWhatsapp(numero?: number | string | null): string {
    if (!numero) return '';
    const num = numero.toString().replace(/\D/g, '');
    if (num.length === 11) {
      return `(${num.slice(0, 2)}) ${num.slice(2, 7)}-${num.slice(7)}`;
    }
    if (num.length === 10) {
      return `(${num.slice(0, 2)}) ${num.slice(2, 6)}-${num.slice(6)}`;
    }
    return num;
  }

  // Formata data e hora
  formatarData(dataIso?: string | null): string {
    if (!dataIso) return 'N/A';
    const d = new Date(dataIso);
    if (isNaN(d.getTime())) return dataIso;
    return d.toLocaleString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  // Sanitiza texto para fontes padrão do jsPDF (WinAnsiEncoding)
  private sanitizarTexto(t?: string | number | null): string {
    if (t == null) return '';
    return String(t)
      .replace(/[\u2010-\u2015]/g, '-') // travessões unicode
      .replace(/[\u2018\u2019]/g, "'") // aspas simples curvas
      .replace(/[\u201C\u201D]/g, '"') // aspas duplas curvas
      .replace(/[\uD800-\uDBFF][\uDC00-\uDFFF]/g, '') // remove emojis incompatíveis com fontes básicas
      .replace(/[^\x00-\xFF]/g, ''); // mantém apenas caracteres Latin-1 (português com acentos)
  }

  // Constrói o documento PDF com tamanho de cupom térmico e cálculo dinâmico otimizado
  async gerarPdf(pedido: Pedido): Promise<jsPDF> {
    const { jsPDF } = await import('jspdf');

    // -------------------------------------------------------------
    // Dimensões do Cupom Térmico (limite de 70mm de largura)
    // -------------------------------------------------------------
    const larguraPapel = 70; // Limite de 70mm de largura conforme solicitado
    const margem = 4; // Margem lateral de 4mm de cada lado
    const larguraUtil = larguraPapel - (margem * 2); // 62mm de largura útil para o conteúdo
    const centro = larguraPapel / 2; // 35mm (ponto central)

    // Margem superior para que a cabeça de impressão não corte o cabeçalho
    const margemTop = 10; // 10mm de margem no topo

    // Margem inferior de respiro e avanço para que a guilhotina da impressora térmica não corte nenhuma informação
    const margemCorte = 18; // 18mm de avanço após o término das informações

    // 1. Pré-calcula a altura necessária para textos multi-linha (fontes maiores e em negrito)
    const docCalc = new jsPDF({ unit: 'mm', format: [larguraPapel, 800] });
    docCalc.setFont('courier', 'bold');
    docCalc.setFontSize(9);

    const linhasTexto = docCalc.splitTextToSize(
      this.sanitizarTexto(pedido.texto_logo || 'Sem descrição'),
      larguraUtil - 4,
    );
    const alturaCaixaTexto = Math.max(linhasTexto.length * 4.0 + 3.5, 9);

    let linhasObs: string[] = [];
    let alturaCaixaObs = 0;
    if (pedido.observacao) {
      linhasObs = docCalc.splitTextToSize(this.sanitizarTexto(pedido.observacao), larguraUtil - 4);
      alturaCaixaObs = Math.max(linhasObs.length * 4.0 + 3.5, 8);
    }

    // 2. Altura real baseada na soma exata dos blocos
    let alturaReal = margemTop;
    alturaReal += 4.5; // Cabeçalho: Título da loja
    alturaReal += 4.5; // Cabeçalho: Endereço da loja
    alturaReal += 18 + 3.5; // Box do número do pedido + espaçamento
    alturaReal += 3.5; // Divisor
    alturaReal += 4.2 * 2; // Datas (Pedido e Entrega)
    alturaReal += 3.5; // Divisor
    alturaReal += 4.2; // Modelo
    alturaReal += 3.5; // Divisor
    alturaReal += 2.5 + alturaCaixaTexto + 3.5; // Descrição / Texto / Logo

    if (pedido.observacao) {
      alturaReal += 2.5 + alturaCaixaObs + 3.5; // Observação
    }

    alturaReal += 3.5; // Divisor
    alturaReal += 4.2; // Pagamento
    alturaReal += 5.5; // Total
    alturaReal += 3.5; // Divisor
    alturaReal += 3.5; // Rodapé linha 1
    alturaReal += 3.5; // Rodapé linha 2
    alturaReal += 4 + 3.5; // Espaçamento e linha divisória final
    alturaReal += margemCorte; // Margem de corte da impressora térmica (avanço do papel)

    // 3. Cria o documento PDF final com dimensões exatas calculadas
    const doc = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: [larguraPapel, alturaReal],
    });

    let y = margemTop;

    // Cabeçalho da loja (fontes levemente maiores e em negrito para melhor nitidez)
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(13);
    doc.text('Carimbos Ocian', centro, y, { align: 'center' });
    y += 4.5;

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8.5);
    doc.text('Rua Remo Estefane, 50 - Ocian', centro, y, { align: 'center' });
    y += 4.5;

    // Box do Número do Pedido
    const boxY = y;
    const boxH = 18;
    doc.setDrawColor(0, 0, 0);
    doc.setLineWidth(0.5);
    doc.roundedRect(margem, boxY, larguraUtil, boxH, 1.5, 1.5);

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9);
    doc.text('PEDIDO', centro, boxY + 4.5, { align: 'center' });

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(22);
    doc.text('' + pedido.id, centro, boxY + 14.5, { align: 'center' });
    y += boxH + 3.5;

    const divisor = () => {
      doc.setDrawColor(0, 0, 0);
      doc.setLineWidth(0.2);
      doc.setLineDashPattern([1.2, 0.8], 0);
      doc.line(margem, y, margem + larguraUtil, y);
      y += 3.5;
    };

    // Linha dupla com rótulo e valor - ambos em negrito e tamanho maior para visualização rápida
    const linhaDupla = (rotulo: string, valor: string) => {
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(8.5);
      doc.text(rotulo, margem, y);
      doc.text(this.sanitizarTexto(valor), margem + larguraUtil, y, { align: 'right' });
      y += 4.2;
    };

    divisor();

    // Datas
    const dataPedido = this.formatarData(pedido.data_pedido);
    const dataEntrega = this.formatarData(pedido.data_entrega).replace(',', ' às');
    linhaDupla('Data Pedido:', dataPedido);
    linhaDupla('Entrega Prevista:', dataEntrega);

    divisor();

    // Dados do Cliente / Modelo
    linhaDupla('Modelo:', pedido.tipo_modelo || 'N/A');

    divisor();

    // Descrição / Texto / Logo (caixa com texto em negrito para facilitar a produção)
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8.5);
    doc.text('Descrição / Texto / Logo:', margem, y);
    y += 2.5;

    doc.setDrawColor(180, 180, 180);
    doc.setLineWidth(0.15);
    doc.setLineDashPattern([], 0);
    doc.rect(margem, y, larguraUtil, alturaCaixaTexto);

    doc.setFont('courier', 'bold');
    doc.setFontSize(9);
    doc.text(linhasTexto, margem + 2, y + 3.5);
    y += alturaCaixaTexto + 3.5;

    // Observação (se houver)
    if (pedido.observacao) {
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(8.5);
      doc.text('Observação:', margem, y);
      y += 2.5;

      doc.setDrawColor(180, 180, 180);
      doc.setLineWidth(0.15);
      doc.rect(margem, y, larguraUtil, alturaCaixaObs);

      doc.setFont('courier', 'bold');
      doc.setFontSize(9);
      doc.text(linhasObs, margem + 2, y + 3.5);
      y += alturaCaixaObs + 3.5;
    }

    divisor();

    // Pagamento e Total
    linhaDupla('Pagamento:', pedido.tipo_pagamento || 'N/A');

    const valorFormatado = Number(pedido.valor || 0).toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(9.5);
    doc.text('VALOR TOTAL:', margem, y);
    doc.setFontSize(11.5);
    doc.text('R$ ' + valorFormatado, margem + larguraUtil, y + 0.5, { align: 'right' });
    y += 5.5;

    divisor();

    // Rodapé de retirada
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8);
    doc.text('RETIRADA: DENTRO DO CHAVEIRO OCIAN', centro, y, { align: 'center' });
    y += 3.5;
    doc.setFont('helvetica', 'bold');
    doc.text('Rua Remo Estefane, 50 - Ocian', centro, y, { align: 'center' });

    // Linha divisória final para que a máquina entenda que ainda há texto e avance antes de cortar
    y += 4;
    divisor();

    return doc;
  }

  // Gera o PDF e abre em uma aba independente sem travar a aplicação principal
  async imprimir(pedido: Pedido): Promise<void> {
    const doc = await this.gerarPdf(pedido);

    const pdfBlob = doc.output('blob');
    const pdfUrl = URL.createObjectURL(pdfBlob);

    // Abre o PDF em aba independente para evitar bloqueio modal na aplicação
    const novaAba = window.open(pdfUrl, '_blank');
    if (!novaAba) {
      // Caso popup blocker esteja ativo, baixa o PDF diretamente
      doc.save(`cupom-pedido-${pedido.id}.pdf`);
    }
  }

  // Download direto do arquivo PDF do cupom
  async baixarPdf(pedido: Pedido): Promise<void> {
    const doc = await this.gerarPdf(pedido);
    doc.save(`cupom-pedido-${pedido.id}.pdf`);
  }
}
