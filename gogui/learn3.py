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

INF = 999999;

class ReachedInfinite(Exception):
    def __init__(self, value):
        self.value = value
    def __str__(self):
        return repr(self.value)

def higherWeights(weights):
    pos = 0;

    higherWeights =[];

    for i in range(1,len(weights)):
        if (weights[0] <= weights[i]):
            higherWeights.append(weights[i]);

    if (len(higherWeights) == 0):
        higherWeights = [INF];

    return higherWeights;

def testInfUp(weights):
    pos = 0;

    higherWeights =[];

    for i in range(1,len(weights)):
        if (weights[0] <= weights[i]):
            higherWeights.append(weights[i]);

    if (len(higherWeights) == 0):
        return 1;

    return 0;

def lowerWeights(weights):
    pos = 0;

    lowerWeights =[];

    for i in range(1,len(weights)):
        if (weights[0] >= weights[i]):
            lowerWeights.append(weights[i]);

    if (len(lowerWeights) == 0):
        lowerWeights = [-INF];

    return lowerWeights;

def testInfDown(weights):
    pos = 0;

    lowerWeights =[];

    for i in range(1,len(weights)):
        if (weights[0] >= weights[i]):
            lowerWeights.append(weights[i]);

    if (len(lowerWeights) == 0):
        return 1;

    return 0;

def minChangeUp(weightsInput, pos):
    smallNumber = 0.001;
    minEpsilon = [];

    weights = [0, 0, 0, 0];
    for i in range(len(weightsInput)):
        weights[i] = weightsInput[i];

    tmp = weights[0];
    weights[0] = weights[pos];
    weights[pos] = tmp;

    numInf = 0;

    # 4 options
    # A | B | C | D
    if (weights[0] == weights[1] or weights[0] == weights[2] or weights[0] == weights[3]):
        minEpsilon.append(weights[0] + smallNumber);
    else:
        minEpsilon.append(min(higherWeights(weights)) );
        numInf += testInfUp(weights);

    # 3 options
    # A | B C | D
    if (weights[0] == weights[1] + weights[2] or weights[0] == weights[3]):
        minEpsilon.append(weights[0] + smallNumber);
    else:
        minEpsilon.append(min(higherWeights([weights[0], weights[1] + weights[2], weights[3]])) );
        numInf += testInfUp([weights[0], weights[1] + weights[2], weights[3]])
    # A | B | C D
    if (weights[0] == weights[2] + weights[3] or weights[0] == weights[1]):
        minEpsilon.append(weights[0] + smallNumber);
    else:    
        minEpsilon.append(min(higherWeights([weights[0], weights[1], weights[2] + weights[3]])) );
        numInf += testInfUp([weights[0], weights[1], weights[2] + weights[3]]);
    # A | B D | C
    if (weights[0] == weights[1] + weights[3] or weights[0] == weights[2]):
        minEpsilon.append(weights[0] + smallNumber);
    else:    
        minEpsilon.append(min(higherWeights([weights[0], weights[1] + weights[3], weights[2]])) );
        numInf += testInfUp([weights[0], weights[1] + weights[3], weights[2]]);

    # A B | C | D
    if (weights[0] + weights[1] == weights[2] or weights[0] + weights[1] == weights[3]):
        minEpsilon.append(weights[0] + smallNumber);
    else:    
        minEpsilon.append(min(higherWeights([weights[0] + weights[1], weights[2], weights[3]]))  - weights[1]);
        numInf += testInfUp([weights[0] + weights[1], weights[2], weights[3]]);
    # A C | B | D
    if (weights[0] + weights[2] == weights[1] or weights[0] + weights[2] == weights[3]):
        minEpsilon.append(weights[0] + smallNumber);
    else:    
        minEpsilon.append(min(higherWeights([weights[0] + weights[2], weights[1], weights[3]]))  - weights[2]);
        numInf += testInfUp([weights[0] + weights[2], weights[1], weights[3]]);
    # A D | B | C
    if (weights[0] + weights[3] == weights[1] or weights[0] + weights[3] == weights[2]):
        minEpsilon.append(weights[0] + smallNumber);
    else:    
        minEpsilon.append(min(higherWeights([weights[0] + weights[3], weights[1], weights[2]]))  - weights[3]);
        numInf += testInfUp([weights[0] + weights[3], weights[1], weights[2]]);

    # 2 options
    # A | B C D
    if (weights[0] == weights[1] +  weights[2] + weights[3]):
        minEpsilon.append(weights[0] + smallNumber);
    else:    
        minEpsilon.append(min(higherWeights([weights[0], weights[1] + weights[2] + weights[3]])) );
        numInf += testInfUp([weights[0], weights[1] + weights[2] + weights[3]]);

    # A B | C D
    if (weights[0] + weights[1] == weights[2] + weights[3]):
        minEpsilon.append(weights[0] + smallNumber);
    else:    
        minEpsilon.append(min(higherWeights([weights[0] + weights[1], weights[2] + weights[3]]))  - weights[1]);
        numInf += testInfUp([weights[0] + weights[1], weights[2] + weights[3]]);
    # A C | B D
    if (weights[0] + weights[2] == weights[1] + weights[3]):
        minEpsilon.append(weights[0] + smallNumber);
    else:    
        minEpsilon.append(min(higherWeights([weights[0] + weights[2], weights[1] + weights[3]]))  - weights[2]);
        numInf += testInfUp([weights[0] + weights[2], weights[1] + weights[3]]);
    # A D | B C
    if (weights[0] + weights[3] == weights[1] + weights[2]):
        minEpsilon.append(weights[0] + smallNumber);
    else:    
        minEpsilon.append(min(higherWeights([weights[0] + weights[3], weights[1] + weights[2]]))  - weights[3]);
        numInf += testInfUp([weights[0] + weights[3], weights[1] + weights[2]]);

    # A B C | D
    if (weights[0] + weights[1] + weights[2] == weights[3]):
        minEpsilon.append(weights[0] + smallNumber);
    else:    
        minEpsilon.append(min(higherWeights([weights[0] + weights[1] + weights[2], weights[3]]))  - weights[1] - weights[2]);
        numInf += testInfUp([weights[0] + weights[1] + weights[2], weights[3]]);
    # A C D | B
    if (weights[0] + weights[2] + weights[3] == weights[1]):
        minEpsilon.append(weights[0] + smallNumber);
    else:  
        minEpsilon.append(min(higherWeights([weights[0] + weights[2] + weights[3], weights[1]]))  - weights[2] - weights[3]);
        numInf += testInfUp([weights[0] + weights[2] + weights[3], weights[1]]);
    # A B D | C
    if (weights[0] + weights[1] + weights[3] == weights[2]):
        minEpsilon.append(weights[0] + smallNumber);
    else:  
        minEpsilon.append(min(higherWeights([weights[0] + weights[1] + weights[3], weights[2]]))  - weights[1] - weights[3]);
        numInf += testInfUp([weights[0] + weights[1] + weights[3], weights[2]]);
 
    if (numInf == 14):
        raise ReachedInfinite(1);

    for i in range(len(minEpsilon)):
        if (minEpsilon[i] == weights[0]):
            minEpsilon[i] = INF;

    return min(minEpsilon);

def minChangeDown(weightsInput, pos):
    smallNumber = 0.001;
    maxEpsilon = [];

    weights = [0, 0, 0, 0];
    for i in range(len(weightsInput)):
        weights[i] = weightsInput[i];

    tmp = weights[0];
    weights[0] = weights[pos];
    weights[pos] = tmp;

    numInf = 0;

    # 4 options
    # A | B | C | D
    if (weights[0] == weights[1] or weights[0] == weights[2] or weights[0] == weights[3]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:
        maxEpsilon.append(max(lowerWeights(weights)));
        numInf += testInfDown(weights);

    # 3 options
    # A | B C | D
    if (weights[0] == weights[1] + weights[2] or weights[0] == weights[3]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:
        maxEpsilon.append(max(lowerWeights([weights[0], weights[1] + weights[2], weights[3]])) );
        numInf += testInfDown([weights[0], weights[1] + weights[2], weights[3]])
    # A | B | C D
    if (weights[0] == weights[2] + weights[3] or weights[0] == weights[1]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:    
        maxEpsilon.append(max(lowerWeights([weights[0], weights[1], weights[2] + weights[3]])) );
        numInf += testInfDown([weights[0], weights[1], weights[2] + weights[3]]);
    # A | B D | C
    if (weights[0] == weights[1] + weights[3] or weights[0] == weights[2]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:    
        maxEpsilon.append(max(lowerWeights([weights[0], weights[1] + weights[3], weights[2]])) );
        numInf += testInfDown([weights[0], weights[1] + weights[3], weights[2]]);

    # A B | C | D
    if (weights[0] + weights[1] == weights[2] or weights[0] + weights[1] == weights[3]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:    
        maxEpsilon.append(max(lowerWeights([weights[0] + weights[1], weights[2], weights[3]]))  - weights[1]);
        numInf += testInfDown([weights[0] + weights[1], weights[2], weights[3]]);
    # A C | B | D
    if (weights[0] + weights[2] == weights[1] or weights[0] + weights[2] == weights[3]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:    
        maxEpsilon.append(max(lowerWeights([weights[0] + weights[2], weights[1], weights[3]]))  - weights[2]);
        numInf += testInfDown([weights[0] + weights[2], weights[1], weights[3]]);
    # A D | B | C
    if (weights[0] + weights[3] == weights[1] or weights[0] + weights[3] == weights[2]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:    
        maxEpsilon.append(max(lowerWeights([weights[0] + weights[3], weights[1], weights[2]]))  - weights[3]);
        numInf += testInfDown([weights[0] + weights[3], weights[1], weights[2]]);

    # 2 options
    # A | B C D
    if (weights[0] == weights[1] +  weights[2] + weights[3]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:    
        maxEpsilon.append(max(lowerWeights([weights[0], weights[1] + weights[2] + weights[3]])) );
        numInf += testInfDown([weights[0], weights[1] + weights[2] + weights[3]]);

    # A B | C D
    if (weights[0] + weights[1] == weights[2] + weights[3]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:    
        maxEpsilon.append(max(lowerWeights([weights[0] + weights[1], weights[2] + weights[3]]))  - weights[1]);
        numInf += testInfDown([weights[0] + weights[1], weights[2] + weights[3]]);
    # A C | B D
    if (weights[0] + weights[2] == weights[1] + weights[3]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:    
        maxEpsilon.append(max(lowerWeights([weights[0] + weights[2], weights[1] + weights[3]]))  - weights[2]);
        numInf += testInfDown([weights[0] + weights[2], weights[1] + weights[3]]);
    # A D | B C
    if (weights[0] + weights[3] == weights[1] + weights[2]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:    
        maxEpsilon.append(max(lowerWeights([weights[0] + weights[3], weights[1] + weights[2]]))  - weights[3]);
        numInf += testInfDown([weights[0] + weights[3], weights[1] + weights[2]]);

    # A B C | D
    if (weights[0] + weights[1] + weights[2] == weights[3]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:    
        maxEpsilon.append(max(lowerWeights([weights[0] + weights[1] + weights[2], weights[3]]))  - weights[1] - weights[2]);
        numInf += testInfDown([weights[0] + weights[1] + weights[2], weights[3]]);
    # A C D | B
    if (weights[0] + weights[2] + weights[3] == weights[1]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:  
        maxEpsilon.append(max(lowerWeights([weights[0] + weights[2] + weights[3], weights[1]]))  - weights[2] - weights[3]);
        numInf += testInfDown([weights[0] + weights[2] + weights[3], weights[1]]);
    # A B D | C
    if (weights[0] + weights[1] + weights[3] == weights[2]):
        maxEpsilon.append(weights[0] - smallNumber);
    else:  
        maxEpsilon.append(max(lowerWeights([weights[0] + weights[1] + weights[3], weights[2]]))  - weights[1] - weights[3]);
        numInf += testInfDown([weights[0] + weights[1] + weights[3], weights[2]]);
    
    if (numInf == 14):
        raise ReachedInfinite(0);

    for i in range(len(maxEpsilon)):
        if (maxEpsilon[i] == weights[0]):
            maxEpsilon[i] = -INF;

    return max(maxEpsilon);


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
    
    if (random.random() < 0.5):
        try:
            print "Changing " + str(modifyAgent) + " up.";
            newWeight[modifyAgent] = minChangeUp(bestWeight, modifyAgent);
        except ReachedInfinite:
            print "Failed. Changing it down instead.";
            newWeight[modifyAgent] = minChangeDown(bestWeight, modifyAgent);
    else:
        try:
            print "Changing " + str(modifyAgent) + " down.";
            newWeight[modifyAgent] = minChangeDown(bestWeight, modifyAgent);
        except ReachedInfinite:
            print "Failed. Changing it up instead.";
            newWeight[modifyAgent] = minChangeUp(bestWeight, modifyAgent);
    
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
    logF.write(str(iteration) + ", " + str(testWeight[0]) + ", " + str(testWeight[1]) + ", " + str(testWeight[2]) + ", " + str(testWeight[3]) + ", " + str(result) + ", " + str(bestResult) + "\n");
    logF.close();

    iteration = iteration + 1;
    
    iterationF = open(base + "parameters/iteration","w");
    iterationF.write(str(iteration));
    iterationF.close();
