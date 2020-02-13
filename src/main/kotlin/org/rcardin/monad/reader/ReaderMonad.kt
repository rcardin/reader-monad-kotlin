package org.rcardin.monad.reader

object ReaderMonad {
    class Reader<From, To>(val f: (From) -> To) {

        operator fun invoke(input: From): To = f(input)

        inline fun <NewTo> map(crossinline transformation: (To) -> NewTo): Reader<From, NewTo> =
            Reader { c  -> transformation(f(c)) }

        inline fun <NewTo> flatMap(crossinline transformation: (To) -> Reader<From, NewTo>): Reader<From, NewTo> =
            Reader { c -> transformation(f(c))(c) }

        companion object Factory {
            fun <From, To> just(a: To): ReaderMonad.Reader<From, To> = Reader { _ -> a }
        }
    }
}