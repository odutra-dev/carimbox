import { CurrencyUtils } from './currency.utils';

describe('CurrencyUtils', () => {
  it('should format cents to BRL currency string', () => {
    const formatted = CurrencyUtils.formatCentsToCurrency('7990');
    // Normalize non-breaking spaces
    expect(formatted.replace(/\s/g, ' ')).toMatch(/R\$\s?79,90/);

    const zeroFormatted = CurrencyUtils.formatCentsToCurrency('0');
    expect(zeroFormatted.replace(/\s/g, ' ')).toMatch(/R\$\s?0,00/);
  });

  it('should convert cents string to double', () => {
    expect(CurrencyUtils.centsToDouble('7990')).toBe(79.9);
    expect(CurrencyUtils.centsToDouble('100')).toBe(1.0);
    expect(CurrencyUtils.centsToDouble('5')).toBe(0.05);
    expect(CurrencyUtils.centsToDouble('')).toBe(0);
  });

  it('should convert double to cents digits string', () => {
    expect(CurrencyUtils.doubleToCentsDigits(79.9)).toBe('7990');
    expect(CurrencyUtils.doubleToCentsDigits(1.0)).toBe('100');
    expect(CurrencyUtils.doubleToCentsDigits(0.05)).toBe('5');
    expect(CurrencyUtils.doubleToCentsDigits(0)).toBe('');
    expect(CurrencyUtils.doubleToCentsDigits(null)).toBe('');
  });
});
