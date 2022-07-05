package life.qbic.variantstore.model

import io.swagger.v3.oas.annotations.media.Schema
import lombok.AllArgsConstructor

/**
 * A tool used to gather information in the context of variants
 *
 * @since: 1.0.0
 */
@AllArgsConstructor
interface Software {

    /**
     * The name of a tool
     */
    final String name
    /**
     * The version of a tool
     */
    final String version
    /**
     * The Digital Object Identifier (DOI) of a tools
     */
    final String doi

    @Schema(description="The software name")
    String getName()

    @Schema(description="The software version")
    String getVersion()

    @Schema(description="The corresponding digital object identifier")
    String getDoi()
}
