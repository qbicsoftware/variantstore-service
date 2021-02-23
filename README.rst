
.. image:: https://travis-ci.com/qbicsoftware/oncostore-proto-project.svg
    :target: https://travis-ci.com/qbicsoftware/oncostore-proto-project
    :alt: Travis Build Status

.. image:: https://readthedocs.org/projects/oncostore-proto-project/badge/?version=latest
    :target: https://oncostore-proto-project.readthedocs.io/en/latest/?badge=latest
    :alt: Documentation Status

Variantstore
============
The **Variantstore** is a Java/Groovy-based service application implemented using the `Micronaut framework <https://micronaut.io>`_ and enables storage and access to information on genomic variants and metadata from a connected database *via* a RESTful API. 


Features
--------
- Import metadata (JSON files using this `schema <https://github.com/qbicsoftware/mtb-metadata-specs/blob/master/schemes/mtb/variants.metadata.schema.json>`_)
- Import variants (VCF files, annotated using `SnpEff <http://snpeff.sourceforge.net>`_ or `VEP <https://www.ensembl.org/info/docs/tools/vep/index.html>`_)
- Import gene information (Ensembl, GFF3 files)  
- Query information on variants, genes, and cases via (secured) REST endpoints
- Ask Beacon endpoint if a specific variant exists in the store
- Export variants in Variant Call Format (VCF) and `FHIR <https://www.hl7.org/fhir/>`_


Documentation
-------------
Please visit the `Documentation <https://oncostore-proto-project.readthedocs.io/en/latest/>`_ for detailed installation, run and configuration instructions.

Detailed documentation of the RESTful API is additionally available on `SwaggerHub <https://app.swaggerhub.com/apis/christopher-mohr/variantstore/0.6>`_.


Database
--------
In the current version, the **Variantstore** service can be used with a MariaDB database. If you want to use a different DBMS,
make sure to specify the database model, set up the datasource in the ``application.yml``, and provide an implementation for the ``VariantstoreStorage`` interface.

The main database `model <models/oncostore-model.sql>`_ expected by the **Variantstore** is the following:

.. image:: images/variantstore-model-diagram.png
    :alt: Variantstore model diagram

Additonally, a database with the following `table <models/transaction-db.sql>`_ is needed to track the transactions in the Variantstore:

.. image:: images/transaction-model-diagram.png
    :alt: Variantstore transaction model diagram

We are currently working on the support of `PostgreSQL <https://www.postgresql.org/>`_. 

Contribute
----------
The contribution guidelines can be found on `Contributing <https://oncostore-proto-project.readthedocs.io/en/latest/contributing.html>`_.


Credits
-------
The Variantstore service was designed by `Christopher Mohr <https://github.com/christopher-mohr>`_ and implemented by `Christopher Mohr <https://github.com/christopher-mohr>`_ and `Lukas Heumos <https://github.com/zethson>`_. Feel free to contribute!
