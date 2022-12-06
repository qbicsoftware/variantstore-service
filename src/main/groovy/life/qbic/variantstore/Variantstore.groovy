package life.qbic.variantstore

import groovy.transform.CompileStatic
import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@OpenAPIDefinition(
        info = @Info(
                title = "Variantstore",
                version = "1.1.0",
                description = "Variantstore Restful API",
                license = @License(name = "MIT License", url = "https://opensource.org/licenses/mit-license.php"),
                contact = @Contact(url = "https://github.com/christopher-mohr", name = "Christopher Mohr",
                        email = "christopher.mohr@uni-tuebingen.de")
        )
)

@CompileStatic
class Variantstore {

    private static final Logger LOGGER = LoggerFactory.getLogger(Variantstore.class);

    static void main(String[] args) {
        LOGGER.info("Variantstore service started.")
        Micronaut.run(Variantstore.class, args)
    }
}
