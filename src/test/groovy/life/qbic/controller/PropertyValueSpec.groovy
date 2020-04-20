package life.qbic.controller

import io.micronaut.context.ApplicationContext
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class PropertyValueSpec  extends Specification{
    @AutoCleanup
    @Shared
    ApplicationContext ctx =  ApplicationContext.run()

    def "application name is variantstore"() {
        expect:
        ctx.getProperty('micronaut.application.name', String).get() == 'variantstore'
    }
}
