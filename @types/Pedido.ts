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
  responsavel?: string | null;
  data_entrega: string;
};
