#!/usr/bin/python

import os
import sys
import subprocess

numGames = 1000;
numNodes = 9;
color = int(sys.argv[1]);

if (color == 0):
    base = "/home/rcf-proj/ls/sorianom/go/black/";
else:
    base = "/home/soriano/pesquisa/desenvolvimento/go/groupGoSimple/";
    #base = "/home/rcf-proj/ls/sorianom/go/white/";

#baseScripts = "/home/rcf-proj/ls/sorianom/go/";
baseScripts = "/home/soriano/pesquisa/desenvolvimento/go/";

gamesPerNode = numGames/numNodes;
rest = numGames - gamesPerNode*numNodes;

gogui = baseScripts + "/groupGoSimple/bin/gogui-twogtp";
results = base + "/experiments/current/" + sys.argv[2] + "/";

os.system("cp " + results + "result-0.dat " + results + "result.dat");

if (rest > 0):
    position = gamesPerNode + 1;
    rest = rest - 1;
else:
    position = gamesPerNode;

while position < numGames:
#    os.system("tail --lines=+17 " + results + "result-" + str(position) + ".dat > " + results + "result-" + str(position) + ".dat.clean");
    os.system("tail --lines=+13 " + results + "result-" + str(position) + ".dat > " + results + "result-" + str(position) + ".dat.clean");
    os.system("cat " + results + "result-" + str(position) + ".dat.clean >> " + results + "result.dat");

    if (rest > 0):
        position = position + gamesPerNode + 1;
        rest = rest - 1;
    else:
        position = position + gamesPerNode;

os.system(gogui + " -analyze " + results + "result.dat");

