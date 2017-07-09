package bupt.networks.sip;

import com.sun.istack.internal.NotNull;

import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.message.Request;

/*
 * Created by Maou Lim on 2017/7/8.
 */
public class SipRequestBuilder {

    private static SipFactoryHelper sipFactoryHelper = SipFactoryHelper.getInstance();

    private SipAgent sipAgent = null;

    public SipRequestBuilder(@NotNull SipAgent sipAgent) {
        this.sipAgent = sipAgent;
    }

//    public Request createRegister(SipUserInfo userInfo) {
//        Address from = sipFactoryHelper.getAddressFactory().createAddress(
//            "sip:" + userInfo.getUserName() + "@" + userInfo.getDomain()
//        );
//        from.setDisplayName(userInfo.getUserName());
//        Address to = sipFactoryHelper.getAddressFactory().createAddress(
//            "sip:" + userInfo.getUserName() + "@" + userInfo.getDomain()
//        );
//        to.setDisplayName(userInfo.getUserName());
//
//        sipFactoryHelper.getAddressFactory().cre
//
//        Address contact = sipFactoryHelper.getAddressFactory().createAddress(
//            "sip:" +userInfo.getUserName() + "@" +
//        );
//
//    }

    public Request createMessage(@NotNull String to, String content) {
        return null;
    }
}
