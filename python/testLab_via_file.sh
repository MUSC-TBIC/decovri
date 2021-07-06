#!/bin/bash

modelPath="models/2019lab_cov.h5"
inputType="file"
input="test_input/pred_mv1_20200229103703.txt"
python testLab.py --model $modelPath --input-type $inputType --input $input