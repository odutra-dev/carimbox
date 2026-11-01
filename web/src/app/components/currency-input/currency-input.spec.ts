import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CurrencyInput } from './currency-input';

describe('CurrencyInput', () => {
  let component: CurrencyInput;
  let fixture: ComponentFixture<CurrencyInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CurrencyInput],
    }).compileComponents();

    fixture = TestBed.createComponent(CurrencyInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should write double value and format display', () => {
    component.writeValue(79.9);
    expect(component.centsDigits).toBe('7990');
    expect(component.displayValue.replace(/\s/g, ' ')).toMatch(/R\$\s?79,90/);
  });
});
