#!/bin/bash

php php/neapel.html | tidy -config php/tidy.cfg > neapel.html
php php/familie.html | tidy -config php/tidy.cfg > familie.html
php php/eiscafe.html | tidy -config php/tidy.cfg > eiscafe.html
php php/spezialitaeten.html | tidy -config php/tidy.cfg > spezialitaeten.html
php php/eiskonfigurator.html | tidy -config php/tidy.cfg > eiskonfigurator.html
php php/index.html | tidy -config php/tidy.cfg > index.html