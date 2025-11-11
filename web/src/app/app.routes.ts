import { Routes } from '@angular/router';
import { ListaPedidos } from './components/lista-pedidos/lista-pedidos';
import { VerDetalhes } from './components/ver-detalhes/ver-detalhes';

export const routes: Routes = [
  { path: '', component: ListaPedidos },
  { path: 'ver-pedido/:id', component: VerDetalhes },
];
