package com.example.utlikotlin

import com.stripe.android.model.ConfirmPaymentIntentParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.stripe.android.payments.paymentlauncher.PaymentLauncher
import org.json.JSONObject

fun PaymentLauncher.confirmPayment(clientSecret: String, paymentData: JSONObject) {
    val confirmParams = ConfirmPaymentIntentParams.createWithPaymentMethodCreateParams(
        PaymentMethodCreateParams.createFromGooglePay(paymentData),
        clientSecret
    )

    confirm(confirmParams)
}