package sample.hello;

import akka.actor.ActorRef;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.Map;

public class Msgs implements Serializable
{
    /*User messages*/
    public static class TalkMsg implements Serializable { String Text;}
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
    public static class ShowAllChannelsMsg implements Serializable {}
    public static class ShowThisChannelUsersMsg implements Serializable {}
    public static class Jump2ChMsg implements Serializable {String chName;}



    /*Client Messages*/
    public static class ClientPrintMessageMsg implements Serializable { String Text;}
    public static class ClientUserServerActorMsg implements Serializable { ActorRef ActRef;}
    public static class SetUserNameMsg implements Serializable { String UName;}
    public static class ShutDownMsg implements Serializable {}


    /*Manager Messages */
    public static class CreateUserServerMsg implements Serializable { String UserName;}

}
