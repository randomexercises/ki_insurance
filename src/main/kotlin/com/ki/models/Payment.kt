package com.ki.models

import com.ki.Config
import java.math.BigDecimal
import java.time.LocalDate

class Payment {
    var customerId = 0
    var date: LocalDate? = null
    var amount = 0
    var fee = 0

    var card: Card? = null
    var bankAccountId: Int? = null

    constructor() {}

    /*
    * This constructor is here but it should NOT be used.
    * Unfortunately this was present on the previous implementation and
    * it might have been used somewhere else.
    * */
    @Deprecated(message = "Data structure can change and should not be tightly connected to a CSV structure. Use 'payment' factory instead")
    constructor(data: Array<String>) {
        customerId = data[0].toInt()
        val paymentFeeRate = Config.paymentFeeRate
        val totalAmount = data[2].toInt()
        fee = paymentFeeRate.multiply(BigDecimal(totalAmount)).toInt()
        amount = totalAmount - fee
        date = LocalDate.parse(data[1])
        val card = Card()
        card.cardId = data[3].toInt()
        card.status = data[4]
        this.card = card
    }

    val isSuccessful: Boolean
        get() = bankAccountId != null || card?.status == "processed"
}
