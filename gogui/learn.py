#!/usr/bin/python

import os;
import time;
import re;
import random;

# globals
numAgents = 4;
numExperiments = 50;
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
    

def obtainResult(iteration):
    
    os.system(baseScripts + "groupGoSimple/evaluate.py " +  str(color) + " " + str(iteration));

    result = open(base + "experiments/current/" + str(iteration) + "/result.summary.dat", "r");

    resultStr = result.readlines();

    resultS = re.findall('(\d+\.?\d*)', resultStr[1]);

    if (color == 0):
        return float(resultS[6]);
    else:
        return (1 - float(resultS[6]));

def modify(bestWeight, numAgents):
    modifyAgent = random.randint(0,numAgents-1);

    newWeight = [0, 0, 0, 0];
    for n in range(numAgents):
        newWeight[n] = bestWeight[n];
    
    newWeight[modifyAgent] = random.uniform(0.9,1.1)*newWeight[modifyAgent];

    return newWeight;

iterationF = open(base + "parameters/iteration","r");
bestResultF = open(base + "parameters/bestResult","r");
bestWeightF = open(base + "parameters/bestWeight","r");
testWeightF = open(base + "parameters/testWeight","r");
#historyF = open(base + "parameters/history","r");
#historyAllF = open(base + "parameters/historyAll", "r");

iteration = int(iterationF.readline());
bestResult = float(bestResultF.readline());

bestWeightTmp = bestWeightF.readline();
bestWeightSplit = bestWeightTmp.split(',');

bestWeight = [0, 0, 0, 0];
for i in range(numAgents):
    bestWeight[i] = float(bestWeightSplit[i]);

testWeightTmp = testWeightF.readline();
testWeightSplit = testWeightTmp.split(',');

testWeight =[0, 0, 0, 0];
for i in range(numAgents):
    testWeight[i] = float(testWeightSplit[i]);

testIteration = int(testWeightSplit[numAgents]);

iterationF.close();
bestResultF.close();
bestWeightF.close();
testWeightF.close();

while (True):
    print "== Iteration " + str(iteration) + " ==\n";
    
    if (testIteration < iteration):
        testWeight = modify(bestWeight, numAgents);

        testIteration = testIteration + 1;
        testWeightF = open(base + "parameters/testWeight","w");
        testWeightF.write(str(testWeight[0]) + ", " + str(testWeight[1]) + ", " + str(testWeight[2]) + ", " + str(testWeight[3]) + ", " + str(testIteration) + "\n");
        testWeightF.close();
    
    os.system(baseScripts + "groupGoSimple/executeExp.py " + str(color) + 
              " " + str(iteration) + " " + str(testWeight[0]) + " " + 
              str(testWeight[1]) + " " + str(testWeight[2]) + " " + str(testWeight[3]));

    print "Executing games with weight " + str(testWeight) + "\n";

    waitExperiments(iteration);

    print "Analyzing result...\n";

    result = obtainResult(iteration);

    # if the result is better, let's accept the modification...
    if (result > bestResult):
        print "Accepted new weight: " + str(testWeight) + "\n";
        
        bestWeight = testWeight;
        bestResult = result;

        bestWeightF = open(base + "parameters/bestWeight","w");
        bestWeightF.write(str(bestWeight[0]) + ", " + str(bestWeight[1]) + ", " + str(bestWeight[2]) + ", " + str(bestWeight[3]) + "\n");
        bestWeightF.close();

        bestResultF = open(base + "parameters/bestResult","w");
        bestResultF.write(str(bestResult));
        bestResultF.close();

        resultHistoryF = open(base + "parameters/history","a");
        resultHistoryF.write(str(bestWeight[0]) + ", " + str(bestWeight[1]) + ", " + str(bestWeight[2]) + ", " + str(bestWeight[3]) + ", " + str(bestResult) + "\n");
        resultHistoryF.close();
 
    logF = open(base + "parameters/log","a");
    logF.write(str(testWeight[0]) + ", " + str(testWeight[1]) + ", " + str(testWeight[2]) + ", " + str(testWeight[3]) + ", " + str(result) + "\n");
    logF.close();

    iteration = iteration + 1;
    
    iterationF = open(base + "parameters/iteration","w");
    iterationF.write(str(iteration));
    iterationF.close();
