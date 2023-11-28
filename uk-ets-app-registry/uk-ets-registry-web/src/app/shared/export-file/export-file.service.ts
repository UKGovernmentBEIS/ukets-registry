import { Injectable } from '@angular/core';
import * as FileSaver from 'file-saver';

@Injectable()
export class ExportFileService {
  /**
   * Utilizes FileSaver.js in order to save a file in the client
   *
   * @param blob the blob to save
   * @param fileName the fileName
   */
  export(blob: Blob, fileName: string) {
    FileSaver.saveAs(blob, fileName);
  }

  /**
   * Reads a content-disposition header e.g attachment; filename="Results.xls" and returns the filename string;
   *
   * @param header the content-disposition header
   */
  getContentDispositionFilename(header: string) {
    return header
      .split(';')[1]
      .trim()
      .split('=')[1]
      .replace(/"/g, '');
  }
}
