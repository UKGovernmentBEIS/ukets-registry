/**
 * TransactionLogService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.itl.webservices.client.transactionlog;

public interface TransactionLogService extends javax.xml.rpc.Service {
    public java.lang.String getTransactionLogPortAddress();

    public uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogPort getTransactionLogPort() throws javax.xml.rpc.ServiceException;

    public uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogPort getTransactionLogPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
