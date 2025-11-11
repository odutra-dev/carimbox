import { Routes } from '@angular/router';
import { ListaPedidos } from './components/lista-pedidos/lista-pedidos';
import { VerDetalhes } from './components/ver-detalhes/ver-detalhes';
import { NovoPedido } from './components/novo-pedido/novo-pedido';
import { AtualizarPedido } from './components/atualizar-pedido/atualizar-pedido';

export const routes: Routes = [
  { path: '', component: ListaPedidos },
  { path: 'ver-pedido/:id', component: VerDetalhes },
  { path: 'novo-pedido', component: NovoPedido },
  { path: 'atualizar-pedido/:id', component: AtualizarPedido },
];
