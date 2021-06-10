

# Local Customizations Required (But Not Available via git) #

## Create and customize your properties files ##

```

cp resources/pipeline.properties.TEMPLATE \
  resources/pipeline.properties

cp src/main/resources/database_connection.properties.TEMPLATE \
  resources/database_connection.properties

```

## Model Files ##

- apache-uima/resources/ctakesModels/sd-med-model.zip
- python/models/
    - 2018med_cov.h5
    - 2019lab_cov.h5

## UMLS Dictionary Files ##

- apache-uima/resources/
    - MRCONSO.RRF
    - MRSTY.RRF
    - MRCONSO_f.RRF

## Logging Configurations ##

Decovri's Java pipeline uses log4j2 to handle logging settings.  The default configuration can be changed in:
- apache-uima/src/main/resources/log4j2-test.xml


Update line 13 in both testMed.py and testLab.py to reflect your actual desired log directory:

```

log.basicConfig( filename = '/data/software/Decovri/logs/testMed.log' ,
                 format = "%(asctime)s testMed.py [%(levelname)s] %(message)s" ,
                 datefmt = '%Y-%m-%d %H:%M:%S' ,
                 level = log.DEBUG )

...

log.basicConfig( filename = '/data/software/Decovri/logs/testLab.log' ,
                 format = "%(asctime)s testMed.py [%(levelname)s] %(message)s" ,
                 datefmt = '%Y-%m-%d %H:%M:%S' ,
                 level = log.DEBUG )

```

# Building Decovri #

## Command Line Build Environment ##

```
export JAVA_HOME=/path/to/jdk1.8.0_131.jdk/Contents/Home

export UIMA_HOME=/path/to/apache-uima-2.9.0

export PATH=$PATH:$UIMA_HOME/bin

export CTAKES_HOME="/path/to/apache-ctakes-4.0.0"

export CONCEPTMAPPER_HOME="/path/to/ConceptMapper-2.10.2"

export PIPELINE_ROOT=/path/to/decovri/apache-uima

export UIMA_CLASSPATH=${PIPELINE_ROOT}/target/classes
export UIMA_CLASSPATH=$UIMA_CLASSPATH:${PIPELINE_ROOT}/lib
export UIMA_CLASSPATH=${UIMA_CLASSPATH}:${CONCEPTMAPPER_HOME}/lib
export UIMA_CLASSPATH=${UIMA_CLASSPATH}:${CONCEPTMAPPER_HOME}/src
export UIMA_CLASSPATH=${UIMA_CLASSPATH}:${CTAKES_HOME}/lib:${CTAKES_HOME}/resources
export UIMA_CLASSPATH=${UIMA_CLASSPATH}:${PIPELINE_ROOT}/resources

export UIMA_DATAPATH=${PIPELINE_ROOT}/resources

export UIMA_JVM_OPTS="-Xms128M -Xmx2G"

cd ${PIPELINE_ROOT}

mvn package

```

## IntelliJ Settings ##

## Eclipse Settings ##

Build Path -> Configure Build Path -> Libraries -> Add Variable -> Extend Variable

- UIMA_HOME/lib/
    - uima-core.jar
    - uima-document-annotation.jar
- UIMAFIT_HOME/lib/
    - commons-io-2.6.jar
    - uimafit-core.3.0.0.jar

- CTAKES_HOME/lib/ctakes-core-4.0.0.jar

- OPENNLP_HOME/lib/
    - opennlp-tools-1.8.0.jar
    - opennlp-uima-1.8.0.jar

- CONCEPTMAPPER_HOME/lib/ConceptMapper-2.10.2.jar

# Running Decovri #

## Command Line Run Environment ##

```
export JAVA_HOME=/path/to/jdk1.8.0_131.jdk/Contents/Home

export CONCEPTMAPPER_HOME="/path/to/ConceptMapper-2.10.2"

## This version can be verified in the pom.xml file
export VERSION=21.21.0

java -cp \
  resources:target/classes:target/decovri-${VERSION}-SNAPSHOT-jar-with-dependencies.jar:${CONCEPTMAPPER_HOME}/lib:${CONCEPTMAPPER_HOME}/bin:resources/lib/jtds-1.3.1.jar \
    edu.musc.tbic.uima.Decovri -h

java -cp \
  resources:target/classes:target/decovri-${VERSION}-SNAPSHOT-jar-with-dependencies.jar:${CONCEPTMAPPER_HOME}/lib:${CONCEPTMAPPER_HOME}/bin:resources/lib/jtds-1.3.1.jar \
    edu.musc.tbic.uima.Decovri

```

## Anaconda Environment for Python Meds and Labs Annotator

```
conda create -n decovri-stable-py3.6 python=3.6
conda activate decovri-stable-py3.6

pip install --upgrade pip

cd decovri/src/python
pip install -r requirements.txt

```

## Creating a key file for SSL connections ##

Run the following on whatever machine you'll need to connect to via
SSL (that is, the machine the Python scripts are running on).

```

openssl req -newkey rsa:2048 \
  -nodes \
  -keyout ${SERVER_NAME}Server.key \
  -x509 \
  -days 365 \
  -out ${SERVER_NAME}localhostServer.crt
  ## Update line 47 in testLab.py/testMed.py to include whatever names you chose
  ## here:
  ##     context.load_cert_chain( certfile = "server.crt" , keyfile = "server.key" )
  
keytool -import \
  -alias covid \
  -file ${SERVER_NAME}Server.crt \
  -keystore ${SERVER_NAME}Keystore.jks

```
