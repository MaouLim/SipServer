package bupt.networks.sip;

import bupt.util.Configuration;

import javax.sip.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.TooManyListenersException;

/*
 * Created by Maou Lim on 2017/7/8.
 */
public abstract class SipAgent implements SipListener {

    public static final String TCP = "tcp";
    public static final String UDP = "udp";

    private SipStack    sipStack    = null;
    private SipProvider sipProvider = null;
    private String      transport   = UDP;

    public SipAgent(Configuration sipConfig, int port, String transport)
            throws InvalidArgumentException,
                   TransportNotSupportedException,
                   PeerUnavailableException,
                   ObjectInUseException,
                   TooManyListenersException {
        if ((port < 0 || 65535 < port) ||
            (!TCP.equals(transport) && !UDP.equals(transport))) {
            throw new InvalidArgumentException("invalid arg: " + port + ", " + transport);
        }

        this.transport = transport;

        this.sipStack = SipFactoryHelper.getInstance().createSipStack(sipConfig);
        ListeningPoint point =
                sipStack.createListeningPoint(this.sipStack.getIPAddress(), port, this.transport);

        this.sipProvider = this.sipStack.createSipProvider(point);
        this.sipProvider.addSipListener(this);
    }

    public SipProvider getSipProvider() {
        return sipProvider;
    }

    public String getAddress() {
        return sipStack.getIPAddress();
    }

    public int getPort() {
        return sipProvider.getListeningPoint(transport).getPort();
    }

    public String getTransport() {
        return transport;
    }

//    public ClientTransaction
//        sendRequestWithClientTransaction(Request request)
//            throws SipException {
//        ClientTransaction transaction = sipProvider.getNewClientTransaction(request);
//        transaction.sendRequest();
//        return transaction;
//    }
//
//    public ServerTransaction
//        sendResponseForRequest(Request request, Response response)
//            throws SipException, InvalidArgumentException {
//        ServerTransaction transaction = sipProvider.getNewServerTransaction(request);
//        transaction.sendResponse(response);
//        return transaction;
//    }
}
