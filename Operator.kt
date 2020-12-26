package ir.app7030.android.helper

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

import java.util.Arrays

import ir.app7030.android.Base
import ir.app7030.android.R
import ir.app7030.android.utils.AppLogger
import ir.app7030.android.utils.getColorInt


enum class Operator constructor(val value: String) {
    MCI("mci"),
    IRANCELL("irancell"),
    MOKHABERAT("mokhaberat"),
    RIGHTEL("rightel");

    override fun toString(): String {
        return value
    }

    fun getPersianName(): String {
        return when(this){
            IRANCELL -> Base.get().getString(R.string.irancell)
            MCI -> Base.get().getString(R.string.mci)
            RIGHTEL -> Base.get().getString(R.string.rightel)
            MOKHABERAT -> Base.get().getString(R.string.telecommunications)
        }
    }
    fun getChargeTypes() : Array<ChargeType>{
        return when (this) {
            MCI -> {
                arrayOf(ChargeType.NORMAL,ChargeType.YOUNG ,ChargeType.LADIES)
            }
            IRANCELL -> {
                arrayOf(ChargeType.NORMAL,ChargeType.AMAZING_IRANCELL , ChargeType.IRANCELL_POSTPAID_BILL)
            }
            RIGHTEL -> {
                arrayOf(ChargeType.NORMAL,ChargeType.AMAZING_RIGHTEL)
            }
            MOKHABERAT -> {
                arrayOf()
            }

        }
    }
    fun getSimcardTypes() : Array<SimcardType>{
        return when (this) {
            MCI -> {
                arrayOf(SimcardType.PREPAID,SimcardType.POSTPAID )
            }
            IRANCELL -> {
                arrayOf(SimcardType.PREPAID,SimcardType.POSTPAID, SimcardType.DATA, SimcardType.TDLTE )
            }
            RIGHTEL -> {
                arrayOf(SimcardType.PREPAID,SimcardType.POSTPAID)
            }
            MOKHABERAT -> {
                arrayOf()
            }

        }
    }
    @ColorInt
    fun getOperatorNameTextColor(): Int {
        return when (this) {
            MCI -> Base.get().applicationContext.getColorInt(R.color.mci)

            RIGHTEL -> Base.get().applicationContext.getColorInt(R.color.rightel)

            IRANCELL -> Base.get().applicationContext.getColorInt(R.color.irancellSecond)

            MOKHABERAT -> Base.get().applicationContext.getColorInt(R.color.mokhaberat)


        }

    }
    companion object {

        fun getOperatorFromName(name: String) :Operator?{
            when (name) {
                "mci" -> return MCI

                "rightel" -> return RIGHTEL

                "irancell" -> return IRANCELL

                "mokhaberat" -> return MOKHABERAT
            }
            return null
        }

        fun getPersianName(name: String?): String {
            when (name) {
                "mci" -> return Base.get().getString(R.string.mci)

                "rightel" -> return Base.get().getString(R.string.rightel)

                "irancell" -> return Base.get().getString(R.string.irancell)

                "mokhaberat" -> return Base.get().getString(R.string.telecommunications)
            }
            return ""
        }

        fun getOperatorFromPhoneNumber(phone: String?): Operator? {
            if (phone==null || phone=="")
                return null

            var mPhone = phone
            val areaCode: String
            var operator: Operator? = null
            mPhone = mPhone.replace(" ", "")

            areaCode = when {
                mPhone.startsWith("+98") ->if (mPhone.length>7) mPhone.substring(3,6) else ""
                mPhone.startsWith("98") ->if (mPhone.length>6) mPhone.substring(2,5) else ""
                mPhone.startsWith("09") ->if (mPhone.length>5) mPhone.substring(1,4) else ""
                else -> {
                    // its telephone number
                    if (mPhone.length>3)
                      mPhone.substring(1,3)
                    else
                        ""
                }
            }
            if (areaCode=="") return null
            if (Arrays.asList(*Base.get().resources.getStringArray(R.array.mci_area_code)).contains(areaCode)) {
                operator = MCI
            }
            if (Arrays.asList(*Base.get().resources.getStringArray(R.array.irancell_area_code)).contains(areaCode)) {
                operator = IRANCELL
            }

            if (Arrays.asList(*Base.get().resources.getStringArray(R.array.rightel_area_code)).contains(areaCode)) {
                operator = RIGHTEL
            }
            if (Arrays.asList(*Base.get().resources.getStringArray(R.array.mokhaberat_area_code)).contains(areaCode)) {
                operator = MOKHABERAT
            }
            return operator
        }

        @ColorInt
        fun getOperatorColor(name: String): Int {
            return when (name) {
                MCI.value -> Base.get().applicationContext.getColorInt(R.color.mci)

                RIGHTEL.value -> Base.get().applicationContext.getColorInt(R.color.rightel)

                IRANCELL.value -> Base.get().applicationContext.getColorInt(R.color.irancell)

                MOKHABERAT.value -> Base.get().applicationContext.getColorInt(R.color.mokhaberat)

                else -> 0
            }

        }

        @DrawableRes
        fun getOperatorIcon(operator: Operator?): Int {
            return when (operator) {
                MCI -> {
                    AppLogger.e("Operator: operator is mci")
                    R.drawable.ic_operator_hamrah_aval_24
                }
                IRANCELL -> {
                    AppLogger.e("Operator: operator is irancell")
                    R.drawable.ic_operator_irancell_24
                }
                RIGHTEL -> {
                    AppLogger.e("Operator: operator is rightel")
                    R.drawable.ic_operator_rightel_24
                }
                MOKHABERAT -> {
                    AppLogger.e("Operator: operator is mokhaberat")
                    R.drawable.ic_operator_mokhaberat_24
                }
                else -> 0

            }

        }
    }





    enum class ChargeType(val value: String, val chargeNameFa: Int,val  chargeTitle: Int) {
        NORMAL("normal", R.string.normal_charge_simple, R.string.normal_direct_charge),
        AMAZING_IRANCELL("amazingirancell", R.string.amazing_direct_charge_simple, R.string.amazing_direct_charge),
        YOUNG("young", R.string.mci_young_simple, R.string.mci_young),
        LADIES("ladies", R.string.mci_ladies_simple, R.string.mci_ladies),
        AMAZING_RIGHTEL("amazingrightel", R.string.shrewd_charge_simple, R.string.shrewd_charge),
        IRANCELL_POSTPAID_BILL("postpaidbill", R.string.postpaid_bill_irancell_simple, R.string.postpaid_bill_irancell);


        companion object {
            fun getPersianChargeType(value: String?) : ChargeType?{
                return when(value){
                    NORMAL.value -> NORMAL
                    AMAZING_IRANCELL.value -> AMAZING_IRANCELL
                    YOUNG.value -> YOUNG
                    LADIES.value -> LADIES
                    AMAZING_RIGHTEL.value -> AMAZING_RIGHTEL
                    IRANCELL_POSTPAID_BILL.value -> IRANCELL_POSTPAID_BILL


                    else -> {
                        null
                    }
                }
            }
        }
    }

    enum class SimcardType(val value: String, val simTypeFa: Int) {
        PREPAID("prepaid", R.string.prepaid),
        POSTPAID("postpaid", R.string.postpaid),
        DATA("data", R.string.data),
        TDLTE("tdlte", R.string.tdlte);

        companion object {
            fun getPersianName(name: String): String {
                when (name) {
                    "prepaid" -> return Base.get().getString(R.string.prepaid)
                    "postpaid" -> return Base.get().getString(R.string.postpaid)
                    "data" -> return Base.get().getString(R.string.data)
                    "tdlte" -> return Base.get().getString(R.string.tdlte)
                }
                return ""
            }
        }

    }


}
