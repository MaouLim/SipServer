package bupt.sipchat.client;

import bupt.networks.sip.*;
import bupt.networks.sip.exceptions.InitFailureException;
import bupt.util.Configuration;

import javax.sip.*;
import javax.sip.message.Request;
import javax.sip.message.Response;

/*
 * Created by Maou Lim on 2017/7/10.
 */
public class ClientController implements SipProcessor {

    private SipUserAgent       userAgent          = null;
    private ClientTransaction  currentTransaction = null;
    private Dialog             currentDialog      = null;
    private SipRequestBuilder  requestBuilder     = null;
    private SipResponseBuilder responseBuilder    = null;

    public ClientController(Configuration sipConfig,
                            SipContactAOR contactAOR,
                            String        transport) throws InitFailureException {
        try {
            userAgent = new SipUserAgent(sipConfig, contactAOR, transport) {

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

            responseBuilder = userAgent.createResponseBuilder();
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
}
