import { Injectable } from '@angular/core';
import { createClient, SupabaseClient } from '@supabase/supabase-js';
import { environment } from '../../environments/environment';
import { Pedido } from '../../@types/pedido';

@Injectable({
  providedIn: 'root',
})
export class Supabase {
  private supabase: SupabaseClient;

  constructor() {
    const SUPABASE_URL = environment.supabaseUrl;
    const SUPABASE_ANON_KEY = environment.supabaseAnonKey;
    this.supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY);
  }

  // 🔹 Buscar todos os pedidos
  async getPedidos(): Promise<Pedido[]> {
    const { data, error } = await this.supabase
      .from('Pedidos')
      .select('*')
      .order('id', { ascending: false });

    if (error) throw error;
    return data as Pedido[];
  }

  // 🔹 Atualizar um pedido (por ID)
  async updatePedido(id: number, updates: Partial<Pedido>): Promise<Pedido | null> {
    const { data, error } = await this.supabase
      .from('Pedidos')
      .update(updates)
      .eq('id', id)
      .select()
      .single();

    if (error) throw error;
    return data as Pedido;
  }

  // 🔹 Buscar um pedido específico (opcional, se quiser usar em "ver detalhes")
  async getPedidoById(id: number): Promise<Pedido> {
    const { data, error } = await this.supabase.from('Pedidos').select('*').eq('id', id).single();

    if (error) throw error;
    return data as Pedido;
  }
}
