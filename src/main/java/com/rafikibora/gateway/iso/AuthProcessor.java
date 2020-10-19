package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOMsg;

public interface AuthProcessor {

    ISOMsg login (ISOMsg isoMsg);
}
