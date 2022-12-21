package com.ki.services

import com.ki.models.Payment
import com.opencsv.CSVReaderBuilder
import java.io.FileReader
import java.io.IOException

class PaymentProcessor {
    fun getPayments(csvPath: String, source: String): Array<Payment> {
        val payments = ArrayList<Payment>()
        try {
            val file = FileReader(csvPath)
            val reader = CSVReaderBuilder(file).withSkipLines(1).build()
            while (true) {
                val line = reader.readNext() ?: break
                val payment = Payment(line)
                payments.add(payment)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return payments.toArray(arrayOf())
    }

    fun verifyPayments(payments: Array<Payment>): Array<Payment> {
        val filtered = ArrayList<Payment>()
        for (payment in payments) {
            if (payment.isSuccessful) {
                filtered.add(payment)
            }
        }
        return filtered.toArray(arrayOf())
    }
}