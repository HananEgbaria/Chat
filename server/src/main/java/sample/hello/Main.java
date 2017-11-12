package sample.hello;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

  public static ActorSystem MySystem = ActorSystem.create("ManagerHelper");
  public static ActorRef MyManager;
  public static void main(String[] args) throws IOException {

    //ActorSystem system = ActorSystem.create("HelloSystem");
    //MyManager = system.actorOf(Props.create(Manager.class), "MyManager");
    MyManager = MySystem.actorOf(Props.create(Manager.class), "MyManager");
    System.out.println("Server started..");
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    String line = reader.readLine();
    System.out.println("Server shutting down..");
    MySystem.terminate();
  }
}
