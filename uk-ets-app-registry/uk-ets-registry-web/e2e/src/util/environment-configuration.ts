export interface EnvironmentConfiguration {
  emulatorUrl: string;
  keycloakBaseUrl: string;
  keycloakUsername: string;
  keycloakPassword: string;
  mailhogProtocol: string;
  mailhogHost: string;
  mailhogPort: number;
  postgrestRegistryBaseUrl: string;
  postgrestTransactionLogBaseUrl: string;
  actuatorInfoUrl: string;
  registryApiUrl: string;
}

export function environment(): EnvironmentConfiguration {
  return {
    emulatorUrl: 'http://localhost:7701/itl-ws/action',
    keycloakBaseUrl: 'http://localhost:8091/auth',
    keycloakUsername: 'uk-ets-admin',
    keycloakPassword: 'uk-ets-admin',
    mailhogProtocol: 'http:',
    mailhogHost: 'localhost',
    mailhogPort: 8025,
    postgrestRegistryBaseUrl: 'http://localhost:3000',
    postgrestTransactionLogBaseUrl: 'http://localhost:3001',
    actuatorInfoUrl: `http://localhost:8080/api-registry/actuator/info`,
    registryApiUrl: `http://localhost:8080/api-registry`,
  };
}
