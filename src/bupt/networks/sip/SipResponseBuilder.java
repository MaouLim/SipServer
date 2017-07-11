package bupt.networks.sip;

import com.sun.istack.internal.NotNull;

import javax.sip.address.Address;
import javax.sip.address.AddressFactory;
import javax.sip.header.ContactHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;

/*
 * Created by Maou Lim on 2017/7/10.
 */
public class SipResponseBuilder {

    private static SipFactoryHelper sipFactoryHelper = SipFactoryHelper.getInstance();
    private static AddressFactory addressFactory = sipFactoryHelper.getAddressFactory();
    private static HeaderFactory headerFactory = sipFactoryHelper.getHeaderFactory();

    private SipUserAgent sipUserAgent = null;

    public SipResponseBuilder(@NotNull SipUserAgent sipUserAgent) {
        this.sipUserAgent = sipUserAgent;
    }

    public Response create(@NotNull Request request, int statusCode) throws ParseException {
        Response response =
                sipFactoryHelper.getMessageFactory().createResponse(statusCode, request);

        Address contact = sipUserAgent.getContactAOR().getSipAddress();
        ContactHeader contactHeader = headerFactory.createContactHeader(contact);
        response.addHeader(contactHeader);

        return response;
    }
}
