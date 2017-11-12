package sample.hello;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

public class Main {

  public static void main(String[] args) throws IOException {
    //akka.Main.main(new String[] { HelloWorld.class.getName() });
    ActorSystem MySystem = ActorSystem.create("Client2");
    ActorRef MyClient = MySystem.actorOf(Props.create(Client.class), "Client");

    System.out.println("Please enter you name:");
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    Msgs.SetUserNameMsg m = new Msgs.SetUserNameMsg();
    m.UName = reader.readLine();

    MyClient.tell(m, null );

    System.out.println("Please type something:");
    while (true) {

      String line = reader.readLine();

      if (line.equalsIgnoreCase("done"))
      {
        MyClient.tell(new Msgs.ShutDownMsg(), null);
        break;
      }

      MyClient.tell(line, null);


    }
    MySystem.terminate();
  }
}
