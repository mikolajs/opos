
FROM opos-debian

COPY . . 
CMD [g++ -o test test.cpp]
CMD [timeout --signal=SIGKILL  1 ./test < dane1.txt]
