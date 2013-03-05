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

    return maxEpsilon;

try:
    weights = [5.001, 3.0, 1.0, 1.0];
    result = minChangeUp(weights, 0);
    print weights;
    print result;
    print "Next weight up: " + str(min(result));
except ReachedInfinite:
    print "Weight is already too high...";

try:
    weights = [0.999, 1.0, 1.0, 1.0];
    result = minChangeDown(weights, 0);
    print weights;
    print result;
    print "Next weight down: " + str(max(result));
except ReachedInfinite:
    print "Weight is already too low...";
