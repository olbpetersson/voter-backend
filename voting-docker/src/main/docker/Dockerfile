FROM lagom-base

COPY voting-api-${project.version}.jar .
COPY voting-impl-${project.version}.jar .
#COPY bin/run.sh .

ENTRYPOINT ["java","-Dhttp.address=0.0.0.0", "-Dhttp.port=8080", "-Dplay.crypto.secret=aSecret" , "-cp","./*", "play.core.server.ProdServerStart"]
