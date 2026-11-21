package com.example.utlikotlin

import org.json.JSONArray
import org.json.JSONObject

object PaymentUtil {
    fun createPaymentRequestJson(publishableKey: String, merchantName: String, amount: Double): JSONObject {
        val paymentMethod = JSONObject().apply {
            put("type", "CARD")
            put("parameters", JSONObject().apply {
                put("allowedAuthMethods", JSONArray(listOf("PAN_ONLY", "CRYPTOGRAM_3DS")))
                put("allowedCardNetworks", JSONArray(listOf("VISA", "MASTERCARD", "AMEX", "DISCOVER")))
            })
            put("tokenizationSpecification", JSONObject().apply {
                put("type", "PAYMENT_GATEWAY")
                put("parameters", JSONObject().apply {
                    put("gateway", "stripe")
                    put("stripe:version", "2018-10-31")
                    put("stripe:publishableKey", publishableKey)
                })
            })
        }

        val transactionInfo = JSONObject().apply {
            put("totalPriceStatus", "FINAL")
            put("totalPrice", amount.toString())
            put("currencyCode", "USD")
        }

        val merchantInfo = JSONObject().apply {
            put("merchantName", merchantName)
        }

        val paymentRequest = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
            put("allowedPaymentMethods", JSONArray(listOf(paymentMethod)))
            put("transactionInfo", transactionInfo)
            put("merchantInfo", merchantInfo)
        }

        return paymentRequest
    }
}