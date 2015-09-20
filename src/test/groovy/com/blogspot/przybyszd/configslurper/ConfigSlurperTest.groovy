package com.blogspot.przybyszd.configslurper

import spock.lang.Specification

class ConfigSlurperTest extends Specification {
    def 'should import configuration from url'() {
        expect:
            new ConfigSlurper().parse(ConfigSlurperTest.getResource('/configuration.properties')).systemName as String != 'test'
    }

    def 'should import configuration from text'() {
        expect:
            new ConfigSlurper().parse(ConfigSlurperTest.getResource('/configuration.properties').text).systemName != 'test'
    }

    def 'should import configuration from properties'() {
        given:
            Properties p = new Properties()
            p.load(ConfigSlurperTest.getResourceAsStream('/configuration.properties'))
        expect:
            new ConfigSlurper().parse(p).systemName as String == 'test'
    }

    def 'should get first endpoint info from properties'() {
        expect:
            fromProperties.endpoint.first.toProperties() == [
                    protocol: 'http',
                    address : 'localhost',
                    port    : '8080',
                    path    : 'test']
    }

    def 'should get list of endpoints'() {
        expect:
            fromProperties.endpoint.keySet() == ['first', 'second'] as Set
    }

    def 'should get nested property'() {
        expect:
            fromProperties.endpoint.first.protocol == 'http'
    }

    def 'should not used nested property as one string'() {
        expect:
            fromProperties.'endpoint.first.protocol' != 'http'
    }

    def 'should allow for nested property as one string when toProperties called'() {
        expect:
            fromProperties.endpoint.toProperties()['first.protocol'] == 'http'
    }

    def 'should allow for nested property as one string when toProperties called with prefix'() {
        expect:
            fromProperties.endpoint.toProperties('myPrefix')['myPrefix.first.protocol'] == 'http'
    }

    def 'should not allow for nested property as one string when cast to properties'() {
        expect:
            (fromProperties.endpoint as Properties).'first.protocol' != 'http'
    }

    def 'should get nested config object'() {
        given:
            ConfigObject first = fromProperties.endpoint.first
        expect:
            first.port == '8080'
    }

    def 'should not throw exception when missing property'() {
        expect:
            fromProperties.endpoint.third.port.toProperties() == [:] as Properties
    }

    def 'should throw exception when asking for too nested property'() {
        when:
            fromProperties.endpoint.first.port.test
        then:
            thrown(MissingPropertyException)
    }

    def 'should pretty print props'() {
        when:
            String result = fromProperties.prettyPrint()
        then:
            result
            println result
    }

    def 'should get config from script as url'() {
        given:
            ConfigObject config = new ConfigSlurper().parse(ConfigSlurperTest.getResource('/configuration.groovy'))
        expect:
            config.systemName == 'test'
    }

    def 'should get config from script as string'() {
        given:
            ConfigObject config = new ConfigSlurper().parse(ConfigSlurperTest.getResource('/configuration.groovy').text)
        expect:
            config.systemName == 'test'
    }

    def 'should get nested properties from script as int'() {
        expect:
            fromScript.endpoint.first.port == 8080
    }

    def 'should get really nested properties from script as whole'() {
        expect:
            fromScript.test.key == ['really': 'nested?'] as Properties
    }

    def 'should get really nested properties from script and continue digging'() {
        expect:
            fromScript.test.key.really == 'nested?'
    }

    private static ConfigObject getFromScript() {
        return new ConfigSlurper().parse(ConfigSlurperTest.class.getResource('/configuration.groovy'))
    }

    private static ConfigObject getFromProperties() {
        return new ConfigSlurper().parse(properties)
    }

    private static Properties getProperties() {
        Properties p = new Properties()
        p.load(ConfigSlurperTest.class.getResourceAsStream('/configuration.properties'))
        return p
    }
}
