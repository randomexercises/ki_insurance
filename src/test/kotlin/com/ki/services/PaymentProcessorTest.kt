package com.ki.services

import com.ki.Fixture
import com.ki.models.Card
import com.ki.models.Payment
import org.junit.Assert
import org.junit.Test

class PaymentProcessorTest {
    @Test
    fun testGetPayments() {
        val fixturePath = Fixture.getPath("card_payments_mixed.csv")
        val processor = PaymentProcessor()
        val payments = processor.getPayments(fixturePath, "card")
        Assert.assertEquals(3, payments.size.toLong())
        Assert.assertEquals(30, payments[0].card!!.cardId)
        Assert.assertEquals(45, payments[1].card!!.cardId)
        Assert.assertEquals(10, payments[2].card!!.cardId)
    }

    @Test
    fun testGetPaymentsEmpty() {
        val fixturePath = Fixture.getPath("card_payments_empty.csv")
        val processor = PaymentProcessor()
        val payments = processor.getPayments(fixturePath, "card")
        Assert.assertEquals(0, payments.size.toLong())
    }

    @Test
    fun testVerifyPayments() {
        val payment1 = createPayment("processed")
        val payment2 = createPayment("declined")
        val payment3 = createPayment("processed")
        val payments = arrayOf<Payment>(payment1, payment2, payment3)
        val processor = PaymentProcessor()
        val result = processor.verifyPayments(payments)
        val expected = arrayOf(payment1, payment3)
        Assert.assertArrayEquals(expected, result)
    }

    private fun createPayment(cardStatus: String): Payment {
        val card = Card()
        card.status = cardStatus
        val payment = Payment()
        payment.card = card
        return payment
    }
}