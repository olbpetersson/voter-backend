#!/usr/bin/env bash

java -Dhttp.address=localhost -Dhttp.port=8080 -Dplay.crypto.secret="aSecret" -cp "../lib/*" play.core.server.ProdServerStart