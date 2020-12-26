
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class UserPhoneNumber (

    @SerializedName("phoneNumber")
    var phoneNumber: String = "",

    @SerializedName("class")
    var clasz: String = "mobile",

    @SerializedName("provider")
    var provider: String? = "",

    @SerializedName("nickname")
    var nickname: String = ""

) :Serializable{
    companion object {
        const val TYPE_NUMBER = 10
        const val TYPE_ADD = 12
        const val TYPE_TEXT_RECENTLY = 15
    }


    @Transient
    var itemType  = TYPE_NUMBER
}


