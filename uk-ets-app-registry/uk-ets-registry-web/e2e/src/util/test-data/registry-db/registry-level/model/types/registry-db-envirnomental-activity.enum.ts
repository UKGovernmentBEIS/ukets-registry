export enum RegistryDbEnvirnomentalActivityEnum {
  AFFORESTATION_AND_REFORESTATION = 'AFFORESTATION_AND_REFORESTATION',
  DEFORESTATION = 'DEFORESTATION',
  FOREST_MANAGEMENT = 'FOREST_MANAGEMENT',
  CROPLAND_MANAGEMENT = 'CROPLAND_MANAGEMENT',
  GRAZING_LAND_MANAGEMENT = 'GRAZING_LAND_MANAGEMENT',
  REVEGETATION = 'REVEGETATION',
  WETLAND_DRAINAGE_AND_REWETTING = 'WETLAND_DRAINAGE_AND_REWETTING',
}

export function getEnvironmentalActivity(value: string) {
  switch (value) {
    case 'Afforestation':
      return RegistryDbEnvirnomentalActivityEnum.AFFORESTATION_AND_REFORESTATION;
    case 'Deforestation':
      return RegistryDbEnvirnomentalActivityEnum.DEFORESTATION;
    case 'Forest management':
      return RegistryDbEnvirnomentalActivityEnum.FOREST_MANAGEMENT;
    case 'Cropland management':
      return RegistryDbEnvirnomentalActivityEnum.CROPLAND_MANAGEMENT;
    case 'Grazing land management':
      return RegistryDbEnvirnomentalActivityEnum.GRAZING_LAND_MANAGEMENT;
    case 'Revegetation':
      return RegistryDbEnvirnomentalActivityEnum.REVEGETATION;
    case 'Wetland drainage and rewetting':
      return RegistryDbEnvirnomentalActivityEnum.WETLAND_DRAINAGE_AND_REWETTING;
    default:
      return null;
  }
}
