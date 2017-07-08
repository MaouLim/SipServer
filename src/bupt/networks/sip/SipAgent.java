package bupt.networks.sip;

import javax.sip.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.TooManyListenersException;

/*
 * Created by Maou Lim on 2017/7/8.
 */
public abstract class SipAgent implements SipListener {

    public static final String SIP_CONFIG_URL = "res/sip-config.yml";

    public static final String TCP = "tcp";
    public static final String UDP = "udp";

    private SipStack    sipStack    = null;
    private SipProvider sipProvider = null;

    public SipAgent(String configURL, int localPort)
            throws InvalidArgumentException,
                   TransportNotSupportedException,
                   PeerUnavailableException,
                   ObjectInUseException,
                   TooManyListenersException {

        sipStack = SipFactoryHelper.getInstance().createSipStack(configURL);
        ListeningPoint tcpPoint =
                sipStack.createListeningPoint(sipStack.getIPAddress(), localPort, TCP);
        ListeningPoint udpPoint =
                sipStack.createListeningPoint(sipStack.getIPAddress(), localPort, UDP);

        sipProvider = sipStack.createSipProvider(tcpPoint);
        sipProvider.addSipListener(this);

        sipProvider = sipStack.createSipProvider(udpPoint);
        sipProvider.addSipListener(this);
    }

    public SipAgent(int localPort)
            throws InvalidArgumentException,
                   TransportNotSupportedException,
                   TooManyListenersException,
                   PeerUnavailableException,
                   ObjectInUseException {
        this(SIP_CONFIG_URL, localPort);
    }

    public SipStack getSipStack() {
        return sipStack;
    }

    public String getAddress() {
        return sipStack.getIPAddress();
    }

    public int getPort() {
        return sipProvider.getListeningPoint(UDP).getPort();
    }

    public ClientTransaction
        sendRequestWithClientTransaction(Request request)
            throws SipException {
        ClientTransaction transaction = sipProvider.getNewClientTransaction(request);
        transaction.sendRequest();
        return transaction;
    }

    public ServerTransaction
        sendResponseForRequest(Request request, Response response)
            throws SipException, InvalidArgumentException {
        ServerTransaction transaction = sipProvider.getNewServerTransaction(request);
        transaction.sendResponse(response);
        return transaction;
    }
}
