import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListaPedidos } from './lista-pedidos';
import { Supabase } from '../../services/supabase';
import { CupomService } from '../../services/cupom';
import { Router } from '@angular/router';
import { Pedido } from '../../../@types/pedido';

describe('ListaPedidos', () => {
  let component: ListaPedidos;
  let fixture: ComponentFixture<ListaPedidos>;
  let mockSupabase: jasmine.SpyObj<Supabase>;
  let mockRouter: jasmine.SpyObj<Router>;
  let mockCupomService: jasmine.SpyObj<CupomService>;

  const mockPedidos: Pedido[] = [
    {
      id: 1,
      cliente: 'João Silva',
      whatsapp: 13999991111,
      tipo_modelo: 'Trodat 4911',
      texto_logo: 'Texto 1',
      tipo_pagamento: 'Pix',
      valor: 50,
      observacao: '',
      label: 'Carimbo',
      data_pedido: '2026-11-01T10:00:00Z',
      data_entrega: '2026-11-02T10:00:00Z',
      entregue: false,
    },
    {
      id: 2,
      cliente: 'Maria Souza',
      whatsapp: 13999992222,
      tipo_modelo: 'Trodat 4912',
      texto_logo: 'Texto 2',
      tipo_pagamento: 'Dinheiro',
      valor: 75,
      observacao: '',
      label: 'Mdf/Acrílico',
      data_pedido: '2026-11-01T11:00:00Z',
      data_entrega: '2026-11-02T11:00:00Z',
      entregue: true,
    },
  ];

  beforeEach(async () => {
    mockSupabase = jasmine.createSpyObj('Supabase', ['getPedidos', 'updatePedido', 'deletePedido']);
    mockSupabase.getPedidos.and.resolveTo([...mockPedidos]);
    mockSupabase.updatePedido.and.resolveTo(mockPedidos[0]);
    mockSupabase.deletePedido.and.resolveTo();

    mockRouter = jasmine.createSpyObj('Router', ['navigate']);
    mockCupomService = jasmine.createSpyObj('CupomService', ['imprimir']);

    await TestBed.configureTestingModule({
      imports: [ListaPedidos],
      providers: [
        { provide: Supabase, useValue: mockSupabase },
        { provide: Router, useValue: mockRouter },
        { provide: CupomService, useValue: mockCupomService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListaPedidos);
    component = fixture.componentInstance;
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should load pedidos on ngOnInit', async () => {
    await component.ngOnInit();
    expect(mockSupabase.getPedidos).toHaveBeenCalled();
    expect(component.pedidos.length).toBe(2);
    expect(component.loading).toBeFalse();
  });

  it('should handle error when loading pedidos fails', async () => {
    mockSupabase.getPedidos.and.rejectWith(new Error('Network error'));
    await component.ngOnInit();
    expect(component.errorMsg).toBe('Erro ao carregar pedidos.');
    expect(component.loading).toBeFalse();
  });

  it('should filter pendentes by default', async () => {
    await component.ngOnInit();
    expect(component.filtro).toBe('pendentes');
    expect(component.pedidosFiltrados.length).toBe(1);
    expect(component.pedidosFiltrados[0].id).toBe(1);
  });

  it('should filter concluidos when selected', async () => {
    await component.ngOnInit();
    component.filtro = 'concluidos';
    expect(component.pedidosFiltrados.length).toBe(1);
    expect(component.pedidosFiltrados[0].id).toBe(2);
  });

  it('should filter todos when selected', async () => {
    await component.ngOnInit();
    component.filtro = 'todos';
    expect(component.pedidosFiltrados.length).toBe(2);
  });

  it('should navigate to details on verDetalhes', () => {
    component.verDetalhes(1);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/ver-pedido', 1]);
  });

  it('should trigger cupom print on imprimirCupom', () => {
    const event = jasmine.createSpyObj('MouseEvent', ['stopPropagation']);
    component.imprimirCupom(mockPedidos[0], event);
    expect(event.stopPropagation).toHaveBeenCalled();
    expect(mockCupomService.imprimir).toHaveBeenCalledWith(mockPedidos[0]);
  });

  it('should mark order as delivered and update list', async () => {
    await component.ngOnInit();
    await component.marcarComoEntregue(mockPedidos[0]);
    expect(mockSupabase.updatePedido).toHaveBeenCalledWith(1, { entregue: true });
    expect(mockPedidos[0].entregue).toBeTrue();
  });
});
