#!/bin/bash

for i in `ls configurations/*.textproto`; do
    perl -i -pe "s+src/main/gamebus_fhir_r4+$PWD+g" $i
done