package life.qbic.oncostore.oncoreader

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import life.qbic.oncostore.DataBase
import life.qbic.oncostore.model.SimpleVariant
import life.qbic.oncostore.util.IdValidator

import javax.inject.Inject
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

@Controller("/variants")
class VariantController {

    private final DataBase dataBase

    @Inject
    VariantController(DataBase database) {
        this.dataBase = dataBase
    }

    @Get(uri = "/{variantID}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getSample(@Parameter('variantID') String identifier) {
        if (!IdValidator.VALID_UUID(identifier)) {
            return HttpResponse.badRequest("No valid variant identifier provided.")
        } else {
            SimpleVariant s = searchVariant(identifier)
            if (s != null) {
                return HttpResponse.ok(s)
            } else {
                return HttpResponse.notFound("Variant was not found in the store.")
            }
        }
    }

    /**
     * Search in store for a variant with specific UUID
     * @param identifier
     * @return SimpleVariant
     */
    private SimpleVariant searchVariant(identifier) {
        SimpleVariant res = null
        String searchStatement = "SELECT * from Sample WHERE UPPER(samples.id) = UPPER(?)"

        try {
            dataBase.connection.prepareStatement(searchStatement).withCloseable { PreparedStatement statement ->
                statement.setString(1, identifier)
                statement.executeQuery().withCloseable { ResultSet rs ->
                    while (rs.next()) {
                        String cancerEntity = rs.getString("cancerEntity")
                        res = new SimpleVariantC(identifier, cancerEntity)
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace()
        }

        return res
    }
}

    /*
    @Get(uri = "{?chromosome,position}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getSamples(@QueryValue){
        List<SimpleVariant> s = searchVariants()
        if(s!=null) {
            return HttpResponse.ok(s)
        } else {
            return HttpResponse.notFound("No samples were found in the store.")
        }
    }

    private List<SimpleVariant> searchVariants(@NotNull ListingArguments args) {

        if (args.getMax().isPresent() && args.getSort().isPresent() && args.getOffset().isPresent() && args.getSort().isPresent()) {
            return genreMapper.findAllByOffsetAndMaxAndSortAndOrder(args.getOffset().get(),
                    args.getMax().get(),
                    args.getSort().get(),
                    args.getOrder().get());
        }
        if (args.getMax().isPresent() && args.getOffset().isPresent() && (!args.getSort().isPresent() || !args.getOrder().isPresent())) {
            return genreMapper.findAllByOffsetAndMax(args.getOffset().get(),
                    args.getMax().get());
        }
        if ((!args.getMax().isPresent() || !args.getOffset().isPresent()) && args.getSort().isPresent() && args.getOrder().isPresent()) {
            return genreMapper.findAllBySortAndOrder(args.getSort().get(),
                    args.getOrder().get());
        }
        return genreMapper.findAll();
    }

}
*/