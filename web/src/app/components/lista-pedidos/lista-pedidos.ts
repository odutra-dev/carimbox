import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Supabase } from '../../services/supabase';
import { Pedido } from '../../../@types/pedido';

@Component({
  selector: 'app-lista-pedidos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './lista-pedidos.html',
  styleUrls: ['./lista-pedidos.css'],
})
export class ListaPedidos implements OnInit {
  pedidos: Pedido[] = [];
  loading = true;
  errorMsg = '';

  constructor(private supabaseService: Supabase) {}

  async ngOnInit() {
    try {
      this.pedidos = await this.supabaseService.getPedidos();
    } catch (err) {
      console.error(err);
      this.errorMsg = 'Erro ao carregar pedidos.';
    } finally {
      this.loading = false;
    }
  }
}
