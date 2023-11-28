import { KnowsThePage } from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheViewAuthorisedRepresentativePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.VIEW_AUTHORISED_REPRESENTATIVE;
    this.setUpTestData();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }
}
