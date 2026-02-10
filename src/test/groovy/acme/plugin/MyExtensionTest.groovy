package acme.plugin

import nextflow.Channel
import nextflow.plugin.Plugins
import nextflow.plugin.TestPluginDescriptorFinder
import nextflow.plugin.TestPluginManager
import nextflow.plugin.extension.PluginExtensionProvider
import org.pf4j.PluginDescriptorFinder
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Timeout

import java.nio.file.Path

/**
 * Tests for the @Function, @Factory, and @Operator
 * extension points in MyExtension.
 */
class MyExtensionTest extends Specification {

    def 'sayHello should return a greeting' () {
        given:
        def ext = new MyExtension()
        when:
        def result = ext.sayHello('World')
        then:
        result == 'Hello, World!'
    }

    def 'sayHello should handle different inputs' () {
        given:
        def ext = new MyExtension()
        expect:
        ext.sayHello(input) == expected
        where:
        input   | expected
        'Alice' | 'Hello, Alice!'
        ''      | 'Hello, !'
    }

}
