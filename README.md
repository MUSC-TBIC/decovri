
[![Java Build on Stable](https://github.com/MUSC-TBIC/decovri/actions/workflows/maven.yml/badge.svg?branch=stable)](https://github.com/MUSC-TBIC/decovri/actions/workflows/maven.yml)

[![Java Build on Develop](https://github.com/MUSC-TBIC/decovri/actions/workflows/maven.yml/badge.svg?branch=develop)](https://github.com/MUSC-TBIC/decovri/actions/workflows/maven.yml)

DECOVRI (Data Extraction for COVID-19 Related Information) is a
software application based on natural language processing (NLP) to
extract COVID-19 related information from clinical text notes.

It is built on a standard framework (Apache UIMA) and combines
components we could reuse ("off-the-shelf") with components adapted
from other local current research ("rule-based" or "deep learning",
not retrained) and a few new custom components.

Nineteen categories of information are extracted and include a
selection of demographics and social history, medical risk factors,
laboratory tests, medications, environment risk factors and some
clinical note structure information (e.g., sections).

# Installation #
## Using the Pre-Compiled .jar ##

```

mkdir ~/bin/decovri

cd ~/bin/decovri

## Update this is necessary to match your deployment
export VERSION=21.43.1

unzip ~/Downloads/decovri_v${VERSION}-SNAPSHOT_Linux.zip

cp resources/pipeline.properties.TEMPLATE \
  resources/pipeline.properties

export CONCEPTMAPPER_HOME="/path/to/ConceptMapper-2.10.2"

java -cp \
  resources:target/decovri-${VERSION}-SNAPSHOT-standalone.jar:${CONCEPTMAPPER_HOME}/lib:${CONCEPTMAPPER_HOME}/bin \
  edu.musc.tbic.uima.Decovri \
    --pipeline-properties pipeline-demo.properties

```

*NOTE*

The cTAKES model file is not correctly automatically deployable.  You
will need to download the cTAKES src build and find
`sd-med-model.zip`.  This zip file should be moved (without unzipping
it) to `resources/ctakesModels` to the replace the version there.

```
apache-ctakes-4.0.0.1-src/ctakes-core-res/src/main/resources/org/apache/ctakes/core/sentdetect/sd-med-model.zip
```

## Building Decovri From Source ##

### Command Line Build Environment ###

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

### Eclipse Settings ###

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

If you have not already defined any of these variables within Eclipse,
you'll need to first "Configure Variables":

Build Path -> Configure Build Path -> Libraries - > Add Variables -> Configure Variables

"Maven Build"
Main
- Base directory:
- - ${workspace_loc:/Decovri}
- Goals
- - package

"Java Application"
Main
- Project -> Decovri
- Main class -> edu.musc.tbic.uima.Decovri
Arguments
- Program arguments:
- - "--help"
- - "--pipeline-properties pipeline-demo.properties"
JRE
- JavaSE-1.8 (or similar Java v8 JRE)
Classpath
- Classpath:
- - User Entries
- - - resources - /Decovri/
- - - classes - /Decovri/target
- - - java - /Decovri/src/main
- - - jtds-1.3.1.jar - /Decovri/resources/lib
- - - Maven Dependencies

## Create and customize your properties files ##

DECOVRI uses a Java properties file to define the flow of the
pipeline. Templates for these files (ending in
`*.properties.TEMPLATE`) are available in `apache-uima/resources`. You
will need to copy these templates to a new filename (so that late
releases of DECOVRI don't overwrite your local settings) and adjust
the paths to match your preferences and local needs.

```

cp resources/pipeline.properties.TEMPLATE \
  resources/pipeline.properties

cp src/main/resources/database_connection.properties.TEMPLATE \
  resources/database_connection.properties

```

## Generating Lexicons for Dynamic ConceptMapper ##

Due to UMLS licensing constraints, the default ConceptMapper
dictionaries that DECOVRI ships have just enough detail to help you
fill in the blanks on your own lexicon generation. There are several
options for quickly creating your own dictionaries, with and without
access to a UMLS license. We have released tools via our
`lexicon-tools` git repository to help with the automated creation of
ConceptMapper dictionaries from a simple seed list of CUIs:

 - https://github.com/musc-tbic/lexicon-tools

For a more directly and immediately deployable lexicon, you can
leverage the COVID-19 Sign and Symptom list published by Wang et
al. (2021) and released via their GitHub account:

 - https://github.com/Medical-NLP/COVID-19-Sign-Symptom

Wang, J., Abu, N., Gray, J., Anh, H., Zhou, Y., Manion, F., Liu, M.,
Song, X., Xu, H., Rouhizadeh, M. and Zhang, Y., COVID-19 SignSym–a
fast adaptation of general clinical NLP tools to identify and
normalize COVID-19 signs and symptoms to OMOP common data
model. Journal of the American Medical Informatics Association, March
1, 2021.

After downloading `covid19_signs_symptoms.csv` and installing the
necessary Python packages to run
`convert_COVID-19_SignSym_lexicon.py`, you will need to run the
conversion script similar to below:

```

python convert_COVID-19_SignSym_lexicon.py \
	--input-file decovri/covid19_signs_symptoms.csv \
	--batch-name SignSym \
	--output-dir decovri
	
```

You will find a file in your specified output directory named
`conceptMapper_SignSym.xml` which will need to be moved to the DECOVRI
folder under `apached-uima/resources/dict`.

Next, you will need to add this lexicon to your `pipeline.properties file`. Preferntially, you should replace the following line in the properties file:

```
conceptMapper.dictionaryPath.symptomsDemo = dict/conceptMapper_symptoms_demo.xml
```

with this new line:

```
##conceptMapper.dictionaryPath.symptomsDemo = dict/conceptMapper_symptoms_demo.xml
conceptMapper.dictionaryPath.symptomsSignSym = dict/conceptMapper_SignSym.xml
```

## Model Files ##

These model files should be automatically included with the
pre-compiled jar. If you're building form source, you'll need to
download them.

- apache-uima/resources/ctakesModels/
  - [sd-med-model.zip](https://github.com/apache/ctakes/tree/trunk/ctakes-core-res/src/main/resources/org/apache/ctakes/core/sentdetect/sd-med-model.zip)
- apache-uima/resources/openNlpModels/
  - [en-token.bin](http://opennlp.sourceforge.net/models-1.5/en-token.bin)
  - [en-pos-maxent.bin](http://opennlp.sourceforge.net/models-1.5/en-pos-maxent.bin)
- python/models/
  - 2018med_cov.h5
  - 2019lab_cov.h5

The cTAKES model file can be downloaded from GitHub:
https://github.com/apache/ctakes/tree/trunk/ctakes-core-res/src/main/resources/org/apache/ctakes/core/sentdetect

The openNLP models can be downloaded from SourceForge:
http://opennlp.sourceforge.net/models-1.5/

The medications and laboratory results/values models can not yet be
freely downloaded. We are working to generate models from
appropriately licensed data such that the models can be freely
released.

## UMLS Dictionary Files ##

The following UMLS dictionary files need to be downloaded from the
[UMLS Knowledge
Sources](https://www.nlm.nih.gov/research/umls/licensedcontent/umlsknowledgesources.html)
page and placed in the specified folder:

- apache-uima/resources/
    - MRCONSO.RRF
    - MRSTY.RRF
    - MRCONSO_f.RRF

## Logging Configurations ##

Decovri's Java pipeline uses log4j2 to handle logging settings.  The
default configuration can be changed in:

- apache-uima/src/main/resources/log4j2-test.xml


Update line 13 in both testMed.py and testLab.py to reflect your
actual desired log directory:

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

# Contributing #
# License #
