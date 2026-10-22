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

    // AJUSTE 1: Largura ajustada para 80mm (Padrão da Tanca TP-650)
    const larguraPapel = 80;
    const margem = 4;
    const larguraUtil = larguraPapel - (margem * 2); // 72mm úteis
    const centro = larguraPapel / 2; // 40mm

    // 1. Pré-calcula a altura necessária para textos multi-linha
    const docCalc = new jsPDF({ unit: 'mm', format: [larguraPapel, 600] });
    docCalc.setFont('courier', 'normal');
    docCalc.setFontSize(8);

    const linhasTexto = docCalc.splitTextToSize(
      this.sanitizarTexto(pedido.texto_logo || 'Sem descrição'),
      larguraUtil - 4,
    );
    const alturaCaixaTexto = Math.max(linhasTexto.length * 3.5 + 3, 8);

    let linhasObs: string[] = [];
    let alturaCaixaObs = 0;
    if (pedido.observacao) {
      linhasObs = docCalc.splitTextToSize(this.sanitizarTexto(pedido.observacao), larguraUtil - 4);
      alturaCaixaObs = linhasObs.length * 3.5 + 6;
    }

    // Altura total estimada ajustada de forma compacta
    const alturaEstimada = Math.ceil(
      16 + 20 + 12 + 16 + 5 + alturaCaixaTexto + alturaCaixaObs + 14 + 16 + 10,
    );

    // AJUSTE 2: Remoção da altura mínima forçada (120mm). 
    // Agora a altura do PDF termina exatamente onde o conteúdo acaba (+ 5mm para o corte).
    const alturaFinal = alturaEstimada + 5;

    // 2. Cria o documento PDF final com dimensões exatas da bobina
    const doc = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: [larguraPapel, alturaFinal],
    });

    let y = 6;

    // Cabeçalho da loja
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(12);
    doc.text('Carimbos Ocian', centro, y, { align: 'center' });
    y += 4;

    doc.setFont('helvetica', 'normal');
    doc.setFontSize(7.5);
    doc.text('Rua Remo Estefane, 50 - Ocian', centro, y, { align: 'center' });
    y += 4;

    // Box do Número do Pedido
    const boxY = y;
    const boxH = 17;
    doc.setDrawColor(0, 0, 0);
    doc.setLineWidth(0.5);
    doc.roundedRect(margem, boxY, larguraUtil, boxH, 1.5, 1.5);

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8);
    doc.text('PEDIDO', centro, boxY + 4.5, { align: 'center' });

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(22);
    doc.text('#' + pedido.id, centro, boxY + 13.5, { align: 'center' });
    y += boxH + 3;

    const divisor = () => {
      doc.setDrawColor(0, 0, 0);
      doc.setLineWidth(0.2);
      doc.setLineDashPattern([1.2, 0.8], 0);
      doc.line(margem, y, margem + larguraUtil, y);
      y += 3;
    };

    const linhaDupla = (rotulo: string, valor: string) => {
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(7.5);
      doc.text(rotulo, margem, y);
      doc.setFont('helvetica', 'normal');
      doc.text(this.sanitizarTexto(valor), margem + larguraUtil, y, { align: 'right' });
      y += 3.5;
    };

    divisor();

    // Datas
    const dataPedido = this.formatarData(pedido.data_pedido);
    const dataEntrega = this.formatarData(pedido.data_entrega).replace(',', ' às');
    linhaDupla('Data Pedido:', dataPedido);
    linhaDupla('Entrega Prevista:', dataEntrega);

    divisor();

    // Dados do Cliente
    const whatsFormatado = this.formatarWhatsapp(pedido.whatsapp);
    //linhaDupla('Cliente:', pedido.cliente || 'Não informado');
    //linhaDupla('WhatsApp:', whatsFormatado || 'Não informado');
    linhaDupla('Modelo:', pedido.tipo_modelo || 'N/A');

    divisor();

    // Descrição / Texto / Logo
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7.5);
    doc.text('Descrição / Texto / Logo:', margem, y);
    y += 2.5;

    doc.setDrawColor(180, 180, 180);
    doc.setLineWidth(0.15);
    doc.setLineDashPattern([], 0);
    doc.rect(margem, y, larguraUtil, alturaCaixaTexto);

    doc.setFont('courier', 'normal');
    doc.setFontSize(8);
    doc.text(linhasTexto, margem + 2, y + 3);
    y += alturaCaixaTexto + 3;

    // Observação (se houver)
    if (pedido.observacao) {
      doc.setFont('helvetica', 'bold');
      doc.setFontSize(7.5);
      doc.text('Observação:', margem, y);
      y += 2.5;

      doc.setDrawColor(180, 180, 180);
      doc.setLineWidth(0.15);
      doc.rect(margem, y, larguraUtil, linhasObs.length * 3.5 + 4);

      doc.setFont('courier', 'normal');
      doc.setFontSize(8);
      doc.text(linhasObs, margem + 2, y + 3);
      y += linhasObs.length * 3.5 + 6;
    }

    divisor();

    // Pagamento e Total
    linhaDupla('Pagamento:', pedido.tipo_pagamento || 'N/A');

    const valorFormatado = Number(pedido.valor || 0).toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

    doc.setFont('helvetica', 'bold');
    doc.setFontSize(8.5);
    doc.text('VALOR TOTAL:', margem, y);
    doc.setFontSize(10.5);
    doc.text('R$ ' + valorFormatado, margem + larguraUtil, y + 1, { align: 'right' });
    y += 5;

    divisor();

    // Rodapé de retirada
    doc.setFont('helvetica', 'bold');
    doc.setFontSize(7);
    doc.text('RETIRADA: DENTRO DO CHAVEIRO OCIAN', centro, y, { align: 'center' });
    y += 3;
    doc.setFont('helvetica', 'normal');
    doc.text('Rua Remo Estefane, 50 - Ocian', centro, y, { align: 'center' });

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