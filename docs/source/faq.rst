===
FAQ
===


Where can I find the supported GameBus activities and properties and FHIR resources?
------------------------------------------------------------------------------------

You can find that in the section :ref:`dev3_dev_mapping_configs:Develop new mapping rules`.


What will happen if I use a GameBus activity that is not supported yet?
-----------------------------------------------------------------------

The server will return an error message like :code:`GameBus activity BASEBALL (796070) is NOT supported yet.`.

As a user, you can ask the server administrator to add support for the game descriptor.

As the server administrator or developer, you can add support for the game descriptor by following
the guide in :ref:`dev3_dev_mapping_configs:Develop new mapping rules`.


What will happen if I use a GameBus property that is not supported yet?
-----------------------------------------------------------------------

The server will not raise any error message. Instead, it will ignore that property and return the
FHIR resources without that property. This will not affect the other properties that are supported.

Thus in the mapped FHIR resources, you will not see the unsupported properties in the :code:`component` field
of the :code:`Observation` resource.


What will happen if I use a GameBus unit that is not supported yet?
-------------------------------------------------------------------

The server will not raise any error message. You will get :code:`"system": "Unit-unharmonized"` in
the :code:`valueQuantity` field of the :code:`Observation` resource.

.. code-block::

    "component": [
        {
            "valueQuantity": {
                "value": 1800,
                "system": "Unit-unharmonized",
                "code": "seconds"
            }
        }
    ]


What will happen if I use a GameBus data type that is not supported yet?
------------------------------------------------------------------------

The server will not raise any error message. It will ignore that data type and return the FHIR resources
without that data type. This will not affect the other data types that are supported.

Thus in the mapped FHIR resources, you will not see the unsupported data types in the :code:`component` field,
e.g. the :code:`valueQuantity` field will be missing.
