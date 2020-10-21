package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

public interface TransactionProcessor {
    ISOMsg processSendMoney(ISOMsg request) throws ISOException;

    ISOMsg processDeposit(ISOMsg request) throws ISOException;

    ISOMsg processReceiveMoney(ISOMsg request) throws ISOException;

    ISOMsg processSale(ISOMsg request) throws ISOException;
}
