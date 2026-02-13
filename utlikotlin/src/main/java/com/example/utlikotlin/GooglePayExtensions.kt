package com.example.utlikotlin

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