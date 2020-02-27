package com.ontology.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.common.Helper;
import com.github.ontio.core.DataSignature;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.Curve;
import com.github.ontio.crypto.ECC;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.io.BinaryReader;
import com.github.ontio.io.BinaryWriter;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;
import com.github.ontio.smartcontract.neovm.abi.BuildParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * SDK 入口类
 */
@Component
@Slf4j
public class SDKUtil {

    @Autowired
    private ConfigParam configParam;

    private OntSdk wm;

    private String contractAddress = "0000000000000000000000000000000000000003";

    private OntSdk getOntSdk() throws Exception {
        if (wm == null) {
            wm = OntSdk.getInstance();
            wm.setRestful(configParam.RESTFUL_URL);
            wm.openWalletFile("wallet.json");
        }
        if (wm.getWalletMgr() == null) {
            wm.openWalletFile("wallet.json");
        }
        return wm;
    }

    public String registerOntId(byte[] key) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Account payerAcct = new Account(Account.getPrivateKeyFromWIF(configParam.PAYER_WIF), ontSdk.getWalletMgr().getSignatureScheme());

        Identity identity = createIdentity(ontSdk, key);
        String txhash = sendRegister(ontSdk, identity, key, payerAcct, Constant.GAS_LIMIT, Constant.GAS_PRICE);

        return identity.ontid;
    }

    private Identity createIdentity(OntSdk ontSdk, byte[] privateKey) throws Exception {
        byte[] salt = ECC.generateKey(16);
        Account account = new Account(privateKey, ontSdk.getWalletMgr().getSignatureScheme());
        com.github.ontio.sdk.wallet.Account acct;
        switch (ontSdk.getWalletMgr().getSignatureScheme()) {
            case SHA256WITHECDSA:
                acct = new com.github.ontio.sdk.wallet.Account("ECDSA", new Object[]{Curve.P256.toString()}, "aes-256-gcm", "SHA256withECDSA", "sha256");
                break;
            case SM3WITHSM2:
                acct = new com.github.ontio.sdk.wallet.Account("SM2", new Object[]{Curve.SM2P256V1.toString()}, "aes-256-gcm", "SM3withSM2", "sha256");
                break;
            default:
                throw new SDKException(ErrorCode.OtherError("scheme type error"));
        }

        acct.key = Helper.toHexString(account.serializePrivateKey());
        acct.address = Address.addressFromPubKey(account.serializePublicKey()).toBase58();

        String uuidStr = UUID.randomUUID().toString();
        String label = uuidStr.substring(0, 8);

        Identity idt = new Identity();
        idt.ontid = Common.didont + acct.address;
        idt.label = label;

        idt.controls = new ArrayList<Control>();
        Control ctl = new Control(acct.key, "keys-1", Helper.toHexString(account.serializePublicKey()));
        ctl.setSalt(salt);
        ctl.setAddress(acct.address);
        idt.controls.add(ctl);
        return idt;
    }

    private String sendRegister(OntSdk ontSdk, Identity ident, byte[] privateKey, Account payerAcct, long gasLimit, long gasPrice) throws Exception {
        if (ident == null || payerAcct == null) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasPrice < 0 || gasLimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }

        Account account = new Account(privateKey, ontSdk.getWalletMgr().getSignatureScheme());

        Transaction tx = makeRegister(ontSdk, ident.ontid, ident.controls.get(0).publicKey, payerAcct.getAddressU160().toBase58(), gasLimit, gasPrice);
        ontSdk.addSign(tx, account);
        ontSdk.addSign(tx, payerAcct);

        boolean b = ontSdk.getConnect().sendRawTransaction(tx.toHexString());
        if (!b) {
            throw new SDKException(ErrorCode.SendRawTxError);
        }
        return tx.hash().toHexString();
    }

    private Transaction makeRegister(OntSdk ontSdk, String ontid, String publickey, String payer, long gaslimit, long gasprice) throws Exception {
        if (payer == null || payer.equals("")) {
            throw new SDKException(ErrorCode.ParamErr("parameter should not be null"));
        }
        if (gasprice < 0 || gaslimit < 0) {
            throw new SDKException(ErrorCode.ParamErr("gas or gaslimit should not be less than 0"));
        }

        byte[] pk = Helper.hexToBytes(publickey);

        List list = new ArrayList();
        list.add(new Struct().add(ontid, pk));
        byte[] args = NativeBuildParams.createCodeParamsScript(list);
        Transaction tx = ontSdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "regIDWithPublicKey", args, payer, gaslimit, gasprice);
        return tx;
    }

    public Object checkEvent(String txHash) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Object event = ontSdk.getConnect().getSmartCodeEvent(txHash);
        return event;
    }

    public byte[] getKey(String wif) {
        return Account.getPrivateKeyFromWIF(wif);
    }

    public String signMessage(String message, String wif) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Account account = new Account(Account.getPrivateKeyFromWIF(wif), ontSdk.getWalletMgr().getSignatureScheme());
        DataSignature sign = new DataSignature(SignatureScheme.SHA256WITHECDSA, account, message.getBytes());
        return Helper.toHexString(sign.signature());
    }

    public String getOntId(byte[] key) throws Exception {
        Account account = new Account(key, getOntSdk().getWalletMgr().getSignatureScheme());
        return "did:ont:" + account.getAddressU160().toBase58();
    }

    public List<String> getDataIdController(String dataOntId) throws Exception {
        OntSdk ontSdk = getOntSdk();

        if (dataOntId == null) {
            throw new SDKException(ErrorCode.ParamErr("ontid should not be null"));
        }
        if (contractAddress == null) {
            throw new SDKException(ErrorCode.NullCodeHash);
        }

        List list = new ArrayList();
        list.add(dataOntId.getBytes());
        byte[] arg = NativeBuildParams.createCodeParamsScript(list);

        Transaction tx = ontSdk.vm().buildNativeParams(new Address(Helper.hexToBytes(contractAddress)), "getDDO", arg, null, 0, 0);
        Object obj = ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String res = ((JSONObject) obj).getString("Result");
        if (res.equals("")) {
            return null;
        }
        return parseControllerFromDdo(res);
    }

    private List<String> parseControllerFromDdo(String res) throws UnsupportedEncodingException {
        byte[] bys = Helper.hexToBytes(res);
        ByteArrayInputStream bais = new ByteArrayInputStream(bys);
        BinaryReader br = new BinaryReader(bais);
        byte[] controllerBytes;
        try {
            br.readVarBytes();
        } catch (Exception e) {
        }
        try {
            br.readVarBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            br.readVarBytes();
        } catch (Exception e) {
        }

        try {
            controllerBytes = br.readVarBytes();
        } catch (Exception e) {
            controllerBytes = new byte[]{};
        }

        List<String> controllers = new ArrayList<>();
        if (controllerBytes.length != 0) {
            String controllerStr = new String(controllerBytes);
            if (controllerStr.startsWith(Common.didont)) {
                controllers.add(controllerStr);
                log.info(controllerStr);
            } else {
                JSONObject jsonObject = JSON.parseObject(controllerStr);
                List<String> members = jsonObject.getObject("members", List.class);
                for (String controller : members) {
                    String ontId = Base64ConvertUtil.decode(controller);
                    controllers.add(ontId);
                }
            }
        }
        return controllers;
    }

    public String regIdWithGroup(List<String> dataOntIdList, List<String> controllers, String userOntId, byte[] userPk) throws Exception {
        OntSdk ontSdk = getOntSdk();

        OutputStream groupOutputStream = new ByteArrayOutputStream();

        BinaryWriter groupWriter = new BinaryWriter(groupOutputStream);
        HelperUtil.writeBigInt(groupWriter, controllers.size());
        for (String member : controllers) {
            groupWriter.writeVarBytes(member.getBytes());
        }
        HelperUtil.writeBigInt(groupWriter, 1);

        OutputStream signerOutputStream = new ByteArrayOutputStream();
        BinaryWriter signerWriter = new BinaryWriter(signerOutputStream);
        HelperUtil.writeBigInt(signerWriter, 1);

        signerWriter.writeVarBytes(userOntId.getBytes());
        HelperUtil.writeBigInt(signerWriter, 1);

        List<Object> paramList = new ArrayList<>();
        paramList.add("reg_ids_with_controller".getBytes());
        List args2 = new ArrayList();
        args2.add(dataOntIdList);
        args2.add(((ByteArrayOutputStream) groupOutputStream).toByteArray());
        args2.add(((ByteArrayOutputStream) signerOutputStream).toByteArray());
        paramList.add(args2);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(configParam.DATA_CONTRACT), null, params, configParam.PAYER_ADDRESS, Constant.GAS_LIMIT, Constant.GAS_PRICE);

        Account account = new Account(userPk, ontSdk.getWalletMgr().getSignatureScheme());
        ontSdk.addSign(tx, account);

        byte[] payer = Account.getPrivateKeyFromWIF(configParam.PAYER_WIF);
        Account payerAcct = new Account(payer, ontSdk.getWalletMgr().getSignatureScheme());

        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);
        return tx.hash().toString();
    }

    public String regIdsWithController(List args1, List userPks) throws Exception {
        OntSdk ontSdk = getOntSdk();
        List<Object> paramList = new ArrayList<>();
        paramList.add("reg_id_with_controller".getBytes());
        paramList.add(args1);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(configParam.DATA_CONTRACT), null, params, configParam.PAYER_ADDRESS, Constant.GAS_LIMIT, Constant.GAS_PRICE);


        for (int i = 0; i < userPks.size(); i++) {
            Account account = new Account((byte[]) userPks.get(i), ontSdk.getWalletMgr().getSignatureScheme());
            ontSdk.addSign(tx, account);
        }

        byte[] payer = Account.getPrivateKeyFromWIF(configParam.PAYER_WIF);
        Account payerAcct = new Account(payer, ontSdk.getWalletMgr().getSignatureScheme());

        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);
        return tx.hash().toString();
    }

    public String regIdsWithOneController(List<String> dataOntIds, String userId) throws Exception {
        OntSdk ontSdk = getOntSdk();

        String[] userIds = new String[]{userId};
        byte[] userPk = RootKeyUtil.rootKey.generateKeys(RootKeyUtil.userKey, userIds)[0];
        String ontId = getOntId(userPk);

        List args1 = new ArrayList();
        args1.add(dataOntIds);
        args1.add(ontId.getBytes());
        args1.add(1);

        List<Object> paramList = new ArrayList<>();
        paramList.add("reg_ids_with_controller".getBytes());
        paramList.add(args1);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(configParam.DATA_CONTRACT), null, params, configParam.PAYER_ADDRESS, Constant.GAS_LIMIT, Constant.GAS_PRICE);

        Account account = new Account(userPk, ontSdk.getWalletMgr().getSignatureScheme());
        ontSdk.addSign(tx, account);
        byte[] payer = Account.getPrivateKeyFromWIF(configParam.PAYER_WIF);
        Account payerAcct = new Account(payer, ontSdk.getWalletMgr().getSignatureScheme());

        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);
        return tx.hash().toString();
    }

    public Long queryHonorPoint(String address) throws Exception {
        OntSdk ontSdk = getOntSdk();

        List args1 = new ArrayList();
        args1.add(Address.decodeBase58(address).toArray());

        List<Object> paramList = new ArrayList<>();
        paramList.add("balanceOf".getBytes());
        paramList.add(args1);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(configParam.HONOR_POINT_CONTRACT), null, params, configParam.PAYER_ADDRESS, Constant.GAS_LIMIT, Constant.GAS_PRICE);

        JSONObject jsonObject = (JSONObject)ontSdk.getConnect().sendRawTransactionPreExec(tx.toHexString());
        String result = jsonObject.getString("Result");
        if (StringUtils.isEmpty(result)) {
            return 0L;
        }
        String reverse = com.github.ontio.common.Helper.reverse(result);
        return Long.valueOf(reverse, 16);

    }

    public void distributeHonorPoint(String address, Long amount) throws Exception {
        OntSdk ontSdk = getOntSdk();

        byte[] owner = Account.getPrivateKeyFromWIF(configParam.HONOR_POINT_WIF);
        Account ownerAcct = new Account(owner, ontSdk.getWalletMgr().getSignatureScheme());

        List args1 = new ArrayList();
        args1.add(ownerAcct.getAddressU160().toArray());
        args1.add(Address.decodeBase58(address).toArray());
        args1.add(amount);

        List<Object> paramList = new ArrayList<>();
        paramList.add("transfer".getBytes());
        paramList.add(args1);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(configParam.HONOR_POINT_CONTRACT), null, params, configParam.PAYER_ADDRESS, Constant.GAS_LIMIT, Constant.GAS_PRICE);

        byte[] payer = Account.getPrivateKeyFromWIF(configParam.PAYER_WIF);
        Account payerAcct = new Account(payer, ontSdk.getWalletMgr().getSignatureScheme());

        ontSdk.addSign(tx, ownerAcct);
        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);
    }

    public String takeOrder(String authId, String receiveAddress, byte[] userPk) throws Exception {
        OntSdk ontSdk = getOntSdk();

        Account account = new Account(userPk, ontSdk.getWalletMgr().getSignatureScheme());

        List args1 = new ArrayList();
        args1.add(Helper.hexToBytes(authId));
        args1.add(Address.decodeBase58(receiveAddress).toArray());
        args1.add(1);
        args1.add(Address.decodeBase58(configParam.OJ_ADDRESS).toArray());

        List<Object> paramList = new ArrayList<>();
        paramList.add("takeOrder".getBytes());
        paramList.add(args1);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(configParam.MP_CONTRACT), null, params, configParam.PAYER_ADDRESS, Constant.GAS_LIMIT, Constant.GAS_PRICE);

        byte[] payer = Account.getPrivateKeyFromWIF(configParam.PAYER_WIF);
        Account payerAcct = new Account(payer, ontSdk.getWalletMgr().getSignatureScheme());

        ontSdk.addSign(tx, account);
        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);

        return tx.hash().toString();
    }

    public String regIdAndAuth(List<String> dataOntIdList, List<String> controllers, String userOntId, byte[] userPk) throws Exception {
        OntSdk ontSdk = getOntSdk();

        OutputStream groupOutputStream = new ByteArrayOutputStream();

        BinaryWriter groupWriter = new BinaryWriter(groupOutputStream);
        HelperUtil.writeBigInt(groupWriter, controllers.size());
        for (String member : controllers) {
            groupWriter.writeVarBytes(member.getBytes());
        }
        HelperUtil.writeBigInt(groupWriter, 1);

        OutputStream signerOutputStream = new ByteArrayOutputStream();
        BinaryWriter signerWriter = new BinaryWriter(signerOutputStream);
        HelperUtil.writeBigInt(signerWriter, 1);

        signerWriter.writeVarBytes(userOntId.getBytes());
        HelperUtil.writeBigInt(signerWriter, 1);

        List args2 = new ArrayList();
        for (String dataOntId : dataOntIdList) {
            List args3 = new ArrayList();
            List ojList = new ArrayList();
            ojList.add(Address.decodeBase58(configParam.OJ_ADDRESS).toArray());
            args3.add(dataOntId.getBytes());
            args3.add("OBDS".getBytes());
            args3.add("openbase dataset".getBytes());
            args3.add(100000000000L);
            args3.add(0);
            args3.add(1);
            args3.add(1);
            args3.add(0);
            args3.add(Helper.hexToBytes(configParam.DATA_TOKEN_CONTRACT));
            args3.add(Address.decodeBase58(userOntId.substring(8)).toArray());
            args3.add(Address.decodeBase58(configParam.OJ_ADDRESS).toArray());
            args3.add(ojList);

            args2.add(args3);
        }

        List<Object> paramList = new ArrayList<>();
        paramList.add("reg_ids_and_auth_order".getBytes());
        List args1 = new ArrayList();
        args1.add(args2);
        args1.add(((ByteArrayOutputStream) groupOutputStream).toByteArray());
        args1.add(((ByteArrayOutputStream) signerOutputStream).toByteArray());
        paramList.add(args1);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(configParam.DATA_CONTRACT), null, params, configParam.PAYER_ADDRESS, Constant.GAS_LIMIT, Constant.GAS_PRICE);

        Account account = new Account(userPk, ontSdk.getWalletMgr().getSignatureScheme());
        ontSdk.addSign(tx, account);

        byte[] payer = Account.getPrivateKeyFromWIF(configParam.PAYER_WIF);
        Account payerAcct = new Account(payer, ontSdk.getWalletMgr().getSignatureScheme());

        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);
        return tx.hash().toString();
    }

    public String consumeToken(Long tokenId, byte[] userPk) throws Exception {
        OntSdk ontSdk = getOntSdk();

        Account account = new Account(userPk, ontSdk.getWalletMgr().getSignatureScheme());

        List args1 = new ArrayList();
        args1.add(tokenId);

        List<Object> paramList = new ArrayList<>();
        paramList.add("consumeToken".getBytes());
        paramList.add(args1);
        byte[] params = BuildParams.createCodeParamsScript(paramList);
        Transaction tx = ontSdk.vm().makeInvokeCodeTransaction(Helper.reverse(configParam.DATA_TOKEN_CONTRACT), null, params, configParam.PAYER_ADDRESS, Constant.GAS_LIMIT, Constant.GAS_PRICE);

        byte[] payer = Account.getPrivateKeyFromWIF(configParam.PAYER_WIF);
        Account payerAcct = new Account(payer, ontSdk.getWalletMgr().getSignatureScheme());

        ontSdk.addSign(tx, account);
        ontSdk.addSign(tx, payerAcct);
        ontSdk.getConnect().sendRawTransaction(tx);

        return tx.hash().toString();
    }
}
