package br.com.zup.ot5

import io.micronaut.runtime.Micronaut.build

fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zup.ot5")
		.start()

}

