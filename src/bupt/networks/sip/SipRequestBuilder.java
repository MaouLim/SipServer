package bupt.networks.sip;

import com.sun.istack.internal.NotNull;

import javax.sip.Dialog;
import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/*
 * Created by Maou Lim on 2017/7/8.
 */
public class SipRequestBuilder {

    private static final AtomicLong sequence = new AtomicLong(0);

    private static SipFactoryHelper sipFactoryHelper = SipFactoryHelper.getInstance();
    private static AddressFactory addressFactory = sipFactoryHelper.getAddressFactory();
    private static HeaderFactory headerFactory = sipFactoryHelper.getHeaderFactory();

    private SipUserAgent sipUserAgent = null;

    public SipRequestBuilder(@NotNull SipUserAgent sipUserAgent) {
        assert (null != addressFactory && null != headerFactory);
        this.sipUserAgent = sipUserAgent;
    }

    public Request createInvite(@NotNull SipContactAOR targetContactAOR,
                                @NotNull String        action,
                                @NotNull String        args) throws Exception {
        SipContactAOR selfContactAOR = sipUserAgent.getContactAOR();

        FromHeader fromHeader = headerFactory.createFromHeader(
                selfContactAOR.getSipAOR().getSipAddress(),
                selfContactAOR.getUserName()
        );
        ToHeader toHeader =
                headerFactory.createToHeader(targetContactAOR.getSipAOR().getSipAddress(), null);

        SipURI requestURI = targetContactAOR.getSipURI();
        requestURI.setTransportParam(sipUserAgent.getTransport());
        ContactHeader contactHeader =
                headerFactory.createContactHeader(selfContactAOR.getSipAddress());

        List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = headerFactory.createViaHeader(
                sipUserAgent.getAddress(),
                sipUserAgent.getPort(),
                sipUserAgent.getTransport(),
                null
        );
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipUserAgent.getSipProvider().getNewCallId();
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
        CSeqHeader cSeqHeader =
                headerFactory.createCSeqHeader(sequence.getAndIncrement(), Request.INVITE);
        ContentTypeHeader contentTypeHeader =
                headerFactory.createContentTypeHeader("application", action);

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
                requestURI, Request.INVITE, callIdHeader, cSeqHeader,
                fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        request.addHeader(contactHeader);
        request.setContent(args, contentTypeHeader);

        return request;
    }

    public Request createBye(@NotNull SipContactAOR targetContactAOR) throws Exception {
        SipContactAOR selfContactAOR = sipUserAgent.getContactAOR();

        FromHeader fromHeader = headerFactory.createFromHeader(
                selfContactAOR.getSipAOR().getSipAddress(),
                selfContactAOR.getUserName()
        );
        ToHeader toHeader =
                headerFactory.createToHeader(targetContactAOR.getSipAOR().getSipAddress(), null);

        SipURI requestURI = targetContactAOR.getSipURI();
        requestURI.setTransportParam(sipUserAgent.getTransport());
        ContactHeader contactHeader =
                headerFactory.createContactHeader(selfContactAOR.getSipAddress());

        List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = headerFactory.createViaHeader(
                sipUserAgent.getAddress(),
                sipUserAgent.getPort(),
                sipUserAgent.getTransport(),
                null
        );
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipUserAgent.getSipProvider().getNewCallId();
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
        CSeqHeader cSeqHeader =
                headerFactory.createCSeqHeader(sequence.getAndIncrement(), Request.BYE);

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
                requestURI, Request.BYE, callIdHeader, cSeqHeader,
                fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        request.addHeader(contactHeader);

        return request;
    }

    public Request createMessage(@NotNull SipContactAOR targetContactAOR, String content) throws Exception {
        SipContactAOR selfContactAOR = sipUserAgent.getContactAOR();

        System.err.println(selfContactAOR.getSipAOR().getSipAddress().toString() + "\n" + selfContactAOR.getUserName());

        FromHeader fromHeader = headerFactory.createFromHeader(
                selfContactAOR.getSipAOR().getSipAddress(),
                selfContactAOR.getUserName()
        );
        ToHeader toHeader =
                headerFactory.createToHeader(targetContactAOR.getSipAOR().getSipAddress(), null);

        SipURI requestURI = targetContactAOR.getSipURI();
        requestURI.setTransportParam(sipUserAgent.getTransport());
        ContactHeader contactHeader =
                headerFactory.createContactHeader(selfContactAOR.getSipAddress());

        List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = headerFactory.createViaHeader(
                sipUserAgent.getAddress(),
                sipUserAgent.getPort(),
                sipUserAgent.getTransport(),
                null
        );
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipUserAgent.getSipProvider().getNewCallId();
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
        CSeqHeader cSeqHeader =
                headerFactory.createCSeqHeader(sequence.getAndIncrement(), Request.MESSAGE);

        ContentTypeHeader contentTypeHeader =
                headerFactory.createContentTypeHeader("text", "plain");

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
            requestURI, Request.MESSAGE, callIdHeader, cSeqHeader,
            fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        request.addHeader(contactHeader);
        request.setContent(content, contentTypeHeader);

        return request;
    }

    /**
     * @param expires for how long to register the current sip contact AOR
     */
    public Request createRegister(@NotNull SipContactAOR targetContactAOR, int expires)
            throws Exception {
        SipContactAOR selfContactAOR = sipUserAgent.getContactAOR();

        FromHeader fromHeader =
                headerFactory.createFromHeader(selfContactAOR.getSipAOR().getSipAddress(), selfContactAOR.getUserName());
        ToHeader toHeader =
                headerFactory.createToHeader(selfContactAOR.getSipAOR().getSipAddress(), null);

        List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = headerFactory.createViaHeader(
            sipUserAgent.getAddress(),
            sipUserAgent.getPort(),
            sipUserAgent.getTransport(),
            null
        );
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);

        SipURI uri = targetContactAOR.getSipURI();
        uri.setTransportParam(sipUserAgent.getTransport());

        CallIdHeader callIdHeader = sipUserAgent.getSipProvider().getNewCallId();
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
        CSeqHeader cSeqHeader =
                headerFactory.createCSeqHeader(sequence.getAndIncrement(), Request.REGISTER);

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
            uri, Request.REGISTER, callIdHeader, cSeqHeader,
            fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        Address contact = addressFactory.createAddress(
            selfContactAOR.toString() + ",transport=" + sipUserAgent.getTransport() +
            ";registering_acc=" + selfContactAOR.getSipAOR().getDomain()
        );

        ContactHeader contactHeader = headerFactory.createContactHeader(contact);
        ExpiresHeader expiresHeader = headerFactory.createExpiresHeader(expires);

        request.addHeader(contactHeader);
        request.addHeader(expiresHeader);

        return request;
    }

    /**
     * @param event for the name of the event to subscribe
     */
    public Request createSubscribe(@NotNull SipContactAOR targetContactAOR, String event) throws Exception {
        SipContactAOR selfContactAOR = sipUserAgent.getContactAOR();

        FromHeader fromHeader = headerFactory.createFromHeader(
                selfContactAOR.getSipAOR().getSipAddress(),
                selfContactAOR.getUserName()
        );
        ToHeader toHeader =
                headerFactory.createToHeader(targetContactAOR.getSipAOR().getSipAddress(), null);

        SipURI requestURI = targetContactAOR.getSipURI();
        requestURI.setTransportParam(sipUserAgent.getTransport());
        ContactHeader contactHeader =
                headerFactory.createContactHeader(selfContactAOR.getSipAddress());

        List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = headerFactory.createViaHeader(
                sipUserAgent.getAddress(),
                sipUserAgent.getPort(),
                sipUserAgent.getTransport(),
                null
        );
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipUserAgent.getSipProvider().getNewCallId();
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
        CSeqHeader cSeqHeader =
                headerFactory.createCSeqHeader(sequence.getAndIncrement(), Request.SUBSCRIBE);

        ContentTypeHeader contentTypeHeader =
                headerFactory.createContentTypeHeader("text", "plain");
        EventHeader eventHeader = headerFactory.createEventHeader(event);

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
                requestURI, Request.SUBSCRIBE, callIdHeader, cSeqHeader,
                fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        request.addHeader(contactHeader);
        request.addHeader(eventHeader);

        return request;
    }

    public Request createNotify(@NotNull SipContactAOR targetContactAOR,
                                String event,
                                String statusInfo) throws Exception {
        SipContactAOR selfContactAOR = sipUserAgent.getContactAOR();

        FromHeader fromHeader = headerFactory.createFromHeader(
                selfContactAOR.getSipAOR().getSipAddress(),
                selfContactAOR.getUserName()
        );
        ToHeader toHeader =
                headerFactory.createToHeader(targetContactAOR.getSipAOR().getSipAddress(), null);

        SipURI requestURI = targetContactAOR.getSipURI();
        requestURI.setTransportParam(sipUserAgent.getTransport());
        ContactHeader contactHeader =
                headerFactory.createContactHeader(selfContactAOR.getSipAddress());

        List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = headerFactory.createViaHeader(
                sipUserAgent.getAddress(),
                sipUserAgent.getPort(),
                sipUserAgent.getTransport(),
                null
        );
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipUserAgent.getSipProvider().getNewCallId();
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
        CSeqHeader cSeqHeader =
                headerFactory.createCSeqHeader(sequence.getAndIncrement(), Request.NOTIFY);

        ContentTypeHeader contentTypeHeader =
                headerFactory.createContentTypeHeader("text", "plain");
        EventHeader eventHeader = headerFactory.createEventHeader(event);

        SubscriptionStateHeader subscriptionStateHeader =
                headerFactory.createSubscriptionStateHeader(SubscriptionStateHeader.ACTIVE);

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
                requestURI, Request.NOTIFY, callIdHeader, cSeqHeader,
                fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );
        //final Request request = dialog.createRequest(Request.NOTIFY);

        request.addHeader(contactHeader);
        request.addHeader(eventHeader);
        request.setContent(statusInfo, contentTypeHeader);
        request.addHeader(subscriptionStateHeader);

        return request;
    }

    public Request createPublish(@NotNull SipContactAOR targetContactAOR,
                                          String        event,
                                          String        statusInfo) throws Exception {
        SipContactAOR selfContactAOR = sipUserAgent.getContactAOR();

        FromHeader fromHeader = headerFactory.createFromHeader(
                selfContactAOR.getSipAOR().getSipAddress(),
                selfContactAOR.getUserName()
        );
        ToHeader toHeader =
                headerFactory.createToHeader(targetContactAOR.getSipAOR().getSipAddress(), null);

        SipURI requestURI = targetContactAOR.getSipURI();
        requestURI.setTransportParam(sipUserAgent.getTransport());
        ContactHeader contactHeader =
                headerFactory.createContactHeader(selfContactAOR.getSipAddress());

        List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = headerFactory.createViaHeader(
                sipUserAgent.getAddress(),
                sipUserAgent.getPort(),
                sipUserAgent.getTransport(),
                null
        );
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipUserAgent.getSipProvider().getNewCallId();
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
        CSeqHeader cSeqHeader =
                headerFactory.createCSeqHeader(sequence.getAndIncrement(), Request.PUBLISH);

        ContentTypeHeader contentTypeHeader =
                headerFactory.createContentTypeHeader("text", "plain");
        EventHeader eventHeader = headerFactory.createEventHeader(event);

        RouteHeader routeHeader = headerFactory.createRouteHeader(targetContactAOR.getSipAddress());

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
                requestURI, Request.PUBLISH, callIdHeader, cSeqHeader,
                fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        request.addHeader(contactHeader);
        request.addHeader(eventHeader);
        request.setContent(statusInfo, contentTypeHeader);
        request.addHeader(routeHeader);

        return request;
    }

    public Request createAck(SipContactAOR targetContactAOR) throws Exception {
        SipContactAOR selfContactAOR = sipUserAgent.getContactAOR();

        FromHeader fromHeader = headerFactory.createFromHeader(
                selfContactAOR.getSipAOR().getSipAddress(),
                selfContactAOR.getUserName()
        );
        ToHeader toHeader =
                headerFactory.createToHeader(targetContactAOR.getSipAOR().getSipAddress(), null);

        SipURI requestURI = targetContactAOR.getSipURI();
        requestURI.setTransportParam(sipUserAgent.getTransport());
        ContactHeader contactHeader =
                headerFactory.createContactHeader(selfContactAOR.getSipAddress());

        List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = headerFactory.createViaHeader(
                sipUserAgent.getAddress(),
                sipUserAgent.getPort(),
                sipUserAgent.getTransport(),
                null
        );
        viaHeaders.add(viaHeader);

        CallIdHeader callIdHeader = sipUserAgent.getSipProvider().getNewCallId();
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
        CSeqHeader cSeqHeader =
                headerFactory.createCSeqHeader(sequence.getAndIncrement(), Request.ACK);

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
                requestURI, Request.ACK, callIdHeader, cSeqHeader,
                fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        request.addHeader(contactHeader);

        return request;
    }

    public static long getRequestCSeq(Request request) {
        CSeqHeader cSeqHeader = (CSeqHeader) request.getHeader(CSeqHeader.NAME);

        if (null == cSeqHeader) {
            return -1;
        }

        return cSeqHeader.getSeqNumber();
    }

}
