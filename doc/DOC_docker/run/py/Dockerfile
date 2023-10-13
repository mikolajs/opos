FROM python:3.10-alpine 

COPY . .
CMD ["timeout", "-s", "KILL", "-k", "9", "3", "python3", "test.py"]
