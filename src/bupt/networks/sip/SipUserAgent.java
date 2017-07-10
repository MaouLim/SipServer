package bupt.networks.sip;

import bupt.util.Configuration;

import javax.sip.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.TooManyListenersException;

/*
 * Created by Maou Lim on 2017/7/9.
 */
public abstract class SipUserAgent extends SipAgent {

    private SipContactAOR contactAOR = null;

    public SipUserAgent(Configuration sipConfig,
                        SipContactAOR contactAOR,
                        String        transport)
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

    public SipRequestBuilder createRequestBuilder() {
        return new SipRequestBuilder(this);
    }

    public SipResponseBuilder createResponseBuilder() {
        return new SipResponseBuilder(this);
    }

    public SipAOR getSipAOR() {
        return contactAOR.getSipAOR();
    }

    public SipContactAOR getContactAOR() {
        return contactAOR;
    }

    public void sendRequest(Request request) throws SipException {
        getSipProvider().sendRequest(request);
    }

    public ClientTransaction sendRequestByTransaction(Request request)
            throws SipException {
        ClientTransaction transaction = getSipProvider().getNewClientTransaction(request);
        transaction.sendRequest();
        return transaction;
    }

    public void sendResponse(Response response) throws SipException {
        getSipProvider().sendResponse(response);
    }

    public ServerTransaction sendResponseByTransaction(Request request, Response response)
            throws SipException, InvalidArgumentException {
        ServerTransaction transaction = getSipProvider().getNewServerTransaction(request);
        transaction.sendResponse(response);
        return transaction;
    }

}
