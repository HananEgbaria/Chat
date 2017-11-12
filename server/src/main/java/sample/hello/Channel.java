package sample.hello;

import akka.actor.AbstractActor;
import java.text.SimpleDateFormat;
import java.util.*;
import sample.hello.Msgs.*;
import akka.actor.ActorRef;

public class Channel extends AbstractActor
{
    private String ChannelName;
    private String ChannelTitle;
    private Map<String,ActorRef> JoinedUsers = null;
    private Set<String> BannedUsers = null;
    private Set<String> VoicedUsers = null;
    private Set<String> OperatorUsers = null;
    private String ChannelOwner;
    ActorRef CurrentSender;

    public Channel()
    {
        JoinedUsers = new HashMap<String,ActorRef>();
        BannedUsers = new HashSet<String>();
        VoicedUsers = new HashSet<String>();
        OperatorUsers = new HashSet<String>();
        ChannelTitle = "";
    }

    @Override
    public Receive createReceive()
    {
        CurrentSender = sender();
        return receiveBuilder()
                .match(InitChannelMsg.class, this::InitChannel)
                .match(GetChUsersListMsg.class, this::SendChannelUsersList)
                .match(UserAsk2JoinChannelMsg.class, this::AddUser2Channel)
                .match(LeaveUserMsg.class, this::RemoveUserFromChannel)
                .match(SetChannelTitleMsg.class, this::SetChannelTitle)
                .match(BanUserMsg.class, this::BanUser)
                .match(KickUserMsg.class, this::KickUser)
                .match(AddVoicedUserMsg.class, this::AddVoicedUser)
                .match(RemoveVoicedUserMsg.class, this::RemoveVoicedUser)
                .match(AddChannelOperatorMsg.class, this::AddChOperator)
                .match(RemoveChannelOperatorMsg.class, this::RemoveChOperator)
                .match(DeleteChannelMsg.class, this::DeleteAll)
                .match(WhatIsMyModeMsg.class, this::ReturnUserMode)
                .match(GetChannelTitleMsg.class, this::ReturnChTitle)

                .build();
    }

    private void ReturnChTitle(GetChannelTitleMsg m)
    {
        ChannelTitleMsg msg = new ChannelTitleMsg();
        msg.ChTitle = ChannelTitle;
        sender().tell(msg, self());
    }

    private void ReturnUserMode(WhatIsMyModeMsg m)
    {
        MyModeMsg msg = new MyModeMsg();
        if ( m.UserName == ChannelOwner)
            msg.MyMode = UserServer.Mode.ChOwner;
        else
        {
            if (OperatorUsers.contains(m.UserName)) msg.MyMode = UserServer.Mode.ChOperator;
            else
            {
                if (VoicedUsers.contains(m.UserName)) msg.MyMode = UserServer.Mode.Voiced;
                else  msg.MyMode = UserServer.Mode.Regular;
            }
        }

        sender().tell(msg,self());
    }

    private void DeleteAll(DeleteChannelMsg m)
    {

        JoinedUsers.clear();
        VoicedUsers.clear();
        OperatorUsers.clear();
        ChannelName = "";
        ChannelTitle = "";
        ChannelOwner = "";
        getContext().stop(self());
    }

    private void InitChannel(InitChannelMsg m)
    {
        ChannelName = m.channelName;
        ChannelOwner = m.channelOwner;
    }

    private void RemoveChOperator(RemoveChannelOperatorMsg m)
    {
        OperatorUsers.remove(m.UserName);
    }

    private void AddChOperator(AddChannelOperatorMsg m)
    {
        OperatorUsers.add(m.UserName);
        VoicedUsers.remove(m.UserName);
    }

    private void RemoveVoicedUser(RemoveVoicedUserMsg m)
    {
        VoicedUsers.remove(m.UserName);
    }

    private void AddVoicedUser(AddVoicedUserMsg m)
    {
        VoicedUsers.add(m.UserName);
        OperatorUsers.remove(m.UserName);
    }


    private void SendChannelUsersList(GetChUsersListMsg m)
    {

        Iterator it = JoinedUsers.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

        }

        TakeChUsersListMsg msg = new TakeChUsersListMsg();
        msg.UsersList = JoinedUsers;
        sender().tell(msg, self());
    }

    private void AddUser2Channel(UserAsk2JoinChannelMsg m)
    {

        String UName = m.userName;
        ActorRef URef = m.userRef;

        ResponsingJoinRequest m1 = new ResponsingJoinRequest();
        if (BannedUsers.contains(UName))
        {
            m1.CanJoin = false;
        }
        else
        {
            m1.CanJoin = true;
            JoinedUsers.put(UName, URef);
        }
        sender().tell(m1,self());

    }


    private void RemoveUserFromChannel(LeaveUserMsg m) {
        String UName = m.UserName;
        JoinedUsers.remove(UName);
    }

    private void SetChannelTitle(SetChannelTitleMsg m)
    {
        ChannelTitle = m.chTitle;
    }

    private void BanUser(BanUserMsg m)
    {
        ActorRef BannededUserRef = JoinedUsers.get(m.bannedUser);
        BannedUsers.add(m.bannedUser);
        JoinedUsers.remove(m.bannedUser);
        OperatorUsers.remove(m.bannedUser);
        VoicedUsers.remove(m.bannedUser);
        KickedMsg msg = new KickedMsg();
        msg.chName = ChannelName;
        BannededUserRef.tell(msg,self());

    }

    private void KickUser(KickUserMsg m)
    {
        ActorRef KickedUserRef = JoinedUsers.get(m.kickedUser);
        JoinedUsers.remove(m.kickedUser);
        JoinedUsers.remove(m.kickedUser);
        OperatorUsers.remove(m.kickedUser);
        VoicedUsers.remove(m.kickedUser);
        KickedMsg msg = new KickedMsg();
        msg.chName = ChannelName;
        KickedUserRef.tell(msg,self());
    }

}