package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOMsg;

public interface AuthProcessor {

    public ISOMsg login (ISOMsg isoMsg);
}
