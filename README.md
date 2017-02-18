# ekikara2oudiaWebapp
A web application for ekikara2oudia on GAE

You can see it on [GAE](https://ekikara2oudia.appspot.com/ "Ekikara2OuDia on GAE") and [Heroku](https://ekikara2oudia.heroku.com/ "Ekikara2OuDia on Heroku").

### Starting a local test server
```
mvn clean appengine:devserver
```
### Uploading to the GAE
```
JAVA_HOME=/usr/lib/jvm/java-7-oraclejdk-amd64 mvn clean appengine:update
```
(We have to force to use Java7 only for GAE...)
