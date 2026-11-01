export class CurrencyUtils {
  private static formatter = new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  });

  static formatCentsToCurrency(centsDigits: string): string {
    const clean = centsDigits.replace(/\D/g, '');
    const cents = Number(clean) || 0;
    const amount = cents / 100;
    return this.formatter.format(amount);
  }

  static centsToDouble(centsDigits: string): number {
    const clean = centsDigits.replace(/\D/g, '');
    const cents = Number(clean) || 0;
    return Number((cents / 100).toFixed(2));
  }

  static doubleToCentsDigits(value: number | null | undefined): string {
    if (value === null || value === undefined || isNaN(value)) {
      return '';
    }
    const cents = Math.round(value * 100);
    return cents > 0 ? cents.toString() : '';
  }

  static formatDoubleToCurrency(value: number | null | undefined): string {
    return this.formatter.format(value || 0);
  }
}
