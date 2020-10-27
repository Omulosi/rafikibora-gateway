package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

public interface AuthProcessor {

    ISOMsg login (ISOMsg isoMsg) throws ISOException;
}
