import {
  Component,
  ElementRef,
  EventEmitter,
  forwardRef,
  HostListener,
  Input,
  Output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';
import { CountryDdi, PhoneUtils } from '../../utils/phone.utils';

@Component({
  selector: 'app-phone-with-ddi-input',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './phone-with-ddi-input.html',
  styleUrls: ['./phone-with-ddi-input.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PhoneWithDdiInput),
      multi: true,
    },
  ],
})
export class PhoneWithDdiInput implements ControlValueAccessor {
  @Input() label = 'WhatsApp';
  @Input() required = true;
  @Input() errorMessage: string | null = null;
  @Input() isSubmitted = false;
  @Output() ddiChange = new EventEmitter<string>();

  countries = PhoneUtils.commonCountries;
  selectedDdi = '55';
  phoneDigits = '';
  displayValue = '';

  dropdownOpen = false;
  showCustomDdiModal = false;
  customDdiInput = '';
  disabled = false;
  touched = false;

  private onChange: (val: number | null) => void = () => {};
  private onTouched: () => void = () => {};

  constructor(private elementRef: ElementRef) {}

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.dropdownOpen = false;
    }
  }

  get currentCountry(): CountryDdi | undefined {
    return this.countries.find((c) => c.code === this.selectedDdi);
  }

  get currentFlag(): string {
    return this.currentCountry?.flag || '🌐';
  }

  get placeholder(): string {
    return PhoneUtils.getPlaceholderForDdi(this.selectedDdi);
  }

  get validationError(): string | null {
    if (this.errorMessage) return this.errorMessage;
    if (this.touched || this.isSubmitted) {
      if (!this.phoneDigits && this.required) {
        return 'WhatsApp é obrigatório';
      }
      return PhoneUtils.getValidationError(this.selectedDdi, this.phoneDigits);
    }
    return null;
  }

  // --- ControlValueAccessor Methods ---
  writeValue(val: any): void {
    if (val === null || val === undefined || val === '') {
      this.phoneDigits = '';
      this.displayValue = '';
      return;
    }
    const parsed = PhoneUtils.parseStoredPhone(val);
    this.selectedDdi = parsed.ddi;
    const maxLen = PhoneUtils.getMaxLengthForDdi(this.selectedDdi);
    this.phoneDigits = parsed.phone.slice(0, maxLen);
    this.displayValue = PhoneUtils.formatInputForDdi(this.selectedDdi, this.phoneDigits);
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

  // --- UI Handlers ---
  toggleDropdown(event: MouseEvent): void {
    event.stopPropagation();
    if (this.disabled) return;
    this.dropdownOpen = !this.dropdownOpen;
  }

  selectCountry(country: CountryDdi): void {
    this.selectedDdi = country.code;
    this.dropdownOpen = false;
    this.ddiChange.emit(this.selectedDdi);
    this.reformatAndPropagate();
  }

  openCustomDdiModal(): void {
    this.dropdownOpen = false;
    this.customDdiInput = this.currentCountry ? '' : this.selectedDdi;
    this.showCustomDdiModal = true;
  }

  confirmCustomDdi(): void {
    const clean = this.customDdiInput.replace(/\D/g, '').slice(0, 4);
    if (clean) {
      this.selectedDdi = clean;
      this.ddiChange.emit(this.selectedDdi);
      this.reformatAndPropagate();
    }
    this.showCustomDdiModal = false;
  }

  cancelCustomDdi(): void {
    this.showCustomDdiModal = false;
  }

  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const clean = input.value.replace(/\D/g, '');
    const maxLen = PhoneUtils.getMaxLengthForDdi(this.selectedDdi);
    this.phoneDigits = clean.slice(0, maxLen);
    this.displayValue = PhoneUtils.formatInputForDdi(this.selectedDdi, this.phoneDigits);
    input.value = this.displayValue;

    this.propagateValue();
  }

  onBlur(): void {
    this.touched = true;
    this.onTouched();
  }

  private reformatAndPropagate(): void {
    const maxLen = PhoneUtils.getMaxLengthForDdi(this.selectedDdi);
    this.phoneDigits = this.phoneDigits.slice(0, maxLen);
    this.displayValue = PhoneUtils.formatInputForDdi(this.selectedDdi, this.phoneDigits);
    this.propagateValue();
  }

  private propagateValue(): void {
    if (!this.phoneDigits) {
      this.onChange(null);
    } else {
      const combined = PhoneUtils.combineDdiAndNumber(this.selectedDdi, this.phoneDigits);
      this.onChange(combined || null);
    }
  }
}
