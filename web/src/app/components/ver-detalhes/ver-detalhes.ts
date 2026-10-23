import { Component, OnInit } from '@angular/core';
import { Pedido } from '../../../@types/pedido';
import { Supabase } from '../../services/supabase';
import { CupomService } from '../../services/cupom';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ver-detalhes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ver-detalhes.html',
  styleUrl: './ver-detalhes.css',
})
export class VerDetalhes implements OnInit {
  loading = true;
  errorMsg = '';
  pedido: Pedido = {} as Pedido;

  constructor(
    private supabaseService: Supabase,
    public router: Router,
    private cupomService: CupomService,
  ) {}

  async ngOnInit() {
    try {
      const id = this.router.url.split('/').pop();
      this.pedido = await this.supabaseService.getPedidoById(Number(id));
    } catch (err) {
      console.error(err);
      this.errorMsg = 'Erro ao carregar pedido.';
    } finally {
      this.loading = false;
    }
  }

  formatarWhatsapp(numero: string): string {
    if (!numero) return '';
    const num = numero.replace(/\D/g, '');
    if (num.length === 11) return `(${num.slice(0, 2)}) ${num.slice(2, 7)}-${num.slice(7)}`;
    if (num.length === 10) return `(${num.slice(0, 2)}) ${num.slice(2, 6)}-${num.slice(6)}`;
    return numero;
  }

  copiarTexto(): void {
    if (!this.pedido || !this.pedido.id) return;
    const p = this.pedido;

    const partes = [
      `Pedido #${p.id}`,
      p.cliente ? `Cliente: ${p.cliente}` : null,
      p.whatsapp ? `WhatsApp: ${this.formatarWhatsapp(p.whatsapp.toString())}` : null,
      p.tipo_modelo ? `Modelo: ${p.tipo_modelo}` : null,
      p.texto_logo ? `Texto/Logo: ${p.texto_logo}` : null,
      p.tipo_pagamento ? `Pagamento: ${p.tipo_pagamento}` : null,
      p.valor != null ? `Valor: R$ ${p.valor.toFixed(2)}` : null,
      p.data_pedido ? `Data Pedido: ${new Date(p.data_pedido).toLocaleString('pt-BR')}` : null,
      p.data_entrega ? `Entrega: ${new Date(p.data_entrega).toLocaleString('pt-BR')}` : null,
      p.observacao ? `Observação: ${p.observacao}` : null,
    ];

    const texto = partes.filter(Boolean).join('\n');

    navigator.clipboard.writeText(texto);
    alert('Dados copiados para a área de transferência.');
  }

  imprimirCupom(): void {
    if (this.pedido && this.pedido.id) {
      this.cupomService.imprimir(this.pedido);
    }
  }

  voltar() {
    this.router.navigate(['/']);
  }

  editarPedido() {
    if (this.pedido && this.pedido.id) {
      this.router.navigate(['/atualizar-pedido', this.pedido.id]);
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
      }
    } catch (err) {
      console.error(err);
      alert('Erro ao atualizar o pedido.');
    }
  }

  // ✅ Marca pedido como não entregue no Supabase
  async marcarComoNaoEntregue(pedido: Pedido) {
    if (!pedido.entregue) {
      alert('Este pedido não está concluído!');
      return;
    }

    const confirmar = confirm(`Deseja desmarcar o pedido #${pedido.id} como concluído?`);
    if (!confirmar) return;

    try {
      const atualizado = await this.supabaseService.updatePedido(pedido.id, { entregue: false });
      if (atualizado) {
        pedido.entregue = false;
        alert('Pedido desmarcado como concluído com sucesso!');
      }
    } catch (err) {
      console.error(err);
      alert('Erro ao atualizar o pedido.');
    }
  }
}
