
FROM ubuntu:jammy

RUN apt-get update
RUN apt-get install -y g++
RUN apt-get install -y nodejs
RUN apt-get install -y python3
RUN apt-get clean

COPY . .
CMD ["echo", "hello world"]
CMD ["g++", "-o", "./test", "test.cpp"]
CMD ["timeout", "--signal=SIGKILL", "3", "./test"]
 
