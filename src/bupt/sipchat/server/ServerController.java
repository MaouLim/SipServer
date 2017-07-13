package bupt.sipchat.server;

import bupt.networks.sip.*;
import bupt.networks.sip.exceptions.InitFailureException;
import bupt.util.Configuration;
import bupt.util.LogUtil;

import javax.sip.*;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/*
 * Created by Maou Lim on 2017/7/10.
 */
public class ServerController implements SipProcessor {

    public static final String SERVER_NAME      = "server_name";
    public static final String SERVER_DOMAIN    = "server_domain";
    public static final String IP_ADDRESS       = "javax.sip.IP_ADDRESS";
    public static final String SERVER_PORT      = "server_port";
    public static final String SERVER_TRANSPORT = "server_transport";

    private static SipFactoryHelper helper = SipFactoryHelper.getInstance();

    private static final SipAOR SERVER_SIP_AOR = new SipAOR("server", "dd.dev.com");

    private SipUserAgent      agent               = null;
    private SipRequestBuilder requestBuilder      = null;
    private SipResponseBuilder responseBuilder    = null;

    private ConcurrentHashMap<Long, ClientTransaction> clientTransactionMap = null;
    private ConcurrentHashMap<Long, ServerTransaction> serverTransactionMap = null;

    private ConcurrentHashMap<String, SipContactAOR> contactMap = null;
    private ConcurrentHashMap<String, Event>         events     = null;
    private ConcurrentHashMap<String, Dialog>        dialogMap  = null;

    public ServerController(String sipConfigURL, String serverConfigURL) throws InitFailureException {

        System.out.println("Server starting...");

        Configuration sipConfig = new Configuration(sipConfigURL);
        Configuration serverConfig = new Configuration(serverConfigURL);

        SipAOR sipAOR = new SipAOR(
                (String) serverConfig.get(SERVER_NAME),
                (String) serverConfig.get(SERVER_DOMAIN)
        );

        SipContactAOR contactAOR = new SipContactAOR(
                (String) serverConfig.get(SERVER_NAME),
                (String) sipConfig.get(IP_ADDRESS),
                (Integer) serverConfig.get(SERVER_PORT),
                sipAOR
        );

        try {
            this.agent = new SipUserAgent(sipConfig,
                                          contactAOR,
                                          (String) serverConfig.get(SERVER_TRANSPORT)) {

                @Override
                public void processRequest(RequestEvent requestEvent) {

                    ServerTransaction transaction = requestEvent.getServerTransaction();
                    Request request = requestEvent.getRequest();

                    LogUtil.lumpedLog("Get Request", request.toString(), false);

                    if (null == transaction) {
                        try {
                            transaction = agent.createServerTransaction(request);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    switch (request.getMethod()) {
                        case Request.INVITE : {
                            processInvite(request, transaction);
                            return;
                        }

                        case Request.BYE : {
                            processBye(request, transaction);
                            return;
                        }

                        case Request.MESSAGE : {
                            processMessage(request, transaction);
                            return;
                        }

                        case Request.REGISTER : {
                            processRegister(request, transaction);
                            return;
                        }

                        case Request.PUBLISH : {
                            processPublish(request, transaction);
                            return;
                        }

                        case Request.SUBSCRIBE : {
                            processSubscribe(request, transaction);
                            return;
                        }

                        case Request.NOTIFY : {
                            processNotifier(request, transaction);
                            return;
                        }

                        case Request.ACK : {
                            processACK(request, transaction);
                            return;
                        }
                    }
                }

                @Override
                public void processResponse(ResponseEvent responseEvent) {
                    LogUtil.lumpedLog("Get Response",
                                      responseEvent.getResponse().toString(),
                                      false);
                    ServerController.this.processResponse(responseEvent);
                }

                @Override
                public void processTimeout(TimeoutEvent timeoutEvent) {
                    ServerController.this.processTimeout(timeoutEvent);
                }

                @Override
                public void processIOException(IOExceptionEvent exceptionEvent) {
                    ServerController.this.processIOException(exceptionEvent);
                }

                @Override
                public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
                    ServerController.this.processTransactionTerminated(transactionTerminatedEvent);
                }

                @Override
                public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
                    System.err.println("DialogTerminated");
                }
            };

            this.requestBuilder = this.agent.createRequestBuilder();
            this.responseBuilder = this.agent.createResponseBuilder();

            this.clientTransactionMap = new ConcurrentHashMap<>();
            this.serverTransactionMap = new ConcurrentHashMap<>();

            this.contactMap = new ConcurrentHashMap<>();
            this.events = new ConcurrentHashMap<>();

            this.dialogMap = new ConcurrentHashMap<>();

            System.out.println("Server started");
            System.out.println("Using localAddress: " + agent.getAddress() + ":" + agent.getPort());
        }
        catch (Exception ex) {
            throw new InitFailureException("SipAgent init failure.", ex);
        }
    }

    public void close() {
        agent.close();
    }

    @Override
    public void processInvite(Request request, ServerTransaction transaction) {
        String toURI =
                ((ToHeader) request.getHeader(ToHeader.NAME)).getAddress().getURI().toString();

        SipContactAOR contactAOR = contactMap.get(toURI);

        /* no such user, reply not-found */
        if (null == contactAOR) {
            try {
                transaction.sendResponse(responseBuilder.create(request, Response.NOT_FOUND));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        /* else send try to the invitor */
        else {
            try {
                transaction.sendResponse(responseBuilder.create(request, Response.TRYING));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        serverTransactionMap.put(SipRequestBuilder.getRequestCSeq(request), transaction);

        /* forward it to the invitee */
        try {
            request.setRequestURI(contactAOR.getSipURI());
            agent.sendRequestByTransaction(request);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void processBye(Request request, ServerTransaction transaction) {
        String toURI =
                ((ToHeader) request.getHeader(ToHeader.NAME)).getAddress().getURI().toString();

        SipContactAOR contactAOR = contactMap.get(toURI);

        if (null != contactAOR) {
            try {
                transaction.sendResponse(responseBuilder.create(request, Response.OK));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try {
            request.setRequestURI(contactAOR.getSipURI());
            agent.sendRequestByTransaction(request);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void processMessage(Request request, ServerTransaction transaction) {
        String toURI =
                ((ToHeader) request.getHeader(ToHeader.NAME)).getAddress().getURI().toString();

        SipAOR sipAOR = new SipAOR(toURI);

        if ("server".equalsIgnoreCase(sipAOR.getUserName())) {
            try {
                transaction.sendResponse(responseBuilder.create(request, Response.OK));
                LogUtil.lumpedLog("Response Sent", "OK <message>", false);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            contactMap.forEach((sipURI, contactAOR) -> {
                ContactHeader contactHeader = helper.getHeaderFactory().
                        createContactHeader(agent.getContactAOR().getSipAddress());
                request.setRequestURI(contactAOR.getSipURI());
                request.setHeader(contactHeader);

                try {
                    agent.sendRequestByTransaction(request);
                }
                catch (SipException ex) {
                    ex.printStackTrace();
                }
            });
        }
        else {
            SipContactAOR contactAOR = contactMap.get(toURI);
            if (null != contactAOR) {

                try {
                    String content = new String(request.getRawContent());
                    FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
                    SipAOR fromAOR = new SipAOR(fromHeader.getAddress().toString());
                    agent.getContactAOR().attachTo(fromAOR);

                    Request forward = requestBuilder.createMessage(contactAOR, content);

                    agent.sendRequestByTransaction(forward);
                    LogUtil.lumpedLog("Forward Sent", forward.toString(), false);

                    agent.getContactAOR().attachTo(SERVER_SIP_AOR);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            else {
                try {
                    transaction.sendResponse(responseBuilder.create(request, Response.NOT_FOUND));
                    LogUtil.lumpedLog("Response Sent", "NOT FOUND <message>", false);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                // todo record the content
            }

            try {
                transaction.sendResponse(responseBuilder.create(request, Response.OK));
                LogUtil.lumpedLog("Response Sent", "OK <message>", false);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void processRegister(Request request, ServerTransaction transaction) {
        String contactURI =
                ((ContactHeader) request.getHeader(ContactHeader.NAME)).getAddress().getURI().toString();
        String toURI =
                ((ToHeader) request.getHeader(ToHeader.NAME)).getAddress().getURI().toString();

        SipContactAOR contactAOR = new SipContactAOR(contactURI);
        contactAOR.attachTo(new SipAOR(toURI));

        /* if the user name exists, reply ambiguous */
        if (contactMap.containsKey(toURI)) {
            try {
                transaction.sendResponse(responseBuilder.create(request, Response.AMBIGUOUS));
                LogUtil.lumpedLog("Response Sent", "AMBIGUOUS <register>", false);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            return;
        }

        /* put into the map */
        contactMap.put(toURI, contactAOR);

        /* reply ok */
        try {
            transaction.sendResponse(responseBuilder.create(request, Response.OK));
            LogUtil.lumpedLog("Response Sent", "OK <register>", false);

            Request welcome = requestBuilder.createMessage(contactAOR, "Welcome to Chat Room!");
            agent.sendRequestByTransaction(welcome);
            LogUtil.lumpedLog("Welcome Request Sent", welcome.toString(), false);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void processPublish(Request request, ServerTransaction transaction) {
        /* the new status information of the event has been published */
        String content = new String(request.getRawContent());

        EventHeader eventHeader = (EventHeader) request.getHeader(EventHeader.NAME);
        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);

        /* send ok to the publisher */
        try {
            transaction.sendResponse(responseBuilder.create(request, Response.OK));
            LogUtil.lumpedLog("Response Sent", "OK <publish>", false);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        /* if the event exists, notify all the subscriber */
        events.computeIfPresent(
                ((EventHeader) request.getHeader(EventHeader.NAME)).getEventType(),
                (eventName, eventMatched) -> {
                    List<SipAOR> subscribers = eventMatched.getSubscribers();

                    for (SipAOR each : subscribers) {
                        SipContactAOR contactAOR = contactMap.get(each.toString());

                        if (null == contactAOR) {
                            continue;
                        }

                        try {
                            Dialog dialog = dialogMap.get(eventName + contactAOR.getSipAOR().toString());

                            agent.getContactAOR().attachTo(SERVER_SIP_AOR);

                            Request notify = dialog.createRequest(Request.NOTIFY);

                            SubscriptionStateHeader subscriptionStateHeader = helper.getHeaderFactory().
                                    createSubscriptionStateHeader(SubscriptionStateHeader.ACTIVE);
//                            ContactHeader contactHeader =
//                                    SipFactoryHelper.getInstance().getHeaderFactory().
//                                            createContactHeader(agent.getContactAOR().getSipAddress());

                            ContentTypeHeader contentTypeHeader = helper.getHeaderFactory().
                                    createContentTypeHeader("text", "plain");

                            notify.addHeader(eventHeader);
                            notify.setHeader(request.getHeader(ContactHeader.NAME));
                            notify.addHeader(subscriptionStateHeader);
                            notify.setContent(content, contentTypeHeader);

                            ClientTransaction clientTransaction = agent.createClientTransaction(notify);
                            dialog.sendRequest(clientTransaction);

                            LogUtil.lumpedLog("Notify Sent", notify.toString(), false);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                  }
                    return eventMatched;
                }
        );
    }

    @Override
    public void processNotifier(Request request, ServerTransaction transaction) {
        System.err.println("Get a notify");
    }

    @Override
    public void processSubscribe(Request request, ServerTransaction transaction) {
        EventHeader eventHeader = (EventHeader) request.getHeader(EventHeader.NAME);
        FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
        String fromURI = fromHeader.getAddress().getURI().toString();

        String eventName = eventHeader.getEventType();
        events.putIfAbsent(eventName, new Event(eventName));

        Event event = events.get(eventName);
        event.subscribe(new SipAOR(fromURI));

        try {
            Dialog dialog = transaction.getDialog();
            dialog.terminateOnBye(false);

            if (null == dialog) {
                System.out.println("dialog is null");
                return;
            }

            Response response = responseBuilder.create(request, Response.OK);
            ExpiresHeader expiresHeader =
                    SipFactoryHelper.getInstance().getHeaderFactory().createExpiresHeader(3600);

            response.addHeader(expiresHeader);
            transaction.sendResponse(response);

            Request notify = dialog.createRequest(Request.NOTIFY);
            SubscriptionStateHeader subscriptionStateHeader =
                    SipFactoryHelper.getInstance().getHeaderFactory().
                            createSubscriptionStateHeader(SubscriptionStateHeader.ACTIVE);
            notify.addHeader(subscriptionStateHeader);
            notify.addHeader(eventHeader);

            ClientTransaction clientTransaction = agent.createClientTransaction(notify);
            dialog.sendRequest(clientTransaction);
            dialogMap.put(eventName + fromURI, dialog);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void processACK(Request request, ServerTransaction transaction) {
        String toURI =
                ((ToHeader) request.getHeader(ToHeader.NAME)).getAddress().getURI().toString();

        SipContactAOR contactAOR = contactMap.get(toURI);

        if (null != contactAOR) {
            try {
                request.setRequestURI(contactAOR.getSipURI());
                agent.sendRequestByTransaction(request);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {

        Response response = responseEvent.getResponse();
        ToHeader toHeader = (ToHeader) response.getHeader(ToHeader.NAME);

        String toURI = toHeader.getAddress().getURI().toString();

        SipContactAOR contactAOR = contactMap.get(toURI);
        if (null != contactAOR) {

            ServerTransaction transaction =
                    serverTransactionMap.get(SipResponseBuilder.getResponseCSeq(response));

            if (transaction == null) {
                return;
            }

            try {
                transaction.sendResponse(response);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        System.err.println("timeout");
    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        System.err.println("IOException");
    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        System.err.println("TransactionTerminated");
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) { }
}
