package life.qbic.oncostore

import groovy.transform.CompileStatic
import groovy.util.logging.Log4j2
import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License

@OpenAPIDefinition(
        info = @Info(
                title = "Variantstore",
                version = "0.9",
                description = "Variantstore Restful API",
                license = @License(name = "", url = ""),
                contact = @Contact(url = "https://github.com/christopher-mohr", name = "Christopher Mohr", email = "christopher.mohr@uni-tuebingen.de")
        )
)

@Log4j2
@CompileStatic
class Application {
    static void main(String[] args) {
        log.info("Variantstore service started.")
        Micronaut.run(Application.class)
    }
}
