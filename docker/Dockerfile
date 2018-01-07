FROM java:8
COPY libs /usr/allowance-tracker/lib
RUN mkdir /usr/allowance-tracker/logs
WORKDIR /usr/allowance-tracker
CMD [ "java", "-DconfigPath=config.properties", "-DEVELOPMENT=true", "-cp", "lib/*", "com.joesbigidea.allowancetracker.TrackerRunner" ]
