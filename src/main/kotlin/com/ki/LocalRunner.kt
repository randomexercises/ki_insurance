package com.ki

import com.ki.services.PaymentProcessor
import com.ki.services.ShareEngine
import com.opencsv.CSVWriter
import net.sourceforge.argparse4j.ArgumentParsers
import net.sourceforge.argparse4j.inf.ArgumentParserException
import net.sourceforge.argparse4j.inf.Namespace
import java.io.IOException
import java.io.StringWriter
import java.math.BigDecimal

object LocalRunner {

    fun simulatePlatform(csvPath: String, source: String, sharePrice: BigDecimal): String {
        val fieldNames = arrayOf(
            "customer_id",
            "shares"
        )
        val paymentProcessor = PaymentProcessor()
        val payments = paymentProcessor.getPayments(csvPath, source)
        val filtered = paymentProcessor.verifyPayments(payments)
        val shareEngine = ShareEngine()
        val shareOrders = shareEngine.generateShareOrders(sharePrice, filtered)
        val data: MutableList<Array<String>> = ArrayList()
        for (shareOrder in shareOrders) {
            data.add(arrayOf(shareOrder.customerId.toString(), shareOrder.shares.toString()))
        }
        return generateCsv(fieldNames, data)
    }

    private fun generateCsv(fieldNames: Array<String>, data: List<Array<String>>): String {
        val output = StringWriter()
        val writer = CSVWriter(output)
        writer.writeNext(fieldNames)
        writer.writeAll(data)
        try {
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return output.toString()
    }
}

fun main(args: Array<String>) {
    val parser = ArgumentParsers.newFor("LocalRunner").build()
    parser.addArgument("csv_path").help("Path to the payments CSV file")
    parser.addArgument("source").help("The source of the payment, currently only 'card' is supported")
    parser.addArgument("share_price").help("Share price to generate share orders for e.g. '1.30'")
    var ns: Namespace? = null
    try {
        ns = parser.parseArgs(args)
    } catch (e: ArgumentParserException) {
        parser.handleError(e)
        System.exit(1)
    }
    val output = LocalRunner.simulatePlatform(
        ns!!.getString("csv_path"), ns.getString("source"), BigDecimal(
            ns.getString("share_price")
        )
    )
    println(output)
}