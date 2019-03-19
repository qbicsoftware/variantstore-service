package life.qbic.oncostore.oncoreader

import io.micronaut.context.annotation.Parameter
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import life.qbic.oncostore.DataBase
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.util.IdValidator

import javax.inject.Inject
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException


@Controller("/samples")
class SampleController {

    private final DataBase dataBase

    @Inject SampleController(DataBase database) {
        this.dataBase = dataBase
    }

    @Get(uri = "/{sampleId}", produces = MediaType.APPLICATION_JSON)
    HttpResponse getSample(@Parameter('sampleId') String identifier){
        if(!IdValidator.isValidSampleCode(identifier))
        {
            return HttpResponse.badRequest("No valid sample identifier provided.")
        } else {
            Sample s = searchSample(identifier)
            if(s!=null) {
                return HttpResponse.ok(s)
            } else {
                return HttpResponse.notFound("Sample was not found in the store.")
            }
        }
    }

    @Get(uri = "/", produces = MediaType.APPLICATION_JSON)
    HttpResponse getSamples(){
        List<Sample> s = searchSamples()
        if(s!=null) {
            return HttpResponse.ok(s)
        } else {
            return HttpResponse.notFound("No samples were found in the store.")
        }
    }

    /**
     * Search in store for sample with specific identifier
     * @param identifier
     * @return Sample
     */
    private Sample searchSample(identifier) {
        Sample res = null
        String searchStatement = "SELECT * from Sample WHERE UPPER(samples.id) = UPPER(?)"

        try {
            dataBase.connection.prepareStatement(searchStatement).withCloseable { PreparedStatement statement ->
                statement.setString(1, identifier)
                statement.executeQuery().withCloseable { ResultSet rs ->
                    while(rs.next()) {
                        String cancerEntity = rs.getString("cancerEntity")
                        res = new Sample(identifier, cancerEntity)
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace()
        }

        return res
    }

    /**
     * Retrieve all samples in store
     * @return List<Sample>
     */
    private List<Sample> searchSamples() {
        List<Sample> samples = null
        String searchStatement = "SELECT * from Sample"

        try {
            dataBase.connection.prepareStatement(searchStatement).withCloseable { PreparedStatement statement ->
                statement.executeQuery().withCloseable { ResultSet rs ->
                    while(rs.next()) {
                        String identifier = rs.getString("qbicID")
                        String cancerEntity = rs.getString("cancerEntity")
                        samples.add(new Sample(identifier, cancerEntity))
                    }
                }

            }
        }

        catch (SQLException e) {
            e.printStackTrace()
        }

        return samples
    }
}
