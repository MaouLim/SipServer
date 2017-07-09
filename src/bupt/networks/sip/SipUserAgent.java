package bupt.networks.sip;

import bupt.util.Configuration;

import javax.sip.*;
import java.util.TooManyListenersException;

/*
 * Created by Maou Lim on 2017/7/9.
 */
public abstract class SipUserAgent extends SipAgent {

    private SipAOR sipAOR = null;

    public SipUserAgent(Configuration configuration)
            throws InvalidArgumentException,
                   TransportNotSupportedException,
                   PeerUnavailableException,
                   ObjectInUseException,
                   TooManyListenersException {
        super(configuration);
        sipAOR = new SipAOR(
            (String) configuration.get("USER_NAME"),
            (String) (configuration.get("javax.sip.IP_ADDRESS") + ":" + configuration.get("LOCAL_PORT"))
        );
    }

    public SipAOR getSipAOR() {
        return sipAOR;
    }


}
