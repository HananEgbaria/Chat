package sample.hello;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by majdy on 28/05/2017.
 */
public class Manager extends AbstractActor
{
    public static Map<String,ActorRef> Channels;
    public static Map<String,ActorRef> Users;
    public static ActorRef DefaultChannel;

    public Manager()
    {
        Channels = new HashMap<>();
        Users = new HashMap<>();
        //DefaultChannel = MySystem.actorOf(Props.create(Channel.class), "Lobby");
        DefaultChannel = Main.MySystem.actorOf(Props.create(Channel.class), "Lobby1");
        Msgs.InitChannelMsg msg = new Msgs.InitChannelMsg();
        msg.channelName = "Lobby";
        msg.channelOwner = "Manager";
        DefaultChannel.tell(msg, self());
        Channels.put("Lobby",DefaultChannel);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Msgs.CreateUserServerMsg.class,this::CreateUser)
                .build();
    }

    private void CreateUser(Msgs.CreateUserServerMsg msg)
    {
        System.out.println("Client is connected !");
        ActorRef userActor = getContext().actorOf(Props.create(UserServer.class), msg.UserName);
        Users.put(msg.UserName,userActor);
        Msgs.InitUserMsg m = new Msgs.InitUserMsg();
        m.nickName = msg.UserName;
        m.DefaultCH = new Pair<>("Lobby" , DefaultChannel);
        m.myClient = sender();
        m.userServerRef = userActor;
        userActor.tell(m,self());

        Msgs.ClientUserServerActorMsg m1 = new Msgs.ClientUserServerActorMsg();
        m1.ActRef = userActor;
        m.myClient.tell(m1,self());
    }

    /*
    private void answerTest(String msg)
    {
        System.out.println("Client is connected !");
        sender().tell("Ahlan from server",self());
    }
*/

}