FHIR for GameBus
================

`GameBus`_ is a digital platform for people playing together healthy social,
cognitive and physical activities in a personalized gaming experience.

Just like the way many healthcare platforms store and share data, GameBus
applies its in-house schema to format and store the data of players' activities
and offers REST API to share these data.

This way, however, brings lots of burden of data transformation when exchanging
healthcare data between platforms that are not using the same schema. It's like
that a translator is always required to help two people exchange ideas when
they don't know each other's language.

An ideal solution of this challenge is to enable different platforms speak the
same language. Here, the “language” for exchanging healthcare info is FHIR.

`FHIR`_ (Fast Healthcare Interoperability Resources) is a standard for exchanging
healthcare information electronically. It describes healthcare data formats and
elements and API. FHIR has been more and more wildely used in industry and
academia, becoming the de-facto standard.

To enable FHIR service for GameBus without changing or replacing GameBus
in-house schema, the GameBus FHIR layer was developed. The diagram below shows
the relationship between GameBus, FHIR layer and end users.

.. image:: image/architecture.png
  :width: 800
  :alt: GameBus FHIR Layer


**Highlights:**

- Open source
- Smooth deployment as microservice
- Adding/changing mappings with ease
- Adding/changing operations for FHIR REST API with ease
- Not only for GameBus, easy to adapt for other healthcare platforms


Tutorials
---------

.. toctree::
   :maxdepth: 3

   tutorial

Guide for Development
---------------------

.. toctree::
   :maxdepth: 4

   dev

.. _GameBus: https://blog.gamebus.eu/
.. _FHIR: http://hl7.org/fhir/
