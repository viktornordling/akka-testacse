This is a test case for showing a problem described in the Akka mailing list:
http://groups.google.com/group/akka-user/browse_thread/thread/8babb9f5b94af04e/17fdf45c62503b5d?show_docid=17fdf45c62503b5d#

To run it, you need to first build the project while skipping the tests (because it will fail)
mvn package -Dmaven.test.skip

Then run exec, this will start the Akka server:
mvn exec:java

Now, in a new session, run:
mvn test

This test fails randomly for me running Akka 1.1.3, but never running 1.1.2.