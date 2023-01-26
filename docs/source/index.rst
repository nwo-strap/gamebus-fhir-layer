FHIR for GameBus
================

`GameBus`_ is a digital platform where you can, together with your family, friends
or team, play healthy social, cognitive, and physical games in a personalized gaming experience.
Like many other healthcare platforms, GameBus uses its in-house schemas to
represent data and offers a specific REST API to share these data.
However, this approach can lead to barriers to information exchange between
platforms that do not use the same schema or API. It’s like the communication
challenge between two people who do not understand each other’s language.
A better solution would be for the different platforms to speak the same language.
Here, a popular “language” is FHIR.

`FHIR`_ (Fast Healthcare Interoperability Resources) is a standard for exchanging
healthcare information electronically. It describes healthcare data formats and
elements and API. FHIR has been more and more widely used in industry and
academia, becoming the de-facto standard.

To enable FHIR service for GameBus system, we have developed GameBus FHIR layer.
It is built on a technology stack of open source software and consists of two
main parts: a mapping engine and a FHIR web server. The two components are
integrated into the FHIR layer in order for GameBus to provide FHIR compliant data.
The FHIR layer can be deployed as a microservice. And more importantly, applying
the FHIR layer to GameBus does not need any change on GameBus system.

Though the FHIR layer is developed for GameBus platform, it can be easily reused
for other healthcare systems with some adaptations to the details.

The diagram below shows the relationship between GameBus system, FHIR layer, and end users.

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
