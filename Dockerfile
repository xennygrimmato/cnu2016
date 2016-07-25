FROM maven:3-jdk-8
VOLUME /tmp
ADD . /
RUN apt-get update
RUN apt-get -y install python-pip
RUN pip install awscli
RUN mvn clean package
CMD aws s3 cp s3://cnu-2016/vtulsyan/application.properties application.properties && java -jar target/1-1.0-SNAPSHOT.jar
