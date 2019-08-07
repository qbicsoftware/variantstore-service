package life.qbic.oncostore.controller

import groovy.util.logging.Log4j2
import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.service.OncostoreService
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.NotNull
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor


@Log4j2
@Controller("/variants")
class VariantController {

    private final OncostoreService service
    private final ExecutorService executor
    private final List<Thread> threads = new ArrayList<Thread>();

    @Inject
    VariantController(OncostoreService service) {
        this.service = service
        this.executor = Executors.newFixedThreadPool(1);
    }

    @Get(uri = "/{id}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getVariant(@Parameter('id') String identifier) {
        try {
            List<Variant> variants = service.getVariantForVariantId(identifier)
            return variants ? HttpResponse.ok(variants.get(0)) : HttpResponse.notFound("Variant not found.")
        }
        catch (IllegalArgumentException e) {
            log.error(e)
            return HttpResponse.badRequest("Invalid variant identifier supplied.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }


    @Get(uri = "{?args*}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getVariants(@Valid ListingArguments args) {
        try {
            List<Variant> variants = service.getVariantsForSpecifiedProperties(args)
            return variants ? HttpResponse.ok(variants) : HttpResponse.notFound("No variants found matching provided attributes.")
        }
        catch (Exception e) {
            log.error(e)
            return HttpResponse.serverError("Unexpected error, resource could not be accessed.")
        }
    }

    @Post(uri = "/upload", consumes = MediaType.TEXT_PLAIN)
    HttpResponse storeVariants(@QueryValue("url") @NotNull String url) {
        println(url)
        try {

            executor.submit(new MyRunnable(service, url));

            //Runnable task = new MyRunnable(service, url);
            //Thread worker = new Thread(task);
            //worker.start()
            //threads.add(worker);
            //println(threads.size().toString())


            if (executor instanceof ThreadPoolExecutor) {
                System.out.println(
                        "Pool size is now " +
                                ((ThreadPoolExecutor) executor).getActiveCount()
                );
            }

            return HttpResponse.accepted()
        }
        catch (Exception e) {
            log.error(e)
        }
    }
}

public class MyRunnable implements Runnable {
    private final String url;
    private final OncostoreService service

    MyRunnable(OncostoreService service, String url) {
        this.service = service;
        this.url = url;
    }

    @Override
    public void run() {
        service.storeVariantsInStore(url)
    }
}