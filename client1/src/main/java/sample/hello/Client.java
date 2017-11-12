package sample.hello;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Terminated;
import scala.Option;
import scala.concurrent.Future;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by majdy on 31/05/2017.
 */
public class Client extends AbstractActor {

    private ActorRef MyUserServer;
    private String UserName;
    private ActorSelection ChatManager;

    public Client() {
        MyUserServer = null;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Msgs.SetUserNameMsg.class, this:: SetUserName)
                .match(Msgs.ClientPrintMessageMsg.class, this::PrintMessage)
                .match(Msgs.ClientUserServerActorMsg.class, this::SetMyUserServer)
                .match(Msgs.ShutDownMsg.class, this:: ShutDown)
                .match(String.class, this::AnalyzeInput)
                .build();
    }

    private void SetUserName(Msgs.SetUserNameMsg m)
    {
        UserName = m.UName;
        Msgs.CreateUserServerMsg msg = new Msgs.CreateUserServerMsg();
        msg.UserName = UserName;

        ChatManager.tell(msg, self());
    }

    private void SetMyUserServer(Msgs.ClientUserServerActorMsg m)
    {
        MyUserServer = m.ActRef;
    }

    private void PrintMessage(Msgs.ClientPrintMessageMsg m)
    {
        System.out.println(m.Text);
    }

    public void AnalyzeInput(String Input)
    {
        String delims = "[ ]+";
        String[] tokens = Input.split(delims);

        switch (tokens[0])
        {
            case "/w": {
                Msgs.TalkWithAnotherClientMsg m = new Msgs.TalkWithAnotherClientMsg();
                m.OtherUserName = tokens[1];
                m.Text = BuildMessage(tokens, 2);
                MyUserServer.tell(m, self());
                break;
            }

            case "/jump":
                Msgs.Jump2ChMsg m1 = new Msgs.Jump2ChMsg();
                m1.chName = tokens[1];
                MyUserServer.tell(m1, self());
                break;

            case "/channels":
                Msgs.ShowAllChannelsMsg m11 = new Msgs.ShowAllChannelsMsg();
                MyUserServer.tell(m11, self());
                break;


            case "/users":
                Msgs.ShowThisChannelUsersMsg m13 = new Msgs.ShowThisChannelUsersMsg();
                MyUserServer.tell(m13, self());
                break;

            case "/join":
                Msgs.ClientAsk2JoinChannelMsg m14 = new Msgs.ClientAsk2JoinChannelMsg();
                m14.ChannelName = tokens[1];
                MyUserServer.tell(m14, self());
                break;

            case "/leave":
                Msgs.LeaveChannelMsg m2 = new Msgs.LeaveChannelMsg();
                m2.chName = tokens[1];
                MyUserServer.tell(m2, self());
                break;


            case "/title":
                Msgs.ChangeChannelTitleMsg m3 = new Msgs.ChangeChannelTitleMsg();
                m3.chName = tokens[1];
                m3.chTitle = tokens[2];
                MyUserServer.tell(m3, self());
                break;


            case "/kick":
                Msgs.ClientKickUserMsg m4 = new Msgs.ClientKickUserMsg();
                m4.kickedUser = tokens[1];
                m4.chName = tokens[2];
                MyUserServer.tell(m4, self());
                break;


            case "/ban":
                Msgs.ClientBanUserMsg m5 = new Msgs.ClientBanUserMsg();
                m5.bannedUser = tokens[1];
                m5.chName = tokens[2];
                MyUserServer.tell(m5, self());
                break;


            case "/add":
                switch (tokens[2])
                {
                    case "v":
                        Msgs.ClientAddsVoicedUserMsg m6 = new Msgs.ClientAddsVoicedUserMsg();
                        m6.chName = tokens[1];
                        m6.UserName = tokens [3];
                        MyUserServer.tell(m6, self());
                        break;


                    case "op":
                        Msgs.ClientAddsChannelOperatorMsg m7 = new Msgs.ClientAddsChannelOperatorMsg();
                        m7.chName = tokens[1];
                        m7.UserName = tokens [3];
                        MyUserServer.tell(m7, self());
                        break;

                }
                break;

            case "/remove":
                switch (tokens[2])
                {
                    case "v":
                        Msgs.ClientRemovesVoicedUserMsg m8 = new Msgs.ClientRemovesVoicedUserMsg();
                        m8.chName = tokens[1];
                        m8.UserName = tokens [3];
                        MyUserServer.tell(m8, self());
                        break;


                    case "op":
                        Msgs.ClientRemovesChannelOperatorMsg m9 = new Msgs.ClientRemovesChannelOperatorMsg();
                        m9.chName = tokens[1];
                        m9.UserName = tokens [3];
                        MyUserServer.tell(m9, self());
                        break;

                }
                break;

            case "/disband":{
                Msgs.DisbandMsg m10 = new Msgs.DisbandMsg();
                m10.chName = tokens[1];
                MyUserServer.tell(m10, self());
                break;}


            default:
                Msgs.TalkMsg msg = new Msgs.TalkMsg();
                msg.Text = Input;
                MyUserServer.tell(msg, self());



        }
        /*for (String t : tokens)
            System.out.println(t);*/
    }

    private String BuildMessage(String[] tokens, int i)
    {
        String str = "";
        int j;
        for (j=i; j < tokens.length; j++)
            str = str + tokens[j] + " ";

        return str;
    }

    private void ShutDown(Msgs.ShutDownMsg m)
    {
        MyUserServer.tell(m, self());
        getContext().stop(self());
        //getContext().system().terminate();
    }

    @Override
    public void preStart() throws IOException
    {
         ChatManager = getContext().actorSelection("akka.tcp://ManagerHelper@127.0.0.1:3553/user/MyManager");

        /*
        Msgs.CreateUserServerMsg msg = new Msgs.CreateUserServerMsg();
        msg.UserName = UserName;

        ChatManager.tell(msg, self());
        */
    }
}
