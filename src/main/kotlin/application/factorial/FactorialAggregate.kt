package application.factorial

import wsi.cqrs.Aggregate
import java.math.BigInteger

class FactorialAggregate(private val number: Int): Aggregate() {
    fun calculate() {
        var factorial = BigInteger.ONE
        for (i in 1..number) {
            factorial = factorial.multiply(BigInteger.valueOf(number.toLong()))
        }

        this.apply(FactorialCalculated(
                result = factorial
        ))
    }
}
