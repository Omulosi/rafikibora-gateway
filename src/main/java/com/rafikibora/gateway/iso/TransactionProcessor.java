package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOMsg;

public interface TransactionProcessor {
    ISOMsg processSendMoney(ISOMsg request);

    ISOMsg processDeposit(ISOMsg request);

    ISOMsg processReceiveMoney(ISOMsg request);

    ISOMsg processSale(ISOMsg request);
}
