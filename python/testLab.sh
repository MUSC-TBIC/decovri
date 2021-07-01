#!/bin/bash

modelPath="models/2019lab_cov.h5"
portNumber=4454
inputType="ssl"
python testLab.py --model $modelPath --port $portNumber --input-type $inputType