package sample.camel.route;

import akka.camel.javaapi.UntypedProducerActor;

public class Producer1 extends UntypedProducerActor {
  public String getEndpointUri() {
    return "direct:welcome";
  }
}
