/*
 * Copyright 2025, Seqera Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package acme.plugin

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import groovyx.gpars.dataflow.DataflowReadChannel
import groovyx.gpars.dataflow.DataflowWriteChannel
import nextflow.Channel
import nextflow.NF
import nextflow.Session
import nextflow.extension.CH
import nextflow.extension.DataflowHelper
import nextflow.plugin.extension.Factory
import nextflow.plugin.extension.Function
import nextflow.plugin.extension.Operator
import nextflow.plugin.extension.PluginExtensionPoint

/**
 * Implements custom functions, channel factories, and operators
 * which can be imported by Nextflow scripts.
 */
@Slf4j
@CompileStatic
class MyExtension extends PluginExtensionPoint {

    private Session session

    @Override
    protected void init(Session session) {
        this.session = session
    }

    /*
     * Custom @Function
     *
     * A function can be used anywhere in a Nextflow script.
     * Usage:
     *   include { sayHello } from 'plugin/nf-plugin-template'
     *   channel.of('World').map { target -> sayHello(target) }
     */

    /**
     * Say hello to the given target.
     *
     * @param target
     */
    @Function
    String sayHello(String target) {
        log.info "Hello, ${target}!"
        return "Hello, ${target}!"
    }

    /*
     * Custom @Factory
     *
     * A factory creates a new channel. It returns a DataflowWriteChannel
     * and is invoked like a built-in channel factory.
     * Usage:
     *   include { fromRepeat } from 'plugin/nf-plugin-template'
     *   channel.fromRepeat('Hello', 3)  // emits: 'Hello', 'Hello', 'Hello'
     */

    /**
     * Create a channel that emits the given value a specified number of times.
     *
     * @param value The value to emit
     * @param n     The number of times to emit the value
     * @return A channel that emits the value n times
     */
    @Factory
    DataflowWriteChannel fromRepeat(String value, int n) {
        final channel = CH.create()
        session.addIgniter { ->
            for (int i = 0; i < n; i++) {
                channel.bind(value)
            }
            channel.bind(Channel.STOP)
        }
        return channel
    }

    /*
     * Custom @Operator
     *
     * An operator transforms an existing channel. It takes a DataflowReadChannel
     * as the first parameter and returns a DataflowWriteChannel.
     * Usage:
     *   include { reverse } from 'plugin/nf-plugin-template'
     *   channel.of('hello', 'world') | reverse
     */

    /**
     * Reverse each string element in the source channel.
     *
     * @param source The source channel
     * @return A new channel with reversed strings
     */
    @Operator
    DataflowWriteChannel reverse(DataflowReadChannel source) {
        final target = CH.createBy(source)
        final next = { Object it ->
            target.bind(it.toString().reverse())
        }
        final done = {
            target.bind(Channel.STOP)
        }
        DataflowHelper.subscribeImpl(source, [onNext: next, onComplete: done])
        return target
    }

}
