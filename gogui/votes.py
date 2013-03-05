#!/usr/bin/python

import itertools;

def generateVotes(numCases, agentSet):
    if (numCases == len(agentSet)):
        for i in range(len(agentSet)):
            print agentSet[i] + "|",
        print;
    elif (len(agentSet)>0):
        print agentSet[0] + "|",
        generateVotes(numCases-1, agentSet[1:len(agentSet)]);
    else:
        print;

for i in range(4):
    generateVotes(i+1, 'ABCD');
