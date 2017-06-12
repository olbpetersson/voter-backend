FROM lagom-base

COPY lib/se.olapetersson-voting-api-1.0-SNAPSHOT.jar .
COPY ../../target/lib/se.olapetersson-voting-impl-1.0-SNAPSHOT.jar .
#COPY bin/run.sh .

ENTRYPOINT ["java","-Dhttp.address=localhost", "-Dhttp.port=8080", "-Dplay.crypto.secret=aSecret" , "-cp","./*", "play.core.server.ProdServerStart"]
