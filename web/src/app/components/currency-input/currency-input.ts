import {
  Component,
  forwardRef,
  Input,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CurrencyUtils } from '../../utils/currency.utils';

@Component({
  selector: 'app-currency-input',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './currency-input.html',
  styleUrls: ['./currency-input.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CurrencyInput),
      multi: true,
    },
  ],
})
export class CurrencyInput implements ControlValueAccessor {
  @Input() label = 'Valor (R$)';
  @Input() required = true;
  @Input() errorMessage: string | null = null;
  @Input() isSubmitted = false;

  centsDigits = '';
  displayValue = '';
  disabled = false;
  touched = false;

  private onChange: (val: number | null) => void = () => {};
  private onTouched: () => void = () => {};

  get validationError(): string | null {
    if (this.errorMessage) return this.errorMessage;
    if (this.touched || this.isSubmitted) {
      if (this.required) {
        const doubleVal = CurrencyUtils.centsToDouble(this.centsDigits);
        if (!this.centsDigits || doubleVal <= 0) {
          return 'Valor é obrigatório';
        }
      }
    }
    return null;
  }

  // --- ControlValueAccessor Methods ---
  writeValue(val: any): void {
    if (val === null || val === undefined || val === '') {
      this.centsDigits = '';
      this.displayValue = '';
      return;
    }
    const num = Number(val);
    if (!isNaN(num)) {
      this.centsDigits = CurrencyUtils.doubleToCentsDigits(num);
      this.displayValue = CurrencyUtils.formatCentsToCurrency(this.centsDigits);
    } else {
      this.centsDigits = '';
      this.displayValue = '';
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  // --- Input Handlers ---
  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digits = input.value.replace(/\D/g, '');
    const clean = digits.replace(/^0+/, '').slice(0, 9);

    this.centsDigits = clean;
    this.displayValue = clean ? CurrencyUtils.formatCentsToCurrency(clean) : '';
    input.value = this.displayValue;

    const doubleVal = CurrencyUtils.centsToDouble(clean);
    this.onChange(clean ? doubleVal : null);
  }

  onBlur(): void {
    this.touched = true;
    this.onTouched();
  }
}
