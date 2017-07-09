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

//    public Request createRegister(SipAOR userInfo) {
//        Address from = sipFactoryHelper.getAddressFactory().createAddress(
//            "sip:" + userInfo.getUserName() + "@" + userInfo.getDomain()
//        );
//        from.setDisplayName(userInfo.getUserName());
//        Address to = sipFactoryHelper.getAddressFactory().createAddress(
//            "sip:" + userInfo.getUserName() + "@" + userInfo.getDomain()
//        );
//        to.setDisplayName(userInfo.getUserName());
//
//        sipFactoryHelper.getAddressFactory().cre
//
//        Address contact = sipFactoryHelper.getAddressFactory().createAddress(
//            "sip:" +userInfo.getUserName() + "@" +
//        );
//
//    }

    private class SipRequestProperties {

        public SipURI uri = null;
        public String type = null;
        public CallIdHeader callIdHeader = null;
        public CSeqHeader cSeqHeader = null;
        public FromHeader fromHeader = null;
        public ToHeader toHeader = null;
        public List<ViaHeader> viaHeaders = null;
        public MaxForwardsHeader maxForwardsHeader = null;

    }

    public Request createMessage(@NotNull SipAOR toAOR, String content) throws Exception {
        SipAOR selfAOR = sipUserAgent.getSipAOR();
        FromHeader fromHeader =
                headerFactory.createFromHeader(selfAOR.getSipAddress(), selfAOR.getUserName());
        ToHeader toHeader =
                headerFactory.createToHeader(toAOR.getSipAddress(), null);
        SipURI uri = toAOR.getSipURI();
        uri.setTransportParam(SipAgent.UDP);

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

        SipURI contactURI = addressFactory.createSipURI(selfAOR.getUserName(), sipUserAgent.getAddress());
        contactURI.setPort(sipUserAgent.getPort());
        Address contact = addressFactory.createAddress(contactURI);
        contact.setDisplayName(selfAOR.getUserName());

        ContactHeader contactHeader = headerFactory.createContactHeader(contact);

        ContentTypeHeader contentTypeHeader =
                headerFactory.createContentTypeHeader("text", "plain");

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
            uri, Request.MESSAGE, callIdHeader, cSeqHeader,
            fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        request.addHeader(contactHeader);
        request.setContent(content, contentTypeHeader);

        return request;
    }

    public Request createRegister(@NotNull SipAOR toAOR) throws Exception {
        SipAOR selfAOR = sipUserAgent.getSipAOR();

        FromHeader fromHeader =
                headerFactory.createFromHeader(selfAOR.getSipAddress(), selfAOR.getUserName());
        ToHeader toHeader =
                headerFactory.createToHeader(toAOR.getSipAddress(), null);

        List<ViaHeader> viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = headerFactory.createViaHeader(
            sipUserAgent.getAddress(),
            sipUserAgent.getPort(),
            sipUserAgent.getTransport(),
            null
        );
        viaHeader.setRPort();
        viaHeaders.add(viaHeader);

        SipURI uri = toAOR.getSipURI();
        uri.setTransportParam(SipAgent.UDP);

        CallIdHeader callIdHeader = sipUserAgent.getSipProvider().getNewCallId();
        MaxForwardsHeader maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);
        CSeqHeader cSeqHeader =
                headerFactory.createCSeqHeader(1l, Request.REGISTER);

        final Request request = sipFactoryHelper.getMessageFactory().createRequest(
            uri, Request.REGISTER, callIdHeader, cSeqHeader,
            fromHeader, toHeader, viaHeaders, maxForwardsHeader
        );

        Address contact = addressFactory.createAddress(
            "sip:" + selfAOR.getUserName() + "@" + sipUserAgent.getAddress() + ":" + sipUserAgent.getPort() +
            ",transport=" + sipUserAgent.getTransport() + ";registering_acc=" + toAOR.getIpAddress()
        );

        ContactHeader contactHeader = headerFactory.createContactHeader(contact);
        ExpiresHeader expiresHeader = headerFactory.createExpiresHeader(3600);

        request.addHeader(contactHeader);
        request.addHeader(expiresHeader);

        return request;
    }

    public Request createSubscribe(@NotNull SipAOR toAOR) {

        return null;
    }

    private SipRequestProperties
        createGeneralProperties(@NotNull SipAOR toAOR, String type) throws Exception {
        SipRequestProperties properties = new SipRequestProperties();
        SipAOR selfAOR = sipUserAgent.getSipAOR();

        properties.type = type;

        properties.fromHeader =
                headerFactory.createFromHeader(selfAOR.getSipAddress(), selfAOR.getUserName());

        properties.toHeader = headerFactory.createToHeader(toAOR.getSipAddress(), null);

        properties.uri = toAOR.getSipURI();
        properties.uri.setTransportParam(SipAgent.UDP);

        properties.viaHeaders = new ArrayList<ViaHeader>();
        ViaHeader viaHeader = headerFactory.createViaHeader(
                sipUserAgent.getAddress(),
                sipUserAgent.getPort(),
                sipUserAgent.getTransport(),
                null
        );
        properties.viaHeaders.add(viaHeader);

        properties.callIdHeader = sipUserAgent.getSipProvider().getNewCallId();
        properties.maxForwardsHeader = headerFactory.createMaxForwardsHeader(70);

        return properties;
    }

}
