package com.opdev.util.encoding.aes;

import com.opdev.exception.InvalidAESCipherException;
import com.opdev.util.RngUtil;
import com.opdev.util.encoding.TalentIdEncoder;
import com.opdev.util.encoding.base64.Base64Util;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Component
public class AESTalentIdEncoder implements TalentIdEncoder {

    private final SecretKey secret;
    private static final int IV_LENGTH = 12;
    private static final int AUTH_TAG_LENGTH = 128;

    public AESTalentIdEncoder(@Value("${security.aes.secret}") String secret){
        this.secret = new SecretKeySpec(Base64Util.decode(secret), "AES");
    }

    @Override
    public String encode(Long talentId, Long companyId) {
        Objects.requireNonNull(talentId);
        Objects.requireNonNull(companyId);

        byte[] inputBytes = longPairToByteArray(talentId, companyId);
        byte[] encodedBytes = encodeBytes(inputBytes);
        return Base64Util.encode(encodedBytes);
    }

    @Override
    public Long decode(String encodedString) {
        byte[] encodedBytes = Base64Util.decode(encodedString);
        byte[] decodedBytes = decodeBytes(encodedBytes);
        return extractFirstLongFromByteArray(decodedBytes);
    }

    @SneakyThrows
    private byte[] encodeBytes(byte[] input) {
        // Generating a new IV for each cipher
        byte[] iv = RngUtil.generateRandomBytes(IV_LENGTH);
        byte[] encodedInput = cipher(input, iv, Cipher.ENCRYPT_MODE);

        // Output follows the form: <iv><cipher>
        ByteBuffer byteBuffer = ByteBuffer.allocate(encodedInput.length + iv.length);
        byteBuffer.put(iv).put(encodedInput);

        return byteBuffer.array();
    }

    private byte[] decodeBytes(byte[] encodedBytes) {
        // Input is the output of encodeBytes, thus contains form <iv><cipher>
        ByteBuffer byteBuffer = ByteBuffer.wrap(encodedBytes);

        // Extract the specific IV which was used for the cipher
        byte[] iv = new byte[IV_LENGTH];
        byteBuffer.get(iv);

        byte[] encodedData = new byte[byteBuffer.remaining()];
        byteBuffer.get(encodedData);

        try {
            return cipher(encodedData, iv, Cipher.DECRYPT_MODE);
        }
        catch(GeneralSecurityException e) {
            throw new InvalidAESCipherException();
        }
    }

    private byte[] cipher(byte[] input, byte[] iv, int mode)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException
    {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(mode, secret, new GCMParameterSpec(AUTH_TAG_LENGTH, iv));
        return cipher.doFinal(input);
    }

    private byte[] longPairToByteArray(Long a, Long b) {
        // Allocate a buffer for 2 long numbers, each long is 8 bytes
        ByteBuffer buffer = ByteBuffer.allocate(8 * 2);
        buffer.putLong(a);
        buffer.putLong(b);
        return buffer.array();
    }

    private long extractFirstLongFromByteArray(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        return buffer.asLongBuffer().get(0);
    }

}
