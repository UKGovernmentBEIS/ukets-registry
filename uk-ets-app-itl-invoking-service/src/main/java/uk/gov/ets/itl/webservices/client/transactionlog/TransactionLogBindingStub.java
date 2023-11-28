/**
 * TransactionLogBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package uk.gov.ets.itl.webservices.client.transactionlog;

public class TransactionLogBindingStub extends org.apache.axis.client.Stub implements uk.gov.ets.itl.webservices.client.transactionlog.TransactionLogPort {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[11];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("acceptMessage");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "acceptMessageRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "MessageRequest"), uk.gov.ets.kp.webservices.shared.types.MessageRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "MessageResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.MessageResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "acceptMessageResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("acceptProposal");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "acceptProposalRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProposalRequest"), uk.gov.ets.kp.webservices.shared.types.ProposalRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProposalResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.ProposalResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "acceptProposalResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("acceptNotification");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "acceptNotificationRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "NotificationRequest"), uk.gov.ets.kp.webservices.shared.types.NotificationRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "NotificationResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.NotificationResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "acceptNotificationResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getTransactionStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "getTransactionStatusRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "TransactionStatusRequest"), uk.gov.ets.kp.webservices.shared.types.TransactionStatusRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "TransactionStatusResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.TransactionStatusResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "getTransactionStatusResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("receiveReconciliationResult");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "receiveReconciliationResultRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReconciliationResultRequest"), uk.gov.ets.kp.webservices.shared.types.ReconciliationResultRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReconciliationResultResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "receiveReconciliationResultResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("provideAuditTrail");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "provideAuditTrailRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideAuditTrailRequest"), uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideAuditTrailResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "provideAuditTrailResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("provideTotals");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "provideTotalsRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideTotalsRequest"), uk.gov.ets.kp.webservices.shared.types.ProvideTotalsRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideTotalsResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "provideTotalsResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("provideUnitBlocks");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "provideUnitBlocksRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideUnitBlocksRequest"), uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideUnitBlocksResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "provideUnitBlocksResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("receiveAuditTrail");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "receiveAuditTrailRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveAuditTrailRequest"), uk.gov.ets.kp.webservices.shared.types.ReceiveAuditTrailRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveAuditTrailResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.ReceiveAuditTrailResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "receiveAuditTrailResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("receiveTotals");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "receiveTotalsRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveTotalsRequest"), uk.gov.ets.kp.webservices.shared.types.ReceiveTotalsRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveTotalsResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.ReceiveTotalsResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "receiveTotalsResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("receiveUnitBlocks");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "receiveUnitBlocksRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveUnitBlocksRequest"), uk.gov.ets.kp.webservices.shared.types.ReceiveUnitBlocksRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveUnitBlocksResponse"));
        oper.setReturnClass(uk.gov.ets.kp.webservices.shared.types.ReceiveUnitBlocksResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "receiveUnitBlocksResponse"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[10] = oper;

    }

    public TransactionLogBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public TransactionLogBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public TransactionLogBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ArrayOfEvaluationResult");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.EvaluationResult[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "EvaluationResult");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ArrayOfInt");
            cachedSerQNames.add(qName);
            cls = int[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ArrayOfReconciliationTransactionUnitBlock");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReconciliationTransactionUnitBlock[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReconciliationTransactionUnitBlock");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ArrayOfReconciliationUnitBlockIdentifier");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReconciliationUnitBlockIdentifier[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReconciliationUnitBlockIdentifier");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ArrayOfTotal");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.Total[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "Total");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ArrayOfTransaction");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.Transaction[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "Transaction");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ArrayOfTransactionUnitBlock");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.TransactionUnitBlock[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "TransactionUnitBlock");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ArrayOfUnitBlock");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.UnitBlock[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "UnitBlock");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ArrayOfUnitBlockIdentifier");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.UnitBlockIdentifier[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "UnitBlockIdentifier");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ArrayOfUnitBlockRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.UnitBlockRequest[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "UnitBlockRequest");
            qName2 = null;
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "EvaluationResult");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.EvaluationResult.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "MessageRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.MessageRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "MessageResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.MessageResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "NotificationRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.NotificationRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "NotificationResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.NotificationResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProposalRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ProposalRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProposalResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ProposalResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProposalTransaction");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ProposalTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideAuditTrailRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideAuditTrailResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideTotalsRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ProvideTotalsRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideTotalsResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideUnitBlocksRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ProvideUnitBlocksResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveAuditTrailRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReceiveAuditTrailRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveAuditTrailResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReceiveAuditTrailResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveTotalsRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReceiveTotalsRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveTotalsResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReceiveTotalsResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveUnitBlocksRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReceiveUnitBlocksRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReceiveUnitBlocksResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReceiveUnitBlocksResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReconciliationResultRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReconciliationResultRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReconciliationResultResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReconciliationTransactionUnitBlock");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReconciliationTransactionUnitBlock.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "ReconciliationUnitBlockIdentifier");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.ReconciliationUnitBlockIdentifier.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "Total");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.Total.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "Transaction");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.Transaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "TransactionStatusRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.TransactionStatusRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "TransactionStatusResponse");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.TransactionStatusResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "TransactionUnitBlock");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.TransactionUnitBlock.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "UnitBlock");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.UnitBlock.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "UnitBlockIdentifier");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.UnitBlockIdentifier.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:Types:1.0:0.0", "UnitBlockRequest");
            cachedSerQNames.add(qName);
            cls = uk.gov.ets.kp.webservices.shared.types.UnitBlockRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public uk.gov.ets.kp.webservices.shared.types.MessageResponse acceptMessage(uk.gov.ets.kp.webservices.shared.types.MessageRequest acceptMessageRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("acceptMessage");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "acceptMessage"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {acceptMessageRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.MessageResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.MessageResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.MessageResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public uk.gov.ets.kp.webservices.shared.types.ProposalResponse acceptProposal(uk.gov.ets.kp.webservices.shared.types.ProposalRequest acceptProposalRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("acceptProposal");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "acceptProposal"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {acceptProposalRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.ProposalResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.ProposalResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.ProposalResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public uk.gov.ets.kp.webservices.shared.types.NotificationResponse acceptNotification(uk.gov.ets.kp.webservices.shared.types.NotificationRequest acceptNotificationRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("acceptNotification");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "acceptNotification"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {acceptNotificationRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.NotificationResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.NotificationResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.NotificationResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public uk.gov.ets.kp.webservices.shared.types.TransactionStatusResponse getTransactionStatus(uk.gov.ets.kp.webservices.shared.types.TransactionStatusRequest getTransactionStatusRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getTransactionStatus");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "getTransactionStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {getTransactionStatusRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.TransactionStatusResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.TransactionStatusResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.TransactionStatusResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse receiveReconciliationResult(uk.gov.ets.kp.webservices.shared.types.ReconciliationResultRequest receiveReconciliationResultRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("receiveReconciliationResult");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "receiveReconciliationResult"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {receiveReconciliationResultRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.ReconciliationResultResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse provideAuditTrail(uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailRequest provideAuditTrailRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("provideAuditTrail");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "provideAuditTrail"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {provideAuditTrailRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.ProvideAuditTrailResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse provideTotals(uk.gov.ets.kp.webservices.shared.types.ProvideTotalsRequest provideTotalsRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("provideTotals");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "provideTotals"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {provideTotalsRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.ProvideTotalsResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse provideUnitBlocks(uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksRequest provideUnitBlocksRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("provideUnitBlocks");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "provideUnitBlocks"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {provideUnitBlocksRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.ProvideUnitBlocksResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public uk.gov.ets.kp.webservices.shared.types.ReceiveAuditTrailResponse receiveAuditTrail(uk.gov.ets.kp.webservices.shared.types.ReceiveAuditTrailRequest receiveAuditTrailRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("receiveAuditTrail");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "receiveAuditTrail"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {receiveAuditTrailRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.ReceiveAuditTrailResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.ReceiveAuditTrailResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.ReceiveAuditTrailResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public uk.gov.ets.kp.webservices.shared.types.ReceiveTotalsResponse receiveTotals(uk.gov.ets.kp.webservices.shared.types.ReceiveTotalsRequest receiveTotalsRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("receiveTotals");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "receiveTotals"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {receiveTotalsRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.ReceiveTotalsResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.ReceiveTotalsResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.ReceiveTotalsResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public uk.gov.ets.kp.webservices.shared.types.ReceiveUnitBlocksResponse receiveUnitBlocks(uk.gov.ets.kp.webservices.shared.types.ReceiveUnitBlocksRequest receiveUnitBlocksRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("receiveUnitBlocks");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:KyotoProtocol:RegistrySystem:ITL:1.0:0.0", "receiveUnitBlocks"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {receiveUnitBlocksRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (uk.gov.ets.kp.webservices.shared.types.ReceiveUnitBlocksResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (uk.gov.ets.kp.webservices.shared.types.ReceiveUnitBlocksResponse) org.apache.axis.utils.JavaUtils.convert(_resp, uk.gov.ets.kp.webservices.shared.types.ReceiveUnitBlocksResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
