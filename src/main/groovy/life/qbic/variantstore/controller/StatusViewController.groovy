package life.qbic.variantstore.controller

import groovy.util.logging.Log4j2
import io.micronaut.core.util.CollectionUtils
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.management.endpoint.health.HealthEndpoint
import io.micronaut.management.health.indicator.HealthResult
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import io.micronaut.views.View
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Inject

@Log4j2
@Controller("/status")
@Secured(SecurityRule.IS_ANONYMOUS)
class StatusViewController {

    @Inject
    HealthEndpoint healthEndpoint

    @View("home")
    @Get("/")
    HttpResponse index() {

        def healthPublisher = healthEndpoint.getHealth(null)
        HealthResult healthResult = Single.fromPublisher(healthPublisher).blockingGet()
        def dbKey = healthResult.getDetails()["jdbc"]["details"].keySet()[0]

        Map<String, Object> status = new HashMap<String, Object>()
        status.put("healthObjects", healthResult["details"])
        status.put("status", healthResult["status"])
        status.put("dbstatus", healthResult.getDetails()["jdbc"]["status"])
        status.put("dbdetails", dbKey)
        status.put("dbdetailsDb", healthResult.getDetails()["jdbc"]["details"][dbKey]["details"]["database"])
        status.put("dbdetailsVersion", healthResult.getDetails()["jdbc"]["details"][dbKey]["details"]["version"])
        status.put("diskstatus", healthResult.getDetails()["diskSpace"]["status"])
        status.put("diskdetailsTotal", (int) Math.round(healthResult.getDetails()["diskSpace"]["details"]["total"] / (1e6)))
        status.put("diskdetailsFree", (int) Math.round(healthResult.getDetails()["diskSpace"]["details"]["free"] / (1e6)))
        status.put("diskdetailsThreshold", (int) Math.round(healthResult.getDetails()["diskSpace"]["details"]["threshold"] / (1e6)))
        status.put("service", healthResult.getDetails()["service"]["status"])
        status.put("servicedetails", healthResult.getDetails()["service"]["name"])
        return HttpResponse.ok(status)
    }

}
