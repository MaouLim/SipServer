package bupt.networks.sip;

import bupt.util.Configuration;

import javax.sip.*;
import java.util.TooManyListenersException;

/*
 * Created by Maou Lim on 2017/7/9.
 */
public abstract class SipUserAgent extends SipAgent {

    private SipContactAOR contactAOR = null;

    public SipUserAgent(Configuration sipConfig,
                        SipContactAOR contactAOR,
                        String transport)
            throws InvalidArgumentException,
                   TransportNotSupportedException,
                   PeerUnavailableException,
                   ObjectInUseException,
                   TooManyListenersException {
        super(sipConfig, contactAOR.getPort(), transport);

        if (!super.getAddress().equals(contactAOR.getAddress())) {
            throw new InvalidArgumentException("the contactAOR doesn't match to this sipAgent");
        }

        this.contactAOR = contactAOR;
    }

    public SipAOR getSipAOR() {
        return contactAOR.getSipAOR();
    }

    public SipContactAOR getContactAOR() {
        return contactAOR;
    }
}
