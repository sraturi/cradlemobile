package com.cradletrial.cradlevhtapp.utilitiles;

import android.util.Base64;
import android.util.Log;

import com.cradletrial.cradlevhtapp.BuildConfig;

import org.threeten.bp.ZonedDateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;


public class HybridFileEncrypter {
    /* Encryption Design:
     - AES is symmetric, requiring a single private key plus an initialization vector (IV)
     - RSA is asymmetric, requiring a public key for encryption, but limits on data length (~60-117 bytes)
     - Hybrid Cryptosystem:
        * AES encrypt data with a random key
        * RSA encrypt the AES key & IV
    */

    /* Linux commands to process the generated file:
    unzip encrypted_reading_*.zip
    openssl rsautl -decrypt -inkey ~/t/test_rsa/private_key.pem -in aes_key.rsa -out aes_key.base64 -oaep
    openssl rsautl -decrypt -inkey ~/t/test_rsa/private_key.pem -in iv.base64.rsa -out iv.base64 -oaep

    base64 -d aes_key.base64 > aes_key.bin
    base64 -d iv.base64 > iv.bin
    hexdump -e '16/1 "%02x"' aes_key.bin > aes_key.hex
    hexdump -e '16/1 "%02x"' iv.bin > iv.hex

    openssl enc -d -aes-256-cbc -in data.zip.aes -out data_decoded.zip -K `cat aes_key.hex` -iv `cat iv.hex`
    unzip data_decoded.zip
    */
    private static final String TAG = "HybridFileEncrypter";

    private static final int NUM_BITS_AES_KEY = 256;

    private static final String LINEFEED = "\r\n";
    private static final String ABOUT_FILE_CONTENTS =
            "Encrypted Patient Record" + LINEFEED +
                    "This archive generated by the CRADLE VSA Support Application for Android (version " + BuildConfig.VERSION_NAME + ")" + LINEFEED +
                    "" + LINEFEED +
                    "This Zip file contains:" + LINEFEED +
                    "- about.txt        This file, which includes the public RSA (PEM) key used for encryption" + LINEFEED +
                    "- aes_key.rsa      Base64 encoded AES key, encrypted with RSA" + LINEFEED +
                    "- aes_iv.rsa       Base64 encoded AES initialization vector, encrypted with RSA" + LINEFEED +
                    "- data.zip.aes     AES encrypted data file (Zip)" + LINEFEED +
                    "" + LINEFEED +
                    "RSA encryption key used to encrypt the AES key & iv:" + LINEFEED;


    public static File hybridEncryptFile(File inputFile, String outputFileFolder, String rsaPublicKeyPEM) throws GeneralSecurityException, IOException {
        List<File> filesToZip = new ArrayList<>();
        File zipFile = null;
        try {
            // 1. Generate keys / vectors for encryption:
            // .. AES key & initialization vector (iv)
            SecretKey aesSecretKey = generateRandomAESKey(NUM_BITS_AES_KEY);
            byte[] iv = getIV();
            // .. RSA key
            PublicKey rsaPubKey = convertRsaPemToPublicKey(rsaPublicKeyPEM);

            // 2. RSA encrypt AES key; add to zip
            File aesKeyFile = new File(outputFileFolder, "aes_key.rsa");
            {
                String base64AesKey = Base64.encodeToString(aesSecretKey.getEncoded(), Base64.NO_WRAP);
                byte[] encryptedAesKey = encryptRSA(rsaPubKey, base64AesKey.getBytes("UTF-8"));
                writeDataToFile(aesKeyFile, encryptedAesKey);
                filesToZip.add(aesKeyFile);
            }

            // 3. RSA encrypt AES initialization vector (IV)
            File aesIvFile = new File(outputFileFolder, "aes_iv.rsa");
            {
                String base64Iv = Base64.encodeToString(iv, Base64.NO_WRAP);
                byte[] encryptedIv = encryptRSA(rsaPubKey, base64Iv.getBytes("UTF-8"));
                writeDataToFile(aesIvFile, encryptedIv);
                filesToZip.add(aesIvFile);
            }

            // 4. AES encrypt inputFile; add to zip
            File dataFile = new File(outputFileFolder, "data.zip.aes");
            {
                encryptFileWithAES(aesSecretKey, iv, inputFile, dataFile);
                filesToZip.add(dataFile);
            }

            // 5. Add about.txt with RSA public key
            File readmeFile = new File(outputFileFolder, "readme.txt");
            {
                writeDataToFile(readmeFile,
                        ABOUT_FILE_CONTENTS + LINEFEED
                                + rsaPublicKeyPEM + LINEFEED);
                filesToZip.add(readmeFile);
            }

            // zip
            String dateTime = DateUtil.getISODateForFilename(ZonedDateTime.now());
            zipFile = new File(outputFileFolder, "encrypted_reading_" + dateTime + ".zip");
            Zipper.zip(filesToZip, zipFile);
        } finally {
            // cleanup (ignore return value; just cleaning up cache)
            Util.deleteFilesInList(filesToZip);
        }
        return zipFile;
    }

    private static void writeDataToFile(File outFile, byte[] data) throws IOException {
        try (FileOutputStream ofStream = new FileOutputStream(outFile)) {
            ofStream.write(data);
        }
    }

    private static void writeDataToFile(File outFile, String data) throws IOException {
        try (FileWriter writer = new FileWriter(outFile)) {
            writer.write(data);
        }
    }

    public static PublicKey convertRsaPemToPublicKey(String pkcsKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // source: https://stackoverflow.com/questions/7216969/getting-rsa-private-key-from-pem-base64-encoded-private-key-file#7221381
        // Remove the first and last lines
        final String HEADER = "-----BEGIN PUBLIC KEY-----";
        final String FOOTER = "-----END PUBLIC KEY-----";

        String pubKeyPEM = pkcsKey.trim();
        if (!pkcsKey.startsWith(HEADER)) {
            throw new InvalidKeySpecException("Public key must start with: " + HEADER);
        }
        if (!pkcsKey.endsWith(FOOTER)) {
            throw new InvalidKeySpecException("Public key must end with: " + FOOTER);
        }
        pubKeyPEM = pubKeyPEM.replace(HEADER, "");
        pubKeyPEM = pubKeyPEM.replace(FOOTER, "");
        pubKeyPEM = pubKeyPEM.trim();

        // Base64 decode the data
        byte[] keyBytes = Base64.decode(pubKeyPEM, Base64.DEFAULT);
        Log.e(TAG, "Decoded key bytes from base64 length: " + keyBytes.length);

        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private static SecretKey generateRandomAESKey(int numBytesInKey) throws NoSuchAlgorithmException {
        // source: https://stackoverflow.com/questions/18228579/how-to-create-a-secure-random-aes-key-in-java
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(NUM_BITS_AES_KEY);
        return keyGen.generateKey();
    }

    private static byte[] encryptRSA(PublicKey rsaPublicKey, byte[] data) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
        // To use SHA-1 for both digests
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey, new OAEPParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT));
        byte[] encryptedBytes = cipher.doFinal(data);

        return encryptedBytes;
    }

    private static void encryptFileWithAES(SecretKey secretAesKey, byte[] initializationVector, File sourceDataFile, File encryptedDataFile) throws GeneralSecurityException, IOException {
        // Get Cipher instance for AES algorithm (public key)
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // Initialize cipher
        SecretKeySpec skeySpec = new SecretKeySpec(secretAesKey.getEncoded(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector));

        // Encrypt the byte data
        try (FileOutputStream fosData = new FileOutputStream(encryptedDataFile);
             FileInputStream fisData = new FileInputStream(sourceDataFile)) {
            final int BUFF_SIZE = 1024 * 1024;
            byte[] buffer = new byte[BUFF_SIZE];

            while (fisData.available() > 0) {
                // get data
                int bytesRead = fisData.read(buffer);
                // encrypt
                byte[] encryptedBytes = cipher.doFinal(buffer, 0, bytesRead);
                // write to output file
                fosData.write(encryptedBytes);
            }
        }
    }

    private static byte[] getIV() {
        SecureRandom random = new SecureRandom();
        return random.generateSeed(16);
    }
}


