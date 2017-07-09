package bupt.networks.sip;

import javax.sip.*;
import java.util.TooManyListenersException;

/*
 * Created by Maou Lim on 2017/7/9.
 */
public class SipUserAgentClient extends SipAgent {

    private SipAOR sipAOR = null;

    //public SipUserAgentClient(String configURL)

    public SipUserAgentClient(String configURL, int localPort, String transport) throws InvalidArgumentException, TransportNotSupportedException, PeerUnavailableException, ObjectInUseException, TooManyListenersException {
        super(configURL, localPort, transport);
    }

    public SipUserAgentClient(int localPort) throws InvalidArgumentException, TransportNotSupportedException, TooManyListenersException, PeerUnavailableException, ObjectInUseException {
        super(localPort);
    }

    @Override
    public void processRequest(RequestEvent requestEvent) {

    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {

    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {

    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {

    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {

    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {

    }
}
