package org.stoevesand.findow.model;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.stoevesand.findow.provider.ApiUser;
import org.stoevesand.findow.provider.BankingAPI;
import org.stoevesand.findow.provider.figo.FigoTokenService;
import org.stoevesand.findow.provider.finapi.FinapiTokenService;
import org.stoevesand.findow.rest.RestUtils;
import org.stoevesand.findow.server.FindowSystem;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "User")
@Table(name = "USERS")
public class FinUser {

    // internal id used for persistance
    private Long id;
    private String name = "";
    private String backendName = "";
    private String backendSecret = "";
    private String api = "";

    private transient FinToken token = null;
    private List<FinAccount> accounts;

    @Column(name = "NAME")
    @JsonGetter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "BACKEND_NAME")
    @JsonIgnore
    public String getBackendName() {
        return backendName;
    }

    public void setBackendName(String backendName) {
        this.backendName = backendName;
    }

    @Column(name = "BACKEND_SECRET")
    @JsonIgnore
    public String getBackendSecret() {
        return backendSecret;
    }

    public void setBackendSecret(String backendSecret) {
        this.backendSecret = backendSecret;
    }

    public FinUser() {
    }

    public FinUser(String name, String backendName, String backendSecret, String api) {
        this.name = name;
        this.backendName = backendName;
        this.backendSecret = backendSecret;
        this.api = api;
    }

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    @Column(name = "USER_ID")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true) //
    public List<FinAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<FinAccount> a) {
        this.accounts = a;
    }

    @Transient
    @JsonIgnore
    public String getToken() {
        String ret = "";
        if ((token == null) || (!token.isValid())) {
            try {
                BankingAPI bankingAPI = FindowSystem.getBankingAPI(this);

                token = bankingAPI.requestUserToken(backendName, backendSecret);
                if (token != null) {
                    ret = token.getToken();
                }
            } catch (FinErrorHandler e) {
                token = null;
                e.printStackTrace();
            }
        }
        return ret;
    }

    public void setToken(FinToken token) {
        this.token = token;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    public FinAccount getAccount(Long accountId) {

        for (FinAccount acc : accounts) {
            if (acc.getId().equals(accountId)) {
                return acc;
            }
        }

        return null;
    }

    public void removeAccount(FinAccount account) {
        if (this.accounts != null) {
            boolean removed = getAccounts().remove(account);
            account.setUser(null);
        }
    }

    public void removeAccount(Long accountId) {
        List<FinAccount> remacc = new Vector<FinAccount>();
        for (FinAccount account : getAccounts()) {
            if (account.getId().equals(accountId)) {
                account.setUser(null);
                remacc.add(account);
            }
        }
        getAccounts().removeAll(remacc);
    }

    public FinToken refreshToken() throws FinErrorHandler {
        FinToken userToken;
        if ("FIGO".equals(getApi())) {
            String clientId = FindowSystem.getBankingAPI(this).getClientId();
            String clientSecret = FindowSystem.getBankingAPI(this).getClientSecret();

            userToken = FigoTokenService.requestUserToken(clientId, clientSecret, getBackendName(), getBackendSecret());
        } else {
            userToken = FinapiTokenService.requestUserToken(RestUtils.getClientToken(), getBackendName(), getBackendSecret());
        }
        setToken(userToken);
        return userToken;
    }

    public void addAccount(FinAccount account) {
        if (this.accounts != null) {
            accounts.add(account);
            account.setUser(this);
        }
    }

    public void addAccounts(List<FinAccount> accs) {
        for (FinAccount account : accs) {
            addAccount(account);
        }
    }

}
