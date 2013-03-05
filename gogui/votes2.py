#!/usr/bin/python

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
    minEpsilon.append(min(higherWeights(weights)) + smallNumber);
    numInf += testInfUp(weights);

    # 3 options
    # A | B C | D
    minEpsilon.append(min(higherWeights([weights[0], weights[1] + weights[2], weights[3]])) + smallNumber);
    numInf += testInfUp([weights[0], weights[1] + weights[2], weights[3]]);
    # A | B | C D
    minEpsilon.append(min(higherWeights([weights[0], weights[1], weights[2] + weights[3]])) + smallNumber);
    numInf += testInfUp([weights[0], weights[1], weights[2] + weights[3]]);
    # A | B D | C
    minEpsilon.append(min(higherWeights([weights[0], weights[1] + weights[3], weights[2]])) + smallNumber);
    numInf += testInfUp([weights[0], weights[1] + weights[3], weights[2]]);

    # A B | C | D
    minEpsilon.append(min(higherWeights([weights[0] + weights[1], weights[2], weights[3]])) + smallNumber - weights[1]);
    numInf += testInfUp([weights[0] + weights[1], weights[2], weights[3]]);
    # A C | B | D
    minEpsilon.append(min(higherWeights([weights[0] + weights[2], weights[1], weights[3]])) + smallNumber - weights[2]);
    numInf += testInfUp([weights[0] + weights[2], weights[1], weights[3]]);
    # A D | B | C
    minEpsilon.append(min(higherWeights([weights[0] + weights[3], weights[1], weights[2]])) + smallNumber - weights[3]);
    numInf += testInfUp([weights[0] + weights[3], weights[1], weights[2]]);

    # 2 options
    # A | B C D
    minEpsilon.append(min(higherWeights([weights[0], weights[1] + weights[2] + weights[3]])) + smallNumber);
    numInf += testInfUp([weights[0], weights[1] + weights[2] + weights[3]]);    
    # A B | C D
    minEpsilon.append(min(higherWeights([weights[0] + weights[1], weights[2] + weights[3]])) + smallNumber - weights[1]);
    numInf += testInfUp([weights[0] + weights[1], weights[2] + weights[3]]);    
    # A C | B D
    minEpsilon.append(min(higherWeights([weights[0] + weights[2], weights[1] + weights[3]])) + smallNumber - weights[2]);
    numInf += testInfUp([weights[0] + weights[2], weights[1] + weights[3]]);    
    # A D | B C
    minEpsilon.append(min(higherWeights([weights[0] + weights[3], weights[1] + weights[2]])) + smallNumber - weights[3]);
    numInf += testInfUp([weights[0] + weights[3], weights[1] + weights[2]]);

    # A B C | D
    minEpsilon.append(min(higherWeights([weights[0] + weights[1] + weights[2], weights[3]])) + smallNumber - weights[1] - weights[2]);
    numInf += testInfUp([weights[0] + weights[1] + weights[2], weights[3]]);
    # A C D | B
    minEpsilon.append(min(higherWeights([weights[0] + weights[2] + weights[3], weights[1]])) + smallNumber - weights[2] - weights[3]);
    numInf += testInfUp([weights[0] + weights[2] + weights[3], weights[1]]);
    # A B D | C
    minEpsilon.append(min(higherWeights([weights[0] + weights[1] + weights[3], weights[2]])) + smallNumber - weights[1] - weights[3]);
    numInf += testInfUp([weights[0] + weights[1] + weights[3], weights[2]]);
 
    if (numInf == 14):
        raise ReachedInfinite(1);

    return minEpsilon;

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
    maxEpsilon.append(max(lowerWeights(weights)) - smallNumber);
    numInf += testInfDown(weights);

    # 3 options
    # A | B C | D
    maxEpsilon.append(max(lowerWeights([weights[0], weights[1] + weights[2], weights[3]])) - smallNumber);
    numInf += testInfDown([weights[0], weights[1] + weights[2], weights[3]])
    # A | B | C D
    maxEpsilon.append(max(lowerWeights([weights[0], weights[1], weights[2] + weights[3]])) - smallNumber);
    numInf += testInfDown([weights[0], weights[1], weights[2] + weights[3]]);
    # A | B D | C
    maxEpsilon.append(max(lowerWeights([weights[0], weights[1] + weights[3], weights[2]])) - smallNumber);
    numInf += testInfDown([weights[0], weights[1] + weights[3], weights[2]]);

    # A B | C | D
    maxEpsilon.append(max(lowerWeights([weights[0] + weights[1], weights[2], weights[3]])) - smallNumber - weights[1]);
    numInf += testInfDown([weights[0] + weights[1], weights[2], weights[3]]);
    # A C | B | D
    maxEpsilon.append(max(lowerWeights([weights[0] + weights[2], weights[1], weights[3]])) - smallNumber - weights[2]);
    numInf += testInfDown([weights[0] + weights[2], weights[1], weights[3]]);
    # A D | B | C
    maxEpsilon.append(max(lowerWeights([weights[0] + weights[3], weights[1], weights[2]])) - smallNumber - weights[3]);
    numInf += testInfDown([weights[0] + weights[3], weights[1], weights[2]]);

    # 2 options
    # A | B C D
    maxEpsilon.append(max(lowerWeights([weights[0], weights[1] + weights[2] + weights[3]])) - smallNumber);
    numInf += testInfDown([weights[0], weights[1] + weights[2] + weights[3]]);

    # A B | C D
    maxEpsilon.append(max(lowerWeights([weights[0] + weights[1], weights[2] + weights[3]])) - smallNumber - weights[1]);
    numInf += testInfDown([weights[0] + weights[1], weights[2] + weights[3]]);
    # A C | B D
    maxEpsilon.append(max(lowerWeights([weights[0] + weights[2], weights[1] + weights[3]])) - smallNumber - weights[2]);
    numInf += testInfDown([weights[0] + weights[2], weights[1] + weights[3]]);
    # A D | B C
    maxEpsilon.append(max(lowerWeights([weights[0] + weights[3], weights[1] + weights[2]])) - smallNumber - weights[3]);
    numInf += testInfDown([weights[0] + weights[3], weights[1] + weights[2]]);

    # A B C | D
    maxEpsilon.append(max(lowerWeights([weights[0] + weights[1] + weights[2], weights[3]])) - smallNumber - weights[1] - weights[2]);
    numInf += testInfDown([weights[0] + weights[1] + weights[2], weights[3]]);
    # A C D | B
    maxEpsilon.append(max(lowerWeights([weights[0] + weights[2] + weights[3], weights[1]])) - smallNumber - weights[2] - weights[3]);
    numInf += testInfDown([weights[0] + weights[2] + weights[3], weights[1]]);
    # A B D | C
    maxEpsilon.append(max(lowerWeights([weights[0] + weights[1] + weights[3], weights[2]])) - smallNumber - weights[1] - weights[3]);
    numInf += testInfDown([weights[0] + weights[1] + weights[3], weights[2]]);
    
    if (numInf == 14):
        raise ReachedInfinite(0);

    return maxEpsilon;

def dictator(bestWeight, modifyAgent):
    if (bestWeight[modifyAgent] != max(bestWeight)):
        return False;

    positiveSum = 0;
    negativeSum = 0;

    for i in range(len(bestWeight)):
        if (i == modifyAgent):
            continue;

        if (bestWeight[i] < 0):
            negativeSum = negativeSum + bestWeight[i];
        if (bestWeight[i] > 0):
            positiveSum = positiveSum + bestWeight[i];
            
    return (bestWeight[modifyAgent] + negativeSum > positiveSum);

def suppressed(bestWeight, modifyAgent):

    positiveSum = 0;
    negativeSum = 0;

    for i in range(len(bestWeight)):
        if (i == modifyAgent):
            continue;

        if (bestWeight[i] < 0):
            negativeSum = negativeSum + bestWeight[i];
        if (bestWeight[i] > 0):
            positiveSum = positiveSum + bestWeight[i];
            
    return (bestWeight[modifyAgent] + negativeSum > positiveSum);


if (dictator([10, 1, 1, -9],0)):
    print "Dictator!";

#result = minChangeUp([1.4, 1.5, 2.6, 0.8], 0);
try:
    weights = [1.0, 1.0, 0.999, 1.0];
    result = minChangeUp(weights, 2);
    print weights;
    print result;
    print "Next weight up: " + str(min(result));
except ReachedInfinite:
    print "Weight is already too high...";

try:
    result = minChangeDown([0.999, 0.999, 0.997, 1.0], 2);
    print result;
    print "Next weight down: " + str(max(result));
except ReachedInfinite:
    print "Weight is already too low...";
