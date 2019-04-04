package life.qbic.oncostore

import io.micronaut.runtime.Micronaut
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.License

@OpenAPIDefinition(
        info = @Info(
                title = "VariantStore",
                version = "0.2",
                description = "VariantStore Restful API",
                license = @License(name = "", url = ""),
                contact = @Contact(url = "", name = "Christopher Mohr", email = "christopher.mohr@uni-tuebingen.de")
        )
)

class Application {

    static void main(String[] args) {
        Micronaut.run(Application.class)

    }
}
