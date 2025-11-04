export type Pedido = {
  id: number;
  data_pedido: string;
  cliente: string;
  whatsapp: number;
  tipo_modelo: string;
  texto_logo: string;
  tipo_pagamento: string;
  valor: number;
  observacao?: string | null;
  entregue?: boolean;
  data_entrega: string;
};
