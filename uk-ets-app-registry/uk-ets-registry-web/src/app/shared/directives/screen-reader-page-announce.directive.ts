import {
  AfterContentInit,
  Directive,
  ElementRef,
  Input,
  OnInit,
  Renderer2,
} from '@angular/core';

@Directive({
  selector: '[appScreenReaderPageAnnounce]',
})
export class ScreenReaderPageAnnounceDirective
  implements OnInit, AfterContentInit {
  @Input()
  pageTitle: string;

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  ngOnInit() {
    const child = document.createElement('p');
    child.setAttribute('aria-live', 'polite');
    child.setAttribute('id', 'pageTitleEl');
    this.renderer.appendChild(this.el.nativeElement, child);
  }

  ngAfterContentInit(): void {
    const title = this.pageTitle;
    setTimeout(function () {
      const pageTitleElement = document.getElementById('pageTitleEl');
      if (pageTitleElement) {
        pageTitleElement.innerText = title + ' page';
        pageTitleElement.className = 'govuk-visually-hidden';
        pageTitleElement.focus();
      }
    }, 100);
  }
}
