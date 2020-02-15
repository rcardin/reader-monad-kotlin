package org.rcardin.monad.reader

import org.rcardin.monad.reader.ReaderMonad.Reader

object ReaderMonad {
    class Reader<From, To>(val f: (From) -> To) {

        operator fun invoke(input: From): To = f(input)

        inline fun <NewTo> map(crossinline transformation: (To) -> NewTo): Reader<From, NewTo> =
            Reader { c  -> transformation(f(c)) }

        inline fun <NewTo> flatMap(crossinline transformation: (To) -> Reader<From, NewTo>): Reader<From, NewTo> =
            Reader { c -> transformation(f(c))(c) }

        companion object Factory {
            fun <From, To> just(a: To): Reader<From, To> = Reader { _ -> a }
        }
    }
}

object stocks {
    interface StockRepository {
        fun findAll(): Map<String, Double>
        fun sell(stock: String, quantity: Double): Double
        fun buy(stock: String, amount: Double): Double
    }

    object Stocks {
        fun findAll(): Reader<StockRepository, Map<String, Double>> = Reader {
            repo -> repo.findAll()
        }
        fun sell(stock: String, quantity: Double): Reader<StockRepository, Double> = Reader {
            repo -> repo.sell(stock, quantity)
        }
        fun buy(stock: String, amount: Double): Reader<StockRepository, Double> = Reader {
            repo -> repo.buy(stock, amount)
        }
    }

    fun investInStockWithMinValue(): Reader<StockRepository, Double> =
        Stocks.findAll()
            .map { stocks -> stocks.minBy { it.value} }
            .map { it?.key }
            .flatMap { stock -> stock?.let { Stocks.buy(it, 1000.0) } ?: Reader.just(0.0) }

    @JvmStatic
    fun main(args: Array<String>) {
        val stockRepo = object : StockRepository {
            override fun findAll(): Map<String, Double> =
                mapOf("AMZN" to 1631.17, "GOOG" to 1036.05, "TSLA" to 346.00)

            override fun sell(stock: String, quantity: Double): Double =
                findAll()[stock]?.times(quantity) ?: 0.0

            override fun buy(stock: String, amount: Double): Double {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        }
    }
}