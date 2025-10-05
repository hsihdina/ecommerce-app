import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {AuthService} from '../../service/auth.service';
import {Connexion} from '../../model/auth/Connexion';

@Component({
  selector: 'app-connexion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './connexion.component.html',
  styleUrls: ['./connexion.component.css']
})
export class ConnexionComponent {
  credentials: Connexion = {
    email: '',
    motDePasse: ''
  };

  erreur = '';
  chargement = false;

  constructor(private authService: AuthService, private router: Router) {}

  seConnecter(): void {
    this.erreur = '';
    this.chargement = true;

    this.authService.seConnecter(this.credentials).subscribe({
      next: () => {
        this.router.navigate(['/shop']);
      },
      error: (error) => {
        console.error('Erreur de connexion:', error);
        this.erreur = 'Email ou mot de passe incorrect';
        this.chargement = false;
      }
    });
  }
}
