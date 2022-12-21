# Ki take home exercise

## Background

The Ki website allows customers to open an account and purchase shares in one or more funds. Before a customer can buy shares, they top up their account with a desired amount, using a debit card.

The website is supported by an underlying investment platform, consisting of modules that each perform a specific role. Each module is a separate package, installed on the platform as a dependency. One of these modules is tasked with processing an incoming payments CSV file, then generating a share order CSV, consumed by another module.

This report contains the `customer_id` and the number of shares their payment(s) will purchase, given the share price.

```
"customer_id","shares"
"123","100"
"987","3400"
```

For development, the module can be invoked with the `LocalRunner` class, which simulates how it is integrated with the investment platform. This is configured as the main class of the package. To run this locally, build the project with Maven:

```
mvn package
```

You can now call the JAR file directly:

```
java -jar target/ki-takehome-java-1.0-SNAPSHOT.jar <path_to_payments_csv> <source> <share_price>
```

To see example output:

```
java -jar target/ki-takehome-java-1.0-SNAPSHOT.jar card_payments_example.csv card 1.20
```

## Exercise

Some customers have indicated they would prefer to top up their account by making a bank transfer, rather than using their debit card.

Update the `com.ki.services.PaymentProcessor` class to support both card and bank transfer payments. We will receive a separate CSV file from our payment provider, in a similar format to card payments:

```
customer_id,date,amount,bank_account_id
789,2018-10-25,900,20
345,2018-11-03,900,60
```

Unlike card payments, our payment provider will only update us once a bank transfer has been successfully processed.

Remember, the module is installed on the platform as a dependency, therefore the API to both the `PaymentProcessor` and `ShareEngine` service classes must not change.

You can check your implementation by using the example CSV:

```
java -jar target/ki-takehome-java-1.0-SNAPSHOT.jar bank_payments_example.csv bank 1.20
```

This should produce the result:

```
"customer_id","shares"
"789","735"
"345","735"
```

### Tests

You can run the tests from the root of the project with:

```
mvn test
```

#### Notes

The code base has been written to compile under Java 8 (and Kotlin 1.6.0-RC2) and upwards. Dependencies are managed via Maven, see the `pom.xml` file.

