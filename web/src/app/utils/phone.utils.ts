export interface CountryDdi {
  code: string;
  flag: string;
  name: string;
}

export const COMMON_COUNTRIES: CountryDdi[] = [
  { code: '55', flag: '🇧🇷', name: 'Brasil' },
  { code: '351', flag: '🇵🇹', name: 'Portugal' },
  { code: '1', flag: '🇺🇸', name: 'Estados Unidos / Canadá' },
  { code: '54', flag: '🇦🇷', name: 'Argentina' },
  { code: '34', flag: '🇪🇸', name: 'Espanha' },
];

export const BRAZILIAN_DDDS = new Set([
  '11', '12', '13', '14', '15', '16', '17', '18', '19',
  '21', '22', '24', '27', '28',
  '31', '32', '33', '34', '35', '37', '38',
  '41', '42', '43', '44', '45', '46', '47', '48', '49',
  '51', '53', '54', '55',
  '61', '62', '63', '64', '65', '66', '67', '68', '69',
  '71', '73', '74', '75', '77', '79',
  '81', '82', '83', '84', '85', '86', '87', '88', '89',
  '91', '92', '93', '94', '95', '96', '97', '98', '99',
]);

export class PhoneUtils {
  static readonly commonCountries = COMMON_COUNTRIES;
  static readonly brazilianDdds = BRAZILIAN_DDDS;

  static isBrazilianFormat(digits: string): boolean {
    const clean = digits.replace(/\D/g, '');
    if (clean.length === 11) {
      const ddd = clean.slice(0, 2);
      return this.brazilianDdds.has(ddd) && clean[2] === '9';
    }
    if (clean.length === 10) {
      const ddd = clean.slice(0, 2);
      return this.brazilianDdds.has(ddd) && clean[2] >= '2' && clean[2] <= '5';
    }
    return false;
  }

  static formatBrazilianPhone(digits: string): string {
    const clean = digits.replace(/\D/g, '').slice(0, 11);
    if (!clean) return '';
    if (clean.length <= 2) return `(${clean}`;
    if (clean.length <= 6) return `(${clean.slice(0, 2)}) ${clean.slice(2)}`;
    if (clean.length <= 10) {
      return `(${clean.slice(0, 2)}) ${clean.slice(2, 6)}-${clean.slice(6)}`;
    }
    return `(${clean.slice(0, 2)}) ${clean.slice(2, 7)}-${clean.slice(7)}`;
  }

  static formatUsPhone(digits: string): string {
    const clean = digits.replace(/\D/g, '').slice(0, 10);
    if (!clean) return '';
    if (clean.length <= 3) return `(${clean}`;
    if (clean.length <= 6) return `(${clean.slice(0, 3)}) ${clean.slice(3)}`;
    return `(${clean.slice(0, 3)}) ${clean.slice(3, 6)}-${clean.slice(6)}`;
  }

  static formatPortugalPhone(digits: string): string {
    const clean = digits.replace(/\D/g, '').slice(0, 9);
    if (!clean) return '';
    if (clean.length <= 3) return clean;
    if (clean.length <= 6) return `${clean.slice(0, 3)} ${clean.slice(3)}`;
    return `${clean.slice(0, 3)} ${clean.slice(3, 6)} ${clean.slice(6)}`;
  }

  static formatSpainPhone(digits: string): string {
    const clean = digits.replace(/\D/g, '').slice(0, 9);
    if (!clean) return '';
    if (clean.length <= 3) return clean;
    if (clean.length <= 5) return `${clean.slice(0, 3)} ${clean.slice(3)}`;
    if (clean.length <= 7) {
      return `${clean.slice(0, 3)} ${clean.slice(3, 5)} ${clean.slice(5)}`;
    }
    return `${clean.slice(0, 3)} ${clean.slice(3, 5)} ${clean.slice(5, 7)} ${clean.slice(7)}`;
  }

  static formatArgentinaPhone(digits: string): string {
    const clean = digits.replace(/\D/g, '').slice(0, 11);
    if (!clean) return '';
    if (clean.length <= 2) return clean;
    if (clean.length <= 6) return `${clean.slice(0, 2)} ${clean.slice(2)}`;
    return `${clean.slice(0, 2)} ${clean.slice(2, 6)}-${clean.slice(6)}`;
  }

  static formatInputForDdi(ddi: string, digits: string): string {
    const clean = digits.replace(/\D/g, '');
    switch (ddi) {
      case '55':
        return this.formatBrazilianPhone(clean);
      case '1':
        return this.formatUsPhone(clean);
      case '351':
        return this.formatPortugalPhone(clean);
      case '34':
        return this.formatSpainPhone(clean);
      case '54':
        return this.formatArgentinaPhone(clean);
      default: {
        if (clean.length <= 4) return clean;
        if (clean.length <= 8) return `${clean.slice(0, 4)} ${clean.slice(4)}`;
        return `${clean.slice(0, 3)} ${clean.slice(3, 7)} ${clean.slice(7)}`;
      }
    }
  }

  static getPlaceholderForDdi(ddi: string): string {
    switch (ddi) {
      case '55':
        return '(13) 99999-9999';
      case '1':
        return '(202) 555-0123';
      case '351':
        return '912 345 678';
      case '34':
        return '612 34 56 78';
      case '54':
        return '11 2345-6789';
      default:
        return '123456789';
    }
  }

  static getMaxLengthForDdi(ddi: string): number {
    switch (ddi) {
      case '55':
        return 11;
      case '1':
        return 10;
      case '351':
        return 9;
      case '34':
        return 9;
      case '54':
        return 11;
      default:
        return 15;
    }
  }

  static formatPhoneDisplay(rawPhone: number | string | null | undefined): string {
    if (!rawPhone) return '';
    const s = rawPhone.toString().replace(/\D/g, '');
    if (!s || s === '0') return '';

    // Formato brasileiro padrão nacional (10 ou 11 dígitos)
    if (this.isBrazilianFormat(s)) {
      return s.length === 11
        ? `(${s.slice(0, 2)}) ${s.slice(2, 7)}-${s.slice(7)}`
        : `(${s.slice(0, 2)}) ${s.slice(2, 6)}-${s.slice(6)}`;
    }

    // Formato brasileiro com DDI 55
    if (s.startsWith('55') && (s.length === 12 || s.length === 13)) {
      const national = s.slice(2);
      return national.length === 11
        ? `+55 (${national.slice(0, 2)}) ${national.slice(2, 7)}-${national.slice(7)}`
        : `+55 (${national.slice(0, 2)}) ${national.slice(2, 6)}-${national.slice(6)}`;
    }

    // Portugal (+351)
    if (s.startsWith('351') && s.length === 12) {
      const num = s.slice(3);
      return `+351 ${num.slice(0, 3)} ${num.slice(3, 6)} ${num.slice(6)}`;
    }

    // EUA / Canadá (+1)
    if (s.startsWith('1') && s.length === 11) {
      const num = s.slice(1);
      return `+1 (${num.slice(0, 3)}) ${num.slice(3, 6)}-${num.slice(6)}`;
    }

    // Espanha (+34)
    if (s.startsWith('34') && s.length === 11) {
      const num = s.slice(2);
      return `+34 ${num.slice(0, 3)} ${num.slice(3, 5)} ${num.slice(5, 7)} ${num.slice(7)}`;
    }

    // Argentina (+54)
    if (s.startsWith('54')) {
      const num = s.slice(2);
      if (num.startsWith('9') && num.length >= 10) {
        const rest = num.slice(1);
        return `+54 9 ${rest.slice(0, 2)} ${rest.slice(2, 6)}-${rest.slice(6)}`;
      } else if (num.length >= 10) {
        return `+54 ${num.slice(0, 2)} ${num.slice(2, 6)}-${num.slice(6)}`;
      }
      return `+54 ${num}`;
    }

    const known = this.commonCountries.find((c) => s.startsWith(c.code));
    if (known) {
      return `+${known.code} ${s.slice(known.code.length)}`;
    }

    return `+${s}`;
  }

  static getWhatsAppTargetPhone(rawPhone: number | string | null | undefined): string {
    if (!rawPhone) return '';
    const s = rawPhone.toString().replace(/\D/g, '');
    if (!s || s === '0') return '';

    if (s.startsWith('55') && (s.length === 12 || s.length === 13)) {
      return s;
    }

    if (this.isBrazilianFormat(s)) {
      return `55${s}`;
    }

    // Internacional já contém o DDI
    return s;
  }

  static parseStoredPhone(rawPhone: number | string | null | undefined): { ddi: string; phone: string } {
    if (!rawPhone) return { ddi: '55', phone: '' };
    const s = rawPhone.toString().replace(/\D/g, '');
    if (!s || s === '0') return { ddi: '55', phone: '' };

    // Prefixo 55 explícito (12 ou 13 dígitos)
    if (s.startsWith('55') && (s.length === 12 || s.length === 13)) {
      return { ddi: '55', phone: s.slice(2) };
    }

    // Formato brasileiro nacional padrão (10 ou 11 dígitos com DDD válido)
    if (this.isBrazilianFormat(s)) {
      return { ddi: '55', phone: s };
    }

    // Portugal (351)
    if (s.startsWith('351')) {
      return { ddi: '351', phone: s.slice(3) };
    }

    // Espanha (34)
    if (s.startsWith('34')) {
      return { ddi: '34', phone: s.slice(2) };
    }

    // Argentina (54)
    if (s.startsWith('54')) {
      return { ddi: '54', phone: s.slice(2) };
    }

    // EUA / Canadá (1)
    if (s.startsWith('1') && s.length === 11) {
      return { ddi: '1', phone: s.slice(1) };
    }

    const match = this.commonCountries.filter((c) => c.code !== '55').find((c) => s.startsWith(c.code));
    if (match) {
      return { ddi: match.code, phone: s.slice(match.code.length) };
    }

    return { ddi: '55', phone: s };
  }

  static combineDdiAndNumber(ddi: string, numberDigits: string): number {
    const cleanDdi = ddi.replace(/\D/g, '');
    const cleanNum = numberDigits.replace(/\D/g, '');
    if (!cleanNum) return 0;
    if (cleanDdi === '55') {
      return Number(cleanNum) || 0;
    }
    return Number(`${cleanDdi}${cleanNum}`) || 0;
  }

  static isValidPhone(ddi: string, numberDigits: string): boolean {
    const clean = numberDigits.replace(/\D/g, '');
    switch (ddi) {
      case '55':
        return this.isBrazilianFormat(clean);
      case '1':
        return clean.length === 10;
      case '351':
        return clean.length === 9;
      case '34':
        return clean.length === 9;
      case '54':
        return clean.length >= 10 && clean.length <= 11;
      default:
        return clean.length >= 6 && clean.length <= 15;
    }
  }

  static getValidationError(ddi: string, numberDigits: string): string | null {
    const clean = numberDigits.replace(/\D/g, '');
    if (!clean) {
      return 'WhatsApp é obrigatório';
    }

    if (ddi === '55') {
      if (clean.length < 2) {
        return 'Informe o DDD (2 dígitos)';
      }
      const ddd = clean.slice(0, 2);
      if (!this.brazilianDdds.has(ddd)) {
        return `DDD (${ddd}) não é válido no Brasil`;
      }
      if (clean.length < 10) {
        return 'Número incompleto (DDD + 8 ou 9 dígitos)';
      }
      if (clean.length === 11 && clean[2] !== '9') {
        return 'Celular com 9 dígitos deve iniciar com 9';
      }
      if (clean.length === 10 && (clean[2] < '2' || clean[2] > '5')) {
        return 'Telefone fixo com 8 dígitos deve iniciar entre 2 e 5';
      }
      if (!this.isBrazilianFormat(clean)) {
        return 'Número de WhatsApp inválido para o Brasil';
      }
      return null;
    }

    if (!this.isValidPhone(ddi, clean)) {
      return 'Número de WhatsApp inválido para o país selecionado';
    }

    return null;
  }

  static buildWhatsAppUrl(
    rawPhone: number | string | null | undefined,
    message?: string
  ): string {
    const target = this.getWhatsAppTargetPhone(rawPhone);
    if (!target) return '';
    if (message && message.trim()) {
      return `https://api.whatsapp.com/send?phone=${target}&text=${encodeURIComponent(message)}`;
    }
    return `https://api.whatsapp.com/send?phone=${target}`;
  }
}
