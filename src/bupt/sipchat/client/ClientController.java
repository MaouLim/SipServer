package bupt.sipchat.client;

import bupt.networks.sip.*;
import bupt.networks.sip.exceptions.InitFailureException;
import bupt.util.Configuration;
import com.sun.istack.internal.NotNull;

import javax.sip.*;
import javax.sip.message.Request;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Created by Maou Lim on 2017/7/10.
 */
public class ClientController implements SipProcessor, ChatClientService {

    public static final String INVITE_TO_CHAT = "INVITE_TO_CHAT";

    private SipUserAgent       userAgent          = null;
    private ClientTransaction  currentTransaction = null;
    private Dialog             currentDialog      = null;
    private SipRequestBuilder  requestBuilder     = null;
    private SipResponseBuilder responseBuilder    = null;
    private SipContactAOR      serverAOR          = null;

    private ConcurrentHashMap<Long, ClientTransaction> clientTransactionMap = null;
    private ConcurrentHashMap<Long, ServerTransaction> serverTransactionMap = null;

    public ClientController(@NotNull Configuration sipConfig,
                            @NotNull SipContactAOR selfAOR,
                            @NotNull SipContactAOR serverAOR,
                            @NotNull String        transport) throws InitFailureException {
        try {
            userAgent = new SipUserAgent(sipConfig, selfAOR, transport) {

                @Override
                public void processRequest(RequestEvent requestEvent) {
                    String method = requestEvent.getRequest().getMethod();

                    switch (method) {
                        case Request.MESSAGE : {
                            ClientController.this.processMessage(requestEvent);
                            return;
                        }

                        case Request.NOTIFY : {
                            ClientController.this.processNotifier(requestEvent);
                            return;
                        }
                    }
                }

                @Override
                public void processResponse(ResponseEvent responseEvent) {
                    int statusCode = responseEvent.getResponse().getStatusCode();

                    if (200 <= statusCode && statusCode < 300) {
                        // todo handle successful case
                    }
                    else {
                        // todo handle failed case
                    }
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
            };

            this.responseBuilder = userAgent.createResponseBuilder();
            this.requestBuilder = userAgent.createRequestBuilder();
            this.serverAOR = serverAOR;
        }
        catch (Exception ex) {
            throw new InitFailureException("failed to init sipUserAgent", ex);
        }
    }

    @Override
    public void processMessage(RequestEvent requestEvent) {
        ServerTransaction transaction = requestEvent.getServerTransaction();
        try {
            // todo something like updating ui components
            transaction.sendResponse(responseBuilder.createOK(transaction.getRequest()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return;
    }

    @Override
    public void processRegister(RequestEvent requestEvent) {
        // empty
    }

    @Override
    public void processPublish(RequestEvent requestEvent) {
        // empty
    }

    @Override
    public void processNotifier(RequestEvent requestEvent) {
        ServerTransaction transaction = requestEvent.getServerTransaction();
        try {
            // todo something like updating ui components
            transaction.sendResponse(responseBuilder.createOK(transaction.getRequest()));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void processSubscribe(RequestEvent requestEvent) {
        // empty
    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {
        // todo
    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {
        // todo


    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {
        // todo

    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
        // todo
    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
        // todo
    }

    @Override
    public void login(String userName, String password) {

    }

    @Override
    public void inviteToChat(String inviteeAOR) {
        try {
            // send invite to server to ask for the address of the invitee's address
            Request request = requestBuilder.createInvite(serverAOR, INVITE_TO_CHAT, inviteeAOR);
            ClientTransaction transaction = userAgent.sendRequestByTransaction(request);
            clientTransactionMap.put(SipRequestBuilder.getRequestCSeq(request), transaction);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String contactURI, String content) {
        try {
            // send message to
            Request request = requestBuilder.createMessage(new SipContactAOR(contactURI), content);
            ClientTransaction transaction = userAgent.sendRequestByTransaction(request);
            clientTransactionMap.put(SipRequestBuilder.getRequestCSeq(request), transaction);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void createChannel(String channelId) {

    }

    @Override
    public void subscribeChannel(String channelId) {

    }

    @Override
    public void publishToChannel(String channelId, String statusInfo) {

    }
}
