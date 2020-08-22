package com.example.smartp

import android.content.Intent
import android.nfc.NfcAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.OnTextChanged
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils

class MainActivity : AppCompatActivity() , CardNfcAsyncTask.CardNfcInterface {
    private var mAmount: String? = null
    private var mNfcAdapter: NfcAdapter? = null
    private var mCardNfcUtils: CardNfcUtils? = null
    private var mIntentFromCreate = false
    private var mCardNfcAsyncTask: CardNfcAsyncTask? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
    }
    @OnClick(R.id.ok_btn)
    fun approveproess() {
        if (!TextUtils.isEmpty(mAmount)) {
            nfcProcess()
            Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show()


        }
    }

    @OnTextChanged(value = [R.id.amount_et], callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    fun getAmount(input: CharSequence) {
        mAmount = input.toString()
    }

    public override fun onResume() {
        super.onResume()
        mIntentFromCreate = false
        if (mNfcAdapter != null && !mNfcAdapter!!.isEnabled) {
            //show some turn on nfc dialog here. take a look in the samle ;-)
        } else if (mNfcAdapter != null) {
            mCardNfcUtils!!.enableDispatch()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (mNfcAdapter != null) {
            mCardNfcUtils!!.disableDispatch()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (mNfcAdapter != null && mNfcAdapter!!.isEnabled) {
            //this - interface for callbacks
            //intent = intent :)
            //mIntentFromCreate - boolean flag, for understanding if onNewIntent() was called from onCreate or not
            mCardNfcAsyncTask = CardNfcAsyncTask.Builder(this as CardNfcAsyncTask.CardNfcInterface, intent, mIntentFromCreate)
                    .build()
        }
    }

    private fun nfcProcess() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (mNfcAdapter == null) {
            //do something if there are no nfc module on device
            Toast.makeText(this, "Please open nfc", Toast.LENGTH_SHORT).show()
        } else {
            //do something if there are nfc module on device
            mCardNfcUtils = CardNfcUtils(this)
            //next few lines here needed in case you will scan credit card when app is closed
            mIntentFromCreate = true
            onNewIntent(intent)
        }
        Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show()

    }

    override fun startNfcReadCard() {
        //notify user that scannig start
        Toast.makeText(this, "scanning is working", Toast.LENGTH_SHORT).show()

    }

    override fun cardIsReadyToRead() {
        val card = mCardNfcAsyncTask!!.cardNumber
        val expiredDate = mCardNfcAsyncTask!!.cardExpireDate
        val cardType = mCardNfcAsyncTask!!.cardType
    }

    override fun doNotMoveCardSoFast() {
        //notify user do not move the card
        Toast.makeText(this, "please don't move", Toast.LENGTH_SHORT).show()
    }

    override fun unknownEmvCard() {
        //notify user that current card has unnown nfc tag
        Toast.makeText(this, "that current card has unknown nfc tag", Toast.LENGTH_SHORT).show()
    }

    override fun cardWithLockedNfc() {
        //notify user that current card has locked nfc tag
        Toast.makeText(this, "that current card has card has locked nfc tag", Toast.LENGTH_SHORT).show()

    }

    override fun finishNfcReadCard() {
        //notify user that scannig finished
        Toast.makeText(this, "scannig finished", Toast.LENGTH_SHORT).show()

    }
}