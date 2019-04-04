package life.qbic.oncostore.oncoreader

import life.qbic.oncostore.model.Sample
import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.util.ListingArguments

import javax.validation.constraints.NotNull

interface SampleReader {

    List<Sample> searchSamples(@NotNull ListingArguments args)

    Sample searchSample(String identifier)

    List<Sample> findAll(String query)

    List<Sample> findAllByCancerEntity(String query, String chromosome)

}