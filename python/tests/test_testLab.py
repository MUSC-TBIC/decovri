import os
import sys

from mock import patch

import tempfile

import json

import testLab

#############################################
## Early initialization and set-up
#############################################

def test_default_init_args():
    test_args = [ 'testLab.py' ,
                  '--model' , 'models/sample.h5' ,
                  '--port' , '4444' ,
                  '--input-type', 'ssl']
    with patch.object( sys , 'argv' , test_args ):
        args = testLab.init_args( sys.argv[ 1: ] )
        assert args.modelPath == 'models/sample.h5'
        assert args.portNumber == '4444'
        assert args.inputType == 'ssl'
