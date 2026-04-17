import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Supabase } from '../../services/supabase';
import { Pedido } from '../../../@types/pedido';
import { Router } from '@angular/router';

@Component({
  selector: 'app-lista-pedidos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './lista-pedidos.html',
  styleUrls: ['./lista-pedidos.css'],
})
export class ListaPedidos implements OnInit {
  pedidos: Pedido[] = [];
  loading = true;
  errorMsg = '';

  filtro: 'todos' | 'pendentes' | 'concluidos' = 'pendentes';

  constructor(
    private supabaseService: Supabase,
    private router: Router,
  ) {}

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
    const num = numero.replace(/\D/g, '');
    if (num.length === 11) {
      return `(${num.slice(0, 2)}) ${num.slice(2, 7)}-${num.slice(7)}`;
    }
    if (num.length === 10) {
      return `(${num.slice(0, 2)}) ${num.slice(2, 6)}-${num.slice(6)}`;
    }
    return numero;
  }

  // ✅ Mensagem padrão de novo pedido
  mensagemNovoPedido(pedido: Pedido): string {
    const dataEntrega = new Date(pedido.data_entrega).toLocaleString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });

    return `Olá ${pedido.cliente ?? ''}, aqui é da Carimbos Ocian! 😄

Segue o resumo do seu pedido:

🧾 Pedido nº: ${pedido.id}
🔖 Modelo: ${pedido.tipo_modelo ?? 'N/A'}

✏ Descrição:
${pedido.texto_logo}

${pedido.observacao ? `📝 Observação: ${pedido.observacao}` : ''}

💰 Forma de pagamento: ${pedido.tipo_pagamento}
💵 Valor total: R$ ${pedido.valor?.toFixed(2)}
📅 Entrega Prevista: ${dataEntrega.replace(',', ' às')}

*RETIRADA*:  DENTRO do CHAVEIRO OCIAN
Rua Remo Estéfane, 50 – Ocian
https://maps.app.goo.gl/3FzpDGgtgv9ipG1f9`;
  }

  // ✅ Mensagem de pedido concluído
  mensagemPedidoConcluido(): string {
    return `✅ Pedido finalizado! ✅

📦 Seu pedido está pronto para retirada/entrega.
Qualquer dúvida, estamos aqui!

Se puder, avalie a Carimbos Ocian no Google:
👉 https://g.page/r/CbOE8NI63dFnEBM/review

💙 Agradecemos pela confiança! 💙`;
  }

  // ✅ Envia mensagem no WhatsApp
  enviarWhatsApp(pedido: Pedido) {
    const numero = pedido.whatsapp.toString().replace(/\D/g, '');
    const mensagem = this.mensagemNovoPedido(pedido);
    const url = `https://wa.me/55${numero}?text=${encodeURIComponent(mensagem)}`;
    window.open(url, '_blank');
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
    const numero = pedido.whatsapp.toString().replace(/\D/g, '');
    const mensagem = this.mensagemPedidoConcluido();
    const url = `https://wa.me/55${numero}?text=${encodeURIComponent(mensagem)}`;
    window.open(url, '_blank');
  }

  novoPedido() {
    this.router.navigate(['/novo-pedido']); // Substitua pela rota correta
  }

  // ✅ Getter que retorna apenas os pedidos filtrados
  get pedidosFiltrados(): Pedido[] {
    if (this.filtro === 'pendentes') {
      return this.pedidos.filter((p) => !p.entregue);
    } else if (this.filtro === 'concluidos') {
      return this.pedidos.filter((p) => p.entregue);
    }
    return this.pedidos; // todos
  }

  // Função para alterar filtro
  setFiltro(novoFiltro: 'todos' | 'pendentes' | 'concluidos') {
    this.filtro = novoFiltro;
  }
}
