# !/bin/bash

apt install docker.io
addgroup --system docker
adduser jetty docker
chown jetty /var/run/docker.sock
mkDir /home/judgeDir 
mkdir /home/judgeDir/js/
mkdir /home/judgeDir/cpp/
mkdir /home/judgeDir/py/
cp Dockerfile.js.txt /home/judgeDir/js/Dockerfile 
cp Dockerfile.cpp.txt /home/judgeDir/cpp/Dockerfile 
cp Dockerfile.py.txt /home/judgeDir/py/Dockerfile 
cp test.cpp /home/judgeDir/cpp/test.cpp
#cp test.js /home/judgeDir/js/test.js
#cp test.py /home/judgeDir/py/test.py
chown -R jetty /home/judgeDir
docker pull ubuntu:latest
docker pull python:3.10-alpine
docker build . -t opos-js-0:latest -f /home/judgeDir/js/Dockerfile 
docker build . -t opos-cpp-0:latest -f /home/judgeDir/cpp/Dockerfile
docker build . -t opos-py-0:latest -f /home/judgeDir/py/Dockerfile


