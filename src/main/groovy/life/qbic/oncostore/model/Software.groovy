package life.qbic.oncostore.model

import io.swagger.v3.oas.annotations.media.Schema

interface Software {

    final String name
    final String version
    final String doi

    @Schema(description="The software name")
    String getName()

    @Schema(description="The software version")
    String getVersion()

    @Schema(description="The corresponding digital object identifier")
    String getDoi()
}