import {Component} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Contact} from '../../model/Contact';

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.css']
})
export class ContactComponent {
  contact: Contact = {
    email: '',
    message: ''
  };

  messageEnvoye = false;
  erreurs: { [key: string]: string } = {};

  validerFormulaire(): boolean {
    this.erreurs = {};
    let valide = true;

    // Validation de l'email
    if (!this.contact.email) {
      this.erreurs['email'] = 'Lemail est obligatoire';
      valide = false;
    } else if (!this.estEmailValide(this.contact.email)) {
      this.erreurs['email'] = 'Lemail doit être valide';
      valide = false;
    }

    if (!this.contact.message) {
      this.erreurs['message'] = 'Le message est obligatoire';
      valide = false;
    } else if (this.contact.message.length > 300) {
      this.erreurs['message'] = 'Le message doit être inférieur à 300 caractères';
      valide = false;
    }

    return valide;
  }

  estEmailValide(email: string): boolean {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  }

  envoyerMessage(): void {
    if (!this.validerFormulaire()) {
      return;
    }

    console.log('Envoi du message:', this.contact);

    // Réinitialiser le formulaire et afficher le message de succès
    this.contact = { email: '', message: '' };
    this.messageEnvoye = true;

    // Masquer le message après 5 secondes
    setTimeout(() => {
      this.messageEnvoye = false;
    }, 5000);
  }

  obtenirNombreCaracteres(): number {
    return this.contact.message.length;
  }
}
