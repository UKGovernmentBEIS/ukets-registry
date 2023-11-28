import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-back-to-top',
  templateUrl: './back-to-top.component.html'
})
export class BackToTopComponent {
  backToTop() {
    window.scrollTo(0, 0);
    document.getElementById('header').focus();
  }
}
