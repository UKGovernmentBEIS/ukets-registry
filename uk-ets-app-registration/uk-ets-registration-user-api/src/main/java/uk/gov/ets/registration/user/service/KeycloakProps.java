/**
 * 
 */
package uk.gov.ets.registration.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Holds configuration properties for Keycloak server.
 */
@Component
@ConfigurationProperties(prefix = "user.registration.keycloak")
@Validated
public class KeycloakProps {

	private String serverUrl = "http://localhost:8080/auth";
	private String realm = "uk-ets";
	private String username = "uk-ets-admin";
	private String pasword = "uk-ets-admin";
	private String clientId = "admin-cli";
	private String verifyEmailRedirectUrl = "http://localhost:4200";
	@Value("${user.registration.keycloak.verifyEmailExpiration:60}")
	private Long verifyEmailExpiration;
	// Added for new emailFrom property UKETS-3052
	private String emailFrom="keycloak-ukets-dev@trasys.gr";
	// Added for new emailSubject property UKETS-3052
	private String emailSubject="Confirm your email address – UK Emissions Trading Registry";
	
	public String getServerUrl() {
		return serverUrl;
	}

	public String getRealm() {
		return realm;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPasword(String pasword) {
		this.pasword = pasword;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUsername() {
		return username;
	}

	public String getPasword() {
		return pasword;
	}

	public String getClientId() {
		return clientId;
	}

	public String getVerifyEmailRedirectUrl() {
		return verifyEmailRedirectUrl;
	}

	public void setVerifyEmailRedirectUrl(String verifyEmailRedirectUrl) {
		this.verifyEmailRedirectUrl = verifyEmailRedirectUrl;
	}

	public Long getVerifyEmailExpiration() {
		return verifyEmailExpiration;
	}

	public void setVerifyEmailExpiration(Long verifyEmailExpiration) {
		this.verifyEmailExpiration = verifyEmailExpiration;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
}
