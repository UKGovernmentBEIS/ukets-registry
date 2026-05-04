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
  getContentDispositionFilename(header: string | null): string {
    if (!header) return 'download';

    const filenameStarMatch = header.match(/filename\*\s*=\s*UTF-8''([^;]+)/i);
    if (filenameStarMatch) {
      return decodeURIComponent(filenameStarMatch[1]);
    }

    const filenameMatch = header.match(/filename\s*=\s*"?([^";]+)"?/i);
    if (filenameMatch) {
      return filenameMatch[1];
    }

    return 'download';
  }
}
