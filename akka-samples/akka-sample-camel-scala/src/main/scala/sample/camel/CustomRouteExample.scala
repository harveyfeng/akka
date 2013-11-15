package sample.camel

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.camel.CamelExtension
import akka.camel.CamelMessage
import akka.camel.Consumer
import akka.camel.Producer

object CustomRouteExample {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("some-system")
    val producer = system.actorOf(Props[Producer1])
    val mediator = system.actorOf(Props(classOf[Transformer], producer))
    val consumer = system.actorOf(Props(classOf[Consumer3], mediator))
    CamelExtension(system).context.addRoutes(new CustomRouteBuilder)
  }

  class Consumer3(transformer: ActorRef) extends Actor with Consumer {
    def endpointUri = "jetty:http://0.0.0.0:8877/camel/welcome"

    def receive = {
      // Forward a string representation of the message body to transformer
      case msg: CamelMessage => transformer.forward(msg.bodyAs[String])
    }
  }

  class Transformer(producer: ActorRef) extends Actor {
    def receive = {
      // example: transform message body "foo" to "- foo -" and forward result
      // to producer
      case msg: CamelMessage =>
        producer.forward(msg.mapBody((body: String) => "- %s -" format body))
    }
  }

  class Producer1 extends Actor with Producer {
    def endpointUri = "direct:welcome"
  }

  class CustomRouteBuilder extends RouteBuilder {
    def configure {
      from("direct:welcome").process(new Processor() {
        def process(exchange: Exchange) {
          // Create a 'welcome' message from the input message
          exchange.getOut.setBody("Welcome %s" format exchange.getIn.getBody)
        }
      })
    }
  }

}
