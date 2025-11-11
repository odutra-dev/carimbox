import { Component, signal, effect, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrls: ['./app.css'],
})
export class App {
  // Signal que guarda o tema atual
  theme = signal<'light' | 'dark'>('light');

  constructor(@Inject(DOCUMENT) private document: Document) {
    // Detecta a preferência inicial do usuário
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    this.theme.set(prefersDark ? 'dark' : 'light');

    // Atualiza automaticamente o data-theme sempre que a signal muda
    effect(() => {
      this.document.body.setAttribute('data-theme', this.theme());
    });

    // Ouve mudanças na preferência do usuário
    window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', (e) => {
      this.theme.set(e.matches ? 'dark' : 'light');
    });
  }
}
