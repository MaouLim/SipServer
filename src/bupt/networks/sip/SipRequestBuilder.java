package bupt.networks.sip;

import com.sun.istack.internal.NotNull;

import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.address.SipURI;
import javax.sip.header.*;
import javax.sip.message.Request;
import java.util.ArrayList;
import java.util.List;

/*
 * Created by Maou Lim on 2017/7/8.
 */
public class SipRequestBuilder {

    private static SipFactoryHelper sipFactoryHelper = SipFactoryHelper.getInstance();
    private static AddressFactory addressFactory = sipFactoryHelper.getAddressFactory();
    private static HeaderFactory headerFactory = sipFactoryHelper.getHeaderFactory();

    private SipUserAgent sipUserAgent = null;

    public SipRequestBuilder(@NotNull SipUserAgent sipUserAgent) {
        assert (null != addressFactory && null != headerFactory);
        this.sipUserAgent = sipUserAgent;
    }

    public Request createInvite(@NotNull SipContactAOR targetContactAOR, String action) throws Exception {
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
                headerFactory.createCSeqHeader(1l, Request.INVITE);
        ContentTypeHeader contentTypeHeader =
                headerFactory.createContentTypeHeader("application", "action");

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
                requestURI, Request.INVITE, callIdHeader, cSeqHeader,
                fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        request.addHeader(contactHeader);
        request.setContent(action, contentTypeHeader);

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
                headerFactory.createCSeqHeader(1l, Request.BYE);

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
                requestURI, Request.BYE, callIdHeader, cSeqHeader,
                fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        request.addHeader(contactHeader);

        return request;
    }

    public Request createMessage(@NotNull SipContactAOR targetContactAOR, String content) throws Exception {
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
                headerFactory.createCSeqHeader(1l, Request.MESSAGE);

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
                headerFactory.createCSeqHeader(1l, Request.REGISTER);

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
                headerFactory.createCSeqHeader(1l, Request.SUBSCRIBE);

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
                headerFactory.createCSeqHeader(1l, Request.NOTIFY);

        ContentTypeHeader contentTypeHeader =
                headerFactory.createContentTypeHeader("text", "plain");
        EventHeader eventHeader = headerFactory.createEventHeader(event);

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
                requestURI, Request.NOTIFY, callIdHeader, cSeqHeader,
                fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        request.addHeader(contactHeader);
        request.addHeader(eventHeader);
        request.setContent(statusInfo, contentTypeHeader);

        return request;
    }

    public Request createPublish(@NotNull SipContactAOR targetContactAOR,
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
                headerFactory.createCSeqHeader(1l, Request.PUBLISH);

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

}
