package com.ki.services

import com.ki.Config
import com.ki.models.Payment
import com.ki.models.payment.*
import com.opencsv.CSVReaderBuilder
import java.io.FileReader
import java.io.IOException
import java.util.*

class PaymentProcessor {
    fun getPayments(csvPath: String, source: String): Array<Payment> {
        val payments = ArrayList<Payment>()
        try {
            // If source is not CARD or BANK it throws IllegalArgumentException
            val parserData = when (PaymentTypes.valueOf(source.uppercase(Locale.getDefault()).trim())) {
                PaymentTypes.CARD -> sourceCSVParserData(
                    CardPaymentFields.values().map { it.name }.toTypedArray(),
                    parseCardSource(Config)
                )

                PaymentTypes.BANK -> sourceCSVParserData(
                    BankPaymentFields.values().map { it.name }.toTypedArray(),
                    parseBankSource(Config)
                )
            }
            val file = FileReader(csvPath)
            val reader = CSVReaderBuilder(file).build()
            val header = reader.readNext() ?: emptyArray()

            /*
            * This CSV reader should extract a list of Objects with types
            * 1. It validates the header implicitly
            * 2. It validates the datatypes implicitly
            * For the time being the header is checked in the simplest way
            * Dealing with OpenCSV and Kotlin needs a full refactor
            * */

            check(parserData.expectedHeader.contentEquals(header)) {
                "'$source' feed needs a CSV with Header <${
                    parserData.expectedHeader.map { it }
                } > "
            }

            while (true) {
                val line = reader.readNext() ?: break
                val payment = parserData.lineParser(line)
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