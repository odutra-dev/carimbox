import { Component } from '@angular/core';
import { Pedido } from '../../../@types/pedido';
import { Supabase } from '../../services/supabase';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ver-detalhes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ver-detalhes.html',
  styleUrl: './ver-detalhes.css',
})
export class VerDetalhes {
  loading = true;
  errorMsg = '';
  pedido: Pedido = {} as Pedido;

  constructor(private supabaseService: Supabase, public router: Router) {}
  async ngOnInit() {
    try {
      const id = this.router.url.split('/').pop();

      this.pedido = await this.supabaseService.getPedidoById(Number(id));
    } catch (err) {
      console.error(err);
      this.errorMsg = 'Erro ao carregar pedidos.';
    } finally {
      this.loading = false;
    }
  }

  formatarWhatsapp(numero: string) {
    const num = numero.replace(/\D/g, '');
    if (num.length === 11) return `(${num.slice(0, 2)}) ${num.slice(2, 7)}-${num.slice(7)}`;
    if (num.length === 10) return `(${num.slice(0, 2)}) ${num.slice(2, 6)}-${num.slice(6)}`;
    return numero;
  }

  copiarTexto(): void {
    if (!this.pedido) return;
    const texto = `
Pedido #${this.pedido.id}
Cliente: ${this.pedido.cliente}
WhatsApp: ${this.pedido.whatsapp}
Modelo: ${this.pedido.tipo_modelo}
Texto/Logo: ${this.pedido.texto_logo}
Pagamento: ${this.pedido.tipo_pagamento}
Valor: R$ ${this.pedido.valor.toFixed(2)}
Data Pedido: ${new Date(this.pedido.data_pedido).toLocaleString('pt-BR')}
Entrega: ${new Date(this.pedido.data_entrega).toLocaleString('pt-BR')}
Observação: ${this.pedido.observacao || 'Nenhuma'}
    `.trim();

    navigator.clipboard.writeText(texto);
    alert('Dados copiados para a área de transferência.');
  }

  voltar() {
    this.router.navigate(['/']);
  }

  editarPedido() {
    if (this.pedido) {
      this.router.navigate(['/atualizar-pedido', this.pedido.id]);
    }
  }

  // ✅ Marca pedido como não entregue no Supabase
  async marcarComoNaoEntregue(pedido: Pedido) {
    if (!pedido.entregue) {
      alert('Este pedido não já está concluído!');
      return;
    }

    const confirmar = confirm(`Deseja desmarcar o pedido #${pedido.id} como concluído?`);
    if (!confirmar) return;

    try {
      const atualizado = await this.supabaseService.updatePedido(pedido.id, { entregue: false });
      if (atualizado) {
        pedido.entregue = true;
        alert('Pedido desmarcado como concluído com sucesso!');
      }
    } catch (err) {
      console.error(err);
      alert('Erro ao atualizar o pedido.');
    }
  }
}
