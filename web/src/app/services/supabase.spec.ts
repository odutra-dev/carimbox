import { TestBed } from '@angular/core/testing';
import { Supabase } from './supabase';
import { Pedido } from '../../@types/pedido';

describe('Supabase', () => {
  let service: Supabase;
  let mockSupabaseClient: any;

  const mockPedido: Pedido = {
    id: 1,
    cliente: 'Maria Silva',
    whatsapp: 13999998888,
    tipo_modelo: 'Trodat 4911',
    texto_logo: 'Texto Teste',
    tipo_pagamento: 'Pix',
    valor: 55,
    observacao: '',
    label: 'Carimbo',
    data_pedido: '2026-11-01T10:00:00Z',
    data_entrega: '2026-11-02T10:00:00Z',
    entregue: false,
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [Supabase],
    });
    service = TestBed.inject(Supabase);

    // Mock internal supabase client methods
    mockSupabaseClient = {
      from: jasmine.createSpy('from'),
    };
    (service as any).supabase = mockSupabaseClient;
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all pedidos ordered by id descending', async () => {
    const queryBuilder: any = {
      select: jasmine.createSpy('select').and.returnValue({
        order: jasmine.createSpy('order').and.resolveTo({ data: [mockPedido], error: null }),
      }),
    };
    mockSupabaseClient.from.and.returnValue(queryBuilder);

    const result = await service.getPedidos();
    expect(mockSupabaseClient.from).toHaveBeenCalledWith('Pedidos');
    expect(result).toEqual([mockPedido]);
  });

  it('should throw error when getPedidos fails', async () => {
    const queryBuilder: any = {
      select: jasmine.createSpy('select').and.returnValue({
        order: jasmine
          .createSpy('order')
          .and.resolveTo({ data: null, error: new Error('Database error') }),
      }),
    };
    mockSupabaseClient.from.and.returnValue(queryBuilder);

    await expectAsync(service.getPedidos()).toBeRejectedWith(jasmine.any(Error));
  });

  it('should fetch pedido by id', async () => {
    const queryBuilder: any = {
      select: jasmine.createSpy('select').and.returnValue({
        eq: jasmine.createSpy('eq').and.returnValue({
          single: jasmine.createSpy('single').and.resolveTo({ data: mockPedido, error: null }),
        }),
      }),
    };
    mockSupabaseClient.from.and.returnValue(queryBuilder);

    const result = await service.getPedidoById(1);
    expect(mockSupabaseClient.from).toHaveBeenCalledWith('Pedidos');
    expect(result).toEqual(mockPedido);
  });

  it('should create a new pedido', async () => {
    const newPedidoData: Partial<Pedido> = { cliente: 'Novo Cliente' };
    const queryBuilder: any = {
      insert: jasmine.createSpy('insert').and.returnValue({
        select: jasmine.createSpy('select').and.returnValue({
          single: jasmine.createSpy('single').and.resolveTo({ data: mockPedido, error: null }),
        }),
      }),
    };
    mockSupabaseClient.from.and.returnValue(queryBuilder);

    const result = await service.createPedido(newPedidoData);
    expect(mockSupabaseClient.from).toHaveBeenCalledWith('Pedidos');
    expect(result).toEqual(mockPedido);
  });

  it('should update a pedido by id', async () => {
    const updates: Partial<Pedido> = { entregue: true };
    const queryBuilder: any = {
      update: jasmine.createSpy('update').and.returnValue({
        eq: jasmine.createSpy('eq').and.returnValue({
          select: jasmine.createSpy('select').and.returnValue({
            single: jasmine.createSpy('single').and.resolveTo({ data: mockPedido, error: null }),
          }),
        }),
      }),
    };
    mockSupabaseClient.from.and.returnValue(queryBuilder);

    const result = await service.updatePedido(1, updates);
    expect(mockSupabaseClient.from).toHaveBeenCalledWith('Pedidos');
    expect(result).toEqual(mockPedido);
  });

  it('should delete a pedido by id', async () => {
    const queryBuilder: any = {
      delete: jasmine.createSpy('delete').and.returnValue({
        eq: jasmine.createSpy('eq').and.resolveTo({ error: null }),
      }),
    };
    mockSupabaseClient.from.and.returnValue(queryBuilder);

    await service.deletePedido(1);
    expect(mockSupabaseClient.from).toHaveBeenCalledWith('Pedidos');
  });
});
