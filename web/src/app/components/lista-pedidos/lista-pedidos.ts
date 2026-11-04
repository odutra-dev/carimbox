import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Supabase } from '../../services/supabase';
import { CupomService } from '../../services/cupom';
import { Pedido } from '../../../@types/pedido';
import { Router } from '@angular/router';
import { PhoneUtils } from '../../utils/phone.utils';

@Component({
  selector: 'app-lista-pedidos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lista-pedidos.html',
  styleUrls: ['./lista-pedidos.css'],
})
export class ListaPedidos implements OnInit {
  pedidos: Pedido[] = [];
  loading = true;
  errorMsg = '';
  termoBusca = '';

  filtro: 'todos' | 'pendentes' | 'concluidos' = 'pendentes';

  constructor(
    private supabaseService: Supabase,
    private router: Router,
    private cupomService: CupomService,
  ) {}

  // ✅ Dispara impressão do cupom do pedido
  imprimirCupom(pedido: Pedido, event?: MouseEvent) {
    if (event) {
      event.stopPropagation();
    }
    this.cupomService.imprimir(pedido);
  }

  async ngOnInit() {
    try {
      this.pedidos = await this.supabaseService.getPedidos();
    } catch (err) {
      console.error(err);
      this.errorMsg = 'Erro ao carregar pedidos.';
    } finally {
      this.loading = false;
    }
  }

  // ✅ Abre a página de detalhes do pedido
  verDetalhes(id: number) {
    this.router.navigate(['/ver-pedido', id]);
  }

  // ✅ Formata número de WhatsApp
  formatarWhatsapp(numero: string): string {
    return PhoneUtils.formatPhoneDisplay(numero);
  }

  // ✅ Mensagem padrão de novo pedido (layout padronizado com mobile)
  mensagemNovoPedido(pedido: Pedido): string {
    const dataEntregaObj = new Date(pedido.data_entrega);
    const dataFormatada = dataEntregaObj.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
    const horaFormatada = dataEntregaObj.toLocaleTimeString('pt-BR', {
      hour: '2-digit',
      minute: '2-digit',
    });
    const formattedDataEntrega = `${dataFormatada} às ${horaFormatada}`;

    const saudacao = pedido.cliente?.trim()
      ? `Olá ${pedido.cliente.trim()}, aqui é da Carimbos Ocian! 😄`
      : `Olá, aqui é da Carimbos Ocian! 😄`;

    const modeloParte = pedido.tipo_modelo?.trim()
      ? `🔖 Modelo: ${pedido.tipo_modelo.trim()}\n\n`
      : '';
    const observacaoParte = pedido.observacao?.trim()
      ? `\n\n📝 Observação: ${pedido.observacao.trim()}`
      : '';
    const formattedValor = (pedido.valor ?? 0).toFixed(2).replace('.', ',');

    return `${saudacao}

Segue o resumo do seu pedido:

━━━━━━━━━━━━━━━━━
📦 Nº DO PEDIDO
     *Nº: ${pedido.id ?? 0}*
━━━━━━━━━━━━━━━━━

${modeloParte}✏ Descrição:

${pedido.texto_logo}${observacaoParte}

💰 Forma de pagamento: ${pedido.tipo_pagamento}
💵 Valor total: R$ ${formattedValor}

━━━━━━━━━━━━━━━━━
📦 RETIRADA A PARTIR DE
📅 ${formattedDataEntrega}
━━━━━━━━━━━━━━━━━

📍 *RETIRADA: DENTRO do CHAVEIRO OCIAN*
Rua Remo Estéfane, 50 – Ocian`;
  }

  // ✅ Mensagem de pedido concluído (layout padronizado com mobile)
  mensagemPedidoConcluido(pedido: Pedido): string {
    return `✅ Seu pedido já foi finalizado e está disponível para retirada! ✅

━━━━━━━━━━━━━━━━━━━━━━━━
📦 Retirada com o nº do pedido: ${pedido.id ?? 0}
━━━━━━━━━━━━━━━━━━━━━━━━

📍 *RETIRADA: DENTRO do CHAVEIRO OCIAN*
Rua Remo Estéfane, 50 – Ocian
https://maps.app.goo.gl/3FzpDGgtgv9ipG1f9

💙 Agradecemos pela confiança! 💙`;
  }

  // ✅ Envia mensagem no WhatsApp
  enviarWhatsApp(pedido: Pedido) {
    const mensagem = this.mensagemNovoPedido(pedido);
    const url = PhoneUtils.buildWhatsAppUrl(pedido.whatsapp, mensagem);
    if (url) {
      window.open(url, '_blank');
    }
  }

  // ✅ Marca pedido como entregue no Supabase
  async marcarComoEntregue(pedido: Pedido) {
    if (pedido.entregue) {
      alert('Este pedido já está concluído!');
      return;
    }

    const confirmar = confirm(`Deseja marcar o pedido #${pedido.id} como concluído?`);
    if (!confirmar) return;

    try {
      const atualizado = await this.supabaseService.updatePedido(pedido.id, { entregue: true });
      if (atualizado) {
        pedido.entregue = true;
        alert('Pedido concluído com sucesso!');
        this.enviarMensagemConcluido(pedido);
      }
    } catch (err) {
      console.error(err);
      alert('Erro ao atualizar o pedido.');
    }
  }

  // ✅ Exclui pedido no Supabase
  async excluirPedido(pedido: Pedido, event: MouseEvent) {
    event.stopPropagation();
    if (pedido.entregue) {
      alert('Este pedido já está concluído!');
      return;
    }

    const confirmar = confirm(`Deseja excluir o pedido #${pedido.id}?`);
    if (!confirmar) return;

    try {
      const atualizado = await this.supabaseService.deletePedido(pedido.id);
      alert('Pedido excluído com sucesso!');
      this.pedidos = this.pedidos.filter((p) => p.id !== pedido.id);
    } catch (err) {
      console.error(err);
      alert('Erro ao excluir o pedido.');
    }
  }

  // ✅ Envia mensagem de conclusão
  enviarMensagemConcluido(pedido: Pedido) {
    const mensagem = this.mensagemPedidoConcluido(pedido);
    const url = PhoneUtils.buildWhatsAppUrl(pedido.whatsapp, mensagem);
    if (url) {
      window.open(url, '_blank');
    }
  }

  novoPedido() {
    this.router.navigate(['/novo-pedido']); // Substitua pela rota correta
  }

  // ✅ Limpa o termo de busca atual
  limparBusca() {
    this.termoBusca = '';
  }

  // ✅ Getter que retorna os pedidos filtrados por status e busca inteligente
  get pedidosFiltrados(): Pedido[] {
    let lista = this.pedidos;

    if (this.filtro === 'pendentes') {
      lista = lista.filter((p) => !p.entregue);
    } else if (this.filtro === 'concluidos') {
      lista = lista.filter((p) => p.entregue);
    }

    const busca = this.termoBusca?.trim().toLowerCase();
    if (!busca) {
      return lista;
    }

    const digits = busca.replace(/\D/g, '');

    return lista.filter((p) => {
      // 1. Busca por ID
      if (p.id?.toString().toLowerCase().includes(busca)) {
        return true;
      }
      if (digits && p.id?.toString() === digits) {
        return true;
      }

      // 2. Busca por Texto (Cliente, Descrição, Modelo, Observação)
      if (p.cliente?.toLowerCase().includes(busca)) return true;
      if (p.texto_logo?.toLowerCase().includes(busca)) return true;
      if (p.tipo_modelo?.toLowerCase().includes(busca)) return true;
      if (p.observacao?.toLowerCase().includes(busca)) return true;

      // 3. Busca Inteligente por WhatsApp
      if (p.whatsapp != null) {
        const rawPhone = p.whatsapp.toString();
        const formatted = PhoneUtils.formatPhoneDisplay(rawPhone).toLowerCase();
        const target = PhoneUtils.getWhatsAppTargetPhone(rawPhone);

        // Correspondência no formato exibido ou nos dígitos brutos
        if (formatted.includes(busca) || rawPhone.includes(busca)) {
          return true;
        }

        // Correspondência baseada em dígitos
        if (digits.length >= 2) {
          if (rawPhone.includes(digits) || target.includes(digits)) {
            return true;
          }
          // Se digitou 8 ou 9 dígitos (sem DDD), tenta com o DDD 13 padrão ou final do número
          if (digits.length === 8 || digits.length === 9) {
            if (rawPhone.endsWith(digits)) return true;
            if (rawPhone === `13${digits}` || rawPhone === `5513${digits}`) return true;
          }
        }
      }

      return false;
    });
  }

  // Função para alterar filtro
  setFiltro(novoFiltro: 'todos' | 'pendentes' | 'concluidos') {
    this.filtro = novoFiltro;
  }
}
