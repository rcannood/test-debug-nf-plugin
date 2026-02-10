
include { sayHello; fromRepeat; reverse } from 'plugin/nf-plugin-template'

workflow {
    // @Function example: use sayHello as a custom function
    channel.of('Monde', 'Mondo', 'World', 'Mundo')
        .map { target -> sayHello(target) }

    // @Factory example: create a channel that emits a value multiple times
    channel.fromRepeat('Hola', 3)
        .view { v -> "fromRepeat: $v" }

    // @Operator example: reverse each string in a channel
    channel.of('hello', 'world', 'nextflow') 
        .reverse()
        .view { v -> "reversed: $v" }
}
