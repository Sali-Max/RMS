package com.android.oms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

//QrCode
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64







class KeyManager : AppCompatActivity() {
    //Qrcode
    private lateinit var scanButton: Button
    private lateinit var resultTextView: TextView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_key_manager)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize the ImageView
        val imageView = findViewById<ImageView>(R.id.qrcodeView)

        // Generate QR code
        val qrCodeBitmap = generateQRCode(publicKeyToString(getPublic()))

        // Set the QR code bitmap to ImageView
        if (qrCodeBitmap != null) {
            imageView.setImageBitmap(qrCodeBitmap)
        }

    }


    fun writeString(key: String, value: String, group: String) {
        val sharedPref = getSharedPreferences("$group", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString(key, value)  // Save the string value
        editor.apply()                // Commit the changes asynchronously
    }


    fun readString(key: String, group: String="my-key" , defaultValue: String = ""): String? {
        val sharedPref = getSharedPreferences("$group", MODE_PRIVATE)
        return sharedPref.getString(
            key,
            defaultValue
        )  // Return the stored value, or the default value if key is not found
    }


    fun generateQRCode(text: String): Bitmap? {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitMatrix: BitMatrix =
                barcodeEncoder.encode(text, BarcodeFormat.QR_CODE, 1024, 1024)
            return barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }


// Main Activity


    @RequiresApi(Build.VERSION_CODES.O)
    fun getPublic(): PublicKey {


        val public_tmp: String = readString("my-public", "my-key").toString()
        println(public_tmp) //Debug
        if (public_tmp == "") {
            val keyPair = generateRSAKeyPair()
            val publicKey = keyPair.public
            val privateKey = keyPair.private

            writeString("my-public", publicKeyToString(publicKey), "my-key")
            writeString("my-private", privateKeyToString(privateKey), "my-key")

            return publicKey

        }

        val publicKey: PublicKey = stringToPublicKey(readString("my-public", "my-key").toString())

        return publicKey
    }


    fun generateRSAKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2032)
        return keyGen.genKeyPair()
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


    @RequiresApi(Build.VERSION_CODES.O)
    fun privateKeyToString(privateKey: PrivateKey): String {
        // تبدیل کلید به بایت آرایه
        val privateKeyBytes = privateKey.encoded
        // تبدیل بایت آرایه به رشته Base64
        return Base64.getEncoder().encodeToString(privateKeyBytes)
    }

    fun stringToPrivate(base64PrivateKey: String): PrivateKey {
        // Decode the Base64 string to get the byte array
        val privateKeyBytes = Base64.getDecoder().decode(base64PrivateKey)

        // Create a PKCS8EncodedKeySpec from the byte array
        val keySpec = PKCS8EncodedKeySpec(privateKeyBytes)

        // Generate a KeyFactory for the RSA algorithm
        val keyFactory = KeyFactory.getInstance("RSA")

        // Generate and return the PrivateKey from the key specification
        return keyFactory.generatePrivate(keySpec)
    }


    fun addKeyBtn(view: View) {
        val intent = Intent(this, addKey::class.java)
        startActivity(intent)
    }

    public fun generate_key_btn(view: View)
    {



        AlertDialog.Builder(this)
            .setTitle("Confirmation")
            .setMessage("Are you sure you want to change your keys?")
            .setPositiveButton("Yes") { dialog, which ->
                // Change Keys
                val old_my_public: String =  readString("my-public","my-key").toString()
                val old_my_private: String = readString("my-private","my-key").toString()

                val keyPair = generateRSAKeyPair()
                val publicKey = keyPair.public
                val privateKey = keyPair.private

                writeString("my-public", publicKeyToString(publicKey), "my-key")
                writeString("my-private", privateKeyToString(privateKey), "my-key")
                writeString("my-public-old", old_my_public, "my-key")
                writeString("my-private-old", old_my_private, "my-key")
                // End Change Keys

                Toast.makeText(this, "Generated New Key :)", Toast.LENGTH_SHORT).show()

                //reGenerate qrcode
                val imageView = findViewById<ImageView>(R.id.qrcodeView)
                val qrCodeBitmap = generateQRCode(publicKeyToString(getPublic()))
                if (qrCodeBitmap != null) {
                    imageView.setImageBitmap(qrCodeBitmap)
                }
                //End Generate

            }
            .setNegativeButton("No") { dialog, which ->
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
            .setCancelable(true) // Optional: allows dialog to be canceled by clicking outside
            .show()








    }




//    public fun scan_qr(view: View)
//    {
//        val intent = Intent(this, scanQR::class.java)
//        startActivity(intent)
//    }


    public fun scan_btn(view: View) {
        val nameQrcode = findViewById<EditText>(R.id.qrNameView)
        if (nameQrcode.text.toString() == "") {
            Toast.makeText(this, "First Fill QrCode name", Toast.LENGTH_LONG).show()

        } else
        {
            IntentIntegrator(this)
                .setOrientationLocked(false)
                .initiateScan()
        }
    }

    // QrCode

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                // QR code scanned successfully
                Toast.makeText(this, "Added :)", Toast.LENGTH_SHORT).show()

                val nameQrcode = findViewById<EditText>(R.id.qrNameView).text
                writeString("$nameQrcode","${result.contents}", "other-key")

            } else {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }




}


