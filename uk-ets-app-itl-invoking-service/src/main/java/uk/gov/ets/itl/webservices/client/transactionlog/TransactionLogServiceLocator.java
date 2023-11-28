/**
 * TransactionLogServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.itl.webservices.client.transactionlog;

public class TransactionLogServiceLocator extends org.apache.axis.client.Service implements uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogService {

    public TransactionLogServiceLocator() {
    }


    public TransactionLogServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public TransactionLogServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for TransactionLogPort
    private java.lang.String TransactionLogPort_address = "http://itladdress.int/itlws/TransactionLogService";

    public java.lang.String getTransactionLogPortAddress() {
        return TransactionLogPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String TransactionLogPortWSDDServiceName = "TransactionLogPort";

    public java.lang.String getTransactionLogPortWSDDServiceName() {
        return TransactionLogPortWSDDServiceName;
    }

    public void setTransactionLogPortWSDDServiceName(java.lang.String name) {
        TransactionLogPortWSDDServiceName = name;
    }

    public uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogPort getTransactionLogPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(TransactionLogPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTransactionLogPort(endpoint);
    }

    public uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogPort getTransactionLogPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogBindingStub _stub = new uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogBindingStub(portAddress, this);
            _stub.setPortName(getTransactionLogPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTransactionLogPortEndpointAddress(java.lang.String address) {
        TransactionLogPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogPort.class.isAssignableFrom(serviceEndpointInterface)) {
                uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogBindingStub _stub = new uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogBindingStub(new java.net.URL(TransactionLogPort_address), this);
                _stub.setPortName(getTransactionLogPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("TransactionLogPort".equals(inputPortName)) {
            return getTransactionLogPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "TransactionLogService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "TransactionLogPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("TransactionLogPort".equals(portName)) {
            setTransactionLogPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
