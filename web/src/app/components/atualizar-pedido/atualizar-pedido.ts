import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Supabase } from '../../services/supabase';

@Component({
  selector: 'app-atualizar-pedido',
  templateUrl: './atualizar-pedido.html',
  styleUrls: ['./atualizar-pedido.css'],
  standalone: true,
  imports: [
    CommonModule, // necessário para *ngFor, *ngIf
    ReactiveFormsModule, // necessário para [formGroup]
  ],
})
export class AtualizarPedido implements OnInit {
  pedidoForm: FormGroup;
  loading = false;
  formaPagamentoOptions = ['Pix', 'Cartão de Crédito', 'Cartão de Débito', 'Dinheiro', 'A pagar'];
  labelOptions = ['Carimbo', 'Mdf/Acrílico', 'Outros'];
  pedidoId!: number;

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private supabase: Supabase
  ) {
    this.pedidoForm = this.fb.group({
      cliente: [''],
      whatsapp: ['', Validators.required],
      tipo_modelo: [''],
      texto_logo: ['', Validators.required],
      observacao: [''],
      tipo_pagamento: ['', Validators.required],
      valor: ['', Validators.required],
      data_entrega: [new Date(), Validators.required],
      entregue: [false],
      label: 'Carimbo',
    });
  }

  async ngOnInit() {
    // Captura o id do pedido a partir da rota
    this.pedidoId = Number(this.route.snapshot.paramMap.get('id'));

    if (this.pedidoId) {
      this.loading = true;
      try {
        const pedido = await this.supabase.getPedidoById(this.pedidoId);

        // Preenche o formulário com os dados do pedido
        this.pedidoForm.patchValue({
          cliente: pedido.cliente,
          whatsapp: pedido.whatsapp,
          tipo_modelo: pedido.tipo_modelo,
          texto_logo: pedido.texto_logo,
          observacao: pedido.observacao,
          tipo_pagamento: pedido.tipo_pagamento,
          valor: pedido.valor,
          data_entrega: pedido.data_entrega
            ? this.formatDate(new Date(pedido.data_entrega))
            : this.formatDate(new Date()),
          entregue: pedido.entregue,
          label: pedido.label,
        });
      } catch (error) {
        console.error('Erro ao carregar pedido:', error);
        alert('Não foi possível carregar os dados do pedido.');
        this.goBack();
      } finally {
        this.loading = false;
      }
    } else {
      alert('ID do pedido inválido.');
      this.goBack();
    }
  }

  goBack() {
    this.router.navigate(['/']);
  }

  async onSubmit() {
    if (this.pedidoForm.invalid) {
      alert('Preencha todos os campos obrigatórios!');
      return;
    }

    this.loading = true;

    try {
      const formValue = this.pedidoForm.value;

      await this.supabase.updatePedido(this.pedidoId, {
        cliente: formValue.cliente,
        whatsapp: Number(formValue.whatsapp),
        tipo_modelo: formValue.tipo_modelo,
        texto_logo: formValue.texto_logo,
        tipo_pagamento: formValue.tipo_pagamento,
        valor: Number(formValue.valor),
        observacao: formValue.observacao || null,
        entregue: formValue.entregue,
        data_entrega: formValue.data_entrega, // Enviar como string
        label: formValue.label,
      });

      alert('Pedido atualizado com sucesso!');
      this.router.navigate(['/']);
    } catch (err) {
      console.error(err);
      alert('Erro ao atualizar o pedido.');
    } finally {
      this.loading = false;
    }
  }

  // Função para formatar a data no formato 'YYYY-MM-DDTHH:mm'
  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }
}
