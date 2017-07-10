package bupt.sipchat.server;

import bupt.networks.sip.SipServer;
import bupt.networks.sip.exceptions.InitFailureException;

import javax.sip.RequestEvent;
import javax.sip.ResponseEvent;

/*
 * Created by Maou Lim on 2017/7/10.
 */
public class ChatServer extends SipServer {

    public static final String SIP_CONFIG_URL = "res/sip-config.yml";
    public static final String SERVER_CONFIG_URL = "res/server-config.yml";


    private ChatServer() throws InitFailureException {
        super(SIP_CONFIG_URL, SERVER_CONFIG_URL);
    }

    @Override
    public void processMessage(RequestEvent requestEvent) {

    }

    @Override
    public void processRegister(RequestEvent requestEvent) {

    }

    @Override
    public void processPublish(RequestEvent requestEvent) {

    }

    @Override
    public void processNotifier(RequestEvent requestEvent) {

    }

    @Override
    public void processSubscribe(RequestEvent requestEvent) {

    }

    @Override
    public void processOK(RequestEvent requestEvent) {

    }

    @Override
    public void processResponse(ResponseEvent responseEvent) {

    }
}
