import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PhoneWithDdiInput } from './phone-with-ddi-input';

describe('PhoneWithDdiInput', () => {
  let component: PhoneWithDdiInput;
  let fixture: ComponentFixture<PhoneWithDdiInput>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhoneWithDdiInput],
    }).compileComponents();

    fixture = TestBed.createComponent(PhoneWithDdiInput);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize with Brazil DDI 55 and flag 🇧🇷', () => {
    expect(component.selectedDdi).toBe('55');
    expect(component.currentFlag).toBe('🇧🇷');
  });

  it('should write value and format Brazilian phone', () => {
    component.writeValue(13997654321);
    expect(component.phoneDigits).toBe('13997654321');
    expect(component.displayValue).toBe('(13) 99765-4321');
  });

  it('should update on country selection', () => {
    const portugal = component.countries.find((c) => c.code === '351')!;
    component.selectCountry(portugal);
    expect(component.selectedDdi).toBe('351');
    expect(component.currentFlag).toBe('🇵🇹');
  });

  it('should save custom DDI on confirmCustomDdi', () => {
    spyOn(component.ddiChange, 'emit');
    component.openCustomDdiModal();
    expect(component.showCustomDdiModal).toBeTrue();

    // User types 44 (even if bound as number or string)
    component.onCustomDdiChange('44');
    component.confirmCustomDdi();

    expect(component.selectedDdi).toBe('44');
    expect(component.currentFlag).toBe('🌐');
    expect(component.showCustomDdiModal).toBeFalse();
    expect(component.ddiChange.emit).toHaveBeenCalledWith('44');
  });
});
