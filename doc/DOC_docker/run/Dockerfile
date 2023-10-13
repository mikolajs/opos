
FROM opos-2:ujammy2:

COPY . .
CMD ["echo", "hello world"]
CMD ["g++", "-o", "test", "test.cpp"]
CMD ["timeout", "--signal=SIGKILL", "3", "./test"]
 
