import { TimezoneUtils } from '@shared/utils/timezone.utils';
import { empty } from '@shared/shared.util';
import { Section } from "@report-publication/model";

export class PublicationUtils {
  static convertPublicationTimeZone(section, convertToUTC): Section {
    let sectionDetails = section ? { ...section } : null;
    if (!empty(section)) {
      let convertedPublicationDateTime = null;
      if (!empty(sectionDetails.publicationStart)) {
        const publicationDateTime =
          sectionDetails.publicationStart +
          ' ' +
          sectionDetails.publicationTime;
        convertedPublicationDateTime = TimezoneUtils.calculateTimeZone(
          publicationDateTime,
          convertToUTC
        );
      }

      let convertedGenerationDateTime = null;
      if (!empty(sectionDetails.generationDate)) {
        let generationDateTime;
        if (empty(sectionDetails.generationTime)) {
          generationDateTime = sectionDetails.generationDate + ' 12:00:00';
        } else {
          generationDateTime =
            sectionDetails.generationDate + ' ' + sectionDetails.generationTime;
        }
        convertedGenerationDateTime = TimezoneUtils.calculateTimeZone(
          generationDateTime,
          convertToUTC
        );
      }

      sectionDetails = {
        ...sectionDetails,
        publicationStart: convertedPublicationDateTime
          ? convertedPublicationDateTime.date
          : sectionDetails.publicationStart,
        publicationTime: convertedPublicationDateTime
          ? convertedPublicationDateTime.time
          : sectionDetails.publicationTime,
        generationDate: convertedGenerationDateTime
          ? convertedGenerationDateTime.date
          : sectionDetails?.generationDate,
        generationTime: convertedGenerationDateTime
          ? convertedGenerationDateTime.time
          : sectionDetails?.generationTime,
      };
    }
    return sectionDetails;
  }
}
