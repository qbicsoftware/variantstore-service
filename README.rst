**THIS SERVICE IS WORK IN PROGRESS.**

Variantstore
============
The **Variantstore** is a Java/Groovy-based service application implemented using the `Micronaut framework <https://micronaut.io>`_ and enables storage and access to information on genomic variants and metadata from a connected database *via* a RESTful API. 


Features
--------
- Import metadata (JSON files using `this <https://github.com/qbicsoftware/mtb-metadata-specs/blob/master/schemes/mtb/variants.metadata.schema.json>`_
- Import variants (VCF files, annotated using `SnpEff <http://snpeff.sourceforge.net>`_ or `VEP <https://www.ensembl.org/info/docs/tools/vep/index.html>`_)
- Import gene information (Ensembl, GFF3 files)  
- Query variant information via REST endpoints


Documentation
-------------
Please visit the `Documentation <https://oncostore-proto-project.readthedocs.io/en/latest/>`_ for detailed installation, run and configuration instructions.

The remote RESTFUL API documentation will be provided on `SwaggerHub <https://swagger.io/tools/swaggerhub>`_.


Database
----------
In the current version, the **Variantstore** service can be used with a MariaDB database. If you want to use a different DBMS,
make sure to specify the database model, set up the datasource in the ``application.yml``, and provide an implementation for the ``VariantstoreStorage`` interface.

The database model expected by the **Variantstore** is the following: 

.. image:: images/oncostore-model-diagram.png
    :alt: Oncostore model diagram

We are currently working on the support of `PostgreSQL <https://www.postgresql.org/>`_. 

Contribute
----------

The contribution guidelines can be found on :ref:`contributing`.


Credits
-------
OncoStore service was designed by `Christopher Mohr <https://github.com/christopher-mohr>`_ and implemented by `Christopher Mohr <https://github.com/christopher-mohr>`_ and `Lukas Heumos <https://github.com/zethson>`_. Feel free to contribute!
