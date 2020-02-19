package com.ontology.utils;

import com.github.ontio.account.Account;
import com.github.ontio.common.Address;
import com.github.ontio.common.Common;
import com.github.ontio.common.Helper;
import com.github.ontio.crypto.ECC;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.wallet.Control;
import com.github.ontio.sdk.wallet.Identity;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.ec.CustomNamedCurves;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class RootKey {

    private static final X9ECParameters CURVE = CustomNamedCurves.getByName("secp256r1");
    private static int N = 16384;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public SignatureScheme scheme = SignatureScheme.SHA256WITHECDSA;
    private byte[] chainCode = ECC.generateKey();

    private static byte[] hmacSha512(byte[] keyBytes, byte[] text) {
        HMac hmac = new HMac(new SHA512Digest());
        byte[] resBuf = new byte[hmac.getMacSize()];
        CipherParameters pm = new KeyParameter(keyBytes);
        hmac.init(pm);
        hmac.update(text, 0, text.length);
        hmac.doFinal(resBuf, 0);
        return resBuf;
    }

    private static byte[] hmacSha256(byte[] keyBytes, byte[] text) {
        HMac hmac = new HMac(new SHA256Digest());
        byte[] resBuf = new byte[hmac.getMacSize()];
        CipherParameters pm = new KeyParameter(keyBytes);
        hmac.init(pm);
        hmac.update(text, 0, text.length);
        hmac.doFinal(resBuf, 0);
        return resBuf;
    }

    public void setChainCode(byte[] chainCode) {
        this.chainCode = chainCode;
    }

    private byte[] derivePrivateKey(byte[] rootKey, int i) throws KeyCollisionException {
        return derivePrivateKey(chainCode, rootKey, i);
    }

    private byte[] derivePrivateKey(byte[] rootKey, String id) throws KeyCollisionException {
        int i = 0;
        while (true) {
            try {
                return derivePrivateKey(chainCode, hmacSha256(rootKey, id.getBytes()), i);
            } catch (KeyCollisionException e) {
                ++i;
            }
        }
    }

    private byte[] derivePrivateKey(byte[] chainCode, byte[] parentKey, int i) throws KeyCollisionException {
        // https://github.com/bitcoin/bips/blob/master/bip-0032.mediawiki#private-parent-key--private-child-key
        // https://github.com/btcsuite/btcutil/hdkeychain/extendedkey.go # Child(i)

        // The data used to derive the child key depends on whether or not the
        // child is hardened per [BIP32].
        // For hardened children:
        //   0x00 || ser256(parentKey) || ser32(i)

        final byte[] data = new byte[1 + 32 + 4];
        data[0] = 0;
        System.arraycopy(parentKey, 0, data, 1, 32);
        System.arraycopy(ByteBuffer.allocate(4).putInt(i & 0xFFFFFFFF).array(), 0, data, 33, 4);

        // Take the HMAC-SHA512 of the current key's chain code and the derived
        // data:
        //   I = HMAC-SHA512(Key = chainCode, Data = data)
        final byte[] I = hmacSha512(chainCode, data);
        Arrays.fill(data, (byte) 0);

        // Split "I" into two 32-byte sequences Il and Ir where:
        //   Il = intermediate key used to derive the child
        //   Ir = child chain code
        final byte[] Il = Arrays.copyOf(I, 32);
        final byte[] Ir = new byte[I.length - 32];
        System.arraycopy(I, 32, Ir, 0, Ir.length);

        // Both derived public or private keys rely on treating the left 32-byte
        // sequence calculated above (Il) as a 256-bit integer that must be
        // within the valid range for a secp256k1 private key.  There is a small
        // chance (< 1 in 2^127) this condition will not hold, and in that case,
        // a child extended key can't be created for this index and the caller
        // should simply increment to the next index.

        // all curve in Ontology is using secp256r1 than secp256k1, update accordingly
        final BigInteger parse256_Il = new BigInteger(1, Il);
        //   childKey = parse256(Il) + parentKey
        final BigInteger ki = parse256_Il.add(new BigInteger(1, parentKey)).mod(CURVE.getN());
        if (parse256_Il.compareTo(CURVE.getN()) >= 0 || ki.equals(BigInteger.ZERO)) {
//            return derivePrivateKey(chainCode, rootKey, i + 1);
            throw new KeyCollisionException(i);
        }


//        // Public extended key -> Non-hardened child public extended key
//        // For public children:
//        //   childKey = serP(point(parse256(Il)) + parentKey)
//        final ECPoint ki = CURVE.getG().multiply(parse256_Il).add(CURVE.getCurve().decodePoint(rootKey));
//
//        if (parse256_Il.compareTo(CURVE.getN()) >= 0 || ki.isInfinity()) {
//            // or throw exception
//            return derivePrivateKey(chainCode, rootKey, i + 1);
//        }


        if (ki.bitLength() > Il.length * 8)
            throw new RuntimeException("ser256 failed, cannot fit integer in buffer");
        final byte[] modArr = ki.toByteArray();
        Arrays.fill(Il, (byte) 0);
        if (modArr.length < Il.length) {
            System.arraycopy(modArr, 0, Il, Il.length - modArr.length, modArr.length);
        } else {
            System.arraycopy(modArr, modArr.length - Il.length, Il, 0, Il.length);
        }
        Arrays.fill(modArr, (byte) 0);

        return Il;
    }

    private Identity createIdentity(String label, byte[] salt, byte[] privateKey, int N) throws Exception {
        Account acct = new Account(privateKey, scheme);
        if (label == null || label.equals("")) {
            String uuidStr = UUID.randomUUID().toString();
            label = uuidStr.substring(0, 8);
        }
        Identity idt = new Identity();
        idt.ontid = Common.didont + Address.addressFromPubKey(acct.serializePublicKey()).toBase58();
        idt.label = label;
        idt.controls = new ArrayList<Control>();
        Control ctl = new Control(Helper.toHexString(acct.serializePrivateKey()), "keys-1", Helper.toHexString(acct.serializePublicKey()));
        ctl.setSalt(salt);
        ctl.setAddress(Address.addressFromPubKey(acct.serializePublicKey()).toBase58());
        idt.controls.add(ctl);

//        System.out.println(Common.didont + Address.addressFromPubKey(acct.serializePublicKey()).toBase58());
//        System.out.println("pubkey:" + Helper.toHexString(acct.serializePublicKey()));
//        System.out.println("prikey:" + Helper.toHexString(acct.serializePrivateKey()));
//        System.out.println("priwif:" + acct.exportWif());
//        System.out.println("password:" + password);
//        System.out.println("encryptedPrikey:" + acct.exportGcmEncryptedPrikey(password, salt, N));
//        System.out.println("addressU160:" + acct.getAddressU160().toHexString());

        return idt;
    }

    public Identity createIdentity(byte[] prikey) throws Exception {
        return createIdentity("", prikey, N);
    }

    public Identity createIdentity(String label, byte[] prikey) throws Exception {
        return createIdentity(label, prikey, N);
    }

    public Identity createIdentity(String label, byte[] prikey, int N) throws Exception {
        byte[] salt = ECC.generateKey(16);
        return createIdentity(label, salt, prikey, N);
    }

    public byte[] generateKeys(byte[] rootKey, int offset) throws Exception {
        return generateKeys(rootKey, offset, 1)[0];
    }

    public synchronized byte[][] generateKeys(byte[] rootKey, int offset, int count) throws Exception {
        ArrayList<byte[]> keys = new ArrayList<>();

        for (int i = offset; i < offset + count; ++i) {
            byte[] pk = derivePrivateKey(rootKey, i);
            keys.add(pk);
        }
        return keys.toArray(new byte[keys.size()][]);
    }

    public synchronized byte[][] generateKeys(byte[] rootKey, String[] ids) throws Exception {
        ArrayList<byte[]> keys = new ArrayList<>();
        for (String id : ids) {
            byte[] pk = derivePrivateKey(rootKey, id);
            keys.add(pk);
        }
        return keys.toArray(new byte[keys.size()][]);
    }
}