import { Pipe, PipeTransform } from '@angular/core';
import { FileBase } from '@registry-web/shared/model/file';

/**
 * A pipe to get the uploaded file metadata from its name
 */
@Pipe({
  name: 'uploadedFile',
})
export class UploadedFilePipe implements PipeTransform {
  transform(documentName: string, uploadedFiles: any[], difference: any): any {
    const fileId = difference?.uploadedFileNameIdMap[documentName];
    return uploadedFiles?.find((f) => f.id === fileId);
  }
}

/**
 * A pipe to get the document name of a file
 */
@Pipe({
  name: 'documentName',
})
export class DocumentNamePipe implements PipeTransform {
  transform(file: FileBase, difference: string): any {
    const diff = JSON.parse(difference);
    return Object.keys(diff?.uploadedFileNameIdMap).find(
      (key) => diff.uploadedFileNameIdMap[key] === file.id
    );
  }
}
