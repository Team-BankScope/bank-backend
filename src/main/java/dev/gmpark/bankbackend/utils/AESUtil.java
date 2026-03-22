package dev.gmpark.bankbackend.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    
    // 16byte 고정 키 (실제 서비스에서는 외부 설정 파일이나 환경변수에서 가져와야 함)
    private static final String SECRET_KEY = "MySuperSecretKey"; // 16자 필수

    public static String encrypt(String plainText) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
            // ECB 모드는 IV가 필요 없고, 같은 평문이면 항상 같은 암호문이 나와 검색에 용이함
            // (보안 수준은 CBC보다 낮지만, DB 검색이 필요한 경우 타협안으로 사용)
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); 
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
