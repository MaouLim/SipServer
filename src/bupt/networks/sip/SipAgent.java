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

    //public static final String SIP_CONFIG_URL = "res/sip-config.yml";

    public static final String TCP = "tcp";
    public static final String UDP = "udp";

    private SipStack    sipStack    = null;
    private SipProvider sipProvider = null;
    private String      transport   = UDP;

    public SipAgent(Configuration configuration)
            throws InvalidArgumentException,
                   TransportNotSupportedException,
                   PeerUnavailableException,
                   ObjectInUseException,
                   TooManyListenersException {

        sipStack = SipFactoryHelper.getInstance().createSipStack(configuration);

        int localPort = Integer.valueOf((String) configuration.get("LOCAL_PORT"));
        this.transport = (String) configuration.get("TRANSPORT_PROTOCOL");

        assert (0 <= localPort && localPort < 65536);
        assert (TCP.equals(transport) || UDP.equals(transport));

        ListeningPoint point =
                sipStack.createListeningPoint(sipStack.getIPAddress(), localPort, transport);

        sipProvider = sipStack.createSipProvider(point);
        sipProvider.addSipListener(this);
    }

    public SipStack getSipStack() {
        return sipStack;
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
