import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NovoPedido } from './novo-pedido';
import { Supabase } from '../../services/supabase';
import { Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';

describe('NovoPedido', () => {
  let component: NovoPedido;
  let fixture: ComponentFixture<NovoPedido>;
  let mockSupabase: jasmine.SpyObj<Supabase>;
  let mockRouter: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    mockSupabase = jasmine.createSpyObj('Supabase', ['createPedido']);
    mockSupabase.createPedido.and.resolveTo({} as any);

    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [NovoPedido, ReactiveFormsModule],
      providers: [
        { provide: Supabase, useValue: mockSupabase },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NovoPedido);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with invalid status due to required fields', () => {
    expect(component.pedidoForm.valid).toBeFalse();
    expect(component.pedidoForm.get('texto_logo')?.hasError('required')).toBeTrue();
    expect(component.pedidoForm.get('whatsapp')?.hasError('required')).toBeTrue();
  });

  it('should update selectedDdi on onDdiChange', () => {
    component.onDdiChange('351');
    expect(component.selectedDdi).toBe('351');
  });

  it('should navigate back on goBack()', () => {
    component.goBack();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should submit valid form, call supabase.createPedido and navigate home', async () => {
    component.pedidoForm.patchValue({
      cliente: 'Carlos Pereira',
      whatsapp: '13997654321',
      tipo_modelo: 'Trodat 4911',
      texto_logo: 'Texto do Carimbo',
      tipo_pagamento: 'Pix',
      valor: 45.0,
      data_entrega: '2026-11-10T14:00',
      observacao: 'Sem observações',
      label: 'Carimbo',
    });

    expect(component.pedidoForm.valid).toBeTrue();

    await component.onSubmit();

    expect(mockSupabase.createPedido).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should not submit when form is invalid', async () => {
    component.pedidoForm.patchValue({ cliente: '' });
    await component.onSubmit();

    expect(component.isSubmitted).toBeTrue();
    expect(mockSupabase.createPedido).not.toHaveBeenCalled();
  });
});
