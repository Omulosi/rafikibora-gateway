package com.rafikibora.gateway.iso;

import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.junit.Test;

public class PackagerTests {
    @Test
    public void testSendMoney() throws Exception {
        ISOMsg msg = new ISOMsg("0200");
        msg.set(2, "4478150055546780"); // pan
        msg.set(3, "260000"); // processing code
        msg.set(4, "13000"); // amount
        msg.set(7, "2010210000"); // date-time transmission (length - 7)
        msg.set(41, "12345678"); // terminal ID (length - 9)
        msg.set(42, "87654321"); // merchant ID
        msg.set(47, "mulungojohnpaul@gmail.com"); // email
        msg.set(49, "040"); // currency Code

        msg.setPackager(new GenericPackager("cfg/altopackager.xml"));
        String msgString = new String(msg.pack());
        System.out.println("Send Money:" + msgString);
    }

    @Test
    public void testDepositMoney() throws Exception {
        ISOMsg msg = new ISOMsg("0200");
        msg.set(2, "4478150055546780"); // pan
        msg.set(3, "210000"); // processing code
        msg.set(4, "13000"); // amount
        msg.set(7, "2010210000"); // date-time transmission
        msg.set(41, "12345678"); // terminal ID
        msg.set(42, "87654321"); // merchant ID
        msg.set(47, "5196010116943992"); // customer pan
        msg.set(49, "040"); // currency Code

        msg.setPackager(new GenericPackager("cfg/altopackager.xml"));
        String msgString = new String(msg.pack());
        System.out.println("Deposit Money : " + msgString);
    }

    @Test
    public void testSale() throws Exception {
        ISOMsg msg = new ISOMsg("0200");
        msg.set(2, "4478150055546780"); // merchant pan
        msg.set(3, "000000"); // processing code
        msg.set(4, "13000"); // amount
        msg.set(7, "201021000000"); // date-time transmission
        msg.set(41, "12345678"); // terminal ID
        msg.set(42, "87654321"); // merchant ID
        msg.set(49, "040"); // currency Code

        msg.setPackager(new GenericPackager("cfg/altopackager.xml"));
        String msgString = new String(msg.pack());
        System.out.println("Sale : " + msgString);
    }

    @Test
    public void testReceiveMoney() throws Exception {
        ISOMsg msg = new ISOMsg("0200");
        msg.set(2, "4478150055546780"); // merchant pan
        msg.set(3, "000000"); // processing code
        msg.set(4, "13000"); // amount
        msg.set(7, "201021000000"); // date-time transmission
        msg.set(41, "12345678"); // terminal ID
        msg.set(42, "87654321"); // merchant ID
        msg.set(47, "123456"); // receive money token
        msg.set(49, "040"); // currency Code

        msg.setPackager(new GenericPackager("cfg/altopackager.xml"));
        String msgString = new String(msg.pack());
        System.out.println("Receive Money : " + msgString);
    }

    @Test
    public void testAuth() throws Exception {
        ISOMsg msg = new ISOMsg("0800");
        msg.set(57, "wangeci@mail.com"); // email
        msg.set(58, "Ellahruth019"); // password

        msg.setPackager(new GenericPackager("cfg/altopackager.xml"));
        String msgString = new String(msg.pack());
        System.out.println("Authentication : " + msgString);
    }
}
