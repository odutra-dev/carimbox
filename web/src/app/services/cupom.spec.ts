import { TestBed } from '@angular/core/testing';
import { CupomService } from './cupom';
import { Pedido } from '../../@types/pedido';

describe('CupomService', () => {
  let service: CupomService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CupomService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should format whatsapp number correctly', () => {
    expect(service.formatarWhatsapp('13999998888')).toBe('(13) 99999-8888');
    expect(service.formatarWhatsapp(1333334444)).toBe('(13) 3333-4444');
  });

  it('should generate cupom PDF without throwing errors', async () => {
    const pedidoMock: Pedido = {
      id: 999,
      data_pedido: '2026-10-21T10:00:00Z',
      data_entrega: '2026-10-22T14:00:00Z',
      cliente: 'João da Silva',
      whatsapp: 13999998888,
      tipo_modelo: 'Trodat 4911',
      texto_logo: 'Texto de Exemplo\nSegunda Linha',
      tipo_pagamento: 'PIX',
      valor: 50.0,
      observacao: 'Urgente',
      label: 'Carimbo',
    };

    const doc = await service.gerarPdf(pedidoMock);
    expect(doc).toBeTruthy();
    const pdfBlob = doc.output('blob');
    expect(pdfBlob.size).toBeGreaterThan(1000);
  });
});
