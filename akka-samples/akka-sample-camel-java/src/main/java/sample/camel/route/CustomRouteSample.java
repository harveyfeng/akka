package sample.camel.route;

import akka.actor.*;
import akka.camel.CamelExtension;

public class CustomRouteSample {
  @SuppressWarnings("unused")
  public static void main(String[] args) {
    try {
      ActorSystem system = ActorSystem.create("some-system");
      final ActorRef producer = system.actorOf(Props.create(Producer1.class));
      final ActorRef mediator = system.actorOf(Props.create(Transformer.class, producer));
      final ActorRef consumer = system.actorOf(Props.create(Consumer3.class, mediator));
      CamelExtension.get(system).context().addRoutes(new CustomRouteBuilder());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
