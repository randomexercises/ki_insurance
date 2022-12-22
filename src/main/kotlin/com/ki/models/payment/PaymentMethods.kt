package com.ki.models.payment

import com.ki.Config
import com.ki.models.Card
import com.ki.models.Payment
import com.opencsv.bean.CsvBindByName
import java.math.BigDecimal
import java.time.LocalDate

sealed class PaymentMethod(val customerId: Int, val amount: Int, val date: LocalDate?)
class CardPayment(customerId: Int, amount: Int, date: LocalDate?, val card: Card) :
    PaymentMethod(customerId, amount, date)

class BankPayment(customerId: Int, amount: Int, date: LocalDate?, val bankAccountId: Int) :
    PaymentMethod(customerId, amount, date)

// Those names are supposed to be all uppercase
enum class BankPaymentFields {
    customer_id, date, amount, bank_account_id
}

enum class CardPaymentFields {
    customer_id, date, amount, card_id, card_status
}

enum class PaymentTypes {
    CARD, BANK
}

fun payment(
    customerId: Int,
    initialAmount: Int, // is initialAmount a good name?
    date: LocalDate?,
    paymentFeeRate: BigDecimal
) = Payment().apply {
    this.customerId = customerId
    // Why to Int? If we have 993 as amount and 0.02 as payment rate we will lose 0.9
    // that is almost the 5% of fee
    // With multiple transactions of 45 we have no fees
    fee = paymentFeeRate.multiply(BigDecimal(initialAmount)).toInt()
    amount = initialAmount - fee
    this.date = date
}

data class sourceCSVParserData(val expectedHeader: Array<String>, val lineParser: (Array<String>) -> Payment)

fun parseBankSource(config: Config) = fun(line: Array<String>): Payment =
    BankPayment(
        customerId = line[BankPaymentFields.customer_id.ordinal].toInt(),
        date = LocalDate.parse(line[BankPaymentFields.date.ordinal]),
        amount = Integer.parseInt(line[BankPaymentFields.amount.ordinal]),
        bankAccountId = line[BankPaymentFields.bank_account_id.ordinal].toInt()
    ).let { bankPayment ->
        payment(
            customerId = bankPayment.customerId,
            initialAmount = bankPayment.amount,
            date = bankPayment.date,
            paymentFeeRate = config.paymentFeeRate
        ).apply {
            bankAccountId = bankPayment.bankAccountId
        }
    }

fun parseCardSource(config: Config) = fun(line: Array<String>): Payment =
    CardPayment(
        customerId = line[CardPaymentFields.customer_id.ordinal].toInt(),
        date = LocalDate.parse(line[CardPaymentFields.date.ordinal]),
        amount = Integer.parseInt(line[CardPaymentFields.amount.ordinal]),
        card = Card().apply {
            cardId = Integer.parseInt(line[CardPaymentFields.card_id.ordinal])
            status = line[CardPaymentFields.card_status.ordinal]
        }
    ).let { cardPayment ->
        payment(
            customerId = cardPayment.customerId,
            initialAmount = cardPayment.amount,
            date = cardPayment.date,
            paymentFeeRate = config.paymentFeeRate
        ).apply {
            card = cardPayment.card
        }
    }
