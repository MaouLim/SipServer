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

    public AddressFactory getAddressFactory() {
        if (null == addressFactory) {
            try {
                addressFactory = sipFactory.createAddressFactory();
            }
            catch (PeerUnavailableException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        return addressFactory;
    }

    public HeaderFactory getHeaderFactory() {
        if (null == headerFactory) {
            try {
                headerFactory = sipFactory.createHeaderFactory();
            }
            catch (PeerUnavailableException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        return headerFactory;
    }

    public MessageFactory getMessageFactory() {
        if (null == messageFactory) {
            try {
                messageFactory = sipFactory.createMessageFactory();
            }
            catch (PeerUnavailableException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        return messageFactory;
    }

    public SipStack createSipStack(Configuration configuration) throws PeerUnavailableException {
        return sipFactory.createSipStack(
             SipConfigBuilder.readFrom(configuration)
        );
    }

    private SipFactoryHelper() {
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName(PATH_NAME);
    }

}
