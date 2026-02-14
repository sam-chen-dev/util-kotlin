package com.example.utlikotlin

import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.button.ButtonConstants
import com.google.android.gms.wallet.button.ButtonOptions
import com.google.android.gms.wallet.button.PayButton

fun PayButton.init() {
    val buttonOptions = ButtonOptions.newBuilder().run {
        setButtonTheme(ButtonConstants.ButtonTheme.DARK)
        setButtonType(ButtonConstants.ButtonType.PAY)
        setCornerRadius(16)
        setAllowedPaymentMethods("[]")
        build()
    }

    initialize(buttonOptions)
}

fun PaymentsClient.loadPaymentData(
    stripePublishableKey: String,
    merchantName: String,
    amount: Double
): Task<PaymentData> {
    val paymentRequestJson = PaymentUtil.createPaymentRequestJson(stripePublishableKey, merchantName, amount)
    val paymentRequest = PaymentDataRequest.fromJson(paymentRequestJson.toString())

    return loadPaymentData(paymentRequest)
}