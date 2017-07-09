package bupt.networks.sip;

import javax.sip.address.Address;
import javax.sip.address.SipURI;

/*
 * Created by Maou Lim on 2017/7/8.
 */
public class SipAOR {

    private String userName = null;
    private String domain   = null;

    public SipAOR(String userName, String domain) {
        this.userName = userName;
        this.domain = domain;
    }

    public SipAOR(String aor) {
        String[] splits = aor.split("@");
        assert (2 == splits.length);
        this.userName = splits[0].substring(splits[0].indexOf(':') + 1);
        this.domain = splits[1];
    }

    public String getUserName() {
        return userName;
    }

    public String getDomain() {
        return domain;
    }

    public String getIpAddress() {
        int index = domain.indexOf(':');
        return index < 0 ? domain : domain.substring(0, index);
    }

    public String getAOR() {
        return "sip:" + userName + "@" + domain;
    }

    public SipURI getSipURI() {
        try {
            return SipFactoryHelper.
                    getInstance().getAddressFactory().createSipURI(userName, domain);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Address getSipAddress() {
        try {
            Address address = SipFactoryHelper.
                    getInstance().getAddressFactory().createAddress(getSipURI());
            address.setDisplayName(userName);
            return address;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
