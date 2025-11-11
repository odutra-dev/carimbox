import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AtualizarPedido } from './atualizar-pedido';

describe('AtualizarPedido', () => {
  let component: AtualizarPedido;
  let fixture: ComponentFixture<AtualizarPedido>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AtualizarPedido]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AtualizarPedido);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
