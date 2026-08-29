package gatling;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class CreateProduct extends Simulation {

    private static final String BASE_URL =
            "http://localhost:3000/api";

    /*
     * Obtain JWT once when the Simulation is initialized.
     */
    private final String jwtToken =
            new JwtTokenProvider(
                    BASE_URL + "/auth/login",
                    "admin@onlineshop.com",
                    "password"
            ).getToken();

    private final HttpProtocolBuilder httpProtocol =
            http
                    .baseUrl(BASE_URL)
                    .acceptHeader("application/json")
                    .contentTypeHeader("application/json");

    private final ScenarioBuilder scenario =
            scenario("Create Product - debug")
                    .exec(http("Create Product")
                            .post("/products")
                            .header("Authorization", "Bearer " + jwtToken)
                            .header("Content-Type", "application/json")

                            .body(StringBody(""" 
                                    { "name": "Newproduct", "description": "description", "price": 2, "weight": 10, "imageUrl": "https://imgs.search.brave.com/Ti5bF1GjEiZDVLBNaWUWPASGaVtYS1algcxWuql3iII/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly93d3cu/bmpveS1vbmxpbmUt/bWFya2V0aW5nLmRl/L3dwLWNvbnRlbnQv/dXBsb2Fkcy9VUkxf/QnJvd3Nlcl9BZHJl/c3NsZWlzdGUuanBn", "categoryId": "ca7e0004-0000-0000-0000-000000000004", "supplierId": "00000000-0000-0000-0000-000000000000" }
                                    """))
                            .check(status().saveAs("productStatus"))

                            .check(status().is(200))

                            .check(bodyString()
                                    .saveAs("productResponse")));

//                    .exec(session ->
//                    {
//                        System.out.println("PRODUCT STATUS: " + session.getString("productStatus"));
//                        System.out.println("PRODUCT RESPONSE: " + session.getString("productResponse"));
//
//                        return session;
//                    });

//    {
//        // 1. baseline ramp up user load simulation
//        setUp(
//                scenario
//                        .injectOpen(
//                                rampUsers(100)
//                                .during(Duration.ofSeconds(30))))
//
//                .protocols(httpProtocol);
//    }

//    {
//        // 2 Sustained load
//        setUp(
//                scenario.
//                        injectOpen(
//                                constantUsersPerSec(10).during(Duration.ofMinutes(5))
//                        )
//                        .protocols(httpProtocol));
//    }

//    {
//        // 3 stress test
//        setUp(
//                scenario.
//                        injectOpen(
//                                rampUsers(100).during(Duration.ofSeconds(30)),
//                                rampUsers(200).during(Duration.ofSeconds(30)),
//                                rampUsers(500).during(Duration.ofSeconds(30)),
//                                rampUsers(1000).during(Duration.ofSeconds(30)),
//                                rampUsers(2000).during(Duration.ofSeconds(30)),
//                                rampUsers(3000).during(Duration.ofSeconds(30)),
//                                rampUsers(4000).during(Duration.ofSeconds(30)),
//                                rampUsers(5000).during(Duration.ofSeconds(30))
//                        )
//                        .protocols(httpProtocol));
//    }

//    {
//        // 4 spike
//        setUp(
//                scenario.
//                        injectOpen(
//                                atOnceUsers(300)
//                        )
//                        .protocols(httpProtocol));
//    }

    {
        // 4 soak
        setUp(
                scenario.
                        injectOpen(
                                constantUsersPerSec(10).during(Duration.ofHours(2))
                        )
                        .protocols(httpProtocol));
    }


}


