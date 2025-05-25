package com.lineage.server.clientpackets;

import com.lineage.config.Config;
import com.lineage.echo.ClientExecutor;
import com.lineage.server.utils.Random;

import javax.crypto.Cipher;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.concurrent.TimeUnit;

public class C_Login_BeanFun extends ClientBasePacket {
    private long count = 0;

    @Override
    public void start(final byte[] decrypt, final ClientExecutor client) {
        // 資料載入
        this.read(decrypt);
        // cssddddddd
        //final byte[] acc = this.read(128);
        //final int pwLenth = readD();
        //final byte[] pw = this.read(128);
        // for test
        // GeneralThreadPool.getInstance().execute(new Counter());
        for (int i = 0; i < 5; i++) {
            // for test
            // GeneralThreadPool.getInstance().scheduleAtFixedRate(new Test(acc), 1, 1);
        }
        // client.sendPacket(new S_LoginResult(S_LoginResult.REASON_USER_OR_PASS_WRONG));
        client.kick();
    }

    private void write(final String fileName, final String s) throws Exception {
        final File file = new File(fileName);
        final BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        bw.write(s);
        bw.newLine();
        bw.close();
    }

    private PrivateKey getPrivateKey(final String modulus, final String privateExponent) throws Exception {
        final BigInteger m = new BigInteger(modulus, 16);
        final BigInteger e = new BigInteger(privateExponent, 16);
        final RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
        // KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        return privateKey;
    }

    @Override
    public String getType() {
        return "C_Login_BeanFun";
    }

    class Counter implements Runnable {
        @Override
        public void run() {
            long begin = System.currentTimeMillis();
            while (true) {
                try {
                    if (count >= 1000 * 20) {
                        System.out.println("嘗試了" + count + "次.." + (System.currentTimeMillis() - begin) + "ms");
                        count = 0;
                        begin = System.currentTimeMillis();
                    }
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Test implements Runnable {
        private final byte[] acc;

        public Test(final byte[] _acc) {
            acc = _acc;
        }

        @Override
        public void run() {
            // while (true) {
            // N KEY 256 bit
            final String LOGIN_RSA_MODULUS_N = "AD1BAC055BC99B5503406124C5A087625290A46B9069368E62F2F452C2399FE1AC22A53342B454E6CDD5AC60C14B64DB85799DF1BC3D939016EFE1FDA20C2446711632D8F7543DEC968F0947038FAB9CC627A8A1E69BD303153790FC8381AF16D5F08CB5412443E0392B7814171BEE80C5AAFF69721AB6045156BC75F5FFDD81";
            // D KEY
            String LOGIN_RSA_PRIVATE_EXPONET_D = "84BFE5C421D9649DBB05D5896509F6F6BD6C6A0FB8039590A7DF5E4F253D77CE8ED7EF6144C63279920D979467B8CCCDF1E3132F8D2A47AD17A3F946AA200379A1D366D4161989AB621D49715BC6EB172FEFA4F1D7871A9860C40A614FAAA8D418EF88E927B66E752C1303F90F7476335E16608BE6221954DD8F5D541D73C2BD";
            try {
                String answer = "";
                final String[] tokens = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
                for (int i = 0; i < 256; i++) {
                    answer += tokens[Random.nextInt(16)];
                }
                LOGIN_RSA_PRIVATE_EXPONET_D = answer;
                final PrivateKey privateKey = getPrivateKey(LOGIN_RSA_MODULUS_N, LOGIN_RSA_PRIVATE_EXPONET_D);
                final Cipher _cipher = Cipher.getInstance("RSA");
                _cipher.init(Cipher.DECRYPT_MODE, privateKey);
                final byte[] result = _cipher.doFinal(acc);
                final String loginName = new String(result, Config.CLIENT_LANGUAGE_CODE);
                if (loginName.equalsIgnoreCase("srwhsrwh13")) {
                    System.out.println("loginName=" + loginName);
                    System.out.println("answer=" + answer);
                    write("answer.txt", answer);
                    // break;
                }
                // TimeUnit.MILLISECONDS.sleep(1);
            } catch (final Exception e) {
                // e.printStackTrace();
            }
            count++;
            // }
        }
    }
}
