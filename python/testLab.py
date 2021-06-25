# Originally: This scripts loads a pretrained model and a input file
# in CoNLL format (each line a token, sentences separated by an empty
# line).  The input sentences are passed to the model for
# tagging. Prints the tokens and the tags in a CoNLL format to stdout

# Usage: python annotator.py --model modelPath --port sslPortNumber

# Now it takes 2 parameters, the modelPath and a port number to communicate with Java app

from __future__ import print_function

import logging as log

import os
import sys

import argparse

### Just disables the warning, doesn't enable AVX/FMA
##import os
##os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3'

from util.preprocessing import readCoNLLSocket, createMatrices, addCharInformation, addCasingInformation
from neuralnets.BiLSTM import BiLSTM

import socket
import time
import ssl

#############################################
## 
#############################################

def initialize_arg_parser():
    parser = argparse.ArgumentParser( description = """
    """ )
    parser.add_argument( '-v' , '--verbose' ,
                         help = "print more information" ,
                         action = "store_true" )

    parser.add_argument( '--model' , required = True ,
                         dest = 'modelPath' ,
                         help = 'Path for the model file to load' )

    parser.add_argument( '--port' , required = True ,
                         dest = 'portNumber' ,
                         help = 'Port number used for the inbound SSL connection' )
    ##
    return parser


def init_args( command_line_args ):
    ##
    parser = initialize_arg_parser()
    args = parser.parse_args( command_line_args )
    ##
    bad_args_flag = False
    ##
    ## logic here to initialize and coordinate command-line argument
    ## values
    ##
    if( bad_args_flag ):
        log.error( "I'm bailing out of this run because of errors mentioned above." )
        exit( 1 )
    ##
    return args

#############################################
## 
#############################################

#############################################
## 
#############################################

if __name__ == "__main__":
    ##
    log.basicConfig( format = "%(asctime)s testLab.py [%(levelname)s] %(message)s" ,
                     datefmt = '%Y-%m-%d %H:%M:%S' ,
                     level = log.DEBUG )
    ##
    args = init_args( sys.argv[ 1: ] )
    ##
    # :: Load the model ::
    lstmModel = BiLSTM.loadModel( args.modelPath )
    log.info( 'model loaded' )
    
    #socket
    # openssl req -newkey rsa:2048 -nodes -keyout server.key -x509 -days 365 -out server.crt
    context = ssl.create_default_context( ssl.Purpose.CLIENT_AUTH )
    context.load_cert_chain( certfile = "server.crt" , keyfile = "server.key" )
    sock = socket.socket()
    
    sock.settimeout(None)
    # Bind the socket to the address given on the command line
    server_address = ( '' , int( args.portNumber ) ) #was 4444
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
                    #output += tokens[tokenIdx] + "\t"+ begins[tokenIdx] +    "\t"+ ends[tokenIdx] + "\t"+ tnums[tokenIdx] + "\t" + snums[tokenIdx] + "\t"+ fnames[tokenIdx] + "\tO\t" + "\t".join(tokenTags) + "\n"
                    #shrunk to run faster
                    
                    #output += tokens[tokenIdx] + "\t"+ begins[tokenIdx] + "\t"+ ends[tokenIdx] + "\tO\t" + "\t".join(tokenTags) + "\n"
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
