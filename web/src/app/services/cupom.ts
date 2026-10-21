import { Injectable } from '@angular/core';
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

  // Gera o HTML do cupom térmico/fiscal para impressão
  gerarHtmlCupom(pedido: Pedido): string {
    const dataPedido = this.formatarData(pedido.data_pedido);
    const dataEntrega = this.formatarData(pedido.data_entrega).replace(',', ' às');
    const whatsapp = this.formatarWhatsapp(pedido.whatsapp);
    const valor = Number(pedido.valor || 0).toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

    const escapeHtml = (text: string = '') =>
      text
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');

    return `<!DOCTYPE html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Cupom - Pedido #${pedido.id}</title>
  <style>
    @page {
      size: 80mm auto;
      margin: 3mm;
    }
    * {
      box-sizing: border-box;
      margin: 0;
      padding: 0;
    }
    body {
      font-family: 'Courier New', Courier, monospace, -apple-system, sans-serif;
      font-size: 13px;
      line-height: 1.35;
      color: #000;
      background: #f1f5f9;
      padding: 16px;
      display: flex;
      flex-direction: column;
      align-items: center;
    }
    .toolbar {
      width: 100%;
      max-width: 340px;
      display: flex;
      gap: 10px;
      margin-bottom: 14px;
    }
    .toolbar button {
      flex: 1;
      padding: 10px;
      font-size: 14px;
      font-weight: bold;
      border: none;
      border-radius: 6px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
    }
    .btn-print {
      background-color: #2563eb;
      color: #ffffff;
    }
    .btn-print:hover {
      background-color: #1d4ed8;
    }
    .btn-close {
      background-color: #e2e8f0;
      color: #334155;
    }
    .btn-close:hover {
      background-color: #cbd5e1;
    }
    .cupom-card {
      width: 100%;
      max-width: 340px;
      background: #fff;
      padding: 18px 14px;
      border: 1px solid #cbd5e1;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      border-radius: 4px;
    }
    .header {
      text-align: center;
      margin-bottom: 6px;
    }
    .loja-nome {
      font-size: 22px;
      font-weight: 900;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      color: #000;
    }
    .loja-endereco {
      font-size: 13px;
      margin-top: 3px;
      color: #111;
      font-weight: 600;
    }
    .bloco-numero {
      text-align: center;
      padding: 8px 0;
      border: 3px solid #000;
      border-radius: 8px;
      margin: 10px 0;
      background-color: #fafafa;
    }
    .rotulo-pedido {
      font-size: 13px;
      font-weight: 800;
      letter-spacing: 2px;
      color: #222;
      text-transform: uppercase;
    }
    .numero-gigante {
      font-size: 52px;
      font-weight: 900;
      line-height: 1.05;
      letter-spacing: -1px;
      color: #000;
    }
    .divisor {
      border: none;
      border-top: 2px dashed #000;
      margin: 10px 0;
    }
    .divisor-fino {
      border: none;
      border-top: 1px dashed #777;
      margin: 8px 0;
    }
    .linha {
      margin-bottom: 5px;
      font-size: 13px;
      display: flex;
      justify-content: space-between;
      align-items: baseline;
      gap: 8px;
      word-break: break-word;
    }
    .linha-coluna {
      margin-bottom: 5px;
      font-size: 13px;
      word-break: break-word;
    }
    .rotulo {
      font-weight: 700;
      color: #000;
    }
    .conteudo-caixa {
      white-space: pre-wrap;
      font-family: inherit;
      background: #f8fafc;
      border: 1px dashed #999;
      padding: 8px;
      margin-top: 4px;
      margin-bottom: 6px;
      border-radius: 4px;
      font-size: 13px;
      line-height: 1.4;
      color: #000;
    }
    .linha-total {
      display: flex;
      justify-content: space-between;
      align-items: baseline;
      font-size: 15px;
      font-weight: 700;
      margin-top: 6px;
      padding-top: 4px;
    }
    .valor-total {
      font-size: 20px;
      font-weight: 900;
      color: #000;
    }
    .rodape {
      text-align: center;
      font-size: 11px;
      color: #222;
      margin-top: 10px;
      line-height: 1.45;
    }
    .rodape-destaque {
      font-weight: 800;
      text-transform: uppercase;
    }
    @media print {
      body {
        background: #fff;
        padding: 0;
      }
      .toolbar {
        display: none !important;
      }
      .cupom-card {
        border: none;
        box-shadow: none;
        padding: 0;
        max-width: 100%;
        width: 100%;
      }
      .conteudo-caixa {
        background: transparent;
        border: 1px dashed #555;
      }
      .bloco-numero {
        background: transparent;
      }
    }
  </style>
</head>
<body>
  <div class="toolbar">
    <button class="btn-print" onclick="window.print()">🖨️ Imprimir / Salvar PDF</button>
    <button class="btn-close" onclick="window.close()">✕ Fechar</button>
  </div>

  <div class="cupom-card">
    <div class="header">
      <h1 class="loja-nome">Carimbos Ocian</h1>
      <p class="loja-endereco">Rua Remo Estéfane, 50 – Ocian</p>
    </div>

    <div class="bloco-numero">
      <div class="rotulo-pedido">PEDIDO</div>
      <div class="numero-gigante">#${pedido.id}</div>
    </div>

    <hr class="divisor" />

    <div class="linha">
      <span class="rotulo">Data do Pedido:</span>
      <span>${dataPedido}</span>
    </div>

    <div class="linha">
      <span class="rotulo">Entrega Prevista:</span>
      <span>${dataEntrega}</span>
    </div>

    <hr class="divisor-fino" />

    <div class="linha-coluna">
      <span class="rotulo">Cliente:</span>
      <span>${escapeHtml(pedido.cliente || 'Não informado')}</span>
    </div>

    <div class="linha-coluna">
      <span class="rotulo">WhatsApp:</span>
      <span>${whatsapp || 'Não informado'}</span>
    </div>

    <div class="linha-coluna">
      <span class="rotulo">Modelo:</span>
      <span>${escapeHtml(pedido.tipo_modelo || 'N/A')}</span>
    </div>

    <hr class="divisor-fino" />

    <div class="linha-coluna">
      <span class="rotulo">Descrição / Texto / Logo:</span>
      <div class="conteudo-caixa">${escapeHtml(pedido.texto_logo || 'Sem descrição')}</div>
    </div>

    ${
      pedido.observacao
        ? `
    <div class="linha-coluna">
      <span class="rotulo">Observação:</span>
      <div class="conteudo-caixa">${escapeHtml(pedido.observacao)}</div>
    </div>
    `
        : ''
    }

    <hr class="divisor-fino" />

    <div class="linha">
      <span class="rotulo">Forma de Pagamento:</span>
      <span>${escapeHtml(pedido.tipo_pagamento || 'N/A')}</span>
    </div>

    <div class="linha-total">
      <span class="rotulo">VALOR TOTAL:</span>
      <span class="valor-total">R$ ${valor}</span>
    </div>

    <hr class="divisor" />

    <div class="rodape">
      <p class="rodape-destaque">RETIRADA: DENTRO DO CHAVEIRO OCIAN</p>
      <p>Rua Remo Estéfane, 50 – Ocian</p>
      <p style="margin-top: 6px;">Agradecemos pela preferência!</p>
    </div>
  </div>

  <script>
    window.addEventListener('load', () => {
      setTimeout(() => {
        window.print();
      }, 300);
    });
  </script>
</body>
</html>`;
  }

  // Dispara a impressão
  imprimir(pedido: Pedido): void {
    const conteudoHtml = this.gerarHtmlCupom(pedido);
    const printWindow = window.open('', '_blank', 'width=420,height=720');

    if (printWindow) {
      printWindow.document.open();
      printWindow.document.write(conteudoHtml);
      printWindow.document.close();
      printWindow.focus();
    } else {
      const iframe = document.createElement('iframe');
      iframe.style.position = 'fixed';
      iframe.style.right = '0';
      iframe.style.bottom = '0';
      iframe.style.width = '0';
      iframe.style.height = '0';
      iframe.style.border = '0';
      document.body.appendChild(iframe);

      const doc = iframe.contentWindow?.document;
      if (doc) {
        doc.open();
        doc.write(conteudoHtml);
        doc.close();
        iframe.contentWindow?.focus();
        setTimeout(() => {
          iframe.contentWindow?.print();
          setTimeout(() => {
            if (document.body.contains(iframe)) {
              document.body.removeChild(iframe);
            }
          }, 1500);
        }, 400);
      }
    }
  }
}
