export enum EnvironmentalActivity {
  AFFORESTATION_AND_REFORESTATION = 'AFFORESTATION_AND_REFORESTATION',
  DEFORESTATION = 'DEFORESTATION',
  FOREST_MANAGEMENT = 'FOREST_MANAGEMENT',
  CROPLAND_MANAGEMENT = 'CROPLAND_MANAGEMENT',
  GRAZING_LAND_MANAGEMENT = 'GRAZING_LAND_MANAGEMENT',
  REVEGETATION = 'REVEGETATION',
  WETLAND_DRAINAGE_AND_REWETTING = 'WETLAND_DRAINAGE_AND_REWETTING'
}

export const EnvironmentalActivityLabelMap = new Map<
  EnvironmentalActivity,
  string
>([
  [
    EnvironmentalActivity.AFFORESTATION_AND_REFORESTATION,
    'Afforestation and reforestation'
  ],
  [EnvironmentalActivity.DEFORESTATION, 'Deforestation'],
  [EnvironmentalActivity.FOREST_MANAGEMENT, 'Forest management'],
  [EnvironmentalActivity.CROPLAND_MANAGEMENT, 'Cropland management'],
  [EnvironmentalActivity.GRAZING_LAND_MANAGEMENT, 'Grazing land management'],
  [EnvironmentalActivity.REVEGETATION, 'Revegetation'],
  [
    EnvironmentalActivity.WETLAND_DRAINAGE_AND_REWETTING,
    'Wetland, Drainage and rewetting'
  ]
]);
