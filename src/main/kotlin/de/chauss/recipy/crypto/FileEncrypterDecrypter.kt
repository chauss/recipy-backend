package de.chauss.recipy.crypto

import java.io.*
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun FileEncrypterDecrypter(
    secretKeyAsString: String
): FileEncrypterDecrypter {
    val decodedSecretKey = Base64.getDecoder().decode(secretKeyAsString)
    val secretKey: SecretKey = SecretKeySpec(decodedSecretKey, 0, decodedSecretKey.size, "AES")
    return FileEncrypterDecrypter(secretKey)
}

class FileEncrypterDecrypter(
    val secretKey: SecretKey,
) {
    private val cipher: Cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

    fun encrypt(content: String, fileName: String) {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val iv = cipher.iv
        FileOutputStream(fileName).use { fileOut ->
            CipherOutputStream(fileOut, cipher).use { cipherOut ->
                fileOut.write(iv)
                cipherOut.write(content.toByteArray())
            }
        }
    }

    fun decryptFile(fileName: String): String? {
        var content: String?
        FileInputStream(fileName).use { fileIn ->
            content = decrypt(fileIn)
        }
        return content
    }

    fun decrypt(inputStream: InputStream): String? {
        var content: String?
        val fileIv = ByteArray(16)
        inputStream.read(fileIv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(fileIv))
        CipherInputStream(inputStream, cipher).use { cipherIn ->
            InputStreamReader(cipherIn).use { inputReader ->
                BufferedReader(inputReader).use { reader ->
                    val sb = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line)
                    }
                    content = sb.toString()
                }
            }
        }
        return content
    }

}