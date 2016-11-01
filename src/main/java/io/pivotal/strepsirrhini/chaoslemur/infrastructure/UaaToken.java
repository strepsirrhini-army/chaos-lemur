package io.pivotal.strepsirrhini.chaoslemur.infrastructure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UaaToken {

    private String access_token;
    private String token_type;
    private String expires_in;
    private String scope;
    private String jti;

    public UaaToken() {
    }

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}

	@Override
	public String toString() {
		return "UaaToken [access_token=" + access_token + ", token_type=" + token_type + ", expires_in=" + expires_in
				+ ", scope=" + scope + ", jti=" + jti + "]";
	}


}
