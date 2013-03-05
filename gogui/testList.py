#!/usr/bin/python

import os;
import time;
import re;
import random;

# globals
numAgents = 4;
numExperiments = 1000;
#color = int(sys.argv[1]);
color = 1;

if (color == 0):
    base = "/home/rcf-proj/ls/sorianom/go/black/";
else:
    base = "/home/rcf-proj/ls/sorianom/go/white/";
    
baseScripts = "/home/rcf-proj/ls/sorianom/go/";

def waitExperiments(iteration):
    i = 0;
    while (i < numExperiments):
        if (not os.path.isfile(base + "experiments/testing/" + str(iteration) + "/result-"+str(i)+".sgf")):
            time.sleep(60);
            continue;
        else:
            i = i + 1;


iterationF = open("./parameters/iterationList","r");
testWeightF = open("./parameters/verifyList","r");

iteration = int(iterationF.readline());

testWeightTmp = testWeightF.readlines();

iterationF.close();
testWeightF.close();

for n in range(iteration, len(testWeightTmp)):
    testWeightSplit = testWeightTmp[n].split(',');

    print testWeightSplit;

    testWeight = [0, 0, 0, 0];
    for i in range(numAgents):
        testWeight[i] = float(testWeightSplit[i]);

    
    os.system(baseScripts + "groupGoSimpleTest/executeExp.py " + str(color) + 
              " " + str(iteration) + " " + str(testWeight[0]) + " " + 
              str(testWeight[1]) + " " + str(testWeight[2]) + " " + str(testWeight[3]));

    print "Executing games with weight " + str(testWeight) + "\n";
    
    waitExperiments(n);

    print "Analyzing result...\n";

    result = obtainResult(n);

    logF = open(base + "parameters/log","a");
    logF.write(str(testWeight[0]) + ", " + str(testWeight[1]) + ", " + str(testWeight[2]) + ", " + str(testWeight[3]) + ", " + str(result) + "\n");
    logF.close();

    iterationF = open(base + "parameters/iteration","w");
    iterationF.write(str(n));
    iterationF.close();
