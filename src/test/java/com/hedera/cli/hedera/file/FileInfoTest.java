package com.hedera.cli.hedera.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

@ExtendWith(MockitoExtension.class)
public class FileInfoTest {

    @Test
    public void testEncodeDecode() {
        Base64.Encoder enc = Base64.getEncoder();
        Base64.Decoder dec = Base64.getDecoder();
        String str = "77+9x6s=";

        // encode data using BASE64
        String encoded = enc.encodeToString(str.getBytes());
        System.out.println("encoded value is \t" + encoded);

        // Decode data
        String decoded = new String(dec.decode(encoded));
        System.out.println("decoded value is \t" + decoded);
        System.out.println("original value is \t" + str);
    }
}
