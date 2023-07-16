package se.haxtrams.launchpad.backend.util;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Component;
import se.haxtrams.launchpad.backend.model.MessageDigestAlgorithm;

@Component
public class HashUtil {
    public String getAbsolutePathHash(File file) {
        var payload = file.getAbsolutePath().getBytes(StandardCharsets.UTF_8);

        return HexUtils.toHexString(calculateHash(payload, MessageDigestAlgorithm.SHA1));
    }

    @SuppressWarnings("SameParameterValue")
    private byte[] calculateHash(byte[] payload, MessageDigestAlgorithm algorithm) {
        try {
            return MessageDigest.getInstance(algorithm.getValue()).digest(payload);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
