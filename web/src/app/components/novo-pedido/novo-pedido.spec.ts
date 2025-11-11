import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NovoPedido } from './novo-pedido';

describe('NovoPedido', () => {
  let component: NovoPedido;
  let fixture: ComponentFixture<NovoPedido>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NovoPedido]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NovoPedido);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
