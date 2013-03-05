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
        if (not os.path.isfile(base + "experiments/current/" + str(iteration) + "/result-"+str(i)+".sgf")):
            time.sleep(60);
            continue;
        else:
            i = i + 1;


iteration = 2;

bestWeightF = open(base + "parameters/verifyWeight","r");

bestWeightTmp = bestWeightF.readline();
bestWeightSplit = bestWeightTmp.split(',');

bestWeight = [0, 0, 0, 0];
for i in range(numAgents):
    bestWeight[i] = float(bestWeightSplit[i]);

bestWeightF.close();

os.system(baseScripts + "groupGoSimpleTest/executeExp.py " + str(color) + 
          " " + str(iteration) + " " + str(bestWeight[0]) + " " + 
          str(bestWeight[1]) + " " + str(bestWeight[2]) + " " + str(bestWeight[3]));

waitExperiments(iteration);
