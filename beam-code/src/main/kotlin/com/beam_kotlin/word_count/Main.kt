package com.beam_kotlin.word_count

import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.extensions.kryo.KryoCoder
import org.apache.beam.sdk.extensions.kryo.KryoCoderProvider
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.options.Default
import org.apache.beam.sdk.options.Description
import org.apache.beam.sdk.options.PipelineOptions
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.transforms.MapElements
import org.apache.beam.sdk.transforms.SimpleFunction
import org.apache.beam.sdk.transforms.Sum
import org.apache.beam.sdk.values.PCollection

interface MyOption : PipelineOptions {
    @get:Description("Path of the file to read from")
    @get:Default.String("gs://apache-beam-samples/shakespeare/kinglear.txt")
    var input: String
    var output: String
}

data class Record(val x: Int)

fun main(args: Array<String>): Unit {
    // create a beam pipeline
    PipelineOptionsFactory.register(MyOption::class.java)
    val option: MyOption = PipelineOptionsFactory
        .fromArgs(*args).withValidation().`as`(MyOption::class.java)
    val pipeline = Pipeline.create(option)
    KryoCoderProvider.of({ kryo ->
        kryo.register(Record::class.java)
    }).registerTo(pipeline)

    // declare transformations
    val lines = pipeline.apply("ReadLines", TextIO.read().from(option.input))
    val r: PCollection<Long> = lines.apply(
        "map-elements",
        MapElements.via(object : SimpleFunction<String, Long>() {
            override fun apply(input: String): Long {
                return input.length.toLong()
            }
        })
    ).apply(
        Sum.longsGlobally()
    )
    val total = r.apply(
        MapElements.via(object : SimpleFunction<Long, Map<String, Int>>() {
            override fun apply(input: Long): Map<String, Int> {
                println("total $input")
                return java.util.HashMap<String, Int>()
            }
        })
    )
    total.coder = KryoCoder.of()

    // run pipeline
    pipeline.run()
}

