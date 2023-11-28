import { KnowsThePage } from '../../util/knows-the-page.po';
import { RegistryScreen } from '../../util/screens';

export class KnowsTheSpecifytheRepresentativePage extends KnowsThePage {
  constructor() {
    super();
    this.name = RegistryScreen.SPECIFY_THE_REPRESENTATIVE;
    this.setUpTestData();
  }

  setUpTestData() {}

  getData(): Map<string, Promise<string>> {
    return new Map();
  }
}
