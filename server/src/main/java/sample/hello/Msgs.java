package sample.hello;

import akka.actor.ActorRef;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.Map;

public class Msgs implements Serializable
{

    /*channel messages*/
    public static class GetChUsersListMsg { }
    public static class UserAsk2JoinChannelMsg{String userName; ActorRef userRef;}
    public static class LeaveUserMsg { String UserName;}
    public static class SetChannelTitleMsg {  String chTitle;}
    public static class BanUserMsg { String bannedUser;}
    public static class KickUserMsg { String kickedUser;}
    public static class AddVoicedUserMsg { String UserName;}
    public static class RemoveVoicedUserMsg { String UserName;}
    public static class AddChannelOperatorMsg { String UserName;}
    public static class RemoveChannelOperatorMsg { String UserName;}
    public static class ResponsingJoinRequest { Boolean CanJoin;}
    public static class WhatIsMyModeMsg { String UserName;}
    public static class InitChannelMsg { String channelName; String channelOwner; }
    public static class GetChannelTitleMsg { }
    public static class DeleteChannelMsg { }

    /*User messages*/
    public static class TalkMsg implements Serializable { String Text;}
    public static class TakeChUsersListMsg implements Serializable {Map<String,ActorRef> UsersList;}
    public static class ClientAsk2JoinChannelMsg implements Serializable {String ChannelName;}
    public static class LeaveChannelMsg implements Serializable { String chName;}
    public static class ClientBanUserMsg implements Serializable { String bannedUser; String chName;}
    public static class ClientKickUserMsg implements Serializable { String kickedUser; String chName;}
    public static class ChangeChannelTitleMsg implements Serializable {  String chTitle; String chName;}
    public static class ClientAddsVoicedUserMsg implements Serializable { String UserName; String chName;}
    public static class ClientRemovesVoicedUserMsg implements Serializable { String UserName; String chName;}
    public static class ClientAddsChannelOperatorMsg implements Serializable { String UserName; String chName;}
    public static class ClientRemovesChannelOperatorMsg implements Serializable { String UserName; String chName;}
    public static class DisbandMsg implements Serializable { String chName;}
    public static class TalkWithAnotherClientMsg implements Serializable {String OtherUserName; String Text;}
    public static class PrintMessageMsg implements Serializable { String Text;}
    public static class InitUserMsg implements Serializable { String nickName; Pair<String,ActorRef> DefaultCH; ActorRef myClient; ActorRef userServerRef;}
    public static class KickedMsg { String chName;}
    public static class MyModeMsg { UserServer.Mode MyMode;}
    public static class ShowAllChannelsMsg implements Serializable {}
    public static class ShowThisChannelUsersMsg implements Serializable {}
    public static class Jump2ChMsg implements Serializable {String chName;}
    public static class UpdateYourModeMsg implements Serializable {UserServer.Mode newMode;}
    public static class ChannelTitleMsg { String ChTitle; }



    /*Client Messages*/
    public static class ClientPrintMessageMsg implements Serializable { String Text;}
    public static class ClientUserServerActorMsg implements Serializable { ActorRef ActRef;}
    public static class ShutDownMsg implements Serializable {}


    /*Manager Messages */
    public static class CreateUserServerMsg implements Serializable { String UserName;}

}
