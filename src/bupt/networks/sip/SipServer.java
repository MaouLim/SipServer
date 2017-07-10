package bupt.networks.sip;

import bupt.networks.sip.exceptions.InitFailureException;
import bupt.util.Configuration;

import javax.sip.*;
import javax.sip.message.Request;

/*
 * Created by Maou Lim on 2017/7/10.
 */
public abstract class SipServer implements SipProcessor {

    public static final String SERVER_NAME = "server_name";
    public static final String SERVER_DOMAIN = "server_domain";
    public static final String IP_ADDRESS = "javax.sip.IP_ADDRESS";
    public static final String SERVER_PORT = "server_port";
    public static final String SERVER_TRANSPORT = "server_transport";

    private SipUserAgent agent = null;

    public SipServer(String sipConfigURL, String serverConfigURL) throws InitFailureException {

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
            agent = new SipUserAgent(sipConfig, contactAOR, (String) serverConfig.get(SERVER_TRANSPORT)) {

                @Override
                public void processRequest(RequestEvent requestEvent) {
                    String method = requestEvent.getRequest().getMethod();

                    switch (method) {
                        case Request.MESSAGE : {
                            SipServer.this.processMessage(requestEvent);
                            return;
                        }

                        case Request.REGISTER : {
                            SipServer.this.processRegister(requestEvent);
                            return;
                        }

                        case Request.PUBLISH : {
                            SipServer.this.processPublish(requestEvent);
                            return;
                        }

                        case Request.SUBSCRIBE : {
                            SipServer.this.processSubscribe(requestEvent);
                            return;
                        }

                        case Request.NOTIFY : {
                            SipServer.this.processNotifier(requestEvent);
                        }
                    }
                }

                @Override
                public void processResponse(ResponseEvent responseEvent) {
                    SipServer.this.processResponse(responseEvent);
                }

                @Override
                public void processTimeout(TimeoutEvent timeoutEvent) {
                    SipServer.this.processTimeout(timeoutEvent);
                }

                @Override
                public void processIOException(IOExceptionEvent exceptionEvent) {
                    SipServer.this.processIOException(exceptionEvent);
                }

                @Override
                public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {
                    SipServer.this.processTransactionTerminated(transactionTerminatedEvent);
                }

                @Override
                public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {
                    System.err.println("DialogTerminated");
                }
            };
        }
        catch (Exception ex) {
            throw new InitFailureException("SipAgent init failure.", ex);
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
}
