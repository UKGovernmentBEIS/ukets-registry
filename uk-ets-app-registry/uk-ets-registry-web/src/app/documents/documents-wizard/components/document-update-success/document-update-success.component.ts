import { Component, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { DocumentUpdateType } from '@registry-web/documents/models/document-update-type.model';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  standalone: true,
  selector: 'app-document-update-success',
  imports: [RouterModule, SharedModule],
  templateUrl: './document-update-success.component.html',
})
export class DocumentUpdateSuccessComponent implements OnInit {
  @Input()
  storedUpdateType: DocumentUpdateType;

  text: string;

  ngOnInit() {
    switch (this.storedUpdateType) {
      case DocumentUpdateType.ADD_DOCUMENT_CATEGORY:
        this.text = 'The category will appear in the documents page.';
        break;
      case DocumentUpdateType.UPDATE_DOCUMENT_CATEGORY:
        this.text =
          'The category will appear updated according to the new changes.';
        break;
      case DocumentUpdateType.DELETE_DOCUMENT_CATEGORY:
        this.text = 'The category will not be available in the documents page.';
        break;
      case DocumentUpdateType.ADD_DOCUMENT:
        this.text = 'The document will be available in the list of documents.';
        break;
      case DocumentUpdateType.UPDATE_DOCUMENT:
        this.text =
          'The updated document will be available in the list of documents.';
        break;
      case DocumentUpdateType.DELETE_DOCUMENT:
        this.text =
          'The document will not be available in the list of documents.';
        break;
      default:
        break;
    }
  }
}
