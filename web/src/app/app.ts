import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ListaPedidos } from './components/lista-pedidos/lista-pedidos';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ListaPedidos],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('web');
}
