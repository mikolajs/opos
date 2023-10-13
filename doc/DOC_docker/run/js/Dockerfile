FROM ubuntu:latest

RUN apt-get update
RUN apt-get install -y nodejs
RUN apt-get clean

COPY . .
CMD ["timeout", "--signal=SIGKILL", "3", "nodejs", "test.js"]
