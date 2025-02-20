package com.example.statusbartemp.LogicAndData

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.InvalidKeyException
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

private const val ALGORITHM = "AES/GCM/NoPadding"
private const val IV_SIZE = 12 // 96 bits for GCM
private const val TAG_SIZE = 128 // 128 bits authentication tag

class SecureEncrypter {
    
    private var secretKey = getSecretKey()
    private val SECRET_KEY_ALIAS = "stb_secret_key_alias"
    
    fun encrypt(value: String, secretKey: SecretKey): String? {
        try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val cipherText = cipher.doFinal(value.toByteArray())
            (iv + cipherText).decodeToString()
            return Base64.encodeToString(iv + cipherText, Base64.DEFAULT)
        }
        catch (e : Exception){
            return null
        }
    }
    
    fun decrypt(value: String?, secretKey: SecretKey?): String? {
        //Log.v("decrypt", "value = $value")
        if (value != null && secretKey != null){
            try{
                val decodedValue = Base64.decode(value, Base64.DEFAULT)
                val iv = decodedValue.sliceArray(0 until IV_SIZE)
                val cipherText = decodedValue.sliceArray(IV_SIZE until decodedValue.size)
                
                val cipher = Cipher.getInstance(ALGORITHM)
                val spec = GCMParameterSpec(TAG_SIZE, iv)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
                val plainText = cipher.doFinal(cipherText)
                return String(plainText)
            }
            catch(e : Exception){
                if (e is InvalidKeyException){
                    return null
                }
                return null
            }
        }
        return null
    }
    
    fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            SECRET_KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }
    
    fun getSecretKeyWithCheck(): SecretKey {
        val secretKey = getSecretKey()
        secretKey?.let{ return it }
        return generateSecretKey()
    }
    
    fun getSecretKey(): SecretKey? {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            val secretKey = keyStore.getKey(SECRET_KEY_ALIAS, null) as? SecretKey
            return secretKey
        }
        catch (e : Exception){
            return null
        }
    }
    
    fun isKeyValid(): Boolean {
        return try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            keyStore.getKey(SECRET_KEY_ALIAS, null) != null
        } catch (e: Exception) {
            false // Key is invalid or doesn't exist
        }
    }
}