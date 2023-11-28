import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { DocumentsRequestType } from '@shared/model/request-documents/documents-request-type';
import { RequestDocumentsOrigin } from '@shared/model/request-documents/request-documents-origin';

@Component({
  selector: 'app-documents-request-submitted',
  templateUrl: './documents-request-submitted.component.html',
})
export class DocumentsRequestSubmittedComponent implements OnInit {
  @Input()
  submittedRequestIdentifier: string;
  @Input()
  documentsRequestType: DocumentsRequestType;
  @Input()
  originatingPath: string;
  @Input()
  parentRequestId: string;
  @Input()
  origin: RequestDocumentsOrigin;
  @Output() readonly navigateToEmitter = new EventEmitter<any>();

  documentType: string;
  returnPage: string;

  ngOnInit(): void {
    if (this.documentsRequestType === DocumentsRequestType.USER) {
      this.documentType = 'user';
    } else {
      this.documentType = 'account holder';
    }
    if (this.origin === RequestDocumentsOrigin.USER) {
      this.returnPage = 'user details';
    } else if (this.origin === RequestDocumentsOrigin.ACCOUNT_DETAILS) {
      this.returnPage = 'account holder';
    } else {
      this.returnPage = 'task';
    }
  }

  navigateTo() {
    this.navigateToEmitter.emit({
      path: this.originatingPath,
      taskRequestId: this.parentRequestId,
      origin: this.origin,
    });
  }
}
