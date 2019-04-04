package life.qbic.oncostore.oncoreader

import life.qbic.oncostore.model.Variant
import life.qbic.oncostore.util.ListingArguments

interface VariantReader {

    List<Variant> searchVariants(ListingArguments args)

    Variant searchVariant(String identifier)

    List<Variant> findAll(String query)

    List<Variant> findAllByChromosome(String query, String chromosome)

    List<Variant> findAllByStartPosition(String query, BigInteger startPosition)

    List<Variant> searchVariantForBeaconReponse(String chromosome, BigInteger startPosition, String reference, String
            observed, String assemblyId, ListingArguments args)

}