package ir.app7030.android.helper

import android.content.Context
import androidx.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import ir.app7030.android.R

import java.util.ArrayList

import ir.app7030.android.data.model.api.user.UserPhoneNumber
import ir.app7030.android.utils.FontUtils
import ir.app7030.android.utils.getColorInt
import ir.app7030.android.utils.getCompatDrawable
import ir.app7030.android.utils.toEnglishNumber
import ir.app7030.android.widget.AutoCompletePhoneListItemView


@Suppress("UNCHECKED_CAST")
class AutoCompletePhonesAdapter(private val mContext: Context, @LayoutRes private val viewResourceId: Int, private val items: ArrayList<UserPhoneNumber>) : ArrayAdapter<UserPhoneNumber>(mContext, viewResourceId, items) {
    private var userNumbersAll = ArrayList<UserPhoneNumber>()
    private var userNumbers = ArrayList<UserPhoneNumber>()
    private val textWatcher :PhoneNumberFormatTextWatcher = PhoneNumberFormatTextWatcher(context,4f)


    companion object {
        const val TAG="AutoCompleteAirportAdapter"

    }

    init {
        this.userNumbersAll = items.clone() as ArrayList<UserPhoneNumber>
        userNumbers.addAll(userNumbersAll)

    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var v :AutoCompletePhoneListItemView?= if(convertView==null) null else convertView as AutoCompletePhoneListItemView

        try {
            if (v == null) {
                v =AutoCompletePhoneListItemView(context = context)

            }
            when(userNumbers[position].itemType){
                UserPhoneNumber.TYPE_NUMBER ->{
                    v.setIconColor(null)
                    v.setTitleTextWatcher(textWatcher)
                    v.setTitle(userNumbers[position].nickname)
                    v.setSubTitle(userNumbers[position].phoneNumber)
                    val operatorId =Operator.getOperatorIcon(Operator.getOperatorFromPhoneNumber(userNumbers[position].phoneNumber))
                    v.setIcon(context.getCompatDrawable(if (operatorId==0) R.drawable.ic_simcard_24 else operatorId))
                    v.setTitleColor(context.getColorInt(R.color.colorBlack87))
                    v.setTitleTypeFace(FontUtils.getRegularFont(context))
                }
                UserPhoneNumber.TYPE_ADD ->{
                    v.removeTitleTextWatcher(textWatcher)
                    v.setTitle(context.getString(R.string.add_new_number))
                    v.setSubTitle(null)
                    v.setIcon(context.getCompatDrawable(R.drawable.ic_cross_24))
                    v.setTitleColor(context.getColorInt(R.color.colorSecondary))
                    v.setTitleTypeFace(FontUtils.getBoldFont(context))
                }

                UserPhoneNumber.TYPE_TEXT_RECENTLY ->{
                    v.removeTitleTextWatcher(textWatcher)
                    v.setTitle(null)
                    v.setSubTitle(context.getString(R.string.recently_numbers))
                    v.setIcon(null)
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()

        }

        return v!!
    }

    override fun getCount(): Int {

        return userNumbers.size
    }
    override fun getItem(position: Int): UserPhoneNumber? {

        return userNumbers[position]
    }

    override fun getFilter(): Filter {
        return phoneFilter

    }
    private var phoneFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String {
            return (resultValue as? UserPhoneNumber)?.phoneNumber ?: ""
        }

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val filterResults = FilterResults()
            val phoneSuggestion = ArrayList<UserPhoneNumber>()
            return if (constraint != null) {
                //typed = constraint.toString()

                for(phone in userNumbersAll){
                    if (phone.phoneNumber.toEnglishNumber().contains(constraint.toString().toEnglishNumber())) {
                        phoneSuggestion.add(phone)
                    }
                }

                filterResults.values = phoneSuggestion
                filterResults.count  = phoneSuggestion.size
                filterResults
            } else {
                Filter.FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults?) {
            if (results != null && results.count > 0) {
                userNumbers = results.values as ArrayList<UserPhoneNumber>
                notifyDataSetChanged()

            }else if (constraint == null) {

                // no filter, add entire original list widget_back in
                userNumbers.clear()
                userNumbers.addAll(userNumbersAll.subList(0,if (userNumbersAll.size > 6) 6 else userNumbersAll.size))
                notifyDataSetInvalidated()
            }
        }
    }

    /*internal var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String {
            return if (resultValue is UserPhoneNumber) resultValue.getName() else ""
        }

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            if (constraint != null) {
                typed = constraint.toString()
                userNumbers.clear()
                for (UserPhoneNumber in userNumbersAll) {
                    if (UserPhoneNumber.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        userNumbers.add(UserPhoneNumber)
                        if (userNumbers.size > 4)
                            break
                    }
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = userNumbers
                filterResults.count = userNumbers.size
                return filterResults
            } else {
                return Filter.FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence, results: Filter.FilterResults?) {

            if (results != null && results.count > 0) {
                val filteredList = results.values as ArrayList<UserPhoneNumber>
                clear()
                addAll(filteredList)
                notifyDataSetChanged()
            }
        }
    }*/

}
