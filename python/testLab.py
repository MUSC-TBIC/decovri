#!/usr/bin/python

# Originally: This scripts loads a pretrained model and a input file
# in CoNLL format (each line a token, sentences separated by an empty
# line).  The input sentences are passed to the model for
# tagging. Prints the tokens and the tags in a CoNLL format to stdout

# Usage: python RunModel_ConLL_Format.py modelPath inputPathToConllFile

# For pretrained models see docs/

# Now it takes 2 parameters, the modelPath and a port number to communicate with Java app

from __future__ import print_function

import logging as log
log.basicConfig( filename = '/data/logs/Decovri/testLab.log' ,
                 format = "%(asctime)s testLab.py [%(levelname)s] %(message)s" ,
                 datefmt = '%Y-%m-%d %H:%M:%S' ,
                 level = log.DEBUG )

### Just disables the warning, doesn't enable AVX/FMA
##import os
##os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

from util.preprocessing import readCoNLLSocket, createMatrices, addCharInformation, addCasingInformation
from neuralnets.BiLSTM import BiLSTM
import sys

import socket
import time
import ssl

if len(sys.argv) < 3:
    print ("Usage: python3 testLab.py modelPath portNumber")
    exit()

modelPath = sys.argv[1]
portNumber = sys.argv[2]
# :: Load the model ::
lstmModel = BiLSTM.loadModel(modelPath)
log.info( 'model loaded' )

#socket
# openssl req -newkey rsa:2048 -nodes -keyout server.key -x509 -days 365 -out server.crt
context = ssl.create_default_context( ssl.Purpose.CLIENT_AUTH )
context.load_cert_chain( certfile = "server.crt" , keyfile = "server.key" )
sock = socket.socket()

sock.settimeout(None)
# Bind the socket to the address given on the command line
server_address = ('', int(portNumber)) #was 4444
sock.bind(server_address)
log.info( 'starting service_name=testLab with socket_host=%s socket_port=%s' % sock.getsockname() )
sock.listen(1)

endMsg = b"--<eosc>--"
endMsgE = b"--<eoscE>--"

inputColumns = {0: "tokens", 1: "begin", 2: "end", 3: "tnum", 4: "snum", 5: "fname"}

jobEnd = "no"

while True:
  
    log.info( 'waiting for a connection' )
    connection, client_address = sock.accept()
    connstream = context.wrap_socket( connection , server_side = True )
    try:
        log.info( 'service_name=testLab contacted by client_host=%s on client_port=%s' % client_address )
        data = b''
        #endMsgE could get split
        rd = b''

        while True:
            rd = connstream.recv(10000)        #was 10000
            #print ("rd:")
            #print (rd)
            #print("data:")
            #print (data)
            if rd.strip() == endMsg:
                jobEnd = "yes"
                break

            data += rd
        
            if data.strip().endswith(endMsgE):
                data = data.replace(endMsgE, b"")
                break

            if data.strip() == endMsg:
                jobEnd = "yes"
                break

        log.info( 'Tagging...' )

        data = data.decode('utf-8')
        
        # :: Prepare the input ::
        sentences = readCoNLLSocket(data, inputColumns)

        addCharInformation(sentences)

        addCasingInformation(sentences)

        dataMatrix = createMatrices(sentences, lstmModel.mappings, True)

        # :: Tag the input ::
        tags = lstmModel.tagSentences(dataMatrix)

        # :: Output to stdout ::
        output = ''        
        for sentenceIdx in range(len(sentences)):
            #tokens = sentences[sentenceIdx]['tokens']
            begins = sentences[sentenceIdx]['begin']
            ends = sentences[sentenceIdx]['end']
            #tnums = sentences[sentenceIdx]['tnum']
            #snums = sentences[sentenceIdx]['snum']
            #fnames = sentences[sentenceIdx]['fname']
            
            for tokenIdx in range(len(begins)): #was tokens changed since tokens removed, but they have the same length
                tokenTags = []
                for modelName in sorted(tags.keys()):
                    tokenTags.append(tags[modelName][sentenceIdx][tokenIdx])

                #output += ("%s\t%s\t%s\t%s\t%s\t%s\tO\t%s\n" % (tokens[tokenIdx], begins[tokenIdx], ends[tokenIdx], tnums[tokenIdx], snums[tokenIdx], fnames[tokenIdx], "\t".join(tokenTags)))      
                #this should be faster than the original above
#                output += tokens[tokenIdx] + "\t"+ begins[tokenIdx] +    "\t"+ ends[tokenIdx] + "\t"+ tnums[tokenIdx] + "\t" + snums[tokenIdx] + "\t"+ fnames[tokenIdx] + "\tO\t" + "\t".join(tokenTags) + "\n"
#               shrunk to run faster

#                output += tokens[tokenIdx] + "\t"+ begins[tokenIdx] + "\t"+ ends[tokenIdx] + "\tO\t" + "\t".join(tokenTags) + "\n"
                output += begins[tokenIdx] + "\t"+ ends[tokenIdx] + "\tO\t" + "\t".join(tokenTags) + "\n"
             
            output += "\n"
    
        #print output
        #print("%s" % output.encode('utf-8'))        

        connstream.sendall(output.encode('utf-8'))

    finally:
        connstream.shutdown( socket.SHUT_RDWR )
        connstream.close()
        log.info( "connection closed" )
        
    if jobEnd == "yes":
        break        
        
log.info( "Job done for service_name=testLab" )
