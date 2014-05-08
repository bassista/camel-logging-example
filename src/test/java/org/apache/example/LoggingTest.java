package org.apache.example;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Created by ceposta on 5/7/14.
 */
public class LoggingTest extends CamelTestSupport {

    private static final String ROUTE_ID = LoggingTest.class.getCanonicalName() + ".route";

    @Test
    public void testLoggingPath()  {
        System.out.println("ROUTE: " + ROUTE_ID);
        NotifyBuilder notifier = new NotifyBuilder(context).whenCompleted(2).create();
        LinkedList<String> bodies = new LinkedList<String>();
        bodies.add("Camel");
        bodies.add("Mule");
        bodies.add("Spring Integration");
        template.sendBody("direct:start", bodies);
        notifier.matches(2, TimeUnit.SECONDS);
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").routeId(ROUTE_ID)
                        .wireTap("direct:logBody")
                        .split().body()
                        .choice()
                        .when(body().isEqualTo("Camel"))
                            .log("${body} is awesome")
                        .otherwise()
                            .log("${body} kinda sucks")
                        .end()
                        .wireTap("direct:logBody");

                from("direct:logBody")
                        .log(LoggingLevel.DEBUG, ROUTE_ID + ".inoutBody", "INOUT: ${body}");

            }
        };
    }
}
