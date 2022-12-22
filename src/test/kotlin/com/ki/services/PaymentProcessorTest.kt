package com.ki.services

import com.ki.Fixture
import com.ki.models.Card
import com.ki.models.Payment
import org.junit.Assert
import org.junit.Test

class PaymentProcessorTest {
    @Test
    fun testGetCardPayments() {
        val fixturePath = Fixture.getPath("card_payments_mixed.csv")
        val processor = PaymentProcessor()
        val payments = processor.getPayments(fixturePath, "card")

        payments.forEach { payment ->
            Assert.assertNull(payment.bankAccountId)
        }

        Assert.assertEquals(3, payments.size.toLong())
        Assert.assertEquals(30, payments[0].card!!.cardId)
        Assert.assertEquals(45, payments[1].card!!.cardId)
        Assert.assertEquals(10, payments[2].card!!.cardId)
    }

    @Test
    fun testGetBankPayments() {
        val fixturePath = Fixture.getPath("bank_payments_mixed.csv")
        val processor = PaymentProcessor()
        val payments = processor.getPayments(fixturePath, "bank")
        Assert.assertEquals(3, payments.size.toLong())

        payments.forEach { payment ->
            Assert.assertNull(payment.card)
        }

        arrayOf(20, 60, 90).forEachIndexed { idx, bankAccountId ->
            Assert.assertEquals(bankAccountId, payments[idx].bankAccountId)
        }
    }

    @Test
    fun testGetPaymentsEmpty() {
        data class TestEntry(val csvFile: String, val source: String)

        // There are Junit Dynamic tests for cases like this one...

        listOf(
            TestEntry("card_payments_empty.csv", "card"),
            TestEntry("bank_payments_empty.csv", "bank")
        ).forEach { (csvFile, source) ->
            val fixturePath = Fixture.getPath(csvFile)
            val processor = PaymentProcessor()
            val payments = processor.getPayments(fixturePath, source)
            Assert.assertEquals(0, payments.size.toLong())
        }
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

        // Invalid payments
        arrayOf(
            Payment().apply {
                bankAccountId = null
                card = null
            },
            Payment().apply {
                bankAccountId = null
                card = Card().apply {
                    cardId = 0
                    status = null
                }
            },
            Payment().apply {
                bankAccountId = null
                card = Card().apply {
                    cardId = 0
                    status = "ANYTHING APART processed"
                }
            }
        ).also { paymentsResult ->
            Assert.assertTrue(paymentsResult.none { it.isSuccessful })
        }

        Assert.assertTrue("A payment with bank ID is considered valid", Payment().apply {
            bankAccountId = 10
            card = null
        }.isSuccessful)
    }

    private fun createPayment(cardStatus: String): Payment {
        val card = Card()
        card.status = cardStatus
        val payment = Payment()
        payment.card = card
        return payment
    }
}