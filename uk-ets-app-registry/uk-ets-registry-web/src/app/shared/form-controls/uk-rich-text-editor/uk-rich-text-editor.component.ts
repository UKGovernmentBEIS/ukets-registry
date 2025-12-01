import {
  AfterViewInit,
  Component,
  ElementRef,
  Input,
  OnInit,
  Renderer2,
  ViewChild,
} from '@angular/core';
import { CustomModule, QuillModules, QuillToolbarConfig } from 'ngx-quill';
import { ControlContainer, FormGroupDirective } from '@angular/forms';
import { UkProtoFormComponent } from '@shared/form-controls/uk-proto-form.component';
import { CustomLink } from '@shared/components/notifications/custom-link';
import { CustomParchmentBlock } from '@shared/components/notifications/custom-parchment-block';

@Component({
  selector: 'app-uk-rich-text-editor',
  templateUrl: './uk-rich-text-editor.component.html',
  styleUrls: ['./uk-rich-text-editor.component.scss'],
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkRichTextEditorComponent
  extends UkProtoFormComponent
  implements OnInit, AfterViewInit
{
  readonly formattingButtons = ['bold', 'italic', 'underline'];
  readonly listButtons = [{ list: 'ordered' }, { list: 'bullet' }];
  readonly linkButton = ['link'];
  // whitelist of allowed formats from copy/paste.
  // NOTE: indent is needed for nested lists (not only for copy/paste)
  readonly formats = ['bold', 'italic', 'underline', 'link', 'list', 'indent'];

  @ViewChild('quillis') quillisInput: ElementRef;

  @Input() labelTitleBold = false;
  @Input() moreInfo: string;
  // When true, it enables bold, italic and underline toolbar buttons
  @Input() showFormattingButtons: boolean;
  @Input() maxlength = 256;

  // NOTE: according to the docs it should be possible to configure this globally,
  // but it did not work.
  customModules: CustomModule[] = [
    {
      implementation: CustomParchmentBlock,
      path: 'blots/block',
    },

    {
      implementation: CustomLink,
      path: 'formats/link',
    },
  ];

  constructor(
    parentF: FormGroupDirective,
    private renderer: Renderer2
  ) {
    super(parentF);
  }

  ngOnInit(): void {
    super.ngOnInit();
  }

  getModules(): QuillModules {
    return {
      toolbar: this.getToolbarOptions(),
      keyboard: this.getKeyboardWithBindings(),
    };
  }

  /**
   * Conditionally add the formatting buttons in the toolbar.
   */
  private getToolbarOptions(): QuillToolbarConfig {
    let allButtons = [];
    if (this.showFormattingButtons) {
      allButtons = [...allButtons, this.formattingButtons];
    }
    return [...allButtons, this.listButtons, this.linkButton];
  }

  /**
   * Conditionally disable the formatting keyboard bindings in the editor.
   */
  private getKeyboardWithBindings() {
    let bindings;
    if (!this.showFormattingButtons) {
      bindings = {
        bold: this.disableControlBindingForKey('B'),
        italic: this.disableControlBindingForKey('I'),
        underline: this.disableControlBindingForKey('U'),
      };
    }
    return { bindings };
  }

  /**
   * The handler created here returns false which is equivalent to disabling
   * the key binding.
   *
   * NOTE: (If we want to do some action and then delegate to default binding
   * we could return true here).
   *
   * NOTE 2: "shortKey" here is metaKey on a Mac and
   * ctrlKey on Linux and Windows.
   */
  private disableControlBindingForKey(key: string) {
    return {
      key: key,
      shortKey: true,
      handler: () => false,
    };
  }

  ngAfterViewInit(): void {
    if (this.quillisInput) {
      setTimeout(() => {
        const qlEditorElement =
          this.quillisInput['elementRef'].nativeElement?.querySelector(
            '.ql-editor'
          );
        this.renderer.setAttribute(qlEditorElement, 'aria-multiline', 'true');
        this.renderer.setAttribute(qlEditorElement, 'type', 'text');
        this.renderer.setAttribute(qlEditorElement, 'area-label', 'Content');
        qlEditorElement.addEventListener('keydown', this.removeFocusOnEscape);

        const orderButton = this.quillisInput[
          'elementRef'
        ].nativeElement?.querySelector('button[value="ordered"]');
        this.renderer.setAttribute(orderButton, 'aria-label', 'order button');
        const bulletButton = this.quillisInput[
          'elementRef'
        ].nativeElement?.querySelector('button[value="bullet"]');
        this.renderer.setAttribute(bulletButton, 'aria-label', 'bullet button');
        const linkButton = this.quillisInput[
          'elementRef'
        ].nativeElement?.querySelector('button[class="ql-link"]');
        this.renderer.setAttribute(linkButton, 'aria-label', 'link button');
      }, 0);
    }
  }

  removeFocusOnEscape(event) {
    const elem = event.target as HTMLElement;
    if (event.keyCode == 27 || event.key === 'Escape') {
      elem.blur();
    }
  }
}
