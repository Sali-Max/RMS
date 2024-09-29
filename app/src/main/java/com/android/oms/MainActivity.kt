package com.android.oms

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

//Security
import java.security.MessageDigest



// Encrypy RSA
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.util.Base64
import javax.crypto.Cipher



//Write SMS
import android.telephony.SmsManager
import android.view.ViewGroup
import android.widget.Toast





//Read SMS



import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

import android.app.Activity
import android.content.pm.PackageManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import com.android.oms.databinding.ActivityMainBinding


import android.Manifest



class MainActivity : AppCompatActivity() {


    // Global Variable
    private val SMS_PERMISSION_CODE = 100
    val PICK_CONTACT_REQUEST = 1  // یک کد درخواست برای نتیجه مخاطبین

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()





    }


    private fun checkPermission() { //  Give SMS Prmisson
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
        }
    }


    fun keyIsValid(publicKey: PublicKey):Boolean
    {
        try {
            encrypt("TEST", publicKey)
        }
        catch (e:Exception)
        {
            return false
        }
        return true
    }

    //On click send BTN
    @RequiresApi(Build.VERSION_CODES.O)
    fun sendBtn(view: View) {

        val messageView: TextView = findViewById(R.id.messageView)
        val message = messageView.text.toString()

        if (message !="") { //Check Empty

            val name_key = findViewById<EditText>(R.id.keyNameView)
            val key: String = readString(name_key.text.toString(), "other-key").toString()
            val phoneNumberView: TextView = findViewById(R.id.phoneNumberView)
            val phone = phoneNumberView.text.toString()

                if (name_key.text.toString() == "key")  //send with my key public
                {
                    sendRMS(phone, message, getPublic())    //Send OMS with Key
                }
                else
                {
                    if (name_key.text.toString() != "" && key != "") { // Check Avilable Name_Key
                        try {
                            sendRMS(phone, message, stringToPublicKey(key))    //Send OMS with Key
                        }
                        catch (e:Exception) // Key Error
                        {
                            Toast.makeText(this, "Key is Not Valid :(", Toast.LENGTH_LONG).show()
                            return
                        }

                    } else {
                        Toast.makeText(this, "Enter Valid name Key :(", Toast.LENGTH_SHORT).show()
                    }
                }


        }

        else{
            Toast.makeText(this, "Enter Message :(", Toast.LENGTH_SHORT).show()
        }

    }


    //Send SMS


    fun sha256Hash(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }


    fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }


///////////////////////////////////////////////////////////////////////////////////////////////// RSA


    @RequiresApi(Build.VERSION_CODES.O)
    fun sendRMS(phoneNumber: String, message: String, pub_key: PublicKey) {

        // Byte Array to String
        val originalMessage = message
        val encryptedData = encrypt(originalMessage, pub_key)
        var encryptedText: String

        // EncrypyFile To String
        encryptedText = Base64.getEncoder().encodeToString(encryptedData)


        if (pub_key != getPublic()) {
            writeString(encryptedText, message, "send-message")
        }

        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            // Split the message into parts and send
            val parts = smsManager.divideMessage(encryptedText)
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            Toast.makeText(this, "RMS Sent :)", Toast.LENGTH_LONG).show()

            val messageView: TextView = findViewById(R.id.messageView)
            messageView.text = ""   //Clear Message After Send


        } catch (e: Exception) {
            Toast.makeText(this, "Failed to sendRMS :(", Toast.LENGTH_LONG).show()
        }

        return
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getRMS(encryptedText: String):String {

        // String To Byte Array
        val encryptedDataFromBase64 = Base64.getDecoder().decode(encryptedText)

        // Decrypt
        val decryptedMessage = decrypt(encryptedDataFromBase64, getPrivate())

        return decryptedMessage
    }


    fun generateRSAKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2032)
        return keyGen.genKeyPair()
    }


    fun encrypt(data: String, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(data.toByteArray())
    }


    fun decrypt(encryptedData: ByteArray, privateKey: PrivateKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(encryptedData)
        return String(decryptedBytes)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getPublic(): PublicKey {


        val public_tmp: String = readString("my-public", "my-key").toString() //get Public Key

        if (public_tmp == "") {
            val keyPair = generateRSAKeyPair()
            val publicKey = keyPair.public
            val privateKey = keyPair.private

            writeString("my-public", publicKeyToString(publicKey), "my-key")
            writeString("my-private", privateKeyToString(privateKey), "my-key")

            return publicKey

        }

        val publicKey: PublicKey = stringToPublicKey(public_tmp)

        return publicKey
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getPrivate(): PrivateKey {
        val private_tmp: String = readString("my-private", "my-key").toString()
        println(private_tmp) //Debug
        if (private_tmp == "") // if key not Found
        {
            val keyPair = generateRSAKeyPair()  // Create New Keys
            val publicKey = keyPair.public
            val privateKey = keyPair.private

            writeString("my-public", publicKeyToString(publicKey), "my-key")  //Only Save New Key
            writeString(
                "my-private",
                privateKeyToString(privateKey),
                "my-key"
            )   //Save And Return New Key

            return privateKey

        }

        val privateKey: PrivateKey =
            stringToPrivateKey(readString("my-private", "my-key").toString()) //Get AND Return

        return privateKey
    }


///////////////////////////////////////////////////////////////////////////////////////////////// END RSA


////////////////////////////// Save String


    fun writeString(key: String, value: String, group: String = "my-key") {
        val sharedPref = getSharedPreferences("$group", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString(key, value)  // Save the string value
        editor.apply()                // Commit the changes asynchronously
    }


    fun readString(key: String, group: String, defaultValue: String = ""): String? {

        val sharedPref = getSharedPreferences("$group", MODE_PRIVATE)
        return sharedPref.getString(
            key,
            defaultValue
        )  // Return the stored value, or the default value if key is not found
    }


///////////////////////////// Save String


// Get All SMS And Show


// Get All SMS And Show


    // Key Convert

    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToPrivateKey(keyString: String): PrivateKey {
        // تبدیل رشته Base64 به بایت آرایه
        val keyBytes = Base64.getDecoder().decode(keyString)
        // ساختن کلید خصوصی از بایت آرایه
        val keySpec = PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun privateKeyToString(privateKey: PrivateKey): String {
        // تبدیل کلید به بایت آرایه
        val privateKeyBytes = privateKey.encoded
        // تبدیل بایت آرایه به رشته Base64
        return Base64.getEncoder().encodeToString(privateKeyBytes)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToPublicKey(publicKeyString: String): PublicKey {
        // تبدیل رشته Base64 به بایت آرایه
        val keyBytes = Base64.getDecoder().decode(publicKeyString)
        // ساختن کلید عمومی از بایت آرایه
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun publicKeyToString(publicKey: PublicKey): String {
        val publicKeyBytes = publicKey.encoded
        return Base64.getEncoder().encodeToString(publicKeyBytes)
    }


    // End Key Convert


    public fun qrCodeActivity(view: View) {
        val intent = Intent(this, KeyManager::class.java)
        startActivity(intent)
    }

    public fun allMessageActivity(view: View) {
        val intent = Intent(this, Messages::class.java)
        startActivity(intent)
    }

    public fun show_contant(view: View) {
        val contactPickerIntent =
            Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactPickerIntent, PICK_CONTACT_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { contactUri ->
                val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val cursor = contentResolver.query(contactUri, projection, null, null, null)
                cursor?.apply {
                    if (moveToFirst()) {
                        val numberIndex =
                            getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val phoneNumber = getString(numberIndex)
                        // قرار دادن شماره تلفن در TextInput
                        binding.phoneNumberView.setText(phoneNumber)
                    }
                    close()
                }
            }
        }





    }

}


