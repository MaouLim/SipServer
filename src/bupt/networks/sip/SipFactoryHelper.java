package bupt.networks.sip;

import bupt.util.Configuration;

import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import javax.sip.SipStack;
import javax.sip.address.AddressFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;

/*
 * Created by Maou Lim on 2017/7/8.
 */
public class SipFactoryHelper {

    public static final String PATH_NAME = "gov.nist";

    private SipFactory     sipFactory     = null;
    private AddressFactory addressFactory = null;
    private HeaderFactory  headerFactory  = null;
    private MessageFactory messageFactory = null;

    private static SipFactoryHelper sipFactoryHelper = null;

    public static SipFactoryHelper getInstance() {
        if (null == sipFactoryHelper) {
            sipFactoryHelper = new SipFactoryHelper();
        }

        return sipFactoryHelper;
    }

    public AddressFactory getAddressFactory() throws PeerUnavailableException {
        if (null == addressFactory) {
            addressFactory = sipFactory.createAddressFactory();
        }

        return addressFactory;
    }

    public HeaderFactory getHeaderFactory() throws PeerUnavailableException {
        if (null == headerFactory) {
            headerFactory = sipFactory.createHeaderFactory();
        }

        return headerFactory;
    }

    public MessageFactory getMessageFactory() throws PeerUnavailableException {
        if (null == messageFactory) {
            messageFactory = sipFactory.createMessageFactory();
        }

        return messageFactory;
    }

    public SipStack createSipStack(String propertiesResource) throws PeerUnavailableException {
        return sipFactory.createSipStack(
             SipConfigBuilder.readFrom(new Configuration(propertiesResource))
        );
    }

    private SipFactoryHelper() {
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName(PATH_NAME);
    }

}
