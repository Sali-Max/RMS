import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.android.oms.MainActivity
import com.android.oms.R
import com.android.oms.SmsItem
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher




data class SmsItem(
    val sender: String,
    val messageBody: String,
    val dateTime: String,
    val isSent: Boolean, // true اگر پیام ارسال شده باشد
    val receiverPhoneNumber: String // شماره تلفن دریافت‌کننده
)

class SmsAdapter(private val context: Context, private val smsList: List<SmsItem>) : BaseAdapter() {

    override fun getCount(): Int {
        return smsList.size
    }

    override fun getItem(position: Int): Any {
        return smsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    // تعیین نوع View
    override fun getItemViewType(position: Int): Int {
        return if (smsList[position].isSent) 0 else 1
    }

    override fun getViewTypeCount(): Int {
        return 2 // دو نوع View
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val viewHolder: ViewHolder

        // انتخاب layout بر اساس نوع پیام
        val smsItem = getItem(position) as SmsItem
        if (smsItem.isSent) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_sent, parent, false)
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_received, parent, false)
        }



        viewHolder = ViewHolder(view)

        // تنظیم اطلاعات
        if (smsItem.isSent) {
            viewHolder.tvSender.text =
                "Send To ${smsItem.receiverPhoneNumber}"   // Change Name your Sender SMS to "Your"
        } else {
            viewHolder.tvSender.text = "Receive From: ${smsItem.sender}"
        }

        if (isMessageEncrypted(smsItem.messageBody)) // iF Message Is encrypted
        {
            try { //Crash Hanlled
                val encryptedDataFromBase64 = Base64.getDecoder().decode(smsItem.messageBody)   // Convert Message to Array (required for decrypt)
                 val final_message : String = decrypt(encryptedDataFromBase64,getPrivate())   // Decrypy And Show
                if (isMessageEncrypted(final_message))
                {

                    viewHolder.tvMessageBody.text = "---------------\naccess denied\n---------------"
                    viewHolder.tvMessageBody.setTextColor(Color.RED)
                }
                else{
                    viewHolder.tvMessageBody.text = final_message
                    viewHolder.tvMessageBody.setTextColor(Color.GREEN)
                    println("--------------------------------------")
                    print(final_message)
                    println("--------------------------------------")
                }
            }
            catch (e : Exception)
            {
                viewHolder.tvMessageBody.text = smsItem.messageBody
            }

        }
        else    // if Message is Normall
        {
            viewHolder.tvMessageBody.text = smsItem.messageBody
        }


        viewHolder.tvDateTime.text = smsItem.dateTime

        return view
    }

    // ViewHolder برای نگه‌داری ویوها
    private class ViewHolder(view: View) {
        val tvSender: TextView = view.findViewById(R.id.tvSender)
        val tvMessageBody: TextView = view.findViewById(R.id.tvMessageBody)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
    }

    fun wordCount(input: String): Int {
        val words = input.split(Regex("\\s+")).filter { it.isNotEmpty() }
        return words.size
    }
// Decrypy ///////////////////////////////////////////////////////////////////////////////////////////////////////////////



    fun isMessageEncrypted(input: String): Boolean {

        if (input.length >= 120 && !isNumeric(input))
        {
            if (wordCount(input) <= 4)
            {
                return true
            }
        }

        if(input.length >= 200 && wordCount(input) <= 6 && !isNumeric(input))
        {
            return true
        }


        if (wordCount(input) == 1 && input.length > 50 && !isNumeric(input))  //  word count = 1
        {
                return true
        }

//        val base64Regex = Regex("^[A-Za-z0-9+/=]+$")
//        if (!base64Regex.matches(input)) {
//            return true
//        }

        if (input.endsWith("%") || input.startsWith("'") || input.startsWith("%") || input.endsWith("=" ) || input.endsWith("�") || input.startsWith("�") || input.startsWith("\u0014")) {
            return true
        }

        return false
    }

    fun isNumeric(input: String): Boolean {
        return input.all { it.isDigit() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getRMS(encryptedText: String): String {

        // String To Byte Array
        val encryptedDataFromBase64 = Base64.getDecoder().decode(encryptedText)

        // Decrypt
        val decryptedMessage = decrypt(encryptedDataFromBase64, getPrivate())

        return decryptedMessage
    }

    fun decrypt(encryptedData: ByteArray, privateKey: PrivateKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decryptedBytes = cipher.doFinal(encryptedData)
        return String(decryptedBytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPrivate(): PrivateKey {
        val private_tmp: String = readString("my-private", "my-key").toString()

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
        return stringToPrivate(private_tmp)
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

    fun readString(key: String, group: String = "my-key", defaultValue: String = ""): String? {

        val sharedPref = context.getSharedPreferences("$group", MODE_PRIVATE)
        return sharedPref.getString(
            key,
            defaultValue
        )  // Return the stored value, or the default value if key is not found
    }


    fun generateRSAKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance("RSA")
        keyGen.initialize(2032)
        return keyGen.genKeyPair()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun privateKeyToString(privateKey: PrivateKey): String {
        // تبدیل کلید به بایت آرایه
        val privateKeyBytes = privateKey.encoded
        // تبدیل بایت آرایه به رشته Base64
        return Base64.getEncoder().encodeToString(privateKeyBytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun publicKeyToString(publicKey: PublicKey): String {
        val publicKeyBytes = publicKey.encoded
        return Base64.getEncoder().encodeToString(publicKeyBytes)
    }

    fun writeString(key: String, value: String, group: String = "my-key") {
        val sharedPref = context.getSharedPreferences("$group", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString(key, value)  // Save the string value
        editor.apply()                // Commit the changes asynchronously
    }



}
