package bupt.networks.sip;

import javax.sip.*;

/*
 * Created by Maou Lim on 2017/7/10.
 */
public interface SipProcessor {

    void processMessage(RequestEvent requestEvent);
    void processRegister(RequestEvent requestEvent);
    void processPublish(RequestEvent requestEvent);
    void processNotifier(RequestEvent requestEvent);
    void processSubscribe(RequestEvent requestEvent);

    void processResponse(ResponseEvent responseEvent);

    void processTimeout(TimeoutEvent timeoutEvent);
    void processIOException(IOExceptionEvent exceptionEvent);
    void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent);
    void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent);
}
