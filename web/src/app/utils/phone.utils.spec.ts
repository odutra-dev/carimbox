import { PhoneUtils } from './phone.utils';

describe('PhoneUtils', () => {
  it('should validate valid Brazilian mobile number with valid DDD', () => {
    expect(PhoneUtils.isBrazilianFormat('13997654321')).toBeTrue();
    expect(PhoneUtils.isBrazilianFormat('11988887777')).toBeTrue();
  });

  it('should validate valid Brazilian landline number with valid DDD', () => {
    expect(PhoneUtils.isBrazilianFormat('1334567890')).toBeTrue();
    expect(PhoneUtils.isBrazilianFormat('1123456789')).toBeTrue();
  });

  it('should reject invalid Brazilian DDD', () => {
    // 00 is not a valid DDD
    expect(PhoneUtils.isBrazilianFormat('00997654321')).toBeFalse();
    // 36 is not in the official list
    expect(PhoneUtils.isBrazilianFormat('36997654321')).toBeFalse();
  });

  it('should reject invalid 9th digit for 11 digits', () => {
    // Mobile with 11 digits must start with 9
    expect(PhoneUtils.isBrazilianFormat('13897654321')).toBeFalse();
  });

  it('should format Brazilian phone numbers incrementally', () => {
    expect(PhoneUtils.formatBrazilianPhone('13')).toBe('(13');
    expect(PhoneUtils.formatBrazilianPhone('1399')).toBe('(13) 99');
    expect(PhoneUtils.formatBrazilianPhone('1334567890')).toBe('(13) 3456-7890');
    expect(PhoneUtils.formatBrazilianPhone('13997654321')).toBe('(13) 99765-4321');
  });

  it('should combine DDI and number correctly', () => {
    // Brazil stores national number
    expect(PhoneUtils.combineDdiAndNumber('55', '13997654321')).toBe(13997654321);
    // Other countries store DDI + number
    expect(PhoneUtils.combineDdiAndNumber('351', '912345678')).toBe(351912345678);
  });

  it('should parse stored phone correctly', () => {
    expect(PhoneUtils.parseStoredPhone(13997654321)).toEqual({ ddi: '55', phone: '13997654321' });
    expect(PhoneUtils.parseStoredPhone(5513997654321)).toEqual({ ddi: '55', phone: '13997654321' });
    expect(PhoneUtils.parseStoredPhone(351912345678)).toEqual({ ddi: '351', phone: '912345678' });
  });

  it('should return whatsapp target phone with correct country code prefix', () => {
    expect(PhoneUtils.getWhatsAppTargetPhone(13997654321)).toBe('5513997654321');
    expect(PhoneUtils.getWhatsAppTargetPhone(5513997654321)).toBe('5513997654321');
    expect(PhoneUtils.getWhatsAppTargetPhone(351912345678)).toBe('351912345678');
  });

  it('should return descriptive validation errors for Brazilian numbers', () => {
    expect(PhoneUtils.getValidationError('55', '')).toBe('WhatsApp é obrigatório');
    expect(PhoneUtils.getValidationError('55', '1')).toBe('Informe o DDD (2 dígitos)');
    expect(PhoneUtils.getValidationError('55', '00')).toBe('DDD (00) não é válido no Brasil');
    expect(PhoneUtils.getValidationError('55', '139999')).toBe('Número incompleto (DDD + 8 ou 9 dígitos)');
    expect(PhoneUtils.getValidationError('55', '13888887777')).toBe('Celular com 9 dígitos deve iniciar com 9');
    expect(PhoneUtils.getValidationError('55', '13997654321')).toBeNull();
  });
});
