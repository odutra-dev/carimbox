import { Injectable } from '@angular/core';
import { createClient, SupabaseClient } from '@supabase/supabase-js';
import { environment } from '../../environments/environment';

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

  async getPedidos() {
    const { data, error } = await this.supabase
      .from('Pedidos')
      .select('*')
      .order('id', { ascending: false });

    if (error) throw error;
    return data;
  }
}
