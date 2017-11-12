package sample.hello;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import akka.actor.Props;
import akka.actor.UnhandledMessage;
import akka.pattern.Patterns;
import akka.util.Timeout;
import sample.hello.Msgs.*;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

public class UserServer extends AbstractActor{


    private String NickName;
    private Map<String,ActorRef> JoinedChannels;
    private ActorRef CurrChannel;
    private Map<String,ActorRef> ChUsersList = null;
    private ActorRef MyClient;
    private String timeStamp;
    private Mode CurrMode;
    private AbstractActor.Receive regularUser;
    private AbstractActor.Receive voicedUser;
    private AbstractActor.Receive channelOperator;
    private AbstractActor.Receive channelOwner;

    public enum Mode {
        Regular,
        Voiced,
        ChOperator,
        ChOwner
    }

    @Override
    public Receive createReceive() {
        timeStamp = new SimpleDateFormat("[HH:mm]").format(new java.util.Date());
        return receiveBuilder()
                .match(InitUserMsg.class, this::InitUser)
                .build();
    }



    public UserServer()
    {
        JoinedChannels = new HashMap<>();

        regularUser =
                receiveBuilder()
                        //regularUser operations
                        .match(TalkMsg.class, this::SendMsg2AllUsers)
                        .match(TakeChUsersListMsg.class, this::SetChUsersList)
                        .match(ClientAsk2JoinChannelMsg.class, this::AddUser2Channel)
                        .match(LeaveChannelMsg.class, this::RemoveUserFromChannel)
                        .match(TalkWithAnotherClientMsg.class, this:: SendMsg2SpecificUser)
                        .match(PrintMessageMsg.class, this::PrintMsg)
                        .match(InitUserMsg.class, this::InitUser)
                        .match(KickedMsg.class, this::TellKickedOrBannedUser)
                        .match(ShowAllChannelsMsg.class, this::ShowAllChannels)
                        .match(ShowThisChannelUsersMsg.class, this::ShowThisChannelUsers)
                        .match(Jump2ChMsg.class, this::Jump2Ch)
                        .match(Msgs.ShutDownMsg.class, this:: ShutDown)
                        .match(UpdateYourModeMsg.class,this::ChangeBehavior)

                        .matchAny(this::unhandled)
                        .build();

        voicedUser = receiveBuilder()
                //regularUser operations
                .match(TalkMsg.class, this::SendMsg2AllUsers)
                .match(TakeChUsersListMsg.class, this::SetChUsersList)
                .match(ClientAsk2JoinChannelMsg.class, this::AddUser2Channel)
                .match(LeaveChannelMsg.class, this::RemoveUserFromChannel)
                .match(TalkWithAnotherClientMsg.class, this:: SendMsg2SpecificUser)
                .match(PrintMessageMsg.class, this::PrintMsg)
                .match(InitUserMsg.class, this::InitUser)
                .match(KickedMsg.class, this::TellKickedOrBannedUser)
                .match(ShowAllChannelsMsg.class, this::ShowAllChannels)
                .match(ShowThisChannelUsersMsg.class, this::ShowThisChannelUsers)
                .match(Jump2ChMsg.class, this::Jump2Ch)
                .match(Msgs.ShutDownMsg.class, this:: ShutDown)
                .match(UpdateYourModeMsg.class,this::ChangeBehavior)

                //voicedUser operations
                .match(ChangeChannelTitleMsg.class, this::ChangeChannelTitle)

                .matchAny(this::unhandled)
                .build();

        channelOperator = receiveBuilder()
                //regularUser operations
                .match(TalkMsg.class, this::SendMsg2AllUsers)
                .match(TakeChUsersListMsg.class, this::SetChUsersList)
                .match(ClientAsk2JoinChannelMsg.class, this::AddUser2Channel)
                .match(LeaveChannelMsg.class, this::RemoveUserFromChannel)
                .match(TalkWithAnotherClientMsg.class, this:: SendMsg2SpecificUser)
                .match(PrintMessageMsg.class, this::PrintMsg)
                .match(InitUserMsg.class, this::InitUser)
                .match(KickedMsg.class, this::TellKickedOrBannedUser)
                .match(ShowAllChannelsMsg.class, this::ShowAllChannels)
                .match(ShowThisChannelUsersMsg.class, this::ShowThisChannelUsers)
                .match(Jump2ChMsg.class, this::Jump2Ch)
                .match(Msgs.ShutDownMsg.class, this:: ShutDown)
                .match(UpdateYourModeMsg.class,this::ChangeBehavior)


                //voicedUser operations
                .match(ChangeChannelTitleMsg.class, this::ChangeChannelTitle)

                //channelOperator operations
                .match(ClientAddsVoicedUserMsg.class, this::AddVoicedUser)
                .match(ClientRemovesVoicedUserMsg.class, this::RemoveVoicedUser)
                .match(ClientAddsChannelOperatorMsg.class, this::AddChOperator)
                .match(ClientRemovesChannelOperatorMsg.class, this::RemoveChOperator)
                .match(ClientBanUserMsg.class, this::BanUser)
                .match(ClientKickUserMsg.class, this::KickUser)

                .matchAny(this::unhandled)
                .build();

        channelOwner = receiveBuilder()
                //regularUser operations
                .match(TalkMsg.class, this::SendMsg2AllUsers)
                .match(TakeChUsersListMsg.class, this::SetChUsersList)
                .match(ClientAsk2JoinChannelMsg.class, this::AddUser2Channel)
                .match(LeaveChannelMsg.class, this::RemoveUserFromChannel)
                .match(TalkWithAnotherClientMsg.class, this:: SendMsg2SpecificUser)
                .match(PrintMessageMsg.class, this::PrintMsg)
                .match(InitUserMsg.class, this::InitUser)
                .match(KickedMsg.class, this::TellKickedOrBannedUser)
                .match(ShowAllChannelsMsg.class, this::ShowAllChannels)
                .match(ShowThisChannelUsersMsg.class, this::ShowThisChannelUsers)
                .match(Jump2ChMsg.class, this::Jump2Ch)
                .match(Msgs.ShutDownMsg.class, this:: ShutDown)
                .match(UpdateYourModeMsg.class,this::ChangeBehavior)


                //voicedUser operations
                .match(ChangeChannelTitleMsg.class, this::ChangeChannelTitle)

                //channelOperator operations
                .match(ClientAddsVoicedUserMsg.class, this::AddVoicedUser)
                .match(ClientRemovesVoicedUserMsg.class, this::RemoveVoicedUser)
                .match(ClientAddsChannelOperatorMsg.class, this::AddChOperator)
                .match(ClientRemovesChannelOperatorMsg.class, this::RemoveChOperator)
                .match(ClientBanUserMsg.class, this::BanUser)
                .match(ClientKickUserMsg.class, this::KickUser)


                //channelOwner operations
                .match(DisbandMsg.class, this::DisbandChannel)

                .matchAny(this::unhandled)
                .build();

    }

    @Override
    public void unhandled(Object message) {
        ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
        msg.Text = "You Cannot do this!";
        sender().tell(msg, self());
    }


    private <P> void ChangeBehavior(UpdateYourModeMsg m)
    {
        ChangeMyBehavior(m.newMode);
    }

    private void ChangeMyBehavior(Mode newMode)
    {
        if (newMode == Mode.ChOwner)
            getContext().become(channelOwner);
        else if (newMode == Mode.ChOperator)
            getContext().become(channelOperator);
        else if (newMode == Mode.Voiced)
            getContext().become(voicedUser);
        else getContext().become(regularUser);
    }




    private void Jump2Ch(Jump2ChMsg m) throws Exception {
        if (JoinedChannels.containsKey(m.chName))
        {
            ActorRef ChRef = JoinedChannels.get(m.chName);
            CurrChannel = ChRef;

            WhatIsMyModeMsg msg = new WhatIsMyModeMsg();
            msg.UserName = NickName;

            Timeout timeout = new Timeout(Duration.create(60, "seconds"));
            Future<Object> future = Patterns.ask(CurrChannel, msg, timeout);
            MyModeMsg m2 =  (MyModeMsg) Await.result(future, timeout.duration());
            CurrMode = m2.MyMode;

            ChangeMyBehavior(CurrMode);

            GetChannelTitleMsg m4 = new GetChannelTitleMsg();

            future = Patterns.ask(CurrChannel, m4, timeout);
            ChannelTitleMsg m3 =  (ChannelTitleMsg) Await.result(future, timeout.duration());

            ClientPrintMessageMsg msg1 = new ClientPrintMessageMsg();
            msg1.Text = "You are in " + m.chName + " Now!" + '\n' + "Channel Title is: " + m3.ChTitle ;
            MyClient.tell(msg1, self());
        }
        else
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You should join to channel first!";

            sender().tell(msg, self());
        }
    }

    private void ShowThisChannelUsers(ShowThisChannelUsersMsg m) throws Exception {
        GetChUsersListMsg msg = new GetChUsersListMsg();
        Timeout timeout = new Timeout(Duration.create(5, "seconds"));
        Future<Object> future = Patterns.ask(CurrChannel, msg, timeout);
        TakeChUsersListMsg m1 =  (TakeChUsersListMsg) Await.result(future, timeout.duration());

        Map<String,ActorRef> UsersList = m1.UsersList;

        Iterator it = UsersList.entrySet().iterator();

        String UserNames = "";

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (AskCHannelWhatsMyMode((String) pair.getKey() , CurrChannel) == Mode.ChOwner)
                UserNames = UserNames + "#";
            else if (AskCHannelWhatsMyMode((String) pair.getKey() , CurrChannel) == Mode.ChOperator)
                UserNames = UserNames + "@";
            else if (AskCHannelWhatsMyMode((String) pair.getKey() , CurrChannel) == Mode.Voiced)
                UserNames = UserNames + "+";

            UserNames = UserNames + pair.getKey() + '\n';
        }

        ClientPrintMessageMsg msg1 = new ClientPrintMessageMsg();
        msg1.Text = UserNames;

        sender().tell(msg1, self());
    }


    private void ShowAllChannels(ShowAllChannelsMsg m)
    {
        Iterator it = Manager.Channels.entrySet().iterator();

        String ChannelsNames = "";

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            ChannelsNames = ChannelsNames + pair.getKey();

            if (JoinedChannels.containsKey(pair.getKey()))
                ChannelsNames = ChannelsNames+ "(Joined) ";

            ChannelsNames = ChannelsNames+ '\n';

        }

        ClientPrintMessageMsg msg1 = new ClientPrintMessageMsg();
        msg1.Text = ChannelsNames;

        sender().tell(msg1, self());
    }

    private void TellKickedOrBannedUser(KickedMsg m)
    {
        ClientPrintMessageMsg msg1 = new ClientPrintMessageMsg();
        if (CurrChannel == JoinedChannels.get(m.chName))
        {
            CurrChannel = Manager.DefaultChannel;
            CurrMode = Mode.Regular;

            msg1.Text = timeStamp + " *** " + " you were kicked from " + m.chName + "! now you are in Lobby ";
        }
        else msg1.Text = timeStamp + " *** " + " you were kicked from " + m.chName + "!";

        JoinedChannels.remove(m.chName);

        MyClient.tell(msg1,self());


    }

    private void SendMsg2SpecificUser(TalkWithAnotherClientMsg m)
    {
        timeStamp = new SimpleDateFormat("[HH:mm]").format(new java.util.Date());

        ActorRef WantedUserRef = Manager.Users.get(m.OtherUserName);
        PrintMessageMsg msg = new PrintMessageMsg();
        msg.Text = timeStamp + " <" + NickName + "> " + m.Text; //User sent message: [TIME_STAMP] <userName> “the message”;

        WantedUserRef.tell(msg, self());
    }


    private void InitUser(InitUserMsg m) throws Exception {

        NickName = m.nickName;
        CurrChannel = m.DefaultCH.getValue();
        JoinedChannels.put(m.DefaultCH.getKey(),m.DefaultCH.getValue());
        MyClient = m.myClient;

        UserAsk2JoinChannelMsg msg = new UserAsk2JoinChannelMsg();
        msg.userName = NickName;
        msg.userRef = m.userServerRef;

        CurrChannel.tell(msg, self());
        getContext().become(regularUser);
        System.out.println("User name: " + NickName + ", userserver(actor at server) ref: " + self().toString());

        PrintMessageMsg msg1 = new PrintMessageMsg();
        msg1.Text = NickName + " joined Lobby!";
        IterateAndSend2Users(msg1);

    }


    private void DisbandChannel(DisbandMsg m) throws Exception {
        if (m.chName == "Lobby")
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You can not do this !";
            MyClient.tell(msg, self());
            return;
        }
        if(CurrChannel == JoinedChannels.get(m.chName)) // check if user is really in the channel he wants to disband
        {
            ActorRef chRef = JoinedChannels.get(m.chName);
            IterateAndKickChannelUsers(m.chName,chRef);
            DeleteChannelMsg msg = new DeleteChannelMsg();
            chRef.tell(msg, self());

            Manager.Channels.remove(m.chName);

            if (CurrChannel == chRef)
            {
                CurrChannel = Manager.DefaultChannel;
                CurrMode = Mode.Regular;
                getContext().become(regularUser);
            }
        }
        else
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You need to jump to " + m.chName + " channel first !";
            MyClient.tell(msg, self());
        }
    }



    private void PrintMsg(PrintMessageMsg m)
    {
        ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
        msg.Text = m.Text;
        MyClient.tell(msg, self());
    }

    private void RemoveChOperator(ClientRemovesChannelOperatorMsg m)throws Exception {
        ActorRef chRef = JoinedChannels.get(m.chName);
        if(CurrChannel == JoinedChannels.get(m.chName)) // check if user is really in the channel
        {
                RemoveChannelOperatorMsg msg = new RemoveChannelOperatorMsg();
                msg.UserName = m.UserName;
                chRef.tell(msg, self());

                ActorRef URef = Manager.Users.get(m.UserName);

                UpdateYourModeMsg m1=new UpdateYourModeMsg();
                m1.newMode = Mode.Regular;

                URef.tell(m1 , self());

        }
        else
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You need to jump to " + m.chName + " channel first !";
            MyClient.tell(msg, self());
        }
    }

    private void AddChOperator(ClientAddsChannelOperatorMsg m) throws Exception {
        ActorRef chRef = JoinedChannels.get(m.chName);
        if(CurrChannel == JoinedChannels.get(m.chName)) // check if user is really in the channel he wants to disband
        {
                AddChannelOperatorMsg msg = new AddChannelOperatorMsg();
                msg.UserName = m.UserName;
                chRef.tell(msg, self());

                ActorRef URef = Manager.Users.get(m.UserName);

                UpdateYourModeMsg m1=new UpdateYourModeMsg();
                m1.newMode = Mode.ChOperator;

                URef.tell(m1 , self());
        }
        else
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You need to jump to " + m.chName + " channel first !";
            MyClient.tell(msg, self());
        }
    }

    private  void RemoveVoicedUser(ClientRemovesVoicedUserMsg m) throws Exception {
        ActorRef chRef = JoinedChannels.get(m.chName);
        if(CurrChannel == JoinedChannels.get(m.chName)) // check if user is really in the channel he wants to disband
        {
                RemoveVoicedUserMsg msg = new RemoveVoicedUserMsg();
                msg.UserName = m.UserName;
                chRef.tell(msg, self());

                ActorRef URef = Manager.Users.get(m.UserName);

                UpdateYourModeMsg m1=new UpdateYourModeMsg();
                m1.newMode = Mode.Regular;

                URef.tell(m1 , self());
        }
        else
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You need to jump to " + m.chName + " channel first !";
            MyClient.tell(msg, self());
        }
    }

    private void AddVoicedUser(ClientAddsVoicedUserMsg m) throws Exception
    {
        ActorRef chRef = JoinedChannels.get(m.chName);
        if(CurrChannel == JoinedChannels.get(m.chName)) // check if user is really in the channel
        {
                AddVoicedUserMsg msg = new AddVoicedUserMsg();
                msg.UserName = m.UserName;
                chRef.tell(msg, self());

                ActorRef URef = Manager.Users.get(m.UserName);

                UpdateYourModeMsg m1=new UpdateYourModeMsg();
                m1.newMode = Mode.Voiced;

                URef.tell(m1 , self());
        }
        else
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You need to jump to " + m.chName + " channel first !";
            MyClient.tell(msg, self());
        }
    }

    private void ChangeChannelTitle(ChangeChannelTitleMsg m)throws Exception {
        ActorRef chRef = JoinedChannels.get(m.chName);

        if(CurrChannel == JoinedChannels.get(m.chName)) // check if user is really in the channel he wants to disband
        {
                SetChannelTitleMsg msg = new SetChannelTitleMsg();
                msg.chTitle = m.chTitle;
                chRef.tell(msg, self());

                PrintMessageMsg msg1 = new PrintMessageMsg();
                msg1.Text = "Channel Title changed to : " + m.chTitle;
                IterateAndSend2Users(msg1);
        }
        else
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You need to jump to " + m.chName + " channel first !";
            MyClient.tell(msg, self());
        }
    }

    private void KickUser(ClientKickUserMsg m) throws Exception {
        ActorRef chRef = JoinedChannels.get(m.chName);

        timeStamp = new SimpleDateFormat("[HH:mm]").format(new java.util.Date());
        if(CurrChannel == JoinedChannels.get(m.chName)) // check if user is really in the channel he wants to disband
        {
                KickUserMsg msg = new KickUserMsg();
                msg.kickedUser = m.kickedUser;
                chRef.tell(msg, self());

                PrintMessageMsg msg1 = new PrintMessageMsg();
                msg1.Text = timeStamp + " *** " + m.kickedUser + " kicked by " + NickName; //[TIME_STAMP] *** userName kicked by banningChannelOperatorName
                IterateAndSend2Users(msg1);
        }
        else
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You need to jump to " + m.chName + " channel first !";
            MyClient.tell(msg, self());
        }
    }

    private void BanUser(ClientBanUserMsg m) throws Exception {
        ActorRef chRef = JoinedChannels.get(m.chName);

        timeStamp = new SimpleDateFormat("[HH:mm]").format(new java.util.Date());
        if(CurrChannel == JoinedChannels.get(m.chName)) // check if user is really in the channel he wants to disband
        {
                BanUserMsg msg = new BanUserMsg();
                msg.bannedUser = m.bannedUser;
                chRef.tell(msg, self());

                PrintMessageMsg msg1 = new PrintMessageMsg();
                msg1.Text = timeStamp + " *** " + m.bannedUser + " banned by " + NickName; //[TIME_STAMP] *** userName banned by banningChannelOperatorName
                IterateAndSend2Users(msg1);
        }
        else
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You need to jump to " + m.chName + " channel first !";
            MyClient.tell(msg, self());
        }
    }

    private void RemoveUserFromChannel(LeaveChannelMsg m) throws Exception {
        timeStamp = new SimpleDateFormat("[HH:mm]").format(new java.util.Date());

        ActorRef chRef = JoinedChannels.get(m.chName);
        JoinedChannels.remove(m.chName);

        LeaveUserMsg msg = new LeaveUserMsg();
        msg.UserName = NickName;
        chRef.tell(msg, self());

        PrintMessageMsg msg1 = new PrintMessageMsg();
        msg1.Text = timeStamp + " *** parts: " + NickName; //User joins channel: [TIME_STAMP] *** parts: userName
        IterateAndSend2Users(msg1);

        if (CurrChannel == chRef)
        {
            CurrChannel = Manager.DefaultChannel;
            CurrMode = Mode.Regular;
            getContext().become(regularUser);
        }
    }

    private void AddUser2Channel(ClientAsk2JoinChannelMsg m) throws Exception {
        String chName = m.ChannelName;
        ActorRef TmpCh = null;

        timeStamp = new SimpleDateFormat("[HH:mm]").format(new java.util.Date());
        if (JoinedChannels.containsKey(chName))
        {
            ClientPrintMessageMsg msg = new ClientPrintMessageMsg();
            msg.Text = "You already joined this channel !";
            MyClient.tell(msg, self());
            return;
        }

        else if ( Manager.Channels.containsKey(chName))
        {
            TmpCh = Manager.Channels.get(chName);
            UserAsk2JoinChannelMsg msg = new UserAsk2JoinChannelMsg();
            msg.userName = NickName;
            msg.userRef = self();

            Timeout timeout = new Timeout(Duration.create(60, "seconds"));
            Future<Object> future = Patterns.ask(TmpCh, msg, timeout);
            ResponsingJoinRequest m2 =  (ResponsingJoinRequest) Await.result(future, timeout.duration());
            if (m2.CanJoin)
            {
                CurrChannel = TmpCh;
                JoinedChannels.put(chName, CurrChannel);

                PrintMessageMsg msg2 = new PrintMessageMsg();
                msg2.Text = timeStamp + " *** joins: " + NickName; //User joins channel: [TIME_STAMP] *** joins: userName
                IterateAndSend2Users(msg2);
                getContext().become(regularUser);

                GetChannelTitleMsg m4 = new GetChannelTitleMsg();
                future = Patterns.ask(TmpCh, m4, timeout);
                ChannelTitleMsg m3 =  (ChannelTitleMsg) Await.result(future, timeout.duration());

                ClientPrintMessageMsg msg1 = new ClientPrintMessageMsg();
                msg1.Text = "You are in " + chName + " Now!" + '\n' + "Channel Title is: " + m3.ChTitle ;
                MyClient.tell(msg1, self());
            }
            else
            {
                ClientPrintMessageMsg msg1 = new ClientPrintMessageMsg();
                msg1.Text = "U can't join this Channel! U are banned!";
                MyClient.tell(msg1,self());
            }
        }
        else
        {
            System.out.println("creating new channel..");
            CurrMode = Mode.ChOwner;
            CurrChannel = Main.MySystem.actorOf(Props.create(Channel.class), chName);
            Manager.Channels.put(chName,CurrChannel);


            InitChannelMsg msg = new InitChannelMsg();
            msg.channelName = chName;
            msg.channelOwner = NickName;
            CurrChannel.tell(msg, self());

            UserAsk2JoinChannelMsg msg1 = new UserAsk2JoinChannelMsg();
            msg1.userName = NickName;
            msg1.userRef = self();
            CurrChannel.tell(msg1, self());
            JoinedChannels.put(chName, CurrChannel);


            PrintMessageMsg msg2 = new PrintMessageMsg();
            msg2.Text = timeStamp + " *** joins: " + NickName; //User joins channel: [TIME_STAMP] *** joins: userName
            IterateAndSend2Users(msg2);
            getContext().become(channelOwner);

            ClientPrintMessageMsg m5 = new ClientPrintMessageMsg();
            m5.Text = "You are in " + chName + " Now!";
            MyClient.tell(m5, self());
        }
        WhatIsMyModeMsg msg = new WhatIsMyModeMsg();
        msg.UserName = NickName;

        Timeout timeout = new Timeout(Duration.create(60, "seconds"));
        Future<Object> future = Patterns.ask(CurrChannel, msg, timeout);
        MyModeMsg m2 =  (MyModeMsg) Await.result(future, timeout.duration());
        CurrMode = m2.MyMode;
    }


    private void SetChUsersList(TakeChUsersListMsg m)
    {
        ChUsersList = m.UsersList;
    }


    private void SendMsg2AllUsers(TalkMsg m) throws Exception {
        timeStamp = new SimpleDateFormat("[HH:mm]").format(new java.util.Date());

        PrintMessageMsg msg1 = new PrintMessageMsg();
        msg1.Text = timeStamp + "<" + NickName + ">" + m.Text; //User sent message: [TIME_STAMP] <userName> “the message”

        IterateAndSend2Users(msg1);
    }


    private void IterateAndSend2Users (PrintMessageMsg m) throws Exception {

        GetChUsersListMsg msg = new GetChUsersListMsg();
        Timeout timeout = new Timeout(Duration.create(5, "seconds"));
        Future<Object> future = Patterns.ask(CurrChannel, msg, timeout);
        TakeChUsersListMsg m1 =  (TakeChUsersListMsg) Await.result(future, timeout.duration());

        ChUsersList = m1.UsersList;

        Iterator it = ChUsersList.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ActorRef User2SendRef = (ActorRef) pair.getValue();
            User2SendRef.tell(m, self());
        }
    }

    private void IterateAndKickChannelUsers (String chName ,ActorRef ChannelToKickFrom) throws Exception {

        GetChUsersListMsg msg = new GetChUsersListMsg();
        Timeout timeout = new Timeout(Duration.create(5, "seconds"));
        Future<Object> future = Patterns.ask(ChannelToKickFrom, msg, timeout);
        TakeChUsersListMsg m1 =  (TakeChUsersListMsg) Await.result(future, timeout.duration());

        ChUsersList = m1.UsersList;
        KickedMsg m2 = new KickedMsg();
        m2.chName = chName;


        Iterator it = ChUsersList.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ActorRef User2SendRef = (ActorRef) pair.getValue();
            User2SendRef.tell(m2, self());
        }
    }


    private Mode AskCHannelWhatsMyMode(String NickName, ActorRef ChannelRef) throws Exception {
        WhatIsMyModeMsg msg = new WhatIsMyModeMsg();
        msg.UserName = NickName;

        Timeout timeout = new Timeout(Duration.create(60, "seconds"));
        Future<Object> future = Patterns.ask(ChannelRef, msg, timeout);
        MyModeMsg m2 =  (MyModeMsg) Await.result(future, timeout.duration());
        return m2.MyMode;
    }

    private void ShutDown(Msgs.ShutDownMsg m)
    {
        Manager.Users.remove(NickName);
        Iterator it = JoinedChannels.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            ActorRef chRef = (ActorRef) pair.getValue();

            LeaveUserMsg msg = new LeaveUserMsg();
            msg.UserName = NickName;
            chRef.tell(msg, self());
        }

        getContext().stop(self());
    }
}
