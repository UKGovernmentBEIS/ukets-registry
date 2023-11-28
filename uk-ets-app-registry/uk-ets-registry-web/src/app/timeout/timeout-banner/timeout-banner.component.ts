import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  Inject,
  Input,
  Output,
  Renderer2,
  ViewChild,
} from '@angular/core';
import { DOCUMENT } from '@angular/common';
import dialogPolyfill from 'dialog-polyfill';

@Component({
  selector: 'app-timeout-banner',
  templateUrl: './timeout-banner.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TimeoutBannerComponent implements AfterViewInit {
  @ViewChild('modal') readonly modal: ElementRef;

  @Input() offset: number;

  @Input()
  set isVisible(isVisible: boolean) {
    if (isVisible) {
      this.showDialog();
    } else {
      this.hideDialog();
    }
  }

  @Output() readonly logout: EventEmitter<void> = new EventEmitter<void>();
  @Output() readonly extend: EventEmitter<void> = new EventEmitter<void>();
  @Output() readonly escKeyDown: EventEmitter<void> = new EventEmitter<void>();

  private overlayClass = 'govuk-timeout-warning-overlay';
  private lastFocusedElement = null;

  constructor(
    @Inject(DOCUMENT) private readonly document: Document,
    private readonly renderer: Renderer2
  ) {}

  ngAfterViewInit(): void {
    dialogPolyfill.registerDialog(this.modal.nativeElement);
  }

  isDialogOpen(): boolean {
    if (this.modal) {
      return this.modal.nativeElement.getAttribute('open') === '';
    }
    return false;
  }

  showDialog(): void {
    if (!this.isDialogOpen()) {
      this.renderer.addClass(this.document.body, this.overlayClass);
      this.saveLastFocusedElement();
      this.modal.nativeElement.showModal();
      setTimeout(function () {
        const pageElement = document.getElementById('timeout-dialog');
        if (pageElement) {
          pageElement.setAttribute('tabindex', '-1');
          pageElement.focus();
        }
      }, 100);
    }
  }

  hideDialog(): void {
    if (this.isDialogOpen()) {
      const pageElement = document.getElementById('timeout-dialog');
      if (pageElement) {
        pageElement.removeAttribute('tabindex');
      }
      this.renderer.removeClass(this.document.body, this.overlayClass);
      this.modal.nativeElement.close();
      this.setFocusOnLastFocusedElement();
    }
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      this.escKeyDown.emit();
    }
  }

  saveLastFocusedElement(): void {
    this.lastFocusedElement = document.activeElement;
    if (!this.lastFocusedElement || this.lastFocusedElement === document.body) {
      this.lastFocusedElement = null;
    } else if (document.querySelector) {
      this.lastFocusedElement = document.querySelector(':focus');
    }
  }

  setFocusOnLastFocusedElement(): void {
    if (this.lastFocusedElement) {
      this.lastFocusedElement.focus();
    }
  }
}
