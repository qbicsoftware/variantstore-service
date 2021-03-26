package life.qbic.variantstore

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
                version = "1.0.1",
                description = "Variantstore Restful API",
                license = @License(name = "MIT License", url = "https://opensource.org/licenses/mit-license.php"),
                contact = @Contact(url = "https://github.com/christopher-mohr", name = "Christopher Mohr", email = "christopher.mohr@uni-tuebingen.de")
        )
)

@Log4j2
@CompileStatic
class Variantstore {
    static void main(String[] args) {
        log.info("Variantstore service started.")
        Micronaut.run(Variantstore.class)
    }
}
