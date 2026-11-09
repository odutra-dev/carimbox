import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AtualizarPedido } from './atualizar-pedido';
import { Supabase } from '../../services/supabase';
import { ActivatedRoute, Router } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { Pedido } from '../../../@types/pedido';

describe('AtualizarPedido', () => {
  let component: AtualizarPedido;
  let fixture: ComponentFixture<AtualizarPedido>;
  let mockSupabase: jasmine.SpyObj<Supabase>;
  let mockRouter: jasmine.SpyObj<Router>;

  const mockPedido: Pedido = {
    id: 10,
    cliente: 'Ana Costa',
    whatsapp: 13997654321,
    tipo_modelo: 'Colop C20',
    texto_logo: 'Texto Logo',
    tipo_pagamento: 'Pix',
    valor: 60.0,
    data_pedido: '2026-11-01T10:00:00Z',
    data_entrega: '2026-11-05T14:00:00Z',
    observacao: 'Observação teste',
    label: 'Carimbo',
    entregue: false,
  };

  beforeEach(async () => {
    mockSupabase = jasmine.createSpyObj('Supabase', ['getPedidoById', 'updatePedido']);
    mockSupabase.getPedidoById.and.resolveTo(mockPedido);
    mockSupabase.updatePedido.and.resolveTo(mockPedido);

    mockRouter = jasmine.createSpyObj('Router', ['navigate']);

    const mockActivatedRoute = {
      snapshot: {
        paramMap: {
          get: (key: string) => (key === 'id' ? '10' : null),
        },
      },
    };

    await TestBed.configureTestingModule({
      imports: [AtualizarPedido, ReactiveFormsModule],
      providers: [
        { provide: Supabase, useValue: mockSupabase },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(AtualizarPedido);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load pedido and populate form on ngOnInit', async () => {
    await component.ngOnInit();

    expect(mockSupabase.getPedidoById).toHaveBeenCalledWith(10);
    expect(component.pedidoId).toBe(10);
    expect(component.pedidoForm.get('cliente')?.value).toBe('Ana Costa');
    expect(component.selectedDdi).toBe('55');
  });

  it('should navigate back on goBack()', () => {
    component.goBack();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should update pedido on valid submit and navigate to home', async () => {
    await component.ngOnInit();
    expect(component.pedidoForm.valid).toBeTrue();

    await component.onSubmit();

    expect(mockSupabase.updatePedido).toHaveBeenCalled();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });
});
