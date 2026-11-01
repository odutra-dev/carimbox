import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Supabase } from '../../services/supabase';
import { PhoneWithDdiInput } from '../phone-with-ddi-input/phone-with-ddi-input';
import { CurrencyInput } from '../currency-input/currency-input';
import { PhoneUtils } from '../../utils/phone.utils';

@Component({
  selector: 'app-atualizar-pedido',
  templateUrl: './atualizar-pedido.html',
  styleUrls: ['./atualizar-pedido.css'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PhoneWithDdiInput,
    CurrencyInput,
  ],
})
export class AtualizarPedido implements OnInit {
  pedidoForm: FormGroup;
  loading = false;
  isSubmitted = false;
  selectedDdi = '55';
  pedidoId!: number;

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
    private route: ActivatedRoute,
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
      tipo_pagamento: ['', Validators.required],
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

  async ngOnInit() {
    this.pedidoId = Number(this.route.snapshot.paramMap.get('id'));

    if (this.pedidoId) {
      this.loading = true;
      try {
        const pedido = await this.supabase.getPedidoById(this.pedidoId);

        if (pedido.whatsapp) {
          this.selectedDdi = PhoneUtils.parseStoredPhone(pedido.whatsapp).ddi;
        }

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
          entregue: !!pedido.entregue,
          label: pedido.label,
        });

        this.pedidoForm.get('whatsapp')?.updateValueAndValidity();
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
    this.isSubmitted = true;
    this.pedidoForm.markAllAsTouched();

    if (this.pedidoForm.invalid) {
      alert('Por favor, preencha todos os campos obrigatórios corretamente.');
      return;
    }

    this.loading = true;

    try {
      const formValue = this.pedidoForm.value;

      await this.supabase.updatePedido(this.pedidoId, {
        cliente: formValue.cliente ? formValue.cliente.trim() : '',
        whatsapp: Number(formValue.whatsapp),
        tipo_modelo: formValue.tipo_modelo ? formValue.tipo_modelo.trim() : '',
        texto_logo: formValue.texto_logo.trim(),
        tipo_pagamento: formValue.tipo_pagamento,
        valor: Number(formValue.valor),
        observacao: formValue.observacao?.trim() || null,
        entregue: !!formValue.entregue,
        data_entrega: new Date(formValue.data_entrega).toISOString(),
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
