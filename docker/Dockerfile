FROM java:8
COPY libs /usr/allowance-tracker/lib
WORKDIR /usr/allowance-tracker
#CMD ["java", "com.joesbigidea.allowancetracker.TrackerRunner"]
cmd java -DconfigPath=config.properties -DDEVELOPMENT=true -cp "lib/*" com.joesbigidea.allowancetracker.TrackerRunner
