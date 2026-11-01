import { Component } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { Supabase } from '../../services/supabase';
import { CommonModule } from '@angular/common';
import { PhoneWithDdiInput } from '../phone-with-ddi-input/phone-with-ddi-input';
import { CurrencyInput } from '../currency-input/currency-input';
import { PhoneUtils } from '../../utils/phone.utils';

@Component({
  selector: 'app-novo-pedido',
  templateUrl: './novo-pedido.html',
  styleUrls: ['./novo-pedido.css'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PhoneWithDdiInput,
    CurrencyInput,
  ],
})
export class NovoPedido {
  pedidoForm: FormGroup;
  loading = false;
  isSubmitted = false;
  selectedDdi = '55';

  formaPagamentoOptions = [
    'Pix',
    'Cartão de Crédito',
    'Cartão de Débito',
    'Dinheiro',
    'Link de pagamento',
    'A pagar',
  ];
  labelOptions = ['Carimbo', 'Mdf/Acrílico', 'Outros'];

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private supabase: Supabase
  ) {
    this.pedidoForm = this.fb.group({
      cliente: [''],
      whatsapp: [
        null,
        [Validators.required, (c: AbstractControl) => this.validatePhone(c)],
      ],
      tipo_modelo: [''],
      texto_logo: ['', [Validators.required, this.nonEmptyStringValidator]],
      observacao: [''],
      tipo_pagamento: ['Pix', Validators.required],
      valor: [null, [Validators.required, this.positiveValueValidator]],
      data_entrega: [this.formatDate(new Date()), Validators.required],
      entregue: [false],
      label: ['Carimbo', Validators.required],
    });
  }

  private validatePhone(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return { required: true };
    }
    const error = PhoneUtils.getValidationError(this.selectedDdi, control.value.toString());
    return error ? { phoneInvalid: error } : null;
  }

  private nonEmptyStringValidator(control: AbstractControl): ValidationErrors | null {
    if (!control.value || control.value.toString().trim() === '') {
      return { required: true };
    }
    return null;
  }

  private positiveValueValidator(control: AbstractControl): ValidationErrors | null {
    if (control.value === null || control.value === undefined || control.value === '') {
      return { required: true };
    }
    const num = Number(control.value);
    if (isNaN(num) || num <= 0) {
      return { positiveRequired: true };
    }
    return null;
  }

  onDdiChange(ddi: string) {
    this.selectedDdi = ddi;
    this.pedidoForm.get('whatsapp')?.updateValueAndValidity();
  }

  // Função para formatar a data no formato 'YYYY-MM-DDTHH:mm' exigido por input datetime-local
  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  goBack() {
    this.router.navigate(['/']);
  }

  async onSubmit() {
    this.isSubmitted = true;
    this.pedidoForm.markAllAsTouched();

    if (this.pedidoForm.invalid) {
      alert('Por favor, preencha todos os campos obrigatórios corretamente.');
      return;
    }

    this.loading = true;

    try {
      const formValue = this.pedidoForm.value;

      await this.supabase.createPedido({
        cliente: formValue.cliente ? formValue.cliente.trim() : '',
        whatsapp: Number(formValue.whatsapp),
        tipo_modelo: formValue.tipo_modelo ? formValue.tipo_modelo.trim() : '',
        texto_logo: formValue.texto_logo.trim(),
        tipo_pagamento: formValue.tipo_pagamento,
        valor: Number(formValue.valor),
        observacao: formValue.observacao?.trim() || null,
        entregue: false,
        data_entrega: new Date(formValue.data_entrega).toISOString(),
        label: formValue.label,
      });

      alert('Pedido criado com sucesso!');

      this.isSubmitted = false;
      this.pedidoForm.reset({
        cliente: '',
        whatsapp: null,
        tipo_modelo: '',
        texto_logo: '',
        tipo_pagamento: 'Pix',
        valor: null,
        observacao: '',
        entregue: false,
        data_entrega: this.formatDate(new Date()),
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
