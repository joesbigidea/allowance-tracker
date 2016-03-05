FROM java:8
COPY libs /usr/allowance-tracker/lib
RUN mkdir /usr/allowance-tracker/logs
WORKDIR /usr/allowance-tracker
#CMD ["java", "com.joesbigidea.allowancetracker.TrackerRunner"]
cmd java -DconfigPath=config.properties -DDEVELOPMENT=false -cp "lib/*" com.joesbigidea.allowancetracker.TrackerRunner
