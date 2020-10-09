package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOMsg;

public interface TransactionProcessor {
    public ISOMsg processSendMoney(ISOMsg request);

    public ISOMsg processDeposit(ISOMsg request);
    public ISOMsg processWithdraw(ISOMsg request);
    public ISOMsg processSale(ISOMsg request);
}
