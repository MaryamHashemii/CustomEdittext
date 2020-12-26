package ir.app7030.android.widget


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.text.method.TransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import ir.app7030.android.R
import ir.app7030.android.data.database.repository.debitcard.DebitCard
import ir.app7030.android.data.model.api.bill.responses.SavedBill
import ir.app7030.android.data.model.api.user.UserPhoneNumber
import ir.app7030.android.helper.*
import ir.app7030.android.utils.*
import org.jetbrains.anko.*


class CustomEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr), View.OnFocusChangeListener, TextWatcher {


    private val TAG = "CustomEditText"
    private var tvTitle: TextView? = null
    private var tvBottomText: TextView? = null
    private var mEditText: AppCompatAutoCompleteTextView? = null
    private var mUnderline: ImageView? = null
    private var mLeftIcon: ImageView? = null
    private var mRightIcon: ImageView? = null
    private var underlineLayoutParams: LinearLayout.LayoutParams? = null
    private var container: RelativeLayout? = null
    private var isSetLeftIconColor: Boolean = true

    private val userPhoneNumbers: ArrayList<UserPhoneNumber> = arrayListOf()
    private val userCardNumber:ArrayList<DebitCard> = arrayListOf()
    private val userBillNumber:ArrayList<SavedBill.BillTypes> = arrayListOf()
    var onRightIconClickListener: ((view: View) -> Unit)? = null

    private var mAdapterSuggestions: ArrayAdapter<*>? = null
    private val title: String?


    private var hint: String?

    @ColorInt
    private var hintColor = -1

    @ColorInt
    private var textColor = -1

    @ColorInt
    private var titleColor = -1


    var leftIcon: Drawable? = null
    var rightIcon: Drawable? = null



    @State
    private var state: Int = 0


    private var inputType: Int
    private var imeOptions: Int

    private var mGravity: Int

    private var maxLength: Int
    private var maxLines: Int

    var mListener: OnEditTextListener? = null
    var mTextChangeListener: OnTextChangeListener? = null
    var mAddNumberListener: OnAddFavoriteNumberClickListener? = null
    var selectedUserPhoneNumberPos: Int?=null
    var selectedUserPhoneNumber: UserPhoneNumber?=null
    var selectedUserBillNumber: SavedBill?=null

    val isEmpty: Boolean
        get() = mEditText == null || mEditText?.text.toString().trim { it <= ' ' } == ""
    var text: String
        get() = mEditText?.text.toString()
        set(text) {
            mEditText?.setText("")
            mEditText?.append(text)
        }

    /*  @Retention(AnnotationRetention.SOURCE)
      @IntDef(INPUT_TYPE_PHONE, INPUT_TYPE_PASSWORD, INPUT_TYPE_TEXT, INPUT_TYPE_NUMBER)
      internal annotation class InputType*/

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(STATE_NORMAL, STATE_ERROR, STATE_FOCUSED)
    internal annotation class State

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText, defStyleAttr, 0)


        title = a.getString(R.styleable.CustomEditText_mTitle)

        hint = a.getString(R.styleable.CustomEditText_android_hint)

        hintColor = a.getColor(R.styleable.CustomEditText_hintColor, context.getColorInt(R.color.defaultHintColor))

        titleColor = a.getColor(R.styleable.CustomEditText_titleColor, context.getColorInt(R.color.colorBlack))

        textColor = a.getColor(R.styleable.CustomEditText_android_textColor, context.getColorInt(R.color.colorBlack))

        inputType = a.getInt(R.styleable.CustomEditText_android_inputType, EditorInfo.TYPE_NULL)

        mGravity = a.getInt(R.styleable.CustomEditText_android_gravity, -1)

        maxLength = a.getInt(R.styleable.CustomEditText_android_maxLength, -1)

        maxLines = a.getInt(R.styleable.CustomEditText_android_maxLines, -1)

        imeOptions = a.getInt(R.styleable.CustomEditText_android_imeOptions, -1)
        isSetLeftIconColor = a.getBoolean(R.styleable.CustomEditText_isSetLeftIconColor, true)


        //leftIcon = a.getDrawable(R.styleable.CustomEditText_leftIcon)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            leftIcon = a.getDrawable(R.styleable.CustomEditText_leftIcon)
        } else {
            val drawableTopId =a.getResourceId(R.styleable.CustomEditText_leftIcon, -1)

            if(drawableTopId != -1)
                leftIcon = context.getCompatDrawable(drawableTopId)
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            rightIcon = a.getDrawable(R.styleable.CustomEditText_rightIcon)
        } else {
            val drawableTopId =a.getResourceId(R.styleable.CustomEditText_rightIcon, -1)

            if(drawableTopId != -1)
                rightIcon = context.getCompatDrawable(drawableTopId)
        }



        a.recycle()

        init()

    }

    private fun init() {
        orientation = VERTICAL
        setState(STATE_NORMAL)
        tvTitle = TextView(context)
        tvTitle?.text = title  ?: ""
        tvTitle?.gravity = Gravity.START
        tvTitle?.typeface = FontUtils.getRegularFont(context)
        if (titleColor != -1) {
            tvTitle?.setTextColor(titleColor)
        }
        addView(tvTitle, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))


        addEditTextView()
        addUnderLine()
        addBottomTextView()

    }



    private fun addEditTextView() {
        container = RelativeLayout(context)
        container?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        mLeftIcon = ImageView(context)
        mLeftIcon?.setPadding(4, 4, 4, 4)
        val leftParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        leftParams.alignParentLeft()
        leftParams.addRule(RelativeLayout.CENTER_VERTICAL)
        mLeftIcon?.layoutParams = leftParams
        if (isSetLeftIconColor) mLeftIcon?.setColorFilter(context.getColorFromAttr(R.attr.colorAccent))

        mLeftIcon?.backgroundResource = context.getResourceId(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) android.R.attr.selectableItemBackgroundBorderless else android.R.attr.selectableItemBackground)

        leftIcon?.let {
            mLeftIcon?.setImageDrawable(it)
        }
        mLeftIcon?.setOnClickListener {
            mListener?.onLeftIconClick(it)
        }

        mRightIcon = ImageView(context)
        mRightIcon?.backgroundResource = context.getResourceId(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) android.R.attr.selectableItemBackgroundBorderless else android.R.attr.selectableItemBackground)
        rightIcon?.let {
            mRightIcon?.setImageDrawable(it)
        }

        mRightIcon?.setOnClickListener {
            onRightIconClickListener?.invoke(it)
        }

        val rightParams = RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        rightParams.rightMargin = 4
        rightParams.alignParentRight()
        rightParams.addRule(RelativeLayout.CENTER_VERTICAL)
        mRightIcon?.layoutParams = rightParams

        mEditText = AppCompatAutoCompleteTextView(context)
        mEditText?.typeface = FontUtils.getRegularFont(context)
        if (inputType != EditorInfo.TYPE_NULL) {
           setInputType(inputType)
        }

        if (maxLines>0){
            setMaxLines(maxLines)
        }

        setImeOption(imeOptions)

        setHint(hint)

        mEditText?.setTextColor(textColor)
        mEditText?.onFocusChangeListener = this
        mEditText?.addTextChangedListener(this)
        setUpGravity(mGravity)

        if (maxLength != -1) {
            val fArray = arrayOfNulls<InputFilter>(1)
            fArray[0] = LengthFilter(maxLength)
            mEditText?.filters = fArray
        }

        mEditText?.background = null
        val edittextLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        edittextLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL)

        mEditText?.setPadding(0, context.resources.getDimension(R.dimen.activity_padding_small).toInt(), 0, context.resources.getDimension(R.dimen.activity_padding_small).toInt())
        container?.addView(mLeftIcon)
        container?.addView(mEditText, edittextLayoutParams)
        container?.addView(mRightIcon)
        container?.isFocusable =false

        mLeftIcon?.bringToFront()
        mRightIcon?.bringToFront()


        addView(container)
    }

     fun setHint(hint: String?) {
        this.hint = hint
        if (hint != null) {
            mEditText?.hint = hint
            mEditText?.setHintTextColor(hintColor)
        }else{
            mEditText?.hint = ""
        }
    }

     fun setImeOption(imeOptions: Int) {
        this.imeOptions = imeOptions
        if (imeOptions>0){
            mEditText?.imeOptions = imeOptions
        }
    }

    private fun setUpGravity(gravity: Int) {
        mGravity = gravity
        if (gravity > 0) {
            mEditText?.gravity = gravity
        }
    }

    private fun setMaxLines(maxLines: Int) {
        this.maxLines = maxLines
        mEditText?.maxLines = maxLines
        if (maxLines==1){
            mEditText?.singleLine = true
        }
    }

     fun setInputType(inputType: Int) {
        this.inputType = inputType
        mEditText?.inputType =inputType

        if (isInputPhone()) {
            mEditText?.keyListener = DigitsKeyListener.getInstance("0123456789")

            mEditText?.filters = arrayOf(LengthFilter(11))

            mEditText?.addTextChangedListener(PhoneNumberFormatTextWatcher(mEditText!!))
        }
    }
    fun setGravityCenter(){
        mEditText?.gravity  = Gravity.CENTER
    }


    private fun addUnderLine() {
        if (mUnderline == null)
            mUnderline = ImageView(context)

        mUnderline?.setBackgroundColor(context.getColorInt(R.color.inputUnderlineNormalColor))

        if (underlineLayoutParams == null)
            underlineLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, context.resources.getDimensionPixelSize(R.dimen.custom_edittext_underline_height_normal))

        addView(mUnderline, underlineLayoutParams)
    }


    override fun onFocusChange(v: View, hasFocus: Boolean) {
        if (hasFocus) {
            if (state == STATE_ERROR) {
                mUnderline?.setBackgroundColor(context.getColorInt(R.color.inputUnderlineErrorColor))
            } else {
                setState(STATE_FOCUSED)

                //mUnderline?.setBackgroundColor(mContext.getColorInt(R.color.inputUnderlineFocusColor))
                mUnderline?.setBackgroundColor(context.getPrimaryColor())
            }
            mUnderline?.layoutParams?.height = context.resources.getDimensionPixelSize(R.dimen.custom_edittext_underline_height_focused)
            mUnderline?.requestLayout()
        } else {
            if (state == STATE_FOCUSED)
                setState(STATE_NORMAL)

            mUnderline?.setBackgroundColor(context.getColorInt(R.color.inputUnderlineNormalColor))
            mUnderline?.layoutParams?.height = context.resources.getDimensionPixelSize(R.dimen.custom_edittext_underline_height_normal)
            mUnderline?.requestLayout()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

    }


    fun setState(@State state: Int) {
        this.state = state
    }

    @State
    fun getState(): Int {
      return state
    }

    fun setError(@StringRes error: Int) {
        setError(resources.getString(error))
    }
    fun removeError() {
        setNormalState()
    }
    fun setError(error: String) {


        setState(STATE_ERROR)

        if (tvTitle != null) {
            tvTitle?.setTextColor(context.getColorInt(R.color.inputUnderlineErrorColor))
        }

        mUnderline?.setBackgroundColor(context.getColorInt(R.color.inputUnderlineErrorColor))
        mUnderline?.layoutParams?.height = context.resources.getDimensionPixelSize(R.dimen.custom_edittext_underline_height_focused)
        mUnderline?.requestLayout()

        setBottomInfo(error, context.getColorInt(R.color.inputUnderlineErrorColor))
    }

    private fun addBottomTextView() {
        tvBottomText = TextView(context)
        tvBottomText?.typeface = FontUtils.getRegularFont(context)
        tvBottomText?.gravity = Gravity.RIGHT
        tvBottomText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        val params=LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.topMargin = context.resources.getDimensionPixelSize(R.dimen.activity_vertical_margin_small)
        addView(tvBottomText, params)

    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {

        if (state == STATE_ERROR) {
           setNormalState()
        }

        if (isInputPhone()){
            if (s.length==11){
                selectedUserPhoneNumberPos?.let {
                    selectedUserPhoneNumber = if (userPhoneNumbers[it].phoneNumber == s.toString())
                        userPhoneNumbers[it]
                    else
                        UserPhoneNumber(phoneNumber = s.toString(), nickname = "...")
                }?: run{
                    selectedUserPhoneNumber= UserPhoneNumber(phoneNumber = s.toString(), nickname = "...")

                }
                showOperatorFromPhoneNumber()
                mListener?.onUserPhoneSelected(selectedUserPhoneNumber)
            }else{

                removeRightIcon()
                resetSelectedPhoneNumber()
            }

        }

        mTextChangeListener?.afterTextChange(s.toString().toEnglishNumber())

    }

    private fun resetSelectedPhoneNumber() {
        selectedUserPhoneNumberPos =null
        selectedUserPhoneNumber=null
        mTextChangeListener?.onRemovePhoneNumber()
        setBottomInfo("", null)
    }

    private fun setNormalState() {
        setState(STATE_NORMAL)
        tvBottomText?.text=""
        if (tvTitle != null)
            tvTitle?.setTextColor(titleColor)

        mUnderline?.setBackgroundColor(context.getPrimaryColor())
    }

    private fun removeRightIcon() {
        mRightIcon?.setImageDrawable(null)
    }


    fun showOperatorFromPhoneNumber() : Operator?{
        selectedUserPhoneNumber?.let { it ->

            val operator = Operator.getOperatorFromPhoneNumber(it.phoneNumber)
            operator?.let {
                 if (Operator.getOperatorIcon(it)!=0) {
                     setRightIcon(Operator.getOperatorIcon(it))
                     setBottomInfo(it.getPersianName(), it.getOperatorNameTextColor(), FontUtils.BOLD)
                 }
                 else
                     mRightIcon?.setImageDrawable(null)

                return it
            }?: run {
                mRightIcon?.setImageDrawable(null)
                return null
            }
        }?:kotlin.run {
            return null
        }
    }

    fun setCustomOperator(operator: Operator){
        setRightIcon(Operator.getOperatorIcon(operator))
        setBottomInfo(operator.getPersianName(), operator.getOperatorNameTextColor(), FontUtils.BOLD)
    }

    private fun isInputPhone() = inputType==InputType.TYPE_CLASS_PHONE

    fun setSuggestionListPhone(saved: ArrayList<UserPhoneNumber>, recently: ArrayList<UserPhoneNumber>) {


        this.userPhoneNumbers.clear()

        this.userPhoneNumbers.addAll(saved)
        val addItem= UserPhoneNumber()
        addItem.itemType = UserPhoneNumber.TYPE_ADD
        userPhoneNumbers.add(addItem)
        if (recently.size >0 ){
            val textRecentlyItem= UserPhoneNumber()
            textRecentlyItem.itemType = UserPhoneNumber.TYPE_TEXT_RECENTLY
            userPhoneNumbers.add(textRecentlyItem)
            userPhoneNumbers.addAll(recently)
        }


        mAdapterSuggestions = AutoCompletePhonesAdapter(context, R.layout.row_auto_complete_phone, userPhoneNumbers)

        mEditText?.setAdapter(mAdapterSuggestions)
        mEditText?.setOnItemClickListener { _, _, position, _ ->
            when(userPhoneNumbers[position].itemType){
                UserPhoneNumber.TYPE_NUMBER -> {
                    selectedUserPhoneNumberPos = position
                }

                UserPhoneNumber.TYPE_ADD -> {
                    mAddNumberListener?.onAddFavoriteNumberClick()
                }
            }


        }
        mEditText?.isFocusable =true

        mEditText?.setOnClickListener {

            mListener?.onEditTextClick(it)
        }
        mEditText?.setOnFocusChangeListener { _, hasFocus ->
            /*if (hasFocus) {
                mEditText?.requestFocus()
                mEditText?.performClick()
            }*/
        }

    }


    fun setSuggestionListCard(saved: ArrayList<DebitCard>
//                              , recently: ArrayList<DebitCard>
    ) {
        this.userCardNumber.clear()

        this.userCardNumber.addAll(saved)
        val addItem= DebitCard()
        addItem.itemType = DebitCard.TYPE_ADD
        userCardNumber.add(addItem)
//        if (recently.size >0 ){
//            val textRecentlyItem= DebitCard()
//            textRecentlyItem.itemType = DebitCard.TYPE_TEXT_RECENTLY
//            userCardNumber.add(textRecentlyItem)
//            userCardNumber.addAll(recently)
//        }


        mAdapterSuggestions = AutoCompleteCardAdapter(context, R.layout.row_auto_complete_phone, userCardNumber)

        mEditText?.setAdapter(mAdapterSuggestions)
        mEditText?.setOnItemClickListener { _, _, position, _ ->
            when(userCardNumber[position].itemType){
                DebitCard.TYPE_NUMBER -> {
                    selectedUserPhoneNumberPos = position


                }

                DebitCard.TYPE_ADD -> {
                    mAddNumberListener?.onAddFavoriteNumberClick()
                }
            }


        }
        mEditText?.isFocusable =true

        mEditText?.setOnClickListener {

            mListener?.onEditTextClick(it)
        }
        mEditText?.setOnFocusChangeListener { _, hasFocus ->
            /*if (hasFocus) {
                mEditText?.requestFocus()
                mEditText?.performClick()
            }*/
        }

    }

    fun setSuggestionListBill(saved: ArrayList<SavedBill>, recently: ArrayList<SavedBill>) {
        this.userBillNumber.clear()
        this.userBillNumber.addAll(saved.map { SavedBill.BillTypes.Bill(it) })
        this.userBillNumber.add(SavedBill.BillTypes.TypeAdd)

        if (recently.size >0 ){
            val textRecentlyItem= SavedBill()
            textRecentlyItem.itemType = SavedBill.TYPE_TEXT_RECENTLY
//            userBillNumber.add(textRecentlyItem)
            userBillNumber.addAll(recently.map { SavedBill.BillTypes.Bill(it) })
        }

        mAdapterSuggestions = AutoCompleteBillAdapter(context, R.layout.row_auto_complete_bill, userBillNumber)

        mEditText?.setAdapter(mAdapterSuggestions)
        mEditText?.setOnItemClickListener { _, _, position, _ ->
            when(userBillNumber[position]){
                is SavedBill.BillTypes.Bill -> {
                    mEditText?.setText((userBillNumber[position] as SavedBill.BillTypes.Bill).savedBill.subtitle ?: "", TextView.BufferType.EDITABLE);

                    selectedUserPhoneNumberPos = position
                }
                is SavedBill.BillTypes.TypeAdd -> {
                    mAddNumberListener?.onAddFavoriteNumberClick()
                }
            }
        }
        mEditText?.isFocusable =true

        mEditText?.setOnClickListener {

            mListener?.onEditTextClick(it)
        }
        mEditText?.setOnFocusChangeListener { _, hasFocus ->
            /*if (hasFocus) {
                mEditText?.requestFocus()
                mEditText?.performClick()
            }*/
        }

    }

    fun openDropDown() {
        mEditText?.showDropDown()
    }

    fun hideDropDown(){
        mEditText?.dismissDropDown()
    }

    fun setPhoneText(userNumber: UserPhoneNumber) {
        mEditText?.setText(userNumber.phoneNumber)
        selectedUserPhoneNumber=userNumber
    }
    fun setBillText(userNumber: SavedBill) {
        mEditText?.setText(userNumber.info?.water?.billId)
        selectedUserBillNumber=userNumber
    }

    fun setRightIcon(icon: Int) {
        mRightIcon?.setImageDrawable(context.getCompatDrawable(icon))

    }

    fun getInputAsPriceRial(): String {
        return (text + "0")
    }

    fun setBottomInfo(textInfo: String?, color: Int?, typeface: Int? = null) {
        if (tvBottomText==null) addBottomTextView()
        tvBottomText?.text = textInfo?.let { it } ?: run { "" }
        tvBottomText?.setTextColor(color?.let { it } ?: run { Color.BLACK })

        tvBottomText?.typeface = FontUtils.getFont(context, typeface ?: FontUtils.REGULAR)
    }

    fun addTextChangedListener(textWatcher: TextWatcher) {
        mEditText?.addTextChangedListener(textWatcher)
    }

    fun setLeftIconColorTint(color: Int) {
        mLeftIcon?.setColorFilter(context.getColorInt(color))
    }
    fun setLeftIcon(icon: Int) {
        mLeftIcon?.setImageDrawable(context.getCompatDrawable(icon))

    }


    fun disableInputText() {
        mEditText?.isEnabled = false
        mEditText?.isFocusable = false
    }


    fun enableInputText(){
        mEditText?.isEnabled = true
        mEditText?.isFocusable = true
    }

    fun setTransformationMethod(method: TransformationMethod?) {
        mEditText?.transformationMethod = method
    }

    interface OnEditTextListener {
        fun onLeftIconClick(view: View)
        fun onEditTextClick(view: View)
        fun onUserPhoneSelected(userPhoneNumber: UserPhoneNumber?)
    }

    interface OnAddFavoriteNumberClickListener{
        fun onAddFavoriteNumberClick()
    }

    interface OnTextChangeListener{
        fun afterTextChange(text: String?)
        fun onRemovePhoneNumber()
    }

    companion object {

        const val INPUT_TYPE_PHONE = 0
        const val INPUT_TYPE_PASSWORD = 1
        const val INPUT_TYPE_TEXT = 2
        const val INPUT_TYPE_NUMBER = 3

        const val STATE_NORMAL = 0
        const val STATE_FOCUSED = 1
        const val STATE_ERROR = 2
    }

}
