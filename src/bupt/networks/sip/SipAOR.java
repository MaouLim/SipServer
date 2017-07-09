package bupt.networks.sip;

import gov.nist.javax.sip.clientauthutils.AccountManager;
import gov.nist.javax.sip.clientauthutils.UserCredentials;

import javax.sip.ClientTransaction;

/*
 * Created by Maou Lim on 2017/7/8.
 */
public class SipUserInfo implements AccountManager {

    private String userName = null;
    private String password = null;
    private String domain   = null;

    public SipUserInfo(String userName,
                       String password,
                       String domain) {
        this.userName = userName;
        this.password = password;
        this.domain = domain;
    }

    @Override
    public UserCredentials
        getCredentials(ClientTransaction challengedTransaction, String realm) {

        return new UserCredentials() {
            @Override
            public String getUserName() {
                return userName;
            }

            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public String getSipDomain() {
                return domain;
            }
        };
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getDomain() {
        return domain;
    }

}
