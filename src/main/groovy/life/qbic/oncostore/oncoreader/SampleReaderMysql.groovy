package life.qbic.oncostore.oncoreader

import life.qbic.oncostore.DataBase
import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.util.ListingArguments

import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.PositiveOrZero
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class SampleReaderMysql implements SampleReader{

    private final DataBase dataBase

    String searchAllSamples = "select * from Sample;"
    String searchSampleByCancerEntity = "where Sample.cancerEntity=?;"
    String searchSampleById = "where Sample.qbicID=?;"


    @Inject
    SampleReaderMysql(DataBase dataBase) {
        this.dataBase = dataBase
    }

    @Override
    List<Sample> searchSamples(@NotNull ListingArguments args) {
        if (args.getCancerEntity().isPresent()) {
            return findAllByCancerEntity(searchAllSamples.replace(";", searchSampleByCancerEntity), args.getCancerEntity().get())
        }

        return findAll(searchAllSamples)
    }

    @Override
    Sample searchSample(String identifier) {
        def sample = null
        String searchStatement = searchAllSamples.replace(";", searchSampleById)

        try {
            dataBase.connection.prepareStatement(searchStatement).withCloseable { PreparedStatement statement ->
                statement.setString(1, identifier)
                statement.executeQuery().withCloseable { ResultSet rs ->
                    while (rs.next()) {
                        sample = createSample(rs)
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace()
        }

        return sample
    }

    @Override
    List<Sample> findAll(String sqlStatement) {
        def samples = []

        try {
            dataBase.connection.prepareStatement(sqlStatement).withCloseable { PreparedStatement statement ->
                statement.executeQuery().withCloseable { ResultSet rs ->
                    while(rs.next()) {
                        samples.add(createSample(rs))
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace()
        }

        return samples
    }

    public List<Sample> findAllFiltered(PreparedStatement sqlStatement) {
        def samples = []

        try {
            sqlStatement.withCloseable { PreparedStatement statement ->
                statement.executeQuery().withCloseable { ResultSet rs ->
                    while(rs.next()) {
                        samples.add(createSample(rs))
                    }
                }
            }
        }

        catch (SQLException e) {
            e.printStackTrace()
        }

        return samples
    }

    @Override
    List<Sample> findAllByCancerEntity(String query, String cancerEntity) {
        def searchSamples = dataBase.connection.prepareStatement(query)
        searchSamples.setString(cancerEntity)

        return findAllFiltered(searchSamples)
    }

    Sample createSample(ResultSet rs) {
        Sample sample = new Sample()

        sample.setIdentifier(rs.getString("qbicID"))
        sample.setCancerEntity(rs.getString("Case_id"))
        sample.setCaseID(rs.getString("cancerEntity"))

        return sample
    }

}
