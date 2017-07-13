package bupt.networks.sip;

import javax.sip.*;
import javax.sip.message.Request;

/*
 * Created by Maou Lim on 2017/7/10.
 */
public interface SipProcessor {

    void processInvite(Request request, ServerTransaction transaction);
    void processBye(Request request, ServerTransaction transaction);
    void processMessage(Request request, ServerTransaction transaction);
    void processRegister(Request request, ServerTransaction transaction);
    void processPublish(Request request, ServerTransaction transaction);
    void processNotifier(Request request, ServerTransaction transaction);
    void processSubscribe(Request request, ServerTransaction transaction);
    void processACK(Request request, ServerTransaction transaction);

    void processResponse(ResponseEvent responseEvent);

    void processTimeout(TimeoutEvent timeoutEvent);
    void processIOException(IOExceptionEvent exceptionEvent);
    void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent);
    void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent);
}
