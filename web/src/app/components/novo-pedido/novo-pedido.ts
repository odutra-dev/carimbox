import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Supabase } from '../../services/supabase'; // serviço customizado para Supabase
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-novo-pedido',
  templateUrl: './novo-pedido.html',
  styleUrls: ['./novo-pedido.css'],
  standalone: true,
  imports: [
    CommonModule, // ← necessário para *ngFor, *ngIf
    ReactiveFormsModule, // ← necessário para [formGroup]],
  ],
})
export class NovoPedido {
  pedidoForm: FormGroup;
  loading = false;
  formaPagamentoOptions = ['Pix', 'Cartão de Crédito', 'Cartão de Débito', 'Dinheiro', 'A pagar'];
  labelOptions = ['Carimbo', 'Mdf/Acrílico', 'Outros'];

  constructor(private fb: FormBuilder, private router: Router, private supabase: Supabase) {
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
      label: ['Carimbo'],
    });
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

      const data = await this.supabase.createPedido({
        cliente: formValue.cliente,
        whatsapp: Number(formValue.whatsapp),
        tipo_modelo: formValue.tipo_modelo,
        texto_logo: formValue.texto_logo,
        tipo_pagamento: formValue.tipo_pagamento,
        valor: Number(formValue.valor),
        observacao: formValue.observacao || null,
        entregue: formValue.entregue,
        data_entrega: new Date(formValue.data_entrega).toISOString(),
        label: formValue.label,
      });

      alert('Pedido criado com sucesso!');

      this.pedidoForm.reset({
        cliente: '',
        whatsapp: '',
        tipo_modelo: '',
        texto_logo: '',
        tipo_pagamento: '',
        valor: '',
        observacao: '',
        entregue: false,
        data_entrega: new Date(),
        label: 'Carimbo',
      });

      this.router.navigate(['/']);
    } catch (err) {
      console.error(err);
      alert('Erro ao criar o pedido.');
    } finally {
      this.loading = false;
    }
  }
}
