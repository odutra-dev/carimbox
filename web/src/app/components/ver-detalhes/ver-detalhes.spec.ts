import { ComponentFixture, TestBed } from '@angular/core/testing';
import { VerDetalhes } from './ver-detalhes';
import { Supabase } from '../../services/supabase';
import { CupomService } from '../../services/cupom';
import { Router } from '@angular/router';
import { Pedido } from '../../../@types/pedido';

describe('VerDetalhes', () => {
  let component: VerDetalhes;
  let fixture: ComponentFixture<VerDetalhes>;
  let mockSupabase: jasmine.SpyObj<Supabase>;
  let mockRouter: any;
  let mockCupomService: jasmine.SpyObj<CupomService>;

  const mockPedido: Pedido = {
    id: 42,
    cliente: 'Lucas Oliveira',
    whatsapp: 13997654321,
    tipo_modelo: 'Trodat Printy',
    texto_logo: 'Texto Logo Teste',
    tipo_pagamento: 'Pix',
    valor: 85.0,
    data_pedido: '2026-11-01T10:00:00Z',
    data_entrega: '2026-11-03T18:00:00Z',
    observacao: 'Urgente para entrega',
    label: 'Carimbo',
    entregue: false,
  };

  beforeEach(async () => {
    mockSupabase = jasmine.createSpyObj('Supabase', ['getPedidoById']);
    mockSupabase.getPedidoById.and.resolveTo(mockPedido);

    mockRouter = {
      url: '/ver-pedido/42',
      navigate: jasmine.createSpy('navigate'),
    };

    mockCupomService = jasmine.createSpyObj('CupomService', ['imprimir']);

    await TestBed.configureTestingModule({
      imports: [VerDetalhes],
      providers: [
        { provide: Supabase, useValue: mockSupabase },
        { provide: Router, useValue: mockRouter },
        { provide: CupomService, useValue: mockCupomService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(VerDetalhes);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load pedido details on ngOnInit', async () => {
    await component.ngOnInit();

    expect(mockSupabase.getPedidoById).toHaveBeenCalledWith(42);
    expect(component.pedido).toEqual(mockPedido);
    expect(component.loading).toBeFalse();
  });

  it('should navigate back to home on voltar()', () => {
    component.voltar();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should navigate to edit on editarPedido()', () => {
    component.pedido = mockPedido;
    component.editarPedido();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/atualizar-pedido', 42]);
  });

  it('should call cupomService on imprimirCupom()', () => {
    component.pedido = mockPedido;
    component.imprimirCupom();
    expect(mockCupomService.imprimir).toHaveBeenCalledWith(mockPedido);
  });
});
