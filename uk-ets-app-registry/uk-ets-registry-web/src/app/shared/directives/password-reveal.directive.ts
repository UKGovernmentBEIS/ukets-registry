import { Directive, ElementRef, Renderer2 } from '@angular/core';

@Directive({
  selector: '[appPasswordReveal]',
})
export class PasswordRevealDirective {
  private _shown = false;

  constructor(private el: ElementRef, private renderer: Renderer2) {
    const parent = this.el.nativeElement.parentNode;

    const button = this.renderer.createElement('button');
    button.classList.add(
      'govuk-button',
      'govuk-button--secondary',
      'govuk-!-margin-bottom-0',
      'govuk-!-margin-left-1'
    );
    button.innerHTML = 'Show';
    button.tabIndex = -1; // prevent tab navigation
    button.type = 'button'; // prevent button click event being fired when pressing enter key (prevents also validation and scrolling)
    button.addEventListener('click', () => this.toggle(button));

    this.renderer.appendChild(parent, button);
  }

  toggle(button: HTMLElement) {
    this._shown = !this._shown;
    if (this._shown) {
      this.el.nativeElement.setAttribute('type', 'text');
      button.innerHTML = 'Hide';
    } else {
      this.el.nativeElement.setAttribute('type', 'password');
      button.innerHTML = 'Show';
    }
  }
}
