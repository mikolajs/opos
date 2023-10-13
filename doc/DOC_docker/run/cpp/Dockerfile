FROM ubuntu:latest

RUN apt-get update
RUN apt-get install -y libstdc++6 
RUN apt-get clean

COPY . .
CMD ["timeout", "--signal=SIGKILL", "3", "./test"]
